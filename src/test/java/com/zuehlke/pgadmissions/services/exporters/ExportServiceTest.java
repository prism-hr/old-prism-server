package com.zuehlke.pgadmissions.services.exporters;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.PorticoService;
import com.zuehlke.pgadmissions.services.WorkflowService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml")
public class ExportServiceTest extends AutomaticRollbackTestCase {

    private String uclUserId = "ucl-user-AX78101";

    private String uclBookingReferenceNumber = "P123456";

    private String sftpHost = "localhost";

    private String sftpPort = "22";

    private String sftpUsername = "foo";

    private String sftpPassword = "bar";

    private String targetFolder = "/home/prism";

    private boolean hasBeenCalled = false;

    private WebServiceTemplate webServiceTemplateMock;

    private JSchFactory jschfactoryMock;

    private Application applicationForm;

    private ExportService exportService;

    private SftpAttachmentsSendingService attachmentsSendingService;

    private PorticoAttachmentsZipCreator attachmentsZipCreatorMock;

    private ApplicationFormDAO applicationFormDAOMock;

    private ApplicationService applicationsServiceMock;

    private WorkflowService applicationFormUserRoleServiceMock;

    private ApplicationContext applicationContextMock;

    private CommentDAO commentDAOMock;

    private UserDAO userDAOMock;

