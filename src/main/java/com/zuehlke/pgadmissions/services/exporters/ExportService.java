package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.xpath.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferComment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.exceptions.ExportServiceException;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.PorticoService;

/**
 * This is UCL data export service. Used for situations where we push data to UCL system (PORTICO).
 */
@Service
public class ExportService {

    private final Logger log = LoggerFactory.getLogger(ExportService.class);

    private static final String PRISM_EXCEPTION = "There was an internal PRISM exception [applicationNumber=%s]";

    private static final String WS_CALL_FAILED_NETWORK = "The web service is unreachable because of network issues [applicationNumber=%s]";

    private static final String WS_CALL_FAILED_REFUSED = "The web service refused our request [applicationNumber=%s]";

    private static final String SFTP_CALL_FAILED_UNEXPECTED = "There was an error creating the ZIP file for PORTICO [applicationNumber=%s]";

    private static final String SFTP_CALL_FAILED_CONFIGURATION = "There was an error speaking to the SFTP service due to a misconfiguration in PRISM [applicationNumber=%s]";

    private static final String SFTP_CALL_FAILED_NETWORK = "The SFTP service is unreachable because of network issues [applicationNumber=%s]";

    private static final String SFTP_CALL_FAILED_DIRECTORY = "The SFTP target directory is not accessible [applicationNumber=%s]";

    private WebServiceTemplate webServiceTemplate;

    private CommentDAO commentDAO;

    private UserDAO userDAO;

    private SftpAttachmentsSendingService sftpAttachmentsSendingService;

    private ApplicationFormService applicationsService;

    private ApplicationTransferService applicationFormTransferService;

    private PorticoService porticoService;

    private final ApplicationContext context;

    @Autowired
    public ExportService(WebServiceTemplate webServiceTemplate, ApplicationFormService applicationsService, CommentDAO commentDAO, UserDAO userDAO,
            SftpAttachmentsSendingService sftpAttachmentsSendingService, ApplicationTransferService applicationFormTransferService,
            PorticoService porticoService, ApplicationContext context) {
        this.webServiceTemplate = webServiceTemplate;
        this.commentDAO = commentDAO;
        this.userDAO = userDAO;
        this.sftpAttachmentsSendingService = sftpAttachmentsSendingService;
        this.applicationFormTransferService = applicationFormTransferService;
        this.applicationsService = applicationsService;
        this.porticoService = porticoService;
        this.context = context;
    }

    // oooooooooooooooooooooooooo PUBLIC API IMPLEMENTATION oooooooooooooooooooooooooooooooo

    public void sendToPortico(final ApplicationForm form, final ApplicationTransfer transfer) throws ExportServiceException {
        sendToPortico(form, transfer, new DeafListener());
    }

    public void sendToPortico(final ApplicationForm form, final ApplicationTransfer transfer, TransferListener listener) throws ExportServiceException {
        try {
            log.info(String.format("Submitting application to PORTICO [applicationNumber=%s]", form.getApplicationNumber()));
            ExportService proxy = context.getBean(this.getClass());
            proxy.prepareApplicationForm(form);
            proxy.sendWebServiceRequest(form, transfer, listener);
            proxy.uploadDocuments(form, transfer, listener);
            applicationsService.save(form);
            commentDAO.save(new ApplicationTransferComment(form, userDAO.getSuperadministrators().get(0)));
        } catch (ExportServiceException e) {
            throw e;
        } catch (Exception e) {
            applicationFormTransferService.processApplicationTransferError(listener, form, transfer, e, ApplicationTransferStatus.CANCELLED, PRISM_EXCEPTION,
                    ApplicationTransferErrorHandlingDecision.GIVE_UP, ApplicationTransferErrorType.PRISM_EXCEPTION, log);
        }
    }

    public void setPorticoAttachmentsZipCreator(final PorticoAttachmentsZipCreator zipCreator) {
        sftpAttachmentsSendingService.setPorticoAttachmentsZipCreator(zipCreator);
    }

    public void setSftpAttachmentsSendingService(final SftpAttachmentsSendingService sendingService) {
        sftpAttachmentsSendingService = sendingService;
    }

