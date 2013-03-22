package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.exceptions.UclExportServiceException;

/**
 * This is UCL data export service. Used for situations where we push data to UCL system (PORTICO).
 */
@Service
public class UclExportService {
    
    private final Logger log = LoggerFactory.getLogger(UclExportService.class);

    private final WebServiceTemplate webServiceTemplate;

    private final CommentDAO commentDAO;

    private SftpAttachmentsSendingService sftpAttachmentsSendingService;

    private final ApplicationFormDAO applicationFormDAO;
    
    private final ApplicationFormTransferService applicationFormTransferService;
    
    private static final String WS_CALL_FAILED_NETWORK = "The web service is unreachable because of network issues [applicationNumber=%s]";
    
    private static final String WS_CALL_FAILED_REFUSED = "The web service refused our request [applicationNumber=%s]";

    private static final String SFTP_CALL_FAILED_UNEXPECTED = "There was an error creating the ZIP file for PORTICO [applicationNumber=%s]";

    private static final String SFTP_CALL_FAILED_CONFIGURATION = "There was an error speaking to the SFTP service due to a misconfiguration in PRISM [applicationNumber=%s]";

    private static final String SFTP_CALL_FAILED_NETWORK = "The SFTP service is unreachable because of network issues [applicationNumber=%s]";

    private static final String SFTP_CALL_FAILED_DIRECTORY = "The SFTP target directory is not accessible [applicationNumber=%s]";
    
    public UclExportService() {
        this(null, null, null, null, null);
    }

    @Autowired
    public UclExportService(
            WebServiceTemplate webServiceTemplate,
            ApplicationFormDAO applicationFormDAO,
            CommentDAO commentDAO,
            SftpAttachmentsSendingService sftpAttachmentsSendingService,
            ApplicationFormTransferService applicationFormTransferService) {
        this.webServiceTemplate = webServiceTemplate;
        this.commentDAO = commentDAO;
        this.sftpAttachmentsSendingService = sftpAttachmentsSendingService;
        this.applicationFormTransferService = applicationFormTransferService;
        this.applicationFormDAO = applicationFormDAO;
    }

    // oooooooooooooooooooooooooo PUBLIC API IMPLEMENTATION oooooooooooooooooooooooooooooooo

    public void sendToPortico(final ApplicationForm form, final ApplicationFormTransfer transfer) throws UclExportServiceException {
        sendToPortico(form, transfer, new DeafListener());
    }

    public void sendToPortico(final ApplicationForm form, final ApplicationFormTransfer transfer, TransferListener listener) throws UclExportServiceException {
        log.info(String.format("Submitting application to PORTICO [applicationNumber=%s]", form.getApplicationNumber()));
        prepareApplicationForm(form);
        sendWebServiceRequest(form, transfer, listener);
        uploadDocuments(form, transfer, listener);
    }
    
    public void setPorticoAttachmentsZipCreator(final PorticoAttachmentsZipCreator zipCreator) {
        sftpAttachmentsSendingService.setPorticoAttachmentsZipCreator(zipCreator);
    }

    public void setSftpAttachmentsSendingService(final SftpAttachmentsSendingService sendingService) {
        sftpAttachmentsSendingService = sendingService;
    }

    // ooooooooooooooooooooooooooooooo PRIVATE oooooooooooooooooooooooooooooooo

    @Transactional
    public void sendWebServiceRequest(final ApplicationForm formObj, final ApplicationFormTransfer transferObj, final TransferListener listener) throws UclExportServiceException {
        ApplicationForm form = applicationFormDAO.get(formObj.getId());
        ApplicationFormTransfer transfer = applicationFormTransferService.getById(transferObj.getId());
        ValidationComment validationComment = commentDAO.getValidationCommentForApplication(form);
        Boolean isOverseasStudent = validationComment.getHomeOrOverseas().equals(HomeOrOverseas.OVERSEAS);
        final ByteArrayOutputStream requestMessageBuffer = new ByteArrayOutputStream(5000);
        
        AdmissionsApplicationResponse response = null;
        try {
            SubmitAdmissionsApplicationRequest request = new SubmitAdmissionsApplicationRequestBuilder(
                    new ObjectFactory()).applicationForm(form).isOverseasStudent(isOverseasStudent).build();
            
            listener.webServiceCallStarted(request, form);
            
            log.info(String.format("Calling PORTICO web service [applicationNumber=%s]", form.getApplicationNumber()));
            
            response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request, new WebServiceMessageCallback() {
                public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                    webServiceMessage.writeTo(requestMessageBuffer);
                }});
            
