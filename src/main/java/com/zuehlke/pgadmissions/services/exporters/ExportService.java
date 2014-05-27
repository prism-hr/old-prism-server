package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.services.ApplicationService;
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

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SftpAttachmentsSendingService sftpAttachmentsSendingService;

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private PorticoService porticoService;

    @Autowired
    private ApplicationContext context;

    // oooooooooooooooooooooooooo PUBLIC API IMPLEMENTATION oooooooooooooooooooooooooooooooo

    public void sendToPortico(final Application form)  {
        try {
            log.info(String.format("Submitting application to PORTICO [applicationNumber=%s]", form.getCode()));
            ExportService proxy = context.getBean(this.getClass());
            proxy.prepareApplicationForm(form);
            proxy.sendWebServiceRequest(form);
            proxy.uploadDocuments(form);
            applicationsService.save(form);
//            commentDAO.save(new ApplicationTransferComment(form, userDAO.getSuperadministrators().get(0)));
        } catch (Exception e) {
            // FIXME store information about fail
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
    public void sendWebServiceRequest(final Application formObj)  {
        Application form = applicationsService.getById(formObj.getId());
        Boolean isOverseasStudent = null;
        User primarySupervisor = null;

        final ByteArrayOutputStream requestMessageBuffer = new ByteArrayOutputStream(5000);

        SubmitAdmissionsApplicationRequest request = new SubmitAdmissionsApplicationRequestBuilderV2(new ObjectFactory()).applicationForm(form)
                .isOverseasStudent(isOverseasStudent).primarySupervisor(primarySupervisor).build();

        log.info(String.format("Calling PORTICO web service [applicationNumber=%s]", form.getCode()));

        AdmissionsApplicationResponse response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request,
                new WebServiceMessageCallback() {
                    public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                        webServiceMessage.writeTo(requestMessageBuffer);
                    }
                });

        log.trace(String.format("Sent web service request [applicationNumber=%s, request=%s]", form.getCode(), requestMessageBuffer.toString()));
        log.trace(String.format("Received response from web service [applicationNumber=%s, applicantId=%s, applicationId=%s]", form.getCode(),
                response.getReference().getApplicantID(), response.getReference().getApplicationID()));
        // TODO store external application and applicant IDs
        // applicationFormTransferService.updateApplicationFormPorticoIds(form, response);
        // applicationFormTransferService.updateTransferPorticoIds(transfer, response);
        // applicationFormTransferService.updateTransferStatus(transfer, ApplicationTransferState.QUEUED_FOR_ATTACHMENTS_SENDING);
        log.info(String.format("Finished PORTICO web service [applicationNumber=%s]", form.getCode()));
    }

    @Transactional
    public void uploadDocuments(final Application form) throws Exception {
        log.info(String.format("Calling PORTICO SFTP service [applicationNumber=%s]", form.getCode()));
        String zipFileName = sftpAttachmentsSendingService.sendApplicationFormDocuments(form);
        log.info(String.format("Finished PORTICO SFTP service [applicationNumber=%s, zipFileName=%s]", form.getCode(), zipFileName));
    }

    /*
     * In case we send an incomplete application we still want to send the referees which might have responded with a comment and or a document.
     */
    @Transactional
    protected void prepareApplicationForm(final Application application) {
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
                    } else if (referee.getComment() != null && !referee.getComment().getDeclinedResponse()) {
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

}
