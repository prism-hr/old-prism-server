package com.zuehlke.pgadmissions.services.uclexport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.services.exporters.JSchFactory;
import com.zuehlke.pgadmissions.services.exporters.SubmitAdmissionsApplicationRequestBuilderV1;
import com.zuehlke.pgadmissions.utils.PausableHibernateCompatibleSequentialTaskExecutor;
import com.zuehlke.pgadmissions.utils.StacktraceDump;

/**
 * This is UCL data export service.
 * Used for situations where we push data to UCL system (PORTICO).
 */
@Service
//@Async("central-events-queue") -- only availabe in Spring 3.1.2
@Async()
class UclExportServiceImpl implements UclExportService {
    private static final Logger log = Logger.getLogger(UclExportServiceImpl.class);

    @Resource(name = "webservice-calling-queue-executor")
    private PausableHibernateCompatibleSequentialTaskExecutor webserviceCallingQueueExecutor;

    @Resource(name = "sftp-calling-queue-executor")
    private PausableHibernateCompatibleSequentialTaskExecutor sftpCallingQueueExecutor;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private ProgramInstanceDAO programInstanceDAO;

    @Autowired
    private ApplicationFormTransferDAO applicationFormTransferDAO;

    @Autowired
    private ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;

    private int numberOfConsecutiveSoapFaults = 0;

    @Value("${xml.data.export.webservice.consecutiveSoapFaultsLimit}")
    private int consecutiveSoapFaultsLimit;

    @Value("${xml.data.export.queue_pausing_delay_in_case_of_network_problem}")
    private int queuePausingDelayInCaseOfNetworkProblemsDiscovered;

    @Autowired
    private SftpAttachmentsSendingService sftpAttachmentsSendingService;

    public UclExportServiceImpl() {
    }

    @Autowired
    private JSchFactory jSchFactory;

    //oooooooooooooooooooooooooo PUBLIC API IMPLEMENTATION oooooooooooooooooooooooooooooooo

    public Long sendToUCL(ApplicationForm applicationForm) {
        return this.sendToUCL(applicationForm, new DeafListener());
    }

    public Long sendToUCL(ApplicationForm applicationForm, TransferListener listener) {
        log.debug("submitting application form " + applicationForm.getId() + " for ucl-eexport processing");
        ApplicationFormTransfer transfer = this.createPersistentQueueItem(applicationForm);
        //todo: create an event in application form's timeline ("scheduled transfer to UCL-PORTICO")
        webserviceCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));
        this.triggerQueued(listener);
        log.debug("succecfully added application form " + applicationForm.getId() + " to ucl-export queue (transfer-id=" + transfer.getId() +")");
        return transfer.getId();
    }

    public void systemStartupSendingQueuesRecovery() {
        //todo recreate the contents of both task queues from what we have in database
        //todo plug-in this method to application startup sequence
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
        request = new SubmitAdmissionsApplicationRequestBuilderV1(programInstanceDAO, new ObjectFactory()).applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();

        try  {
            //call webservice
            log.debug("calling marshalSendAndReceive for transfer " + transferId);
            response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request, webServiceMessageCallback);
            log.debug("successfully returned from webservice call for transfer " + transferId);

        }  catch (WebServiceTransportException e) {
            log.debug("WebServiceTransportException during webservice call for transfer " + transferId, e);
            //CASE 1: webservice call failed because of network failure, protocol problems etc
            //seems like we have communication problems so makes no sense to push more appliaction forms at the moment
            //todo: we should measure the time of this problem constantly occuring; after some treashold (1 day?) we should inform admins

            //save error information
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE);
            error.setDiagnosticInfo(StacktraceDump.forException(e));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_AND_PAUSE_TRANSFERS);
            applicationFormTransferErrorDAO.save(error);

            //pause the queue for some time
            this.pauseQueueForMinutes(queuePausingDelayInCaseOfNetworkProblemsDiscovered);

            //inform the listener
            this.triggerTransferFailed(listener, error);

            //schedudle the same transfer again
            this.sendToUCL(applicationForm, listener);

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
                //todo: inform admins that we have repeating webservice problems (probably by sending an email with problem description)
            }

            return;
        }

        //CASE 3; webservice answer was ok (transmission succesful, request approved)
        numberOfConsecutiveSoapFaults = 0;

        //extract precious IDs received from UCL and store them into ApplicationTransfer, ApplicationForm and RegisteredUser
        transfer.setUclUserIdReceived(response.getReference().getUserCode());
        transfer.setUclBookingReferenceReceived(response.getReference().getReferenceID());
        applicationForm.setUclBookingReferenceNumber(response.getReference().getReferenceID());
        if (applicationForm.getApplicant().getUclUserId() == null)
            applicationForm.getApplicant().setUclUserId(response.getReference().getUserCode());
        else {
            if (! applicationForm.getApplicant().getUclUserId().equals(response.getReference().getUserCode()))
                throw new RuntimeException("User code received from PORTICO do not mach with our PRISM user id: PRISM_ID=" +
                    applicationForm.getApplicant().getUclUserId() + " PORTICO_ID=" + response.getReference().getUserCode());
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
        } catch (SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack couldNotCreateAttachmentsPack) {
            //try 5 times then stop the queue



        } catch (SftpAttachmentsSendingService.LocallyDefinedSshConfigurationIsWrong locallyDefinedSshConfigurationIsWrong) {
            //stop queue

        } catch (SftpAttachmentsSendingService.CouldNotOpenSshConnectionToRemoteHost couldNotOpenSshConnectionToRemoteHost) {
            //network problems - just wait some time and try again (pause queue)

        } catch (SftpAttachmentsSendingService.SftpTargetDirectoryNotAccessible sftpTargetDirectoryNotAccessible) {
            //stop queue, inform admin (possibly ucl has to correct their config)

        } catch (SftpAttachmentsSendingService.SftpTransmissionFailedOrProtocolError sftpTransmissionFailedOrProtocolError) {
            //network problems - just wait some time and try again (pause queue)

        }

        //todo: register error, pause the queue for some time, inform admins

    }

    private void pauseQueueForMinutes(int minutes) {
        //todo: implement this!
        throw new RuntimeException("Not implemented yet");
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
