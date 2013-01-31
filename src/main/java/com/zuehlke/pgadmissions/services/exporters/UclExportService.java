package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Level;
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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.mail.DataExportMailSender;
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

    private final ApplicationFormTransferDAO applicationFormTransferDAO;

    private final ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;

    private int numberOfConsecutiveSoapFaults = 0;

    private final int consecutiveSoapFaultsLimit;

    private final int queuePausingDelayInCaseOfNetworkProblemsDiscovered;

    private final SftpAttachmentsSendingService sftpAttachmentsSendingService;

    private final TaskScheduler scheduler;
    
    private final DataExportMailSender dataExportMailSender;

    public UclExportService() {
        this(null, null, null, null, null, 0, 0, null, null, null);
    }
    
    @Autowired
    public UclExportService(
            @Qualifier("webservice-calling-queue-executor") PausableHibernateCompatibleSequentialTaskExecutor webserviceCallingQueueExecutor,
            @Qualifier("sftp-calling-queue-executor") PausableHibernateCompatibleSequentialTaskExecutor sftpCallingQueueExecutor,
            WebServiceTemplate webServiceTemplate, 
            ApplicationFormTransferDAO applicationFormTransferDAO,
            ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO, 
            @Value("${xml.data.export.webservice.consecutiveSoapFaultsLimit}") int consecutiveSoapFaultsLimit,
            @Value("${xml.data.export.queue_pausing_delay_in_case_of_network_problem}") int queuePausingDelayInCaseOfNetworkProblemsDiscovered,
            SftpAttachmentsSendingService sftpAttachmentsSendingService, 
            @Qualifier("ucl-export-service-scheduler") TaskScheduler scheduler,
            DataExportMailSender dataExportMailSender) {
        super();
        this.webserviceCallingQueueExecutor = webserviceCallingQueueExecutor;
        this.sftpCallingQueueExecutor = sftpCallingQueueExecutor;
        this.webServiceTemplate = webServiceTemplate;
        this.applicationFormTransferDAO = applicationFormTransferDAO;
        this.applicationFormTransferErrorDAO = applicationFormTransferErrorDAO;
        this.consecutiveSoapFaultsLimit = consecutiveSoapFaultsLimit;
        this.queuePausingDelayInCaseOfNetworkProblemsDiscovered = queuePausingDelayInCaseOfNetworkProblemsDiscovered;
        this.sftpAttachmentsSendingService = sftpAttachmentsSendingService;
        this.scheduler = scheduler;
        this.dataExportMailSender = dataExportMailSender;
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
    public Long sendToPortico(ApplicationForm applicationForm) {
        return this.sendToPortico(applicationForm, new DeafListener());
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
    public Long sendToPortico(ApplicationForm applicationForm, TransferListener listener) {
        log.info("Submitting application form " + applicationForm.getApplicationNumber() + " for ucl-export processing");

        ApplicationFormTransfer transfer = this.createPersistentQueueItem(applicationForm);
        
        // TODO: This used to use that asynchronous blocking queue but that just does not work properly
        //webserviceCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));
        transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(transfer.getId(), listener);
        
        this.triggerQueued(listener);
        
        log.info(String.format("Succecfully sent application form %s to PORTICO (transfer-id=%d)", applicationForm.getApplicationNumber(), transfer.getId()));
        
        return transfer.getId();
    }

    /**
     * I am recreating the application form transfers executors queues contents based on what we have in the database.
     * This is supposed to be invoked at system startup and is crucial to system crash recovery.
     */
    public void systemStartupSendingQueuesRecovery() {
        // allowing system to start smoothly before working on the queues.
        //this.pauseWsQueueForMinutes(1);
        //this.pauseSftpQueueForMinutes(1);
        
        log.info("Re-initialising the queues for ucl-export processing");
        
        for (ApplicationFormTransfer applicationFormTransfer : this.applicationFormTransferDAO.getAllTransfersWaitingForWebserviceCall()) {
            // TODO: This used to use that asynchronous blocking queue but that just does not work properly
            //webserviceCallingQueueExecutor.execute(new Phase1Task(this, applicationFormTransfer.getApplicationForm().getId(), applicationFormTransfer.getId(), new DeafListener()));
            transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(), new DeafListener());
        }
        
        for (ApplicationFormTransfer applicationFormTransfer : this.applicationFormTransferDAO.getAllTransfersWaitingForAttachmentsSending()) {
            // TODO: This used to use that asynchronous blocking queue but that just does not work properly
            //sftpCallingQueueExecutor.execute(new Phase2Task(this, applicationFormTransfer.getApplicationForm().getId(), applicationFormTransfer.getId(), new DeafListener()));
            transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(), new DeafListener());
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
        request = new SubmitAdmissionsApplicationRequestBuilder(new ObjectFactory()).applicationForm(applicationForm).build();
        listener.sendingSubmitAdmissionsApplicantRequest(request);
        
        try  {
            //call webservice
            log.info(String.format("Calling marshalSendAndReceive for transfer %d (%s)", transferId,  applicationForm.getApplicationNumber()));
            response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request, webServiceMessageCallback);
            log.info(String.format("Successfully returned from webservice call for transfer %d (%s)", transferId, applicationForm.getApplicationNumber()));
            log.info(String
                    .format("Received web service response [transferId=%d, applicationNumber=%s, applicantID=%s, applicationID=%s]",
                            transferId, applicationForm.getApplicationNumber(), response.getReference()
                                    .getApplicantID(), response.getReference().getApplicationID()));
        } catch (WebServiceIOException e) {
            logAndSendEmailToSuperadministrator(String.format(
                    "WebServiceTransportException during webservice call for transfer [transferId=%d, applicationNumber=%s]", transferId,
                    applicationForm.getApplicationNumber()), Level.WARN, e);
            //CASE 1: webservice call failed because of network failure, protocol problems etc
            //seems like we have communication problems so makes no sense to push more application forms at the moment
            //TODO: we should measure the time of this problem constantly occurring; after some threshold (1 day?) we should inform admins

            //save error information
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE);
            error.setDiagnosticInfo(StacktraceDump.printRootCauseStackTrace(e));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY);
            applicationFormTransferErrorDAO.save(error);

            //pause the queue for some time
            //this.pauseWsQueueForMinutes(queuePausingDelayInCaseOfNetworkProblemsDiscovered);

            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            // TODO: This used to use that asynchronous blocking queue but that just does not work properly
            //webserviceCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));

            return;

        } catch (SoapFaultClientException e) {
            logAndSendEmailToSuperadministrator(String.format(
                    "SoapFaultClientException during webservice call for transfer [transferId=%d, applicationNumber=%s]", transferId,
                    applicationForm.getApplicationNumber()), Level.WARN, e);
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
                //webserviceCallingQueueExecutor.pause();
                logAndSendEmailToSuperadministrator(String.format(
                        "Could not transfer application even after %d retries [transferId=%d, applicationNumber=%s]", numberOfConsecutiveSoapFaults, transferId,
                        applicationForm.getApplicationNumber()), Level.ERROR, e);
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
        // TODO: This used to use that asynchronous blocking queue but that just does not work properly
        //sftpCallingQueueExecutor.execute(new Phase2Task(this, applicationForm.getId(), transferId, listener));
        transactionallyExecuteSftpTransferAndUpdatePersistentQueue(transferId, listener);

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
            log.info(String.format("Calling sendApplicationFormDocuments for transfer %d (%s)", transferId,  applicationForm.getApplicationNumber()));
            sftpAttachmentsSendingService.sendApplicationFormDocuments(applicationForm, listener);
            transfer.setStatus(ApplicationTransferStatus.COMPLETED);
            transfer.setTransferFinishTimepoint(new Date());
            this.triggerTransferCompleted(listener, transfer.getUclUserIdReceived(), transfer.getUclBookingReferenceReceived());
            log.info(String.format("Transfer of documents completed for transfer %d (%s)", transferId,  applicationForm.getApplicationNumber()));
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
            
            logAndSendEmailToSuperadministrator(String.format(
                    "CouldNotCreateAttachmentsPack for application [transferId=%d, applicationNumber=%s]", transferId,
                    applicationForm.getApplicationNumber()), Level.ERROR, couldNotCreateAttachmentsPack);
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
            //sftpCallingQueueExecutor.pause();
            
            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            // TODO: This used to use that asynchronous blocking queue but that just does not work properly
            //sftpCallingQueueExecutor.execute(new Phase2Task(this, applicationForm.getId(),  transfer.getId(), listener));

            logAndSendEmailToSuperadministrator(String.format(
                    "LocallyDefinedSshConfigurationIsWrong for application [transferId=%d, applicationNumber=%s]", transferId,
                    applicationForm.getApplicationNumber()), Level.ERROR, locallyDefinedSshConfigurationIsWrong);
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
            //this.pauseSftpQueueForMinutes(queuePausingDelayInCaseOfNetworkProblemsDiscovered);
            
            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            //sftpCallingQueueExecutor.execute(new Phase2Task(this, applicationForm.getId(),  transfer.getId(), listener));
            
            logAndSendEmailToSuperadministrator(String.format(
                    "CouldNotOpenSshConnectionToRemoteHost for application [transferId=%d, applicationNumber=%s]", transferId,
                    applicationForm.getApplicationNumber()), Level.WARN, couldNotOpenSshConnectionToRemoteHost);
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
            //sftpCallingQueueExecutor.pause();
            
            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            // TODO: This used to use that asynchronous blocking queue but that just does not work properly
            //sftpCallingQueueExecutor.execute(new Phase2Task(this, applicationForm.getId(),  transfer.getId(), listener));

            logAndSendEmailToSuperadministrator(String.format(
                    "SftpTargetDirectoryNotAccessible for application [transferId=%d, applicationNumber=%s]", transferId,
                    applicationForm.getApplicationNumber()), Level.WARN, sftpTargetDirectoryNotAccessible);
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
            //this.pauseSftpQueueForMinutes(queuePausingDelayInCaseOfNetworkProblemsDiscovered);
            
            //inform the listener
            this.triggerTransferFailed(listener, error);

            //Schedule the same transfer again
            // TODO: This used to use that asynchronous blocking queue but that just does not work properly
            //sftpCallingQueueExecutor.execute(new Phase2Task(this, applicationForm.getId(),  transfer.getId(), listener));
            
            logAndSendEmailToSuperadministrator(String.format(
                    "SftpTransmissionFailedOrProtocolError for application [transferId=%d, applicationNumber=%s]", transferId,
                    applicationForm.getApplicationNumber()), Level.WARN, sftpTransmissionFailedOrProtocolError);
        }
    }
    
    private void logAndSendEmailToSuperadministrator(final String message, final Level logLevel, final Exception exception) {
        log.log(logLevel, message, exception);
        dataExportMailSender.sendErrorMessage(message, exception);
    }

    protected void pauseWsQueueForMinutes(int minutes) {
        log.info("Pausing WebService queue for " + minutes + " minutes");
        webserviceCallingQueueExecutor.pause();
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("Resuming WebService queue");
                webserviceCallingQueueExecutor.resume();
            }
        }, DateUtils.addMinutes(new Date(), minutes));
    }
    
    protected void pauseSftpQueueForMinutes(int minutes) {
        log.info("Pausing SFTP queue for " + minutes + " minutes");
        sftpCallingQueueExecutor.pause();
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("Resuming SFTP queue");
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