    private PorticoService porticoServiceMock;

//    @Test
//    public void shouldcreateApplicationFormTransfer() {
//        
//        assertNotNull(applicationFormTransfer);
//        assertEquals(applicationFormTransfer.getApplicationForm(), applicationForm);
//        assertEquals(ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getState());
//        assertTrue(DateUtils.isToday(applicationFormTransfer.getBeganTimestamp()));
//    }
//
//    @Test
//    public void shouldReportWebServiceUnreachableAndRescheduleForLaterTransmission() {
//        
//        TransferListener listener = new TransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                assertNotNull(request);
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                Assert.fail("The web service call should not complete but throw an exception instead");
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                assertNotNull(error);
//                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "org.springframework.ws.client.WebServiceIOException: Error"));
//                assertEquals(ApplicationTransferErrorHandlingDecision.RETRY, error.getErrorHandlingStrategy());
//                assertEquals(ApplicationTransferErrorType.WEBSERVICE_UNREACHABLE, error.getProblemClassification());
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//                Assert.fail("The SFTP transfer should not start");
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//                Assert.fail("The SFTP transfer should not start");
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail("The SFTP transfer should not start");
//            }
//        };
//
//        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();
//
//        EasyMock.expect(
//                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
//                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(new WebServiceIOException("Error"));
//
////        EasyMock.expect(applicationsServiceMock.getLatestStateChangeComment(applicationForm, SystemAction.APPLICATION_COMPLETE_VALIDATION_STAGE)).andReturn(
////                new ValidationCommentBuilder().homeOrOverseas(ResidenceStatus.HOME).build());
//
//        EasyMock.replay(webServiceTemplateMock, applicationFormTransferServiceMock, commentDAOMock, applicationsServiceMock);
//
//        try {
//            exportService.sendWebServiceRequest(applicationForm, applicationFormTransfer, listener);
//            fail("UclExportServiceException has not been thrown");
//        } catch (ExportServiceException e) {
//            assertEquals("The web service is unreachable because of network issues [applicationNumber=TMRMBISING01-2012-999999]", e.getMessage());
//        }
//
//        EasyMock.verify(webServiceTemplateMock, commentDAOMock, applicationsServiceMock);
//    }
//
//    @Test
//    public void shouldReportWebServiceSoapFaultAndGiveUpThisTransferOnly() throws IOException {
//        
//        TransferListener listener = new TransferListener() {
//
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                assertNotNull(request);
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                Assert.fail("The web service call should not complete but throw an exception instead");
//
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                assertNotNull(error);
//                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
//                        "org.springframework.ws.soap.client.SoapFaultClientException: Authentication Failed"));
//                assertEquals(ApplicationTransferErrorHandlingDecision.GIVE_UP, error.getErrorHandlingStrategy());
//                assertEquals(ApplicationTransferErrorType.WEBSERVICE_SOAP_FAULT, error.getProblemClassification());
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//                Assert.fail("The SFTP transfer should not start");
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//                Assert.fail("The SFTP transfer should not start");
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail("The SFTP transfer should not start");
//            }
//        };
//
//        SoapFault mockFault = EasyMock.createMock(SoapFault.class);
//        EasyMock.expect(mockFault.getFaultStringOrReason()).andReturn("Authentication Failed");
//
//        SoapBody mockBody = EasyMock.createMock(SoapBody.class);
//        EasyMock.expect(mockBody.getFault()).andReturn(mockFault);
//
//        SoapMessage mockFaultMessage = EasyMock.createMock(SoapMessage.class);
//        EasyMock.expect(mockFaultMessage.getFaultReason()).andReturn("Authentication Failed");
//        EasyMock.expect(mockFaultMessage.getSoapBody()).andReturn(mockBody);
//        mockFaultMessage.writeTo(EasyMock.anyObject(OutputStream.class));
//
//        EasyMock.replay(mockFault, mockBody, mockFaultMessage);
//
//        SoapFaultClientException e = new SoapFaultClientException(mockFaultMessage);
//
//        EasyMock.expect(
//                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
//                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(e);
//
////        EasyMock.expect(applicationsServiceMock.getLatestStateChangeComment(applicationForm, SystemAction.APPLICATION_COMPLETE_VALIDATION_STAGE)).andReturn(
////                new ValidationCommentBuilder().homeOrOverseas(ResidenceStatus.HOME).build());
//
//        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();
//
//        EasyMock.replay(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);
//
//        try {
//            exportService.sendWebServiceRequest(applicationForm, applicationFormTransfer, listener);
//            fail("UclExportServiceException has not been thrown");
//        } catch (ExportServiceException ex) {
//            assertEquals("The web service refused our request [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
//        }
//
//        EasyMock.verify(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);
//
//        assertEquals(ApplicationTransferState.REJECTED_BY_WEBSERVICE, applicationFormTransfer.getState());
//    }
//
//    @Test
//    public void shouldReportWebServiceSoapFaultAndGiveUpCompletelyAfterConfiguredRetries() throws IOException {
//        
//
//        SoapFault mockFault = EasyMock.createMock(SoapFault.class);
//        EasyMock.expect(mockFault.getFaultStringOrReason()).andReturn("Authentication Failed");
//
//        SoapBody mockBody = EasyMock.createMock(SoapBody.class);
//        EasyMock.expect(mockBody.getFault()).andReturn(mockFault);
//
//        SoapMessage mockFaultMessage = EasyMock.createMock(SoapMessage.class);
//        EasyMock.expect(mockFaultMessage.getFaultReason()).andReturn("Authentication Failed");
//        EasyMock.expect(mockFaultMessage.getSoapBody()).andReturn(mockBody);
//        mockFaultMessage.writeTo(EasyMock.anyObject(OutputStream.class));
//
//        EasyMock.replay(mockFault, mockBody, mockFaultMessage);
//
//        SoapFaultClientException e = new SoapFaultClientException(mockFaultMessage);
//
//        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();
//
//        EasyMock.expect(
//                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
//                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(e);
//
////        EasyMock.expect(applicationsServiceMock.getLatestStateChangeComment(applicationForm, SystemAction.APPLICATION_COMPLETE_VALIDATION_STAGE)).andReturn(
////                new ValidationCommentBuilder().homeOrOverseas(ResidenceStatus.HOME).build());
//
//        EasyMock.replay(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);
//
//        try {
//            exportService.sendWebServiceRequest(applicationForm, applicationFormTransfer, new DeafListener());
//            fail("UclExportServiceException has not been thrown");
//        } catch (ExportServiceException ex) {
//            assertEquals("The web service refused our request [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
//        }
//
//        EasyMock.verify(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void shouldSuccessfullyCallWebServiceAndRetrieveAResponse() throws ExportServiceException {
//        
//        TransferListener listener = new TransferListener() {
//
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                assertNotNull(request);
//                assertNull(request.getApplication().getCourseApplication().getAtasStatement());
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                assertNotNull(response);
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail("The web service call should succeed");
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//            }
//        };
//
//        ReferenceTp referenceTp = new ReferenceTp();
//        referenceTp.setApplicantID(uclUserId);
//        referenceTp.setApplicationID(uclBookingReferenceNumber);
//        AdmissionsApplicationResponse response = new AdmissionsApplicationResponse();
//        response.setReference(referenceTp);
//
//        EasyMock.expect(
//                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
//                        EasyMock.anyObject(WebServiceMessageCallback.class))).andReturn(response);
//
////        EasyMock.expect(applicationsServiceMock.getLatestStateChangeComment(applicationForm, SystemAction.APPLICATION_COMPLETE_VALIDATION_STAGE)).andReturn(
////                new ValidationCommentBuilder().homeOrOverseas(ResidenceStatus.HOME).build());
//
//        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();
//
//        // exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
//        // applicationFormTransferService, porticoServiceMock,applicationContextMock) {
//        // @Override
//        // public void uploadDocuments(final ApplicationForm form, final ApplicationTransfer transfer, final TransferListener listener)
//        // throws ExportServiceException {
//        // hasBeenCalled = true;
//        // }
//        // };
//
//        EasyMock.expect(applicationContextMock.getBean(EasyMock.isA(Class.class))).andReturn(exportService);
//
//        applicationsServiceMock.save(applicationForm);
//
//        EasyMock.replay(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, applicationContextMock);
//        exportService.sendToPortico(applicationForm, applicationFormTransfer, listener);
//
//        EasyMock.verify(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);
//
//        // assertEquals(uclUserId, applicationForm.getApplicant().getUclUserId());
//        assertEquals(uclBookingReferenceNumber, applicationForm.getUclBookingReferenceNumber());
//
//        assertEquals(uclUserId, applicationFormTransfer.getExternalApplicantReference());
//        assertEquals(uclBookingReferenceNumber, applicationFormTransfer.getExternalTransferReference());
//
//        assertTrue(hasBeenCalled);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void shouldSuccessfullyCallWebServiceWithOverseasStudent() throws ExportServiceException {
////        OfferRecommendedComment offerComment = new OfferRecommendedCommentBuilder().id(15).application(applicationForm).comment("").projectAbstract("abstract")
////                .recommendedConditionsAvailable(false).recommendedStartDate(new LocalDate()).build();
////        applicationForm.getApplicationComments().add(offerComment);
//        applicationForm.getProgram().setRequireProjectDefinition(true);
//        
//        TransferListener listener = new TransferListener() {
//
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                assertNotNull(request);
//                assertEquals("abstract", request.getApplication().getCourseApplication().getAtasStatement());
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                assertNotNull(response);
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail("The web service call should succeed");
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//            }
//        };
//
//        ReferenceTp referenceTp = new ReferenceTp();
//        referenceTp.setApplicantID(uclUserId);
//        referenceTp.setApplicationID(uclBookingReferenceNumber);
//        AdmissionsApplicationResponse response = new AdmissionsApplicationResponse();
//        response.setReference(referenceTp);
//
//        EasyMock.expect(
//                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
//                        EasyMock.anyObject(WebServiceMessageCallback.class))).andReturn(response);
//
////        EasyMock.expect(applicationsServiceMock.getLatestStateChangeComment(applicationForm, SystemAction.APPLICATION_COMPLETE_VALIDATION_STAGE)).andReturn(
////                new ValidationCommentBuilder().homeOrOverseas(ResidenceStatus.OVERSEAS).build());
//
//        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();
//
//        // exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
//        // applicationFormTransferService, porticoServiceMock,applicationContextMock) {
//        // @Override
//        // public void uploadDocuments(final ApplicationForm form, final ApplicationTransfer transfer, final TransferListener listener)
//        // throws ExportServiceException {
//        // hasBeenCalled = true;
//        // }
//        // };
//
//        expect(applicationContextMock.getBean(EasyMock.isA(Class.class))).andReturn(exportService);
//
//        applicationsServiceMock.save(applicationForm);
//
//        EasyMock.replay(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, applicationContextMock);
//        exportService.sendToPortico(applicationForm, applicationFormTransfer, listener);
//
//        EasyMock.verify(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, applicationContextMock);
//
//        // assertEquals(uclUserId, applicationForm.getApplicant().getUclUserId());
//        assertEquals(uclBookingReferenceNumber, applicationForm.getUclBookingReferenceNumber());
//
//        assertEquals(uclUserId, applicationFormTransfer.getExternalApplicantReference());
//        assertEquals(uclBookingReferenceNumber, applicationFormTransfer.getExternalTransferReference());
//
//        assertTrue(hasBeenCalled);
//    }
//
//    @Test
//    public void shouldGiveUpSendingDocumentsBecauseOfFailureInLocalConfigurationAndRescheduleForLater() throws ResourceNotFoundException, JSchException,
//            ExportServiceException {
//        
//        TransferListener listener = new TransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//                Assert.fail("The sftp transfer should not succeed");
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                assertNotNull(error);
//                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to configure SSH connection"));
//                assertTrue(DateUtils.isToday(error.getTimepoint()));
//                assertEquals(ApplicationTransferErrorType.SFTP_UNEXPECTED_EXCEPTION, error.getProblemClassification());
//                assertEquals(ApplicationTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION, error.getErrorHandlingStrategy());
//            }
//        };
//
//        EasyMock.expect(jschfactoryMock.getInstance()).andThrow(new JSchException());
//
//        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
//                sftpPort, sftpUsername, sftpPassword, targetFolder);
//
//        EasyMock.replay(jschfactoryMock);
//
//        try {
//            exportService.uploadDocuments(applicationForm, listener);
//            fail("UclExportServiceException has not been thrown");
//        } catch (ExportServiceException ex) {
//            assertEquals("There was an error speaking to the SFTP service due to a misconfiguration in PRISM [applicationNumber=TMRMBISING01-2012-999999]",
//                    ex.getMessage());
//        }
//
//        assertEquals(ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getState());
//
//        EasyMock.verify(jschfactoryMock);
//    }
//
//    @Test
//    public void shoulRescheduleSendingDocumentsBecauseOfNetworkFailure() throws ResourceNotFoundException, JSchException {
//        
//        TransferListener listener = new TransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                assertNotNull(error);
//                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to open SSH connection to PORTICO host"));
//                assertTrue(DateUtils.isToday(error.getTimepoint()));
//                assertEquals(ApplicationTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
//                assertEquals(ApplicationTransferErrorHandlingDecision.RETRY, error.getErrorHandlingStrategy());
//            }
//
//        };
//
//        Session sessionMock = EasyMock.createMock(Session.class);
//
//        EasyMock.expect(jschfactoryMock.getInstance()).andReturn(sessionMock);
//
//        sessionMock.connect();
//        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
//            @Override
//            public Object answer() throws Throwable {
//                throw new JSchException();
//            }
//        });
//
//        EasyMock.expect(sessionMock.isConnected()).andReturn(false);
//
//        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
//                sftpPort, sftpUsername, sftpPassword, targetFolder);
//
//        EasyMock.replay(jschfactoryMock, sessionMock);
//
//        try {
//            exportService.uploadDocuments(applicationForm, listener);
//            fail("UclExportServiceException has not been thrown");
//        } catch (ExportServiceException ex) {
//            assertEquals("The SFTP service is unreachable because of network issues [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
//        }
//
//        assertEquals(ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getState());
//
//        EasyMock.verify(jschfactoryMock, sessionMock);
//    }
//
//    @Test
//    public void shoulRescheduleSendingDocumentsBecauseOfSftpProtocolFailure() throws ResourceNotFoundException, JSchException {
//        
//        TransferListener listener = new TransferListener() {
//
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                assertNotNull(error);
//                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to open sftp channel over previously established SSH connection"));
//                assertTrue(DateUtils.isToday(error.getTimepoint()));
//                assertEquals(ApplicationTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
//                assertEquals(ApplicationTransferErrorHandlingDecision.RETRY, error.getErrorHandlingStrategy());
//            }
//        };
//
//        Session sessionMock = EasyMock.createMock(Session.class);
//
//        EasyMock.expect(jschfactoryMock.getInstance()).andReturn(sessionMock);
//
//        sessionMock.connect();
//
//        ChannelSftp sftpChannelMock = EasyMock.createMock(ChannelSftp.class);
//
//        EasyMock.expect(sessionMock.openChannel("sftp")).andReturn(sftpChannelMock);
//
//        sftpChannelMock.connect();
//
//        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
//            @Override
//            public Object answer() throws Throwable {
//                throw new JSchException();
//            }
//        });
//
//        EasyMock.expect(sessionMock.isConnected()).andReturn(true);
//
//        EasyMock.expect(sftpChannelMock.isConnected()).andReturn(false);
//
//        sessionMock.disconnect();
//
//        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
//                sftpPort, sftpUsername, sftpPassword, targetFolder);
//
//        EasyMock.replay(jschfactoryMock, sessionMock, sftpChannelMock);
//
//        try {
//            exportService.uploadDocuments(applicationForm, listener);
//            fail("UclExportServiceException has not been thrown");
//        } catch (ExportServiceException ex) {
//            assertEquals("The SFTP service is unreachable because of network issues [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
//        }
//
//        assertEquals(ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getState());
//
//        EasyMock.verify(jschfactoryMock, sessionMock, sftpChannelMock);
//    }
//
//    @Test
//    public void shouldPauseAndRescheduleSendingDocumentsBecauseThereWasAProblemWithTheTargetFolder() throws ResourceNotFoundException, JSchException,
//            SftpException {
//        
//        TransferListener listener = new TransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                assertNotNull(error);
//                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to access remote directory for SFTP transmission"));
//                assertTrue(DateUtils.isToday(error.getTimepoint()));
//                assertEquals(ApplicationTransferErrorType.SFTP_DIRECTORY_NOT_AVAILABLE, error.getProblemClassification());
//                assertEquals(ApplicationTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION, error.getErrorHandlingStrategy());
//            }
//        };
//
//        Session sessionMock = EasyMock.createMock(Session.class);
//
//        EasyMock.expect(jschfactoryMock.getInstance()).andReturn(sessionMock);
//
//        sessionMock.connect();
//
//        ChannelSftp sftpChannelMock = EasyMock.createMock(ChannelSftp.class);
//
//        EasyMock.expect(sessionMock.openChannel("sftp")).andReturn(sftpChannelMock);
//
//        sftpChannelMock.connect();
//
//        sftpChannelMock.cd(targetFolder);
//
//        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
//            @Override
//            public Object answer() throws Throwable {
//                throw new SftpException(1, "Permission denied");
//            }
//        });
//
//        EasyMock.expect(sessionMock.isConnected()).andReturn(true);
//
//        EasyMock.expect(sftpChannelMock.isConnected()).andReturn(false);
//
//        sessionMock.disconnect();
//
//        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
//                sftpPort, sftpUsername, sftpPassword, targetFolder);
//
//        EasyMock.replay(jschfactoryMock, sessionMock, sftpChannelMock);
//
//        try {
//            exportService.uploadDocuments(applicationForm, listener);
//            fail("UclExportServiceException has not been thrown");
//        } catch (ExportServiceException ex) {
//            assertEquals("The SFTP target directory is not accessible [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
//        }
//
//        assertEquals(ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getState());
//
//        EasyMock.verify(jschfactoryMock, sessionMock, sftpChannelMock);
//    }
//
//    @Test
//    public void shouldPauseAndRescheduleSendingDocumentsBecauseThereWasAProblemWithTheUpload() throws ResourceNotFoundException, JSchException, SftpException,
//            ExportServiceException {
//        
//        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);
//        TransferListener listener = new TransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                assertNotNull(error);
//                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
//                        "SFTP protocol error during transmission of attachments for application form"));
//                assertTrue(DateUtils.isToday(error.getTimepoint()));
//                assertEquals(ApplicationTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
//                assertEquals(ApplicationTransferErrorHandlingDecision.RETRY, error.getErrorHandlingStrategy());
//            }
//        };
//
//        Session sessionMock = EasyMock.createMock(Session.class);
//
//        EasyMock.expect(jschfactoryMock.getInstance()).andReturn(sessionMock);
//
//        sessionMock.connect();
//
//        ChannelSftp sftpChannelMock = EasyMock.createMock(ChannelSftp.class);
//
//        EasyMock.expect(sessionMock.openChannel("sftp")).andReturn(sftpChannelMock);
//
//        sftpChannelMock.connect();
//
//        sftpChannelMock.cd(targetFolder);
//
//        sftpChannelMock.put(applicationForm.getUclBookingReferenceNumber() + ".zip", ChannelSftp.OVERWRITE);
//
//        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
//            @Override
//            public Object answer() throws Throwable {
//                throw new SftpException(1, "Error");
//            }
//        });
//
//        EasyMock.expect(sessionMock.isConnected()).andReturn(true);
//
//        EasyMock.expect(sftpChannelMock.isConnected()).andReturn(false);
//
//        sessionMock.disconnect();
//
//        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
//                sftpPort, sftpUsername, sftpPassword, targetFolder);
//
//        EasyMock.replay(jschfactoryMock, sessionMock, sftpChannelMock);
//
//        try {
//            exportService.uploadDocuments(applicationForm, listener);
//            fail("UclExportServiceException has not been thrown");
//        } catch (ExportServiceException ex) {
//            assertEquals("The SFTP service is unreachable because of network issues [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
//        }
//
//        assertEquals(ApplicationTransferState.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getState());
//
//        EasyMock.verify(jschfactoryMock, sessionMock, sftpChannelMock);
//    }
//
//    @Test
//    public void shouldCancelTransmitAttachedDocumentsOverSftpAfterFailingToCreateDocumentPack() throws CouldNotCreateAttachmentsPack,
//            LocallyDefinedSshConfigurationIsWrong, CouldNotOpenSshConnectionToRemoteHost, SftpTargetDirectoryNotAccessible,
//            SftpTransmissionFailedOrProtocolError {
//        
//        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);
//        TransferListener listener = new TransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                assertNotNull(error);
//                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Error"));
//                assertTrue(DateUtils.isToday(error.getTimepoint()));
//                assertEquals(ApplicationTransferErrorType.SFTP_UNEXPECTED_EXCEPTION, error.getProblemClassification());
//                assertEquals(ApplicationTransferErrorHandlingDecision.GIVE_UP, error.getErrorHandlingStrategy());
//            }
//        };
//
//        SftpAttachmentsSendingService sftpAttachmentsSendingServiceMock = EasyMock.createMock(SftpAttachmentsSendingService.class);
//
//        sftpAttachmentsSendingServiceMock.sendApplicationFormDocuments(applicationForm, listener);
//
//        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
//            @Override
//            public Object answer() throws Throwable {
//                throw new CouldNotCreateAttachmentsPack("Error");
//            }
//        });
//
//        EasyMock.replay(sftpAttachmentsSendingServiceMock);
//
//        try {
//            exportService.uploadDocuments(applicationForm, listener);
//            fail("UclExportServiceException has not been thrown");
//        } catch (ExportServiceException ex) {
//            assertEquals("There was an error creating the ZIP file for PORTICO [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
//        }
//
//        assertEquals(ApplicationTransferState.CANCELLED, applicationFormTransfer.getState());
//
//        EasyMock.verify(sftpAttachmentsSendingServiceMock);
//    }
//
//    @Test
//    public void shouldSuccessfullyTransmitDocumentPackOverSftp() throws CouldNotCreateAttachmentsPack, LocallyDefinedSshConfigurationIsWrong,
//            CouldNotOpenSshConnectionToRemoteHost, SftpTargetDirectoryNotAccessible, SftpTransmissionFailedOrProtocolError, ExportServiceException {
//        
//        applicationFormTransfer.setExternalTransferReference(uclBookingReferenceNumber);
//        applicationFormTransfer.setExternalApplicantReference(uclUserId);
//        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);
//        TransferListener listener = new TransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail();
//            }
//
//            @Override
//            public void sftpTransferStarted(Application form) {
//            }
//
//            @Override
//            public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
//                assertTrue(StringUtils.isNotBlank(zipFileName));
//                assertTrue(StringUtils.isNotBlank(uclUserId));
//                assertTrue(StringUtils.isNotBlank(uclBookingReferenceNumber));
//            }
//
//            @Override
//            public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
//                Assert.fail();
//            }
//        };
//
//        SftpAttachmentsSendingService sftpAttachmentsSendingServiceMock = EasyMock.createMock(SftpAttachmentsSendingService.class);
//
//        EasyMock.expect(sftpAttachmentsSendingServiceMock.sendApplicationFormDocuments(applicationForm, listener)).andReturn("abc.zip");
//
//        EasyMock.replay(sftpAttachmentsSendingServiceMock, applicationContextMock);
//
//        exportService.uploadDocuments(applicationForm, listener);
//
//        assertEquals(ApplicationTransferState.COMPLETED, applicationFormTransfer.getState());
//
//        EasyMock.verify(sftpAttachmentsSendingServiceMock, applicationContextMock);
//    }
//
//    @Test
//    public void shouldPrepareApplicationIfItIsRejectedOrWithdrawn() throws ExportServiceException {
//        applicationForm = new ValidApplicationFormBuilder().build();
//        // exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
//        // applicationFormTransferService, porticoServiceMock,applicationContextMock) {
//        // @Override
//        // public void sendToPortico(final ApplicationForm form, final ApplicationTransfer transfer, TransferListener listener)
//        // throws ExportServiceException {
//        // prepareApplicationForm(applicationForm);
//        // }
//        // };
//        // applicationForm.setStatus(ApplicationFormStatus.REJECTED);
//        for (Referee referee : applicationForm.getReferees()) {
//            referee.setSendToUCL(false);
//        }
//
//        applicationsServiceMock.save(applicationForm);
//
//        EasyMock.replay(applicationsServiceMock);
//
//        exportService.sendToPortico(applicationForm, null);
//
//        EasyMock.verify(applicationsServiceMock);
//    }
//
//    @Test
//    public void shouldPrepareApplicationIfItIsRejectedOrWithdrawnAndRefereeHasNotProvidedReference() throws ExportServiceException {
//        applicationForm = new ValidApplicationFormBuilder().build();
//        // exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
//        // applicationFormTransferService, porticoServiceMock,applicationContextMock) {
//        // @Override
//        // public void sendToPortico(final ApplicationForm form, final ApplicationTransfer transfer, TransferListener listener)
//        // throws ExportServiceException {
//        // prepareApplicationForm(applicationForm);
//        // }
//        // };
//        // applicationForm.setStatus(ApplicationFormStatus.REJECTED);
//        for (Referee referee : applicationForm.getReferees()) {
//            referee.setSendToUCL(false);
//        }
//
//        applicationForm.getReferees().get(0).setComment(null);
//
//        applicationsServiceMock.save(applicationForm);
//
//        EasyMock.replay(applicationsServiceMock);
//
//        exportService.sendToPortico(applicationForm, null);
//
//        EasyMock.verify(applicationsServiceMock);
//    }
//
//    @Test
//    public void shouldPrepareApplicationFormIfLessThan2RefereesHaveBeenSelected() throws ExportServiceException {
//        applicationForm = new ValidApplicationFormBuilder().build();
//        // exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
//        // applicationFormTransferService, porticoServiceMock,applicationContextMock) {
//        // @Override
//        // public void sendToPortico(final ApplicationForm form, final ApplicationTransfer transfer, TransferListener listener)
//        // throws ExportServiceException {
//        // prepareApplicationForm(applicationForm);
//        // }
//        // };
//        // applicationForm.setStatus(ApplicationFormStatus.REJECTED);
//        for (Referee referee : applicationForm.getReferees()) {
//            referee.setSendToUCL(false);
//        }
//
//        applicationForm.getReferees().get(0).setSendToUCL(null);
//        applicationForm.getReferees().get(1).setSendToUCL(true);
//
//        applicationsServiceMock.save(applicationForm);
//
//        EasyMock.replay(applicationsServiceMock);
//
//        exportService.sendToPortico(applicationForm, null);
//
//        EasyMock.verify(applicationsServiceMock);
//    }
//
//    @Before
//    public void prepare() {
//        applicationForm = new ValidApplicationFormBuilder().build(sessionFactory);
//
//        jschfactoryMock = EasyMock.createMock(JSchFactory.class);
//
//        applicationFormTransferErrorDAO = new ApplicationTransferErrorDAO(sessionFactory);
//
//        applicationFormTransferDAO = new ApplicationTransferDAO(sessionFactory);
//
//        attachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername,
//                sftpPassword, targetFolder);
//
//        webServiceTemplateMock = EasyMock.createMock(WebServiceTemplate.class);
//
//        attachmentsZipCreatorMock = EasyMock.createMock(PorticoAttachmentsZipCreator.class);
//
//        applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
//
//        applicationsServiceMock = EasyMock.createMock(ApplicationService.class);
//
//        applicationFormUserRoleServiceMock = EasyMock.createMock(WorkflowService.class);
//
//        applicationFormTransferService = new ApplicationTransferService(applicationFormDAOMock, applicationFormTransferErrorDAO, applicationFormTransferDAO,
//                applicationFormUserRoleServiceMock, commentDAOMock, userDAOMock);
//
//        applicationFormTransferServiceMock = EasyMock.createMock(ApplicationTransferService.class);
//
//        commentDAOMock = EasyMock.createMock(CommentDAO.class);
//
//        userDAOMock = EasyMock.createMock(UserDAO.class);
//
//        porticoServiceMock = EasyMock.createMock(PorticoService.class);
//
//        applicationContextMock = EasyMock.createMock(ApplicationContext.class);
//
//        InjectionUtils.injectInto(webServiceTemplateMock, exportService, "webServiceTemplate");
//        InjectionUtils.injectInto(applicationsServiceMock, exportService, "applicationsService");
//        InjectionUtils.injectInto(commentDAOMock, exportService, "commentDAO");
//        InjectionUtils.injectInto(userDAOMock, exportService, "userDAO");
//        InjectionUtils.injectInto(attachmentsSendingService, exportService, "attachmentsSendingService");
//        InjectionUtils.injectInto(applicationFormTransferService, exportService, "applicationFormTransferService");
//        InjectionUtils.injectInto(porticoServiceMock, exportService, "porticoService");
//        InjectionUtils.injectInto(applicationContextMock, exportService, "applicationContext");
//
//        hasBeenCalled = false;
//    }
}