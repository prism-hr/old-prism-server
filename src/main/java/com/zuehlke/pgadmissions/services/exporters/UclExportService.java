package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.utils.PausableHibernateCompatibleSequentialTaskExecutor;
import com.zuehlke.pgadmissions.utils.StacktraceDump;

/**
 * This is UCL data export service.
 * Used for situations where we push data to UCL system (PORTICO).
 */
@Service
public class UclExportService {
    private static final Logger log = Logger.getLogger(UclExportService.class);

    private final PausableHibernateCompatibleSequentialTaskExecutor webserviceCallingQueueExecutor;
    
    private final PausableHibernateCompatibleSequentialTaskExecutor sftpCallingQueueExecutor;

    private final WebServiceTemplate webServiceTemplate;

    private final ProgramInstanceDAO programInstanceDAO;

    private final ApplicationFormTransferDAO applicationFormTransferDAO;

    private final ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;

    private int numberOfConsecutiveSoapFaults = 0;

    private final int consecutiveSoapFaultsLimit;

    private final int queuePausingDelayInCaseOfNetworkProblemsDiscovered;

    private final SftpAttachmentsSendingService sftpAttachmentsSendingService;

    private final TaskScheduler scheduler;

    private QualificationInstitutionDAO qualificationInstitutionDAO;

    private DomicileDAO domicileDAO;

    public UclExportService() {
        this(null, null, null, null, null, null, 0, 0, null, null, null, null);
    }
    
    @Autowired
    public UclExportService(
            @Qualifier("webservice-calling-queue-executor") PausableHibernateCompatibleSequentialTaskExecutor webserviceCallingQueueExecutor,
            @Qualifier("sftp-calling-queue-executor") PausableHibernateCompatibleSequentialTaskExecutor sftpCallingQueueExecutor,
            WebServiceTemplate webServiceTemplate, 
            ProgramInstanceDAO programInstanceDAO,
            ApplicationFormTransferDAO applicationFormTransferDAO,
            ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO, 
            @Value("${xml.data.export.webservice.consecutiveSoapFaultsLimit}") int consecutiveSoapFaultsLimit,
            @Value("${xml.data.export.queue_pausing_delay_in_case_of_network_problem}") int queuePausingDelayInCaseOfNetworkProblemsDiscovered,
            SftpAttachmentsSendingService sftpAttachmentsSendingService, 
            @Qualifier("ucl-export-service-scheduler") TaskScheduler scheduler,
            QualificationInstitutionDAO qualificationInstitutionDAO,
            DomicileDAO domicileDAO) {
        super();
        this.webserviceCallingQueueExecutor = webserviceCallingQueueExecutor;
        this.sftpCallingQueueExecutor = sftpCallingQueueExecutor;
        this.webServiceTemplate = webServiceTemplate;
        this.programInstanceDAO = programInstanceDAO;
        this.applicationFormTransferDAO = applicationFormTransferDAO;
        this.applicationFormTransferErrorDAO = applicationFormTransferErrorDAO;
        this.consecutiveSoapFaultsLimit = consecutiveSoapFaultsLimit;
        this.queuePausingDelayInCaseOfNetworkProblemsDiscovered = queuePausingDelayInCaseOfNetworkProblemsDiscovered;
        this.sftpAttachmentsSendingService = sftpAttachmentsSendingService;
        this.scheduler = scheduler;
        this.qualificationInstitutionDAO = qualificationInstitutionDAO;
        this.domicileDAO = domicileDAO;
    }

    //oooooooooooooooooooooooooo PUBLIC API IMPLEMENTATION oooooooooooooooooooooooooooooooo

    /**
     * I am scheduling a new application form transfer for a given application form.
     * As a consequence this application form will be sent to UCL (some time later .. this goes in background).
     * The scheduling mechanism is reliable i.e. is able to survive possible system crash.<p/>
     *
     * If the caller wants to observe the sending process - please use sendToUCL(ApplicationForm,TransferListener) method.
     *
     * @param applicationForm application form to be transferred
     * @return application transfer id - may be usable for diagnostic purposes, but usually simple ignore this
     */
    public Long sendToUCL(ApplicationForm applicationForm) {
        return this.sendToUCL(applicationForm, new DeafListener());
    }

