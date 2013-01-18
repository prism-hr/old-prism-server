package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ReferenceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mail.DataExportMailSender;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotOpenSshConnectionToRemoteHost;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.LocallyDefinedSshConfigurationIsWrong;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.SftpTargetDirectoryNotAccessible;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.SftpTransmissionFailedOrProtocolError;
import com.zuehlke.pgadmissions.utils.DateUtils;
import com.zuehlke.pgadmissions.utils.PausableHibernateCompatibleSequentialTaskExecutor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml")
public class UclExportServiceTest extends UclIntegrationBaseTest {

    private PausableHibernateCompatibleSequentialTaskExecutor webserviceCallingQueueExecutorMock;

    private PausableHibernateCompatibleSequentialTaskExecutor sftpCallingQueueExecutorMock;

    private WebServiceTemplate webServiceTemplateMock;

    private JSchFactory jschfactoryMock;

    private TaskScheduler schedulerMock;

    private ApplicationFormTransferDAO applicationFormTransferDAO;

    private ApplicationForm applicationForm;

    private ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;

    private UclExportService exportService;

    private SftpAttachmentsSendingService attachmentsSendingService;

    private QualificationInstitutionDAO qualificationInstitutionDAOMock;
    
    private PorticoAttachmentsZipCreator attachmentsZipCreatorMock;
    
    private DataExportMailSender dataExportMailSenderMock;