            log.info(String.format("Received response from web service [applicationNumber=%s, applicantId=%s, applicationId=%s]", 
                    form.getApplicationNumber(), response.getReference().getApplicantID(), response.getReference().getApplicationID()));
            
            applicationFormTransferService.updateApplicationFormPorticoIds(form, response);
            applicationFormTransferService.updateTransferPorticoIds(transfer, response);
            applicationFormTransferService.updateTransferStatus(transfer, ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING);
            log.info(String.format("Finished PORTICO web service [applicationNumber=%s]", form.getApplicationNumber()));
            listener.webServiceCallCompleted(response, form);
        } catch (WebServiceIOException e) {
            // Network problems
            ApplicationFormTransferError transferError = applicationFormTransferService
                    .createTransferError(new ApplicationFormTransferErrorBuilder().diagnosticInfo(e)
                            .errorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.RETRY)
                            .problemClassification(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE)
                            .requestCopy(requestMessageBuffer.toString()).transfer(transfer));
            listener.webServiceCallFailed(e, transferError, form);
            log.error(String.format(WS_CALL_FAILED_NETWORK, form.getApplicationNumber()), e); 
            throw new UclExportServiceException(String.format(WS_CALL_FAILED_NETWORK, form.getApplicationNumber()), e, transferError);
        } catch (SoapFaultClientException e) {
            // Web service refused our request. Probably with some validation errors
            ApplicationFormTransferError transferError = applicationFormTransferService
                    .createTransferError(new ApplicationFormTransferErrorBuilder().diagnosticInfo(e)
                            .errorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP)
                            .problemClassification(ApplicationFormTransferErrorType.WEBSERVICE_SOAP_FAULT)
                            .responseCopy(e.getWebServiceMessage()).requestCopy(requestMessageBuffer.toString())
                            .transfer(transfer));
            applicationFormTransferService.updateTransferStatus(transfer, ApplicationTransferStatus.REJECTED_BY_WEBSERVICE);
            listener.webServiceCallFailed(e, transferError, form);
            log.error(String.format(WS_CALL_FAILED_REFUSED, form.getApplicationNumber()), e);
            throw new UclExportServiceException(String.format(WS_CALL_FAILED_REFUSED, form.getApplicationNumber()), e, transferError);
        }
    }

    @Transactional
    public void uploadDocuments(final ApplicationForm form, final ApplicationFormTransfer transfer, final TransferListener listener) throws UclExportServiceException {
        try {
            listener.sftpTransferStarted(form);
            log.info(String.format("Calling PORTICO SFTP service [applicationNumber=%s]", form.getApplicationNumber()));
            String zipFileName = sftpAttachmentsSendingService.sendApplicationFormDocuments(form, listener);
            applicationFormTransferService.updateTransferStatus(transfer, ApplicationTransferStatus.COMPLETED);
            log.info(String.format("Finished PORTICO SFTP service [applicationNumber=%s, zipFileName=%s]", form.getApplicationNumber(), zipFileName));
            listener.sftpTransferCompleted(zipFileName, transfer);
        } catch (SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack couldNotCreateAttachmentsPack) {
            // There was an error building our ZIP archive
            ApplicationFormTransferError transferError = applicationFormTransferService
                    .createTransferError(new ApplicationFormTransferErrorBuilder().diagnosticInfo(couldNotCreateAttachmentsPack)
                    .errorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP)
                    .problemClassification(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION)
                    .transfer(transfer));
            applicationFormTransferService.updateTransferStatus(transfer, ApplicationTransferStatus.CANCELLED);
            listener.sftpTransferFailed(couldNotCreateAttachmentsPack, transferError, form);
            log.error(String.format(SFTP_CALL_FAILED_UNEXPECTED, form.getApplicationNumber()), couldNotCreateAttachmentsPack);
            throw new UclExportServiceException(String.format(SFTP_CALL_FAILED_UNEXPECTED, form.getApplicationNumber()), couldNotCreateAttachmentsPack, transferError);
        } catch (SftpAttachmentsSendingService.LocallyDefinedSshConfigurationIsWrong locallyDefinedSshConfigurationIsWrong) {
            // There is an issue with our configuration
            ApplicationFormTransferError transferError = applicationFormTransferService
                    .createTransferError(new ApplicationFormTransferErrorBuilder().diagnosticInfo(locallyDefinedSshConfigurationIsWrong)
                    .errorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION)
                    .problemClassification(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION)
                    .transfer(transfer));
            applicationFormTransferService.updateTransferStatus(transfer, ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL);
            listener.sftpTransferFailed(locallyDefinedSshConfigurationIsWrong, transferError, form);
            log.error(String.format(SFTP_CALL_FAILED_CONFIGURATION, form.getApplicationNumber()), locallyDefinedSshConfigurationIsWrong);
            throw new UclExportServiceException(String.format(SFTP_CALL_FAILED_CONFIGURATION, form.getApplicationNumber()), locallyDefinedSshConfigurationIsWrong, transferError);
        } catch (SftpAttachmentsSendingService.CouldNotOpenSshConnectionToRemoteHost couldNotOpenSshConnectionToRemoteHost) {
            // Network issues
            ApplicationFormTransferError transferError = applicationFormTransferService
                    .createTransferError(new ApplicationFormTransferErrorBuilder().diagnosticInfo(couldNotOpenSshConnectionToRemoteHost)
                    .errorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.RETRY)
                    .problemClassification(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE)
                    .transfer(transfer));
            listener.sftpTransferFailed(couldNotOpenSshConnectionToRemoteHost, transferError, form);
            log.error(String.format(SFTP_CALL_FAILED_NETWORK, form.getApplicationNumber()), couldNotOpenSshConnectionToRemoteHost);
            throw new UclExportServiceException(String.format(SFTP_CALL_FAILED_NETWORK, form.getApplicationNumber()), couldNotOpenSshConnectionToRemoteHost, transferError);
        } catch (SftpAttachmentsSendingService.SftpTargetDirectoryNotAccessible sftpTargetDirectoryNotAccessible) {
            // The target directory is not available. Configuration issue
            ApplicationFormTransferError transferError = applicationFormTransferService
                    .createTransferError(new ApplicationFormTransferErrorBuilder().diagnosticInfo(sftpTargetDirectoryNotAccessible)
                    .errorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION)
                    .problemClassification(ApplicationFormTransferErrorType.SFTP_DIRECTORY_NOT_AVAILABLE)
                    .transfer(transfer));
            listener.sftpTransferFailed(sftpTargetDirectoryNotAccessible, transferError, form);
            log.error(String.format(SFTP_CALL_FAILED_DIRECTORY, form.getApplicationNumber()), sftpTargetDirectoryNotAccessible);
            throw new UclExportServiceException(String.format(SFTP_CALL_FAILED_DIRECTORY, form.getApplicationNumber()), sftpTargetDirectoryNotAccessible, transferError);
        } catch (SftpAttachmentsSendingService.SftpTransmissionFailedOrProtocolError sftpTransmissionFailedOrProtocolError) {
            // We couldn't establish a SFTP connection for some reason
            ApplicationFormTransferError transferError = applicationFormTransferService
                    .createTransferError(new ApplicationFormTransferErrorBuilder().diagnosticInfo(sftpTransmissionFailedOrProtocolError)
                    .errorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.RETRY)
                    .problemClassification(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE)
                    .transfer(transfer));
            listener.sftpTransferFailed(sftpTransmissionFailedOrProtocolError, transferError, form);
            log.error(String.format(SFTP_CALL_FAILED_NETWORK, form.getApplicationNumber()), sftpTransmissionFailedOrProtocolError);
            throw new UclExportServiceException(String.format(SFTP_CALL_FAILED_NETWORK, form.getApplicationNumber()), sftpTransmissionFailedOrProtocolError, transferError);
        }
    }
    
    /*
     * In case we send an incomplete application we still want to send the referees which 
     * might have responded with a comment and or a document.
     */
    @Transactional
    protected void prepareApplicationForm(final ApplicationForm form) {
        if (form.getStatus() == ApplicationFormStatus.WITHDRAWN || form.getStatus() == ApplicationFormStatus.REJECTED) {
            if (form.getReferencesToSendToPortico().isEmpty()) {
                final HashMap<Integer, Referee> refereesToSend = new HashMap<Integer, Referee>();

                for (Referee referee : form.getReferees()) {
                    if (refereesToSend.size() == 2) {
                        break;
                    }
                    
                    if (referee.hasProvidedReference()) {
                        referee.setSendToUCL(true);
                        refereesToSend.put(referee.getId(), referee);
                    }
                }
                
                for (Referee referee : form.getReferees()) {
                    if (refereesToSend.size() == 2) {
                        break;
                    }
                    
                    if (!refereesToSend.containsKey(referee.getId())) {
                        referee.setSendToUCL(true);
                        refereesToSend.put(referee.getId(), referee);
                    }
                }
                
                applicationFormDAO.save(form);
            }
        }
    }

    public ApplicationFormTransfer createOrReturnExistingApplicationFormTransfer(final ApplicationForm form) {
        return applicationFormTransferService.createOrReturnExistingApplicationFormTransfer(form);
    }
}
