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
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml")
public class UclExportServiceTest extends AutomaticRollbackTestCase {
    
    private String uclUserId = "ucl-user-AX78101";
    
    private String uclBookingReferenceNumber = "P123456";
    
    private String sftpHost = "localhost";

    private String sftpPort = "22";

    private String sftpUsername = "foo";

    private String sftpPassword = "bar";

    private String targetFolder = "/home/prism";
    
    private int consecutiveSoapFaultsLimit = 5;
    
	private boolean hasBeenCalled = false;

    private WebServiceTemplate webServiceTemplateMock;

    private JSchFactory jschfactoryMock;

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
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                assertNotNull(request);
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                Assert.fail("The web service call should not complete but throw an exception instead");
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "org.springframework.ws.client.WebServiceIOException: Error"));
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY, error.getErrorHandlingStrategy());
                assertEquals(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE, error.getProblemClassification());
            }

            @Override
            public void sftpTransferStarted() {
                Assert.fail("The SFTP transfer should not start");
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
                Assert.fail("The SFTP transfer should not start");
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
                Assert.fail("The SFTP transfer should not start");                
            }
        };

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(
                        EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(
                new WebServiceIOException("Error"));
        
        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(WebServiceIOException.class));

        EasyMock.expect(qualificationInstitutionDAOMock.getAllInstitutionByName(EasyMock.anyObject(String.class))).andReturn(new ArrayList<QualificationInstitution>());
        
        EasyMock.replay(webServiceTemplateMock, qualificationInstitutionDAOMock, dataExportMailSenderMock);

        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                attachmentsSendingService, dataExportMailSenderMock);

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(), listener);

        EasyMock.verify(webServiceTemplateMock, dataExportMailSenderMock);
    }

    @Test
    public void shouldReportWebServiceSoapFaultAndGiveUpThisTransferOnly() throws IOException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                assertNotNull(request);
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                Assert.fail("The web service call should not complete but throw an exception instead");
                
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),"org.springframework.ws.soap.client.SoapFaultClientException: Authentication Failed"));
                assertEquals(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY, error.getErrorHandlingStrategy());
                assertEquals(ApplicationFormTransferErrorType.WEBSERVICE_SOAP_FAULT, error.getProblemClassification());
            }

            @Override
            public void sftpTransferStarted() {
                Assert.fail("The SFTP transfer should not start");
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
                Assert.fail("The SFTP transfer should not start");                
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
                Assert.fail("The SFTP transfer should not start");
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

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(SoapFaultClientException.class));
        
        EasyMock.expect(qualificationInstitutionDAOMock.getAllInstitutionByName(EasyMock.anyObject(String.class))).andReturn(new ArrayList<QualificationInstitution>());
        
        EasyMock.replay(webServiceTemplateMock, qualificationInstitutionDAOMock, dataExportMailSenderMock);

        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                attachmentsSendingService, dataExportMailSenderMock);


        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(), listener);

        EasyMock.verify(webServiceTemplateMock, dataExportMailSenderMock);

        assertEquals(ApplicationTransferStatus.REJECTED_BY_WEBSERVICE, applicationFormTransfer.getStatus());
    }

    @Test
    public void shouldReportWebServiceSoapFaultAndGiveUpCompletelyAfterConfiguredRetries() throws IOException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        
        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, 0,
                attachmentsSendingService, dataExportMailSenderMock);

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

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(SoapFaultClientException.class));
        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(SoapFaultClientException.class));
        
        EasyMock.replay(webServiceTemplateMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(), new DeafListener());

        EasyMock.verify(webServiceTemplateMock, dataExportMailSenderMock);    }

    @Test
    public void shouldSuccessfullyCallWebServiceAndRetrieveAResponse() {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            
            
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                assertNotNull(request);
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                assertNotNull(response);
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                Assert.fail("The web service call should succeed");
            }

            @Override
            public void sftpTransferStarted() {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
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
        
        EasyMock.expect(qualificationInstitutionDAOMock.getAllInstitutionByName(EasyMock.anyObject(String.class))).andReturn(new ArrayList<QualificationInstitution>());
        
        EasyMock.replay(webServiceTemplateMock, qualificationInstitutionDAOMock);
        
        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                attachmentsSendingService, dataExportMailSenderMock) {
        	@Override
        	public void transactionallyExecuteSftpTransferAndUpdatePersistentQueue(Long transferId, TransferListener listener) {
        		hasBeenCalled = true;
        	}
        };

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(), listener);

        EasyMock.verify(webServiceTemplateMock);

        assertEquals(uclUserId, applicationForm.getApplicant().getUclUserId());
        assertEquals(uclBookingReferenceNumber, applicationForm.getApplication().getUclBookingReferenceNumber());

        assertEquals(uclUserId, applicationFormTransfer.getUclUserIdReceived());
        assertEquals(uclBookingReferenceNumber, applicationFormTransfer.getUclBookingReferenceReceived());
        
        assertTrue(hasBeenCalled);
    }

    @Test
    public void shouldGiveUpSendingDocumentsBecauseOfFailureInLocalConfigurationAndRescheduleForLater()
            throws ResourceNotFoundException, JSchException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted() {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
                Assert.fail("The sftp transfer should not succeed");
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to configure SSH connection"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION, error.getErrorHandlingStrategy());
            }
        };

        EasyMock.expect(jschfactoryMock.getInstance()).andThrow(new JSchException());

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                sftpAttachmentsSendingService, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(LocallyDefinedSshConfigurationIsWrong.class));
        
        EasyMock.replay(jschfactoryMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, dataExportMailSenderMock);
    }

    @Test
    public void shoulRescheduleSendingDocumentsBecauseOfNetworkFailure() throws ResourceNotFoundException,
            JSchException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted() {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
                Assert.fail();
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to open SSH connection to PORTICO host"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY, error.getErrorHandlingStrategy());
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

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                sftpAttachmentsSendingService, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(CouldNotOpenSshConnectionToRemoteHost.class));
        
        EasyMock.replay(jschfactoryMock, sessionMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sessionMock, dataExportMailSenderMock);
    }

    @Test
    public void shoulRescheduleSendingDocumentsBecauseOfSftpProtocolFailure() throws ResourceNotFoundException,
            JSchException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted() {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
                Assert.fail();            
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to open sftp channel over previously established SSH connection"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY, error.getErrorHandlingStrategy());
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

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                sftpAttachmentsSendingService, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(SftpTransmissionFailedOrProtocolError.class));
        
        EasyMock.replay(jschfactoryMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);
    }

    @Test
    public void shouldPauseAndRescheduleSendingDocumentsBecauseThereWasAProblemWithTheTargetFolder()
            throws ResourceNotFoundException, JSchException, SftpException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted() {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
                Assert.fail();
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to access remote directory for SFTP transmission"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_DIRECTORY_NOT_AVAILABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION, error.getErrorHandlingStrategy());
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

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);
        
        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                sftpAttachmentsSendingService, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(SftpTargetDirectoryNotAccessible.class));
        
        EasyMock.replay(jschfactoryMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(), listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);
    }

    @Test
    public void shouldPauseAndRescheduleSendingDocumentsBecauseThereWasAProblemWithTheUpload()
            throws ResourceNotFoundException, JSchException, SftpException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createPersistentQueueItem(applicationForm);
        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
                
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted() {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
                Assert.fail();
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "SFTP protocol error during transmission of attachments for application form"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.PAUSE_TRANSERS_AND_RESUME_AFTER_DELAY, error.getErrorHandlingStrategy());
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

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(
                jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                sftpAttachmentsSendingService, dataExportMailSenderMock);

        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(SftpTransmissionFailedOrProtocolError.class));
        
        EasyMock.replay(jschfactoryMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sessionMock, sftpChannelMock, dataExportMailSenderMock);
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
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted() {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
                Assert.fail();
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Error"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY, error.getErrorHandlingStrategy());
            }
        };

        SftpAttachmentsSendingService sftpAttachmentsSendingServiceMock = EasyMock
                .createMock(SftpAttachmentsSendingService.class);

        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                sftpAttachmentsSendingServiceMock, dataExportMailSenderMock);

        sftpAttachmentsSendingServiceMock.sendApplicationFormDocuments(applicationForm, listener);

        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new CouldNotCreateAttachmentsPack("Error");
            }
        });
        
        dataExportMailSenderMock.sendErrorMessage(EasyMock.anyObject(String.class), EasyMock.isA(CouldNotCreateAttachmentsPack.class));

        EasyMock.replay(sftpAttachmentsSendingServiceMock, dataExportMailSenderMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        assertEquals(ApplicationTransferStatus.CANCELLED, applicationFormTransfer.getStatus());

        EasyMock.verify(sftpAttachmentsSendingServiceMock, dataExportMailSenderMock);
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
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
                Assert.fail();                
            }

            @Override
            public void webServiceCallFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted() {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
                assertTrue(StringUtils.isNotBlank(zipFileName));
                assertTrue(StringUtils.isNotBlank(uclUserId));
                assertTrue(StringUtils.isNotBlank(uclBookingReferenceNumber));
            }

            @Override
            public void sftpTransferFailed(ApplicationFormTransferError error) {
                Assert.fail();
            }
        };

        SftpAttachmentsSendingService sftpAttachmentsSendingServiceMock = EasyMock.createMock(SftpAttachmentsSendingService.class);

        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                sftpAttachmentsSendingServiceMock, dataExportMailSenderMock);

        EasyMock.expect(sftpAttachmentsSendingServiceMock.sendApplicationFormDocuments(applicationForm, listener)).andReturn("abc.zip");

        EasyMock.replay(sftpAttachmentsSendingServiceMock);

        exportService.transactionallyExecuteSftpTransferAndUpdatePersistentQueue(applicationFormTransfer.getId(), listener);

        assertEquals(ApplicationTransferStatus.COMPLETED, applicationFormTransfer.getStatus());

        EasyMock.verify(sftpAttachmentsSendingServiceMock);
    }

    @Before
    public void setup() {
        applicationForm = new ValidApplicationFormBuilder().build(sessionFactory);

        jschfactoryMock = EasyMock.createMock(JSchFactory.class);

        applicationFormTransferDAO = new ApplicationFormTransferDAO(sessionFactory);

        applicationFormTransferErrorDAO = new ApplicationFormTransferErrorDAO(sessionFactory);

        attachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, 
                attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername, sftpPassword, targetFolder);

        webServiceTemplateMock = EasyMock.createMock(WebServiceTemplate.class);

        qualificationInstitutionDAOMock = EasyMock.createMock(QualificationInstitutionDAO.class);
        
        attachmentsZipCreatorMock = EasyMock.createMock(PorticoAttachmentsZipCreator.class);
        
        dataExportMailSenderMock = EasyMock.createMock(DataExportMailSender.class);

        exportService = new UclExportService(webServiceTemplateMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                attachmentsSendingService, 
                dataExportMailSenderMock);
        
        hasBeenCalled = false;
    }
}