    @Test
    public void shouldCreatePersistentQueueItem() {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        assertNotNull(applicationFormTransfer);
        assertEquals(applicationFormTransfer.getApplicationForm(), applicationForm);
        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());
        assertTrue(DateUtils.isToday(applicationFormTransfer.getTransferStartTimepoint()));
    }

    @Test
    public void shouldReportWebServiceUnreachableAndRescheduleForLaterTransmission() {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
                Assert.fail();
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
                        "org.springframework.ws.client.WebServiceIOException: Error"));
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY,
                        error.getErrorHandlingStrategy());
                assertEquals(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE, error.getProblemClassification());
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                Assert.fail();
            }

            @Override
            public void sshConnectionEstablished() {
                Assert.fail();
            }

            @Override
            public void queued() {
                Assert.fail();
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
                Assert.fail();
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                assertNotNull(request);
            }
        };

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(
                        EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(
                new WebServiceIOException("Error"));
        
        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));

        webserviceCallingQueueExecutorMock.pause();
        webserviceCallingQueueExecutorMock.execute(EasyMock.anyObject(Phase1Task.class));

        EasyMock.expect(qualificationInstitutionDAOMock.getAllInstitutionByName(EasyMock.anyObject(String.class))).andReturn(new ArrayList<QualificationInstitution>());
        
        EasyMock.replay(webServiceTemplateMock, webserviceCallingQueueExecutorMock, qualificationInstitutionDAOMock, dataExportMailSenderMock);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                attachmentsSendingService, schedulerMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(), listener);

        EasyMock.verify(webServiceTemplateMock, webserviceCallingQueueExecutorMock, dataExportMailSenderMock);
    }

    @Test
    public void shouldReportWebServiceSoapFaultAndGiveUpThisTransferOnly() throws IOException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
                Assert.fail();
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
                        "org.springframework.ws.soap.client.SoapFaultClientException: Authentication Failed"));
                assertEquals(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY,
                        error.getErrorHandlingStrategy());
                assertEquals(ApplicationFormTransferErrorType.WEBSERVICE_SOAP_FAULT, error.getProblemClassification());
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                Assert.fail();
            }

            @Override
            public void sshConnectionEstablished() {
                Assert.fail();
            }

            @Override
            public void queued() {
                Assert.fail();
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
                Assert.fail();
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                assertNotNull(request);
            }
        };

        SoapFault mockFault = EasyMock.createMock(SoapFault.class);
        EasyMock.expect(mockFault.getFaultStringOrReason()).andReturn("Authentication Failed");

        SoapBody mockBody = EasyMock.createMock(SoapBody.class);
        EasyMock.expect(mockBody.getFault()).andReturn(mockFault);

        SoapMessage mockFaultMessage = EasyMock.createMock(SoapMessage.class);
        EasyMock.expect(mockFaultMessage.getFaultReason()).andReturn("Authentication Failed");
        EasyMock.expect(mockFaultMessage.getSoapBody()).andReturn(mockBody);
        mockFaultMessage.writeTo(EasyMock.anyObject(OutputStream.class));

        EasyMock.replay(mockFault, mockBody, mockFaultMessage);

        SoapFaultClientException e = new SoapFaultClientException(mockFaultMessage);

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(
                        EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(e);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));
        
        EasyMock.expect(qualificationInstitutionDAOMock.getAllInstitutionByName(EasyMock.anyObject(String.class))).andReturn(new ArrayList<QualificationInstitution>());
        
        EasyMock.replay(webServiceTemplateMock, webserviceCallingQueueExecutorMock, qualificationInstitutionDAOMock, dataExportMailSenderMock);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                attachmentsSendingService, schedulerMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(), listener);

        EasyMock.verify(webServiceTemplateMock, webserviceCallingQueueExecutorMock, dataExportMailSenderMock);

        assertEquals(ApplicationTransferStatus.REJECTED_BY_WEBSERVICE, applicationFormTransfer.getStatus());
    }

    @Test
    public void shouldReportWebServiceSoapFaultAndGiveUpCompletelyAfterConfiguredRetries() throws IOException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        
        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO, 0,
                queuePausingDelayInCaseOfNetworkProblemsDiscovered, attachmentsSendingService, schedulerMock,
                dataExportMailSenderMock);

        SoapFault mockFault = EasyMock.createMock(SoapFault.class);
        EasyMock.expect(mockFault.getFaultStringOrReason()).andReturn("Authentication Failed");

        SoapBody mockBody = EasyMock.createMock(SoapBody.class);
        EasyMock.expect(mockBody.getFault()).andReturn(mockFault);

        SoapMessage mockFaultMessage = EasyMock.createMock(SoapMessage.class);
        EasyMock.expect(mockFaultMessage.getFaultReason()).andReturn("Authentication Failed");
        EasyMock.expect(mockFaultMessage.getSoapBody()).andReturn(mockBody);
        mockFaultMessage.writeTo(EasyMock.anyObject(OutputStream.class));

        EasyMock.replay(mockFault, mockBody, mockFaultMessage);

        SoapFaultClientException e = new SoapFaultClientException(mockFaultMessage);

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(
                        EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(e);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));
        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));
        
        EasyMock.expect(qualificationInstitutionDAOMock.getAllInstitutionByName(EasyMock.anyObject(String.class))).andReturn(new ArrayList<QualificationInstitution>());
        
        webserviceCallingQueueExecutorMock.pause();

        EasyMock.replay(webServiceTemplateMock, webserviceCallingQueueExecutorMock, qualificationInstitutionDAOMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(), new DeafListener());

        EasyMock.verify(webServiceTemplateMock, webserviceCallingQueueExecutorMock, dataExportMailSenderMock);
    }

    @Test
    public void shouldSuccessfullyCallWebServiceAndRetrieveAResponse() {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                Assert.fail();
            }

            @Override
            public void sshConnectionEstablished() {
                Assert.fail();
            }

            @Override
            public void queued() {
                Assert.fail();
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
                Assert.fail();
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                assertNotNull(request);
            }
        };

        ReferenceTp referenceTp = new ReferenceTp();
        referenceTp.setApplicantID(uclUserId);
        referenceTp.setApplicationID(uclBookingReferenceNumber);
        AdmissionsApplicationResponse response = new AdmissionsApplicationResponse();
        response.setReference(referenceTp);

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(
                        EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andReturn(response);

        sftpCallingQueueExecutorMock.execute(EasyMock.anyObject(Phase2Task.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        });
        
        EasyMock.expect(qualificationInstitutionDAOMock.getAllInstitutionByName(EasyMock.anyObject(String.class))).andReturn(new ArrayList<QualificationInstitution>());
        
        EasyMock.replay(webServiceTemplateMock, sftpCallingQueueExecutorMock, qualificationInstitutionDAOMock);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                attachmentsSendingService, schedulerMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        EasyMock.verify(webServiceTemplateMock, sftpCallingQueueExecutorMock);

        assertEquals(uclUserId, applicationForm.getApplicant().getUclUserId());
        assertEquals(uclBookingReferenceNumber, applicationForm.getApplication().getUclBookingReferenceNumber());

        assertEquals(uclUserId, applicationFormTransfer.getUclUserIdReceived());
        assertEquals(uclBookingReferenceNumber, applicationFormTransfer.getUclBookingReferenceReceived());
    }

    @Test
    public void shouldGiveUpSendingDocumentsBecauseOfFailureInLocalConfigurationAndRescheduleForLater()
            throws ResourceNotFoundException, JSchException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
                Assert.fail();
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
                        "Failed to configure SSH connection"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION,
                        error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION,
                        error.getErrorHandlingStrategy());
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                Assert.fail();
            }

            @Override
            public void sshConnectionEstablished() {
            }

            @Override
            public void queued() {
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }
        };

        EasyMock.expect(jschfactoryMock.getInstance()).andThrow(new JSchException());

        sftpCallingQueueExecutorMock.pause();
        sftpCallingQueueExecutorMock.execute(EasyMock.anyObject(Phase1Task.class));

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                sftpAttachmentsSendingService, schedulerMock, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));
        
        EasyMock.replay(jschfactoryMock, sftpCallingQueueExecutorMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sftpCallingQueueExecutorMock, dataExportMailSenderMock);
    }

    @Test
    public void shoulRescheduleSendingDocumentsBecauseOfNetworkFailure() throws ResourceNotFoundException,
            JSchException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
                Assert.fail();
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
                        "Failed to open SSH connection to PORTICO host"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY,
                        error.getErrorHandlingStrategy());
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                Assert.fail();
            }

            @Override
            public void sshConnectionEstablished() {
            }

            @Override
            public void queued() {
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }
        };

        Session sessionMock = EasyMock.createMock(Session.class);

        EasyMock.expect(jschfactoryMock.getInstance()).andReturn(sessionMock);

        sessionMock.connect();
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new JSchException();
            }
        });

        EasyMock.expect(sessionMock.isConnected()).andReturn(false);

        sftpCallingQueueExecutorMock.pause();
        sftpCallingQueueExecutorMock.execute(EasyMock.anyObject(Phase1Task.class));

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                sftpAttachmentsSendingService, schedulerMock, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));
        
        EasyMock.replay(jschfactoryMock, sftpCallingQueueExecutorMock, sessionMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sftpCallingQueueExecutorMock, sessionMock, dataExportMailSenderMock);
    }

    @Test
    public void shoulRescheduleSendingDocumentsBecauseOfSftpProtocolFailure() throws ResourceNotFoundException,
            JSchException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
                Assert.fail();
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
                        "Failed to open sftp channel over previously established SSH connection"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY,
                        error.getErrorHandlingStrategy());
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                Assert.fail();
            }

            @Override
            public void sshConnectionEstablished() {
            }

            @Override
            public void queued() {
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }
        };

        Session sessionMock = EasyMock.createMock(Session.class);

        EasyMock.expect(jschfactoryMock.getInstance()).andReturn(sessionMock);

        sessionMock.connect();

        ChannelSftp sftpChannelMock = EasyMock.createMock(ChannelSftp.class);

        EasyMock.expect(sessionMock.openChannel("sftp")).andReturn(sftpChannelMock);

        sftpChannelMock.connect();

        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new JSchException();
            }
        });

        EasyMock.expect(sessionMock.isConnected()).andReturn(true);

        EasyMock.expect(sftpChannelMock.isConnected()).andReturn(false);

        sessionMock.disconnect();

        sftpCallingQueueExecutorMock.pause();
        sftpCallingQueueExecutorMock.execute(EasyMock.anyObject(Phase1Task.class));

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                sftpAttachmentsSendingService, schedulerMock, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));
        
        EasyMock.replay(jschfactoryMock, sftpCallingQueueExecutorMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sftpCallingQueueExecutorMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);
    }

    @Test
    public void shouldPauseAndRescheduleSendingDocumentsBecauseThereWasAProblemWithTheTargetFolder()
            throws ResourceNotFoundException, JSchException, SftpException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
                Assert.fail();
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
                        "Failed to access remote directory for SFTP transmission"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_DIRECTORY_NOT_AVAILABLE,
                        error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION,
                        error.getErrorHandlingStrategy());
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                Assert.fail();
            }

            @Override
            public void sshConnectionEstablished() {
            }

            @Override
            public void queued() {
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }
        };

        Session sessionMock = EasyMock.createMock(Session.class);

        EasyMock.expect(jschfactoryMock.getInstance()).andReturn(sessionMock);

        sessionMock.connect();

        ChannelSftp sftpChannelMock = EasyMock.createMock(ChannelSftp.class);

        EasyMock.expect(sessionMock.openChannel("sftp")).andReturn(sftpChannelMock);

        sftpChannelMock.connect();

        sftpChannelMock.cd(targetFolder);

        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new SftpException(1, "Permission denied");
            }
        });

        EasyMock.expect(sessionMock.isConnected()).andReturn(true);

        EasyMock.expect(sftpChannelMock.isConnected()).andReturn(false);

        sessionMock.disconnect();

        sftpCallingQueueExecutorMock.pause();
        sftpCallingQueueExecutorMock.execute(EasyMock.anyObject(Phase1Task.class));

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                sftpAttachmentsSendingService, schedulerMock, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));
        
        EasyMock.replay(jschfactoryMock, sftpCallingQueueExecutorMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(), listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sftpCallingQueueExecutorMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);
    }

    @Test
    public void shouldPauseAndRescheduleSendingDocumentsBecauseThereWasAProblemWithTheUpload()
            throws ResourceNotFoundException, JSchException, SftpException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);

        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
                Assert.fail();
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
                        "SFTP protocol error during transmission of attachments for application form"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY,
                        error.getErrorHandlingStrategy());
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                Assert.fail();
            }

            @Override
            public void sshConnectionEstablished() {
            }

            @Override
            public void queued() {
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }
        };

        Session sessionMock = EasyMock.createMock(Session.class);

        EasyMock.expect(jschfactoryMock.getInstance()).andReturn(sessionMock);

        sessionMock.connect();

        ChannelSftp sftpChannelMock = EasyMock.createMock(ChannelSftp.class);

        EasyMock.expect(sessionMock.openChannel("sftp")).andReturn(sftpChannelMock);

        sftpChannelMock.connect();

        sftpChannelMock.cd(targetFolder);

        sftpChannelMock.put(applicationForm.getUclBookingReferenceNumber() + ".zip", ChannelSftp.OVERWRITE);

        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new SftpException(1, "Error");
            }
        });

        EasyMock.expect(sessionMock.isConnected()).andReturn(true);

        EasyMock.expect(sftpChannelMock.isConnected()).andReturn(false);

        sessionMock.disconnect();

        sftpCallingQueueExecutorMock.pause();
        sftpCallingQueueExecutorMock.execute(EasyMock.anyObject(Phase1Task.class));

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                sftpAttachmentsSendingService, schedulerMock, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));
        
        EasyMock.replay(jschfactoryMock, sftpCallingQueueExecutorMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sftpCallingQueueExecutorMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);
    }

    @Test
    public void shouldCancelTransmitAttachedDocumentsOverSftpAfterFailingToCreateDocumentPack()
            throws CouldNotCreateAttachmentsPack, LocallyDefinedSshConfigurationIsWrong,
            CouldNotOpenSshConnectionToRemoteHost, SftpTargetDirectoryNotAccessible,
            SftpTransmissionFailedOrProtocolError {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);

        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
                Assert.fail();
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Error"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION,
                        error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY,
                        error.getErrorHandlingStrategy());
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                Assert.fail();
            }

            @Override
            public void sshConnectionEstablished() {
            }

            @Override
            public void queued() {
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }
        };

        SftpAttachmentsSendingService sftpAttachmentsSendingServiceMock = EasyMock
                .createMock(SftpAttachmentsSendingService.class);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                sftpAttachmentsSendingServiceMock, schedulerMock, dataExportMailSenderMock);

        sftpAttachmentsSendingServiceMock.sendApplicationFormDocuments(applicationForm, listener);

        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new CouldNotCreateAttachmentsPack("Error");
            }
        });
        
        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class));

        EasyMock.replay(sftpAttachmentsSendingServiceMock, sftpCallingQueueExecutorMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.CANCELLED, applicationFormTransfer.getStatus());

        EasyMock.verify(sftpAttachmentsSendingServiceMock, sftpCallingQueueExecutorMock, dataExportMailSenderMock);
    }

    @Test
    public void shouldSuccessfullyTransmitDocumentPackOverSftp() throws CouldNotCreateAttachmentsPack,
            LocallyDefinedSshConfigurationIsWrong, CouldNotOpenSshConnectionToRemoteHost,
            SftpTargetDirectoryNotAccessible, SftpTransmissionFailedOrProtocolError {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        applicationFormTransfer.setUclBookingReferenceReceived(uclBookingReferenceNumber);
        applicationFormTransfer.setUclUserIdReceived(uclUserId);
        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);

        TransferListener listener = new TransferListener() {
            @Override
            public void webserviceCallCompleted() {
            }

            @Override
            public void transferStarted() {
            }

            @Override
            public void transferFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }

            @Override
            public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
                assertNotNull(uclUserId, uclBookingReferenceNumber);
                assertTrue(StringUtils.isNotBlank(uclUserId));
                assertTrue(StringUtils.isNotBlank(uclBookingReferenceNumber));
            }

            @Override
            public void sshConnectionEstablished() {
            }

            @Override
            public void queued() {
            }

            @Override
            public void attachmentsSftpTransmissionStarted() {
            }

            @Override
            public void sendingSubmitAdmissionsApplicantRequest(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }
        };

        SftpAttachmentsSendingService sftpAttachmentsSendingServiceMock = EasyMock
                .createMock(SftpAttachmentsSendingService.class);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                sftpAttachmentsSendingServiceMock, schedulerMock, dataExportMailSenderMock);

        sftpAttachmentsSendingServiceMock.sendApplicationFormDocuments(applicationForm, listener);

        EasyMock.replay(sftpAttachmentsSendingServiceMock, sftpCallingQueueExecutorMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.COMPLETED, applicationFormTransfer.getStatus());

        EasyMock.verify(sftpAttachmentsSendingServiceMock, sftpCallingQueueExecutorMock);
    }

    @Before
    public void setup() {
        applicationForm = getValidApplicationForm();

        jschfactoryMock = EasyMock.createMock(JSchFactory.class);

        sftpCallingQueueExecutorMock = EasyMock.createMock(PausableHibernateCompatibleSequentialTaskExecutor.class);

        webserviceCallingQueueExecutorMock = EasyMock.createMock(PausableHibernateCompatibleSequentialTaskExecutor.class);

        applicationFormTransferDAO = new ApplicationFormTransferDAO(sessionFactory);

        applicationFormTransferErrorDAO = new ApplicationFormTransferErrorDAO(sessionFactory);

        attachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, 
                attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        webServiceTemplateMock = EasyMock.createMock(WebServiceTemplate.class);

        schedulerMock = EasyMock.createMock(TaskScheduler.class);
        
        qualificationInstitutionDAOMock = EasyMock.createMock(QualificationInstitutionDAO.class);
        
        attachmentsZipCreatorMock = EasyMock.createMock(PorticoAttachmentsZipCreator.class);
        
        dataExportMailSenderMock = EasyMock.createMock(DataExportMailSender.class);

        exportService = new UclExportService(webserviceCallingQueueExecutorMock, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, applicationFormTransferDAO, applicationFormTransferErrorDAO,
                consecutiveSoapFaultsLimit, queuePausingDelayInCaseOfNetworkProblemsDiscovered,
                attachmentsSendingService, schedulerMock, dataExportMailSenderMock);
    }
}
