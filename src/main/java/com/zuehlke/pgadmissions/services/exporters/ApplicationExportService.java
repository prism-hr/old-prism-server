package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
public class ApplicationExportService {

    private final Logger logger = LoggerFactory.getLogger(ApplicationExportService.class);

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
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ApplicationExportBuilder applicationExportBuilder;

    @Autowired
    private ApplicationDocumentExportBuilder applicationDocumentExportBuilder;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    public void exportUclApplications() {
        List<Application> applications = getUclApplicationsForExport();
        for (Application application : applications) {
            try {
                ApplicationExportService proxy = applicationContext.getBean(this.getClass());
                String applicationCode = application.getCode();
                logger.info("Exporting data for application: " + applicationCode);
                String exportReference = proxy.sendDataExportRequest(application);
                if (exportReference != null) {
                    logger.info("Exporting documents for application: " + applicationCode);
                    proxy.sendDocumentExportRequest(application, exportReference);
                }
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }
    
    protected List<Application> getUclApplicationsForExport() {
        return applicationService.getUclApplicationsForExport();
    }

    @Transactional
    protected String sendDataExportRequest(Application transientApplication) throws DatatypeConfigurationException, JAXBException {
        Application persistentApplication = applicationService.getById(transientApplication.getId());
        SubmitAdmissionsApplicationRequest exportRequest = buildDataExportRequest(transientApplication);
        
        AdmissionsApplicationResponse exportResponse = null;
        String exportException = null;

        if (exportRequest != null) {
            try {
                exportResponse = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(exportRequest, new WebServiceMessageCallback() {
                    public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                        webServiceMessage.writeTo(new ByteArrayOutputStream(5000));
                    }
                });
            } catch (SoapFaultClientException e) {
                exportException = e.getFaultStringOrReason();
            }
        }

        Action exportAction = actionService.getById(PrismAction.APPLICATION_EXPORT);
        String exportReference = exportResponse == null ? null : exportResponse.getReference().getApplicationID();
        
        Institution exportInstitution = persistentApplication.getInstitution();

        Comment comment = new Comment().withUser(exportInstitution.getUser()).withAction(exportAction).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(false).withExportRequest(exportRequest == null ? null : unwrapJaxbObject(exportRequest))
                .withExportResponse(exportResponse == null ? exportException : unwrapJaxbObject(exportResponse)).withExportReference(exportReference);
        actionService.executeSystemAction(persistentApplication, exportAction, comment);

        UserInstitutionIdentity transientUserInstitutionIdentity = new UserInstitutionIdentity().withUser(persistentApplication.getUser())
                .withInstitution(exportInstitution).withIdentityType(PrismUserIdentity.STUDY_APPLICANT)
                .withIdentitier(exportResponse.getReference().getApplicantID());

        entityService.createOrUpdate(transientUserInstitutionIdentity);

        return exportReference;
    }

    protected SubmitAdmissionsApplicationRequest buildDataExportRequest(Application application) throws DatatypeConfigurationException {
        String creatorInstitutionApplicantId = userService.getUserInstitutionId(application.getUser(), application.getInstitution(),
                PrismUserIdentity.STUDY_APPLICANT);
        String creatorIpAddress = applicationService.getApplicationCreatorIpAddress(application);
        Comment offerRecommendationComment = commentService.getLatestComment(application, PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
        User primarySupervisor = applicationService.getPrimarySupervisor(offerRecommendationComment);
        ProgramInstance exportProgramInstance = programService.getExportProgramInstance(application);
        List<ApplicationReferee> exportReferees = applicationService.setApplicationExportReferees(application);

        return exportProgramInstance == null ? null : applicationExportBuilder.build(application, creatorInstitutionApplicantId, creatorIpAddress,
                offerRecommendationComment, primarySupervisor, exportProgramInstance, exportReferees);
    }

    protected String sendDocumentExportRequest(Application application, String exportReference) throws Exception {
        OutputStream outputStream = null;
        try {
            Session session = getSftpSession();
            session.connect();
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.cd(targetFolder);
            String finalZipName = exportReference + ".zip";
            outputStream = buildDocumentExportRequest(application, exportReference, sftpChannel.put(finalZipName, ChannelSftp.OVERWRITE));
            sftpChannel.disconnect();
            session.disconnect();
            return finalZipName;
        } catch (JSchException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }
    
    protected OutputStream buildDocumentExportRequest(Application application, String exportReference, OutputStream outputStream) throws IOException {
        applicationDocumentExportBuilder.getDocuments(application, exportReference, outputStream);
        return outputStream;
    }
    
    private Session getSftpSession() throws JSchException, ResourceNotFoundException {
        try {
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
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private String unwrapJaxbObject(Object jaxbObject) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(Object.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(jaxbObject, writer);
        return writer.toString();
    }

}
