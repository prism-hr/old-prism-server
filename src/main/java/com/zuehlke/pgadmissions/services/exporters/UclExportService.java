package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.mail.DataExportMailSender;
import com.zuehlke.pgadmissions.utils.StacktraceDump;

/**
 * This is UCL data export service. Used for situations where we push data to UCL system (PORTICO).
 */
@Service
public class UclExportService {
    
    private final Logger log = LoggerFactory.getLogger(UclExportService.class);

    private final WebServiceTemplate webServiceTemplate;

    private final ApplicationFormTransferDAO applicationFormTransferDAO;

    private final ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;

    private final CommentDAO commentDAO;

    private int numberOfConsecutiveSoapFaults = 0;

    private final int consecutiveSoapFaultsLimit;

    private SftpAttachmentsSendingService sftpAttachmentsSendingService;

    private final DataExportMailSender dataExportMailSender;

    public UclExportService() {
        this(null, null, null, null, 0, null, null);
    }

    @Autowired
    public UclExportService(WebServiceTemplate webServiceTemplate, ApplicationFormTransferDAO applicationFormTransferDAO,
            ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO, CommentDAO commentDAO,
            @Value("${xml.data.export.webservice.consecutiveSoapFaultsLimit}") int consecutiveSoapFaultsLimit,
            SftpAttachmentsSendingService sftpAttachmentsSendingService, DataExportMailSender dataExportMailSender) {
        super();
        this.webServiceTemplate = webServiceTemplate;
        this.applicationFormTransferDAO = applicationFormTransferDAO;
        this.applicationFormTransferErrorDAO = applicationFormTransferErrorDAO;
        this.commentDAO = commentDAO;
        this.consecutiveSoapFaultsLimit = consecutiveSoapFaultsLimit;
        this.sftpAttachmentsSendingService = sftpAttachmentsSendingService;
        this.dataExportMailSender = dataExportMailSender;
    }

    // oooooooooooooooooooooooooo PUBLIC API IMPLEMENTATION oooooooooooooooooooooooooooooooo

    /**
     * I am scheduling a new application form transfer for a given application form. As a consequence this application form will be sent to UCL (some time later
     * .. this goes in background). The scheduling mechanism is reliable i.e. is able to survive possible system crash.
     * <p/>
     * 
     * If the caller wants to observe the sending process - please use sendToUCL(ApplicationForm,TransferListener) method.
     * 
     * @param applicationForm
     *            application form to be transferred
     * @return application transfer id - may be usable for diagnostic purposes, but usually simple ignore this
     */
    public Long sendToPortico(ApplicationForm applicationForm) {
        return this.sendToPortico(applicationForm, new DeafListener());
    }

    /**
     * I am scheduling a new application form transfer for a given application form. As a consequence this application form will be sent to UCL (some time later
     * .. this goes in background). The scheduling mechanism is reliable i.e. is able to survive possible system crash.
     * 
     * @param applicationForm
     *            application form to be transferred
     * @param listener
     *            callback listener for observing the sending process
     * @return application transfer id - may be usable for diagnostic purposes, but usually simple ignore this
     */
    public Long sendToPortico(ApplicationForm applicationForm, TransferListener listener) {
        log.info("Submitting application form " + applicationForm.getApplicationNumber() + " for ucl-export processing");

        ApplicationFormTransfer transfer = this.createPersistentQueueItem(applicationForm);

        transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(transfer.getId(), listener);

        log.info(String.format("Successfully sent application form %s to PORTICO (transfer-id=%d)", applicationForm.getApplicationNumber(), transfer.getId()));

        return transfer.getId();
    }

    /**
     * I am recreating the application form transfers executors queues contents based on what we have in the database. This is supposed to be invoked at system
     * startup and is crucial to system crash recovery.
     */
    public void systemStartupSendingQueuesRecovery() {

        log.info("Re-initialising the queues for ucl-export processing");

        for (ApplicationFormTransfer applicationFormTransfer : this.applicationFormTransferDAO.getAllTransfersWaitingForWebserviceCall()) {
            transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(), new DeafListener());
        }

