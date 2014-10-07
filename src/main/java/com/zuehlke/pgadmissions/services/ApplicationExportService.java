package com.zuehlke.pgadmissions.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.dto.ApplicationExportDTO;
import com.zuehlke.pgadmissions.exceptions.ApplicationExportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.builders.ApplicationDocumentExportBuilder;
import com.zuehlke.pgadmissions.services.builders.ApplicationExportBuilder;

@Service
@Transactional
public class ApplicationExportService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ApplicationExportService.class);

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
    protected ApplicationService applicationService;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationDocumentExportBuilder applicationDocumentExportBuilder;

    @Autowired
    private ApplicationContext applicationContext;

    public void submitExportRequest(Integer applicationId) throws DeduplicationException {
        Application application = applicationService.getById(applicationId);
        String applicationCode = application.getCode();

        String exportId = null;
        String exportUserId = null;
        String exportException = null;
        OutputStream outputStream = null;

        try {
            LOGGER.info("Exporting data for application: " + applicationCode);
            SubmitAdmissionsApplicationRequest exportRequest = buildDataExportRequest(application);
            AdmissionsApplicationResponse exportResponse = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(exportRequest,
                    new WebServiceMessageCallback() {
                        public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                            webServiceMessage.writeTo(new ByteArrayOutputStream(5000));
                        }
                    });

            if (exportResponse == null) {
                throw new ApplicationExportException("No response to export request for application " + applicationCode);
            }

            LOGGER.info("Exporting documents for application: " + applicationCode);
            ReferenceTp exportReference = exportResponse.getReference();
            exportId = exportReference.getApplicationID();
            exportUserId = exportReference.getApplicantID();
            outputStream = sendDocumentExportRequest(application, exportId);
        } catch (Exception e) {
            LOGGER.error("Error exporting application: " + applicationCode, e);
            exportException = ExceptionUtils.getStackTrace(e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        executeExportAction(application, exportId, exportUserId, exportException);
    }

    protected SubmitAdmissionsApplicationRequest buildDataExportRequest(Application application) throws ApplicationExportException {
        String creatorExportId = userService.getUserInstitutionId(application.getUser(), application.getInstitution(), PrismUserIdentity.STUDY_APPLICANT);
        String creatorIpAddress = applicationService.getApplicationCreatorIpAddress(application);
        Comment offerRecommendationComment = commentService.getLatestComment(application, PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
        User primarySupervisor = applicationService.getPrimarySupervisor(offerRecommendationComment);
        ProgramStudyOptionInstance exportProgramInstance = programService.getFirstEnabledProgramStudyOptionInstance(application.getProgram(), application
                .getProgramDetail().getStudyOption());
        List<ApplicationReferee> applicationExportReferees = applicationService.getApplicationExportReferees(application);

        if (exportProgramInstance == null) {
            throw new ApplicationExportException("No export program instance for application " + application.getCode());
        }

        return applicationContext.getBean(ApplicationExportBuilder.class).build(
                new ApplicationExportDTO().withApplication(application).withCreatorExportId(creatorExportId).withCreatorIpAddress(creatorIpAddress)
                        .withOfferRecommendationComment(offerRecommendationComment).withPrimarySupervisor(primarySupervisor)
                        .withExportProgramInstance(exportProgramInstance).withApplicationReferees(applicationExportReferees));
    }

    protected OutputStream buildDocumentExportRequest(Application application, String exportReference, OutputStream outputStream) throws IOException {
        applicationDocumentExportBuilder.getDocuments(application, exportReference, outputStream);
        return outputStream;
    }

    protected void executeExportAction(Application application, String exportId, String exportUserId, String exportException) throws DeduplicationException {
        Action exportAction = actionService.getById(PrismAction.APPLICATION_EXPORT);
        Institution exportInstitution = application.getInstitution();

        Comment comment = new Comment().withUser(exportInstitution.getUser()).withAction(exportAction).withDeclinedResponse(false)
                .withExportReference(exportId).withExportException(exportException).withCreatedTimestamp(new DateTime());
        actionService.executeSystemAction(application, exportAction, comment);

        UserInstitutionIdentity transientUserInstitutionIdentity = new UserInstitutionIdentity().withUser(application.getUser())
                .withInstitution(exportInstitution).withIdentityType(PrismUserIdentity.STUDY_APPLICANT).withIdentitier(exportUserId);

        entityService.createOrUpdate(transientUserInstitutionIdentity);
    }

    private OutputStream sendDocumentExportRequest(Application application, String exportId) throws SftpException, IOException, ResourceNotFoundException,
            JSchException {
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

}
