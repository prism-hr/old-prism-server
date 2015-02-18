package com.zuehlke.pgadmissions.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ReferenceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ApplicationExportDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReferenceDTO;
import com.zuehlke.pgadmissions.exceptions.ApplicationExportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.services.builders.ApplicationDocumentExportBuilder;
import com.zuehlke.pgadmissions.services.builders.ApplicationExportBuilder;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class ApplicationExportService {

    private PropertyLoader propertyLoader = null;

    @Value("${xml.data.export.sftp.privatekeyfile}")
    private Resource privateKeyFile;

    @Value("${xml.data.export.sftp.host}")
    private String sftpHost;

    @Value("${xml.data.export.sftp.port}")
    private String sftpPort;

    @Value("${xml.data.export.sftp.username}")
    private String sftpUsername;

    @Value("${xml.data.export.sftp.password}")
    private String sftpPassword;

    @Value("${xml.data.export.sftp.folder}")
    private String targetFolder;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationDocumentExportBuilder applicationDocumentExportBuilder;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected ActionService actionService;

    @Autowired
    private ApplicationContext applicationContext;

    public void submitExportRequest(Integer applicationId) throws Exception {
        Application application = applicationService.getById(applicationId);

        String exportId = null;
        String exportUserId = null;
        OutputStream outputStream = null;
        SubmitAdmissionsApplicationRequest exportRequest = null;

        try {
            exportId = applicationService.getApplicationExportReference(application);
            if (exportId == null) {
                exportRequest = buildDataExportRequest(application);
                AdmissionsApplicationResponse exportResponse = sendDataExportRequest(application, exportRequest);

                ReferenceTp exportReference = exportResponse.getReference();
                exportId = exportReference.getApplicationID();
                exportUserId = exportReference.getApplicantID();
            }
            outputStream = sendDocumentExportRequest(application, exportId);
        } catch (RuntimeException e) {
            throw new Exception(e);
        } catch (Exception e) {
            executeExportAction(application, exportRequest, exportId, exportUserId, ExceptionUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    protected SubmitAdmissionsApplicationRequest buildDataExportRequest(Application application) throws ApplicationExportException {
        localize(application);

        String creatorExportId = userService.getUserInstitutionId(application.getUser(), application.getInstitution(), PrismUserIdentity.STUDY_APPLICANT);
        String creatorIpAddress = applicationService.getApplicationCreatorIpAddress(application);
        Comment offerRecommendationComment = commentService.getLatestComment(application, PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
        User primarySupervisor = applicationService.getPrimarySupervisor(offerRecommendationComment);
        ProgramStudyOptionInstance exportProgramInstance = programService.getFirstEnabledProgramStudyOptionInstance(application.getProgram(), application
                .getProgramDetail().getStudyOption());
        List<ApplicationReferenceDTO> applicationExportReferences = applicationService.getApplicationExportReferees(application);

        if (exportProgramInstance == null) {
            throw new ApplicationExportException("No export program instance for application " + application.getCode());
        }

        return applicationContext
                .getBean(ApplicationExportBuilder.class)
                .localize(propertyLoader)
                .build(new ApplicationExportDTO().withApplication(application).withCreatorExportId(creatorExportId).withCreatorIpAddress(creatorIpAddress)
                        .withOfferRecommendationComment(offerRecommendationComment).withPrimarySupervisor(primarySupervisor)
                        .withExportProgramInstance(exportProgramInstance).withApplicationReferences(applicationExportReferences));
    }

    protected OutputStream buildDocumentExportRequest(Application application, String exportReference, OutputStream outputStream) throws IOException,
            IntegrationException {
        localize(application);
        applicationDocumentExportBuilder.localize(propertyLoader).getDocuments(application, exportReference, outputStream);
        return outputStream;
    }

    protected void executeExportAction(Application application, SubmitAdmissionsApplicationRequest exportRequest, String exportId, String exportUserId,
            String exportException) throws DeduplicationException, InstantiationException, IllegalAccessException, JAXBException, BeansException,
            WorkflowEngineException, IOException, IntegrationException {
        Action exportAction = actionService.getById(PrismAction.APPLICATION_EXPORT);
        Institution exportInstitution = application.getInstitution();

        Comment comment = new Comment().withUser(exportInstitution.getUser()).withAction(exportAction).withDeclinedResponse(false)
                .withExportRequest(exportRequest == null ? null : getRequestContent(exportRequest)).withExportReference(exportId)
                .withExportException(exportException).withCreatedTimestamp(new DateTime());
        actionService.executeAction(application, exportAction, comment);

        if (exportUserId != null) {
            userService.createOrUpdateUserInstitutionIdentity(application, exportUserId);
        }
    }

    protected void localize(Application application) {
        propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(application);
    }

    private AdmissionsApplicationResponse sendDataExportRequest(Application application, SubmitAdmissionsApplicationRequest exportRequest) {
        AdmissionsApplicationResponse exportResponse = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(exportRequest,
                new WebServiceMessageCallback() {
                    public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                        webServiceMessage.writeTo(new ByteArrayOutputStream(5000));
                    }
                });

        if (exportResponse == null) {
            throw new ApplicationExportException("No response to export request for application " + application.getCode());
        }
        return exportResponse;
    }

    private OutputStream sendDocumentExportRequest(Application application, String exportId) throws SftpException, IOException, ResourceNotFoundException,
            JSchException, IntegrationException {
        Session session = getSftpSession();
        session.connect();
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
        sftpChannel.cd(targetFolder);
        String finalZipName = exportId + ".zip";
        OutputStream outputStream = buildDocumentExportRequest(application, exportId, sftpChannel.put(finalZipName, ChannelSftp.OVERWRITE));
        sftpChannel.disconnect();
        session.disconnect();
        return outputStream;
    }

    private Session getSftpSession() throws JSchException, ResourceNotFoundException, IOException {
        JSch jSch = new JSch();
        byte[] privateKeyAsByteArray = FileUtils.readFileToByteArray(privateKeyFile.getFile());
        byte[] emptyPassPhrase = new byte[0];
        jSch.addIdentity("prismIdentity", privateKeyAsByteArray, null, emptyPassPhrase);
        Session session = jSch.getSession(sftpUsername, sftpHost, Integer.valueOf(sftpPort));
        session.setPassword(sftpPassword);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        return session;
    }

    private String getRequestContent(SubmitAdmissionsApplicationRequest request) throws JAXBException {
        final Marshaller marshaller = JAXBContext.newInstance(SubmitAdmissionsApplicationRequest.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        final StringWriter writer = new StringWriter();
        marshaller.marshal(request, writer);
        return writer.toString();
    }

}