        for (ApplicationFormTransfer applicationFormTransfer : this.applicationFormTransferDAO.getAllTransfersWaitingForAttachmentsSending()) {
            transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(), new DeafListener());
        }
    }

    public void setPorticoAttachmentsZipCreator(PorticoAttachmentsZipCreator zipCreator) {
        sftpAttachmentsSendingService.setPorticoAttachmentsZipCreator(zipCreator);
    }

    public void setSftpAttachmentsSendingService(SftpAttachmentsSendingService sendingService) {
        sftpAttachmentsSendingService = sendingService;
    }

    // ooooooooooooooooooooooooooooooo PRIVATE oooooooooooooooooooooooooooooooo

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
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getById(transferId);
        ApplicationForm applicationForm = transfer.getApplicationForm();
        ValidationComment validationComment = commentDAO.getValidationCommentForApplication(applicationForm);
        Boolean isOverseasStudent = validationComment.getHomeOrOverseas() == HomeOrOverseas.OVERSEAS;
        // prepare to webservice call
        SubmitAdmissionsApplicationRequest request;
        AdmissionsApplicationResponse response;
        final ByteArrayOutputStream requestMessageBuffer = new ByteArrayOutputStream(5000);

        WebServiceMessageCallback webServiceMessageCallback = new WebServiceMessageCallback() {
            public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                webServiceMessage.writeTo(requestMessageBuffer);
            }
        };

        request = new SubmitAdmissionsApplicationRequestBuilder(new ObjectFactory()).applicationForm(applicationForm).isOverseasStudent(isOverseasStudent)
                .build();
        listener.webServiceCallStarted(request);

        try {
            // call webservice
            log.info(String.format("Calling marshalSendAndReceive for transfer %d (%s)", transferId, applicationForm.getApplicationNumber()));

            response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request, webServiceMessageCallback);

            log.info(String.format("Successfully returned from webservice call for transfer %d (%s)", transferId, applicationForm.getApplicationNumber()));

            log.info(String.format("Received web service response [transferId=%d, applicationNumber=%s, applicantID=%s, applicationID=%s]", transferId,
                    applicationForm.getApplicationNumber(), response.getReference().getApplicantID(), response.getReference().getApplicationID()));
        } catch (WebServiceIOException e) {
            logAndSendEmailToSuperadministrator(String.format(
                    "WebServiceTransportException during webservice call for transfer [transferId=%d, applicationNumber=%s]", transferId,
                    applicationForm.getApplicationNumber()), e);
            // CASE 1: webservice call failed because of network failure, protocol problems etc
            // seems like we have communication problems so makes no sense to push more application forms at the moment
            // TODO: we should measure the time of this problem constantly occurring; after some threshold (1 day?) we should inform admins

            // save error information
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE);
            error.setDiagnosticInfo(StacktraceDump.printRootCauseStackTrace(e));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY);
            applicationFormTransferErrorDAO.save(error);

            listener.webServiceCallFailed(error);

            return;

        } catch (SoapFaultClientException e) {
            logAndSendEmailToSuperadministrator(String.format(
                    "SoapFaultClientException during webservice call for transfer [transferId=%d, applicationNumber=%s]", transferId,
                    applicationForm.getApplicationNumber()), e);
            // CASE 2: webservice is alive but refused to accept our request
            // usually this will be caused by validation problems - and is actually expected as side effect of PORTICO and PRISM evolution

            // save error information
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.WEBSERVICE_SOAP_FAULT);
            error.setDiagnosticInfo(StacktraceDump.forException(e));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY);
            error.setRequestCopy(requestMessageBuffer.toString());
            ByteArrayOutputStream responseMessageBuffer = new ByteArrayOutputStream(5000);
            try {
                e.getWebServiceMessage().writeTo(responseMessageBuffer);
            } catch (IOException ioex) {
                log.warn(ioex.getMessage(), ioex);
                throw new RuntimeException("Line unreachable", e);
            }
            error.setResponseCopy(responseMessageBuffer.toString());
            applicationFormTransferErrorDAO.save(error);

            listener.webServiceCallFailed(error);

            // update transfer status
            transfer.setStatus(ApplicationTransferStatus.REJECTED_BY_WEBSERVICE);
            transfer.setTransferFinishTimepoint(new Date());

            // we count soap-fault situations; if faults are repeating - we will eventually stop the queue and issue an email alert to administrators
            numberOfConsecutiveSoapFaults++;
            if (numberOfConsecutiveSoapFaults > consecutiveSoapFaultsLimit) {
                logAndSendEmailToSuperadministrator(String.format("Could not transfer application even after %d retries [transferId=%d, applicationNumber=%s]",
                        numberOfConsecutiveSoapFaults, transferId, applicationForm.getApplicationNumber()), e);
            }
            return;
        }

        // CASE 3; webservice answer was ok (transmission successful, request approved)
        numberOfConsecutiveSoapFaults = 0;

        // extract precious IDs received from UCL and store them into ApplicationTransfer, ApplicationForm and RegisteredUser
        transfer.setUclUserIdReceived(response.getReference().getApplicantID());
        transfer.setUclBookingReferenceReceived(response.getReference().getApplicationID());
        applicationForm.setUclBookingReferenceNumber(response.getReference().getApplicationID());
        if (applicationForm.getApplicant().getUclUserId() == null) {
            applicationForm.getApplicant().setUclUserId(response.getReference().getApplicantID());
        } else {
            if (!applicationForm.getApplicant().getUclUserId().equals(response.getReference().getApplicantID())) {
                throw new RuntimeException("User code received from PORTICO do not mach with our PRISM user id: PRISM_ID="
                        + applicationForm.getApplicant().getUclUserId() + " PORTICO_ID=" + response.getReference().getApplicantID());
            }
        }

        // update transfer status in the database
        transfer.setStatus(ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING);

        listener.webServiceCallCompleted(response);

        // schedule phase 2 (sftp)
        transactionallyExecuteSftpTransferAndUpdatePersistentQueue(transferId, listener);
    }

    @Transactional
    public void transactionallyExecuteSftpTransferAndUpdatePersistentQueue(Long transferId, TransferListener listener) {
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getById(transferId);
        ApplicationForm applicationForm = transfer.getApplicationForm();

        listener.sftpTransferStarted();

        // pack attachments and send them over sftp
        try {
            log.info(String.format("Calling sendApplicationFormDocuments for transfer %d (%s)", transferId, applicationForm.getApplicationNumber()));

            String zipFileName = sftpAttachmentsSendingService.sendApplicationFormDocuments(applicationForm, listener);

            transfer.setStatus(ApplicationTransferStatus.COMPLETED);

            transfer.setTransferFinishTimepoint(new Date());

            listener.sftpTransferCompleted(zipFileName, transfer.getUclUserIdReceived(), transfer.getUclBookingReferenceReceived());

            log.info(String.format("Transfer of documents completed for transfer %d (%s)", transferId, applicationForm.getApplicationNumber()));
        } catch (SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack couldNotCreateAttachmentsPack) {
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION);
            error.setDiagnosticInfo(StacktraceDump.forException(couldNotCreateAttachmentsPack));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY);
            applicationFormTransferErrorDAO.save(error);

            listener.sftpTransferFailed(error);

            transfer.setStatus(ApplicationTransferStatus.CANCELLED);

            logAndSendEmailToSuperadministrator(
                    String.format("CouldNotCreateAttachmentsPack for application [transferId=%d, applicationNumber=%s]", transferId,
                            applicationForm.getApplicationNumber()), couldNotCreateAttachmentsPack);
        } catch (SftpAttachmentsSendingService.LocallyDefinedSshConfigurationIsWrong locallyDefinedSshConfigurationIsWrong) {
            // stop queue
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION);
            error.setDiagnosticInfo(StacktraceDump.forException(locallyDefinedSshConfigurationIsWrong));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION);
            applicationFormTransferErrorDAO.save(error);

            listener.sftpTransferFailed(error);

            logAndSendEmailToSuperadministrator(
                    String.format("LocallyDefinedSshConfigurationIsWrong for application [transferId=%d, applicationNumber=%s]", transferId,
                            applicationForm.getApplicationNumber()), locallyDefinedSshConfigurationIsWrong);
        } catch (SftpAttachmentsSendingService.CouldNotOpenSshConnectionToRemoteHost couldNotOpenSshConnectionToRemoteHost) {
            // network problems - just wait some time and try again (pause queue)
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE);
            error.setDiagnosticInfo(StacktraceDump.forException(couldNotOpenSshConnectionToRemoteHost));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY);
            applicationFormTransferErrorDAO.save(error);

            listener.sftpTransferFailed(error);

            logAndSendEmailToSuperadministrator(
                    String.format("CouldNotOpenSshConnectionToRemoteHost for application [transferId=%d, applicationNumber=%s]", transferId,
                            applicationForm.getApplicationNumber()), couldNotOpenSshConnectionToRemoteHost);
        } catch (SftpAttachmentsSendingService.SftpTargetDirectoryNotAccessible sftpTargetDirectoryNotAccessible) {
            // stop queue, inform admin (possibly ucl has to correct their config)
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_DIRECTORY_NOT_AVAILABLE);
            error.setDiagnosticInfo(StacktraceDump.forException(sftpTargetDirectoryNotAccessible));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION);
            applicationFormTransferErrorDAO.save(error);

            listener.sftpTransferFailed(error);

            logAndSendEmailToSuperadministrator(
                    String.format("SftpTargetDirectoryNotAccessible for application [transferId=%d, applicationNumber=%s]", transferId,
                            applicationForm.getApplicationNumber()), sftpTargetDirectoryNotAccessible);
        } catch (SftpAttachmentsSendingService.SftpTransmissionFailedOrProtocolError sftpTransmissionFailedOrProtocolError) {
            // network problems - just wait some time and try again (pause queue)
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE);
            error.setDiagnosticInfo(StacktraceDump.forException(sftpTransmissionFailedOrProtocolError));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY);
            applicationFormTransferErrorDAO.save(error);

            listener.sftpTransferFailed(error);

            logAndSendEmailToSuperadministrator(
                    String.format("SftpTransmissionFailedOrProtocolError for application [transferId=%d, applicationNumber=%s]", transferId,
                            applicationForm.getApplicationNumber()), sftpTransmissionFailedOrProtocolError);
        }
    }

    private void logAndSendEmailToSuperadministrator(final String message, final Exception exception) {
        log.error(message, exception);
        dataExportMailSender.sendErrorMessage(message, exception);
    }
}