    // ooooooooooooooooooooooooooooooo PRIVATE oooooooooooooooooooooooooooooooo

    @Transactional
    public void sendWebServiceRequest(final ApplicationForm formObj, final ApplicationTransfer transferObj, final TransferListener listener)
            throws ExportServiceException {
        ApplicationForm form = applicationsService.getById(formObj.getId());
        ApplicationTransfer transfer = applicationFormTransferService.getById(transferObj.getId());
        ValidationComment validationComment = (ValidationComment) applicationsService.getLatestStateChangeComment(form,
                ActionType.APPLICATION_COMPLETE_VALIDATION_STAGE);

        Boolean isOverseasStudent = validationComment == null ? true : validationComment.getHomeOrOverseas().equals(HomeOrOverseas.OVERSEAS);
        OfferRecommendedComment offerRecommendedComment = (OfferRecommendedComment) applicationsService.getLatestStateChangeComment(form,
                ActionType.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
        CommentAssignedUser primarySupervisor = null;
        if (offerRecommendedComment != null) {
            primarySupervisor = offerRecommendedComment.getPrimaryAssignedUser();
        }

        final ByteArrayOutputStream requestMessageBuffer = new ByteArrayOutputStream(5000);

        AdmissionsApplicationResponse response = null;
        try {
            SubmitAdmissionsApplicationRequest request = new SubmitAdmissionsApplicationRequestBuilderV2(new ObjectFactory()).applicationForm(form)
                    .isOverseasStudent(isOverseasStudent).primarySupervisor(primarySupervisor.getUser()).build();

            listener.webServiceCallStarted(request, form);

            log.info(String.format("Calling PORTICO web service [applicationNumber=%s]", form.getApplicationNumber()));

            response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request, new WebServiceMessageCallback() {
                public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                    webServiceMessage.writeTo(requestMessageBuffer);
                }
            });

            log.trace(String.format("Sent web service request [applicationNumber=%s, request=%s]", form.getApplicationNumber(), requestMessageBuffer.toString()));
            log.trace(String.format("Received response from web service [applicationNumber=%s, applicantId=%s, applicationId=%s]", form.getApplicationNumber(),
                    response.getReference().getApplicantID(), response.getReference().getApplicationID()));
            applicationFormTransferService.updateApplicationFormPorticoIds(form, response);
            applicationFormTransferService.updateTransferPorticoIds(transfer, response);
            applicationFormTransferService.updateTransferStatus(transfer, ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING);
            log.info(String.format("Finished PORTICO web service [applicationNumber=%s]", form.getApplicationNumber()));
            listener.webServiceCallCompleted(response, form);
        } catch (WebServiceIOException e) {
            // Network problems
            applicationFormTransferService.processApplicationTransferError(listener, form, transfer, e, ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL,
                    WS_CALL_FAILED_NETWORK, ApplicationTransferErrorHandlingDecision.RETRY, ApplicationTransferErrorType.WEBSERVICE_UNREACHABLE, log);
        } catch (SoapFaultClientException e) {
            // Web service refused our request. Probably with some validation
            // errors
            applicationFormTransferService.processApplicationTransferError(listener, form, transfer, e, ApplicationTransferStatus.REJECTED_BY_WEBSERVICE,
                    WS_CALL_FAILED_REFUSED, ApplicationTransferErrorHandlingDecision.RETRY, ApplicationTransferErrorType.WEBSERVICE_SOAP_FAULT, log);
        }
    }

    @Transactional
    public void uploadDocuments(final ApplicationForm form, final ApplicationTransfer transferObj, final TransferListener listener)
            throws ExportServiceException {
        ApplicationTransfer transfer = applicationFormTransferService.getById(transferObj.getId());
        try {
            listener.sftpTransferStarted(form);
            log.info(String.format("Calling PORTICO SFTP service [applicationNumber=%s]", form.getApplicationNumber()));
            String zipFileName = sftpAttachmentsSendingService.sendApplicationFormDocuments(form, listener);
            applicationFormTransferService.updateTransferStatus(transfer, ApplicationTransferStatus.COMPLETED);
            log.info(String.format("Finished PORTICO SFTP service [applicationNumber=%s, zipFileName=%s]", form.getApplicationNumber(), zipFileName));
            listener.sftpTransferCompleted(zipFileName, transfer);
        } catch (SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack e) {
            // There was an error building our ZIP archive
            applicationFormTransferService.processApplicationTransferError(listener, form, transfer, e, ApplicationTransferStatus.CANCELLED,
                    SFTP_CALL_FAILED_UNEXPECTED, ApplicationTransferErrorHandlingDecision.GIVE_UP,
                    ApplicationTransferErrorType.SFTP_UNEXPECTED_EXCEPTION, log);
        } catch (SftpAttachmentsSendingService.LocallyDefinedSshConfigurationIsWrong e) {
            // There is an issue with our configuration
            applicationFormTransferService.processApplicationTransferError(listener, form, transfer, e, ApplicationTransferStatus.CANCELLED,
                    SFTP_CALL_FAILED_CONFIGURATION, ApplicationTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION,
                    ApplicationTransferErrorType.SFTP_UNEXPECTED_EXCEPTION, log);
        } catch (SftpAttachmentsSendingService.CouldNotOpenSshConnectionToRemoteHost e) {
            // Network issues
            applicationFormTransferService.processApplicationTransferError(listener, form, transfer, e,
                    ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING, SFTP_CALL_FAILED_NETWORK, ApplicationTransferErrorHandlingDecision.RETRY,
                    ApplicationTransferErrorType.SFTP_HOST_UNREACHABLE, log);
        } catch (SftpAttachmentsSendingService.SftpTargetDirectoryNotAccessible e) {
            // The target directory is not available. Configuration issue
            applicationFormTransferService.processApplicationTransferError(listener, form, transfer, e,
                    ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING, SFTP_CALL_FAILED_DIRECTORY,
                    ApplicationTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION,
                    ApplicationTransferErrorType.SFTP_HOST_UNREACHABLE, log);
        } catch (SftpAttachmentsSendingService.SftpTransmissionFailedOrProtocolError e) {
            // We couldn't establish a SFTP connection for some reason
            applicationFormTransferService.processApplicationTransferError(listener, form, transfer, e,
                    ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING, SFTP_CALL_FAILED_NETWORK, ApplicationTransferErrorHandlingDecision.RETRY,
                    ApplicationTransferErrorType.SFTP_DIRECTORY_NOT_AVAILABLE, log);
        }
    }

    /*
     * In case we send an incomplete application we still want to send the referees which might have responded with a comment and or a document.
     */
    @Transactional
    protected void prepareApplicationForm(final ApplicationForm application) {
        if (application.getState().getId() == PrismState.APPLICATION_WITHDRAWN || application.getState().getId() == PrismState.APPLICATION_REJECTED) {
            if (porticoService.getReferencesToSendToPortico(application).size() < 2) {
                final HashMap<Integer, Referee> refereesToSend = new HashMap<Integer, Referee>();

                // try to find two referees which have provided a reference.
                for (Referee referee : application.getReferees()) {
                    if (refereesToSend.size() == 2) {
                        break;
                    }

                    if (BooleanUtils.isTrue(referee.getSendToUCL())) {
                        refereesToSend.put(referee.getId(), referee);
                    } else if (referee.getComment() != null && !referee.getComment().getDeclined()) {
                        referee.setSendToUCL(true);
                        refereesToSend.put(referee.getId(), referee);
                    }
                }

                // select x more referees until we've got 2
                for (Referee referee : application.getReferees()) {
                    if (refereesToSend.size() == 2) {
                        break;
                    }

                    if (!refereesToSend.containsKey(referee.getId())) {
                        referee.setSendToUCL(true);
                        refereesToSend.put(referee.getId(), referee);
                    }
                }
            }
        }
        applicationsService.save(application);
    }

    public ApplicationTransfer createOrReturnExistingApplicationFormTransfer(final ApplicationForm form) {
        return applicationFormTransferService.createOrReturnExistingApplicationFormTransfer(form);
    }

}