    /**
     * I am scheduling a new application form transfer for a given application form.
     * As a consequence this application form will be sent to UCL (some time later .. this goes in background).
     * The scheduling mechanism is reliable i.e. is able to survive possible system crash.
     *
     * @param applicationForm application form to be transferred
     * @param listener callback listener for observing the sending process
     * @return application transfer id - may be usable for diagnostic purposes, but usually simple ignore this
     */
    public Long sendToUCL(ApplicationForm applicationForm, TransferListener listener) {
        log.debug("submitting application form " + applicationForm.getId() + " for ucl-export processing");
        ApplicationFormTransfer transfer = this.createPersistentQueueItem(applicationForm);
        //TODO: create an event in application form's timeline ("scheduled transfer to UCL-PORTICO")
        webserviceCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));
        this.triggerQueued(listener);
        log.debug("succecfully added application form " + applicationForm.getId() + " to ucl-export queue (transfer-id=" + transfer.getId() +")");
        return transfer.getId();
    }

    /**
     * I am recreating the application form transfers executors queues contents based on what we have in the database.
     * This is supposed to be invoked at system startup and is crucial to system crash recovery.
     */
    public void systemStartupSendingQueuesRecovery() {
        // allowing system to start smoothly before working on the queues.
        this.pauseWsQueueForMinutes(1);
        this.pauseSftpQueueForMinutes(1);
        
        log.info("Re-initialising the queues for ucl-eexport processing");
        
        for (ApplicationFormTransfer applicationFormTransfer : this.applicationFormTransferDAO.getAllTransfersWaitingForWebserviceCall()) {
            webserviceCallingQueueExecutor.execute(new Phase1Task(this, applicationFormTransfer.getApplicationForm().getId(),  applicationFormTransfer.getId(), new DeafListener()));
        }
        
        for (ApplicationFormTransfer applicationFormTransfer : this.applicationFormTransferDAO.getAllTransfersWaitingForAttachmentsSending()) {
            webserviceCallingQueueExecutor.execute(new Phase2Task(this, applicationFormTransfer.getApplicationForm().getId(),  applicationFormTransfer.getId(), new DeafListener()));
        }
    }

    //ooooooooooooooooooooooooooooooo PRIVATE oooooooooooooooooooooooooooooooo

    @Transactional
    public ApplicationFormTransfer createPersistentQueueItem(ApplicationForm applicationForm) {
        ApplicationFormTransfer result = new ApplicationFormTransfer();
        result.setApplicationForm(applicationForm);
        result.setTransferStartTimepoint(new Date());
        result.setStatus(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL);
        applicationFormTransferDAO.save(result);
        return result;
    }

    @Transactional
    public void transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(Long transferId, TransferListener listener) {
        this.triggerTransferStarted(listener);

        //retrieve AppliationForm and ApplicationFormTransfer instances from the db
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getById(transferId);
        ApplicationForm applicationForm  = transfer.getApplicationForm();

        //prepare to webservice call
        SubmitAdmissionsApplicationRequest request;
        AdmissionsApplicationResponse response;
        final ByteArrayOutputStream requestMessageBuffer =  new ByteArrayOutputStream(5000);

        WebServiceMessageCallback webServiceMessageCallback = new WebServiceMessageCallback() {
            public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                webServiceMessage.writeTo(requestMessageBuffer);
            }
        };

        //build webservice request
        request = new SubmitAdmissionsApplicationRequestBuilder(programInstanceDAO, qualificationInstitutionDAO, domicileDAO, new ObjectFactory()).applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();
        listener.sendingSubmitAdmissionsApplicantRequest(request);
        
        try  {
            //call webservice
            log.debug("calling marshalSendAndReceive for transfer " + transferId);
            response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request, webServiceMessageCallback);
            log.debug("successfully returned from webservice call for transfer " + transferId);
        } catch (WebServiceIOException e) {            
            log.debug("WebServiceTransportException during webservice call for transfer " + transferId, e);
            //CASE 1: webservice call failed because of network failure, protocol problems etc
            //seems like we have communication problems so makes no sense to push more application forms at the moment
            //TODO: we should measure the time of this problem constantly occurring; after some threshold (1 day?) we should inform admins

            //save error information
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE);
            error.setDiagnosticInfo(StacktraceDump.forException(e));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY);
            applicationFormTransferErrorDAO.save(error);

            //pause the queue for some time
            this.pauseWsQueueForMinutes(queuePausingDelayInCaseOfNetworkProblemsDiscovered);

            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            webserviceCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));

            return;

        } catch (SoapFaultClientException e) {
            log.debug("SoapFaultClientException during webservice call for transfer " + transferId, e);
            //CASE 2: webservice is alive but refused to accept our request
            //usually this will be caused by validation problems - and is actually expected as side effect of PORTICO and PRISM evolution

            //save error information
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.WEBSERVICE_SOAP_FAULT);
            error.setDiagnosticInfo(StacktraceDump.forException(e));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY);
            error.setRequestCopy(requestMessageBuffer.toString());
            ByteArrayOutputStream responseMessageBuffer =  new ByteArrayOutputStream(5000);
            try {
                e.getWebServiceMessage().writeTo(responseMessageBuffer);
            } catch (IOException ioex) {
                throw new RuntimeException("Line unreachable");//writing to in-memory buffer should not fail
            }
            error.setResponseCopy(responseMessageBuffer.toString());
            applicationFormTransferErrorDAO.save(error);

            //inform the listener
            this.triggerTransferFailed(listener, error);

            //update transfer status
            transfer.setStatus(ApplicationTransferStatus.REJECTED_BY_WEBSERVICE);
            transfer.setTransferFinishTimepoint(new Date());

            //we count soap-fault situations;  if faults are repeating - we will eventually stop the queue and issue an email alert to administrators
            numberOfConsecutiveSoapFaults++;
            if (numberOfConsecutiveSoapFaults > consecutiveSoapFaultsLimit) {
                webserviceCallingQueueExecutor.pause();
                //TODO: inform admins that we have repeating webservice problems (probably by sending an email with problem description)
            }
            return;
        }

        //CASE 3; webservice answer was ok (transmission successful, request approved)
        numberOfConsecutiveSoapFaults = 0;

        //extract precious IDs received from UCL and store them into ApplicationTransfer, ApplicationForm and RegisteredUser
        transfer.setUclUserIdReceived(response.getReference().getApplicantID());
        transfer.setUclBookingReferenceReceived(response.getReference().getApplicationID());
        applicationForm.setUclBookingReferenceNumber(response.getReference().getApplicationID());
        if (applicationForm.getApplicant().getUclUserId() == null)
            applicationForm.getApplicant().setUclUserId(response.getReference().getApplicantID());
        else {
            if (! applicationForm.getApplicant().getUclUserId().equals(response.getReference().getApplicantID()))
                throw new RuntimeException("User code received from PORTICO do not mach with our PRISM user id: PRISM_ID=" +
                    applicationForm.getApplicant().getUclUserId() + " PORTICO_ID=" + response.getReference().getApplicantID());
        }

        //update transfer status in the database
        transfer.setStatus(ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING);

        //schedule phase 2 (sftp)
        sftpCallingQueueExecutor.execute(new Phase2Task(this, applicationForm.getId(), transferId, listener));

        //inform the listener
        this.triggerWebserviceCallCompleted(listener);
    }

    @Transactional
    public void transactionallyExecuteSftpTransferAndUpdatePersistentQueue(Long transferId, TransferListener listener) {
        this.triggerAttachmentsSftpTransmissionStarted(listener);

        //retrieve AppliationForm and ApplicationFormTransfer instances from the db
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getById(transferId);
        ApplicationForm applicationForm  = transfer.getApplicationForm();

        this.triggerAttachmentsSftpTransmissionStarted(listener);

        //pack attachments and send them over sftp
        try {
            sftpAttachmentsSendingService.sendApplicationFormDocuments(applicationForm, listener);
            transfer.setStatus(ApplicationTransferStatus.COMPLETED);
            transfer.setTransferFinishTimepoint(new Date());
            this.triggerTransferCompleted(listener, transfer.getUclUserIdReceived(), transfer.getUclBookingReferenceReceived());
        } catch (SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack couldNotCreateAttachmentsPack) {
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION);
            error.setDiagnosticInfo(StacktraceDump.forException(couldNotCreateAttachmentsPack));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY);
            applicationFormTransferErrorDAO.save(error);

            //inform the listener
            this.triggerTransferFailed(listener, error);
            transfer.setStatus(ApplicationTransferStatus.CANCELLED);
            
            //TODO: notify administrators, needs attention

        } catch (SftpAttachmentsSendingService.LocallyDefinedSshConfigurationIsWrong locallyDefinedSshConfigurationIsWrong) {
            //stop queue
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION);
            error.setDiagnosticInfo(StacktraceDump.forException(locallyDefinedSshConfigurationIsWrong));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION);
            applicationFormTransferErrorDAO.save(error);
            
            //pause the queue
            sftpCallingQueueExecutor.pause();
            
            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            sftpCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));

            //TODO: notify administrators, needs attention

        } catch (SftpAttachmentsSendingService.CouldNotOpenSshConnectionToRemoteHost couldNotOpenSshConnectionToRemoteHost) {
            //network problems - just wait some time and try again (pause queue)
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE);
            error.setDiagnosticInfo(StacktraceDump.forException(couldNotOpenSshConnectionToRemoteHost));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY);
            applicationFormTransferErrorDAO.save(error);
            
            // pause
            this.pauseSftpQueueForMinutes(queuePausingDelayInCaseOfNetworkProblemsDiscovered);
            
            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            sftpCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));
            
        } catch (SftpAttachmentsSendingService.SftpTargetDirectoryNotAccessible sftpTargetDirectoryNotAccessible) {
            //stop queue, inform admin (possibly ucl has to correct their config)
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_DIRECTORY_NOT_AVAILABLE);
            error.setDiagnosticInfo(StacktraceDump.forException(sftpTargetDirectoryNotAccessible));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION);
            applicationFormTransferErrorDAO.save(error);

            //pause the queue
            sftpCallingQueueExecutor.pause();
            
            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            sftpCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));

            //TODO: notify administrators, needs attention

        } catch (SftpAttachmentsSendingService.SftpTransmissionFailedOrProtocolError sftpTransmissionFailedOrProtocolError) {
            //network problems - just wait some time and try again (pause queue)
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE);
            error.setDiagnosticInfo(StacktraceDump.forException(sftpTransmissionFailedOrProtocolError));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY);
            applicationFormTransferErrorDAO.save(error);
            
            // pause
            this.pauseSftpQueueForMinutes(queuePausingDelayInCaseOfNetworkProblemsDiscovered);
            
            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            sftpCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));
        }
    }

    protected void pauseWsQueueForMinutes(int minutes) {
        webserviceCallingQueueExecutor.pause();
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                webserviceCallingQueueExecutor.resume();
            }
        }, DateUtils.addMinutes(new Date(), minutes));
    }
    
    protected void pauseSftpQueueForMinutes(int minutes) {
        sftpCallingQueueExecutor.pause();
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                sftpCallingQueueExecutor.resume();
            }
        }, DateUtils.addMinutes(new Date(), minutes));
    }

    @Async
    void triggerQueued(TransferListener listener) {
        try {
            listener.queued();
        } catch (RuntimeException e) {
            e.printStackTrace();//there is nothing better we can do with this exeption
        }
    }

    @Async
    void triggerTransferStarted(TransferListener listener) {
        try {
            listener.transferStarted();
        } catch (RuntimeException e) {
            e.printStackTrace();//there is nothing better we can do with this exeption
        }
    }

    @Async
    void triggerWebserviceCallCompleted(TransferListener listener) {
        try {
            listener.webserviceCallCompleted();
        } catch (RuntimeException e) {
            e.printStackTrace();//there is nothing better we can do with this exeption
        }
    }

    @Async
    void triggerSshConnectionEstablished(TransferListener listener) {
        try {
            listener.sshConnectionEstablished();
        } catch (RuntimeException e) {
            e.printStackTrace();//there is nothing better we can do with this exeption
        }
    }

    @Async
    void triggerAttachmentsSftpTransmissionStarted(TransferListener listener) {
        try {
            listener.attachmentsSftpTransmissionStarted();
        } catch (RuntimeException e) {
            e.printStackTrace();//there is nothing better we can do with this exeption
        }
    }

    @Async
    void triggerTransferCompleted(TransferListener listener, String uclUserId, String uclBookingReferenceNumber) {
        try {
            listener.transferCompleted(uclUserId, uclBookingReferenceNumber);
        } catch (RuntimeException e) {
            e.printStackTrace();//there is nothing better we can do with this exeption
        }
    }

    @Async
    void triggerTransferFailed(TransferListener listener, ApplicationFormTransferError error) {
        try {
            listener.transferFailed(error);
        } catch (RuntimeException e) {
            e.printStackTrace();//there is nothing better we can do with this exeption
        }
    }
}
