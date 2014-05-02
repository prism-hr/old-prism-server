package com.zuehlke.pgadmissions.services.exporters;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
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
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ReferenceTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.OfferRecommendedCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.exceptions.ExportServiceException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.PorticoService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotOpenSshConnectionToRemoteHost;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.LocallyDefinedSshConfigurationIsWrong;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.SftpTargetDirectoryNotAccessible;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.SftpTransmissionFailedOrProtocolError;
import com.zuehlke.pgadmissions.utils.DateUtils;

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

    private ApplicationForm applicationForm;

    private ExportService exportService;

    private SftpAttachmentsSendingService attachmentsSendingService;

    private PorticoAttachmentsZipCreator attachmentsZipCreatorMock;

    private ApplicationFormDAO applicationFormDAOMock;

    private ApplicationFormService applicationsServiceMock;

    private ApplicationFormTransferService applicationFormTransferService;

    private ApplicationFormTransferService applicationFormTransferServiceMock;

    private WorkflowService applicationFormUserRoleServiceMock;

    private ApplicationContext applicationContextMock;

    private CommentDAO commentDAOMock;

    private UserDAO userDAOMock;

    private ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;

    private ApplicationFormTransferDAO applicationFormTransferDAO;

    private PorticoService porticoServiceMock;

    @Test
    public void shouldcreateApplicationFormTransfer() {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        assertNotNull(applicationFormTransfer);
        assertEquals(applicationFormTransfer.getApplicationForm(), applicationForm);
        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());
        assertTrue(DateUtils.isToday(applicationFormTransfer.getTransferStartTimepoint()));
    }

    @Test
    public void shouldReportWebServiceUnreachableAndRescheduleForLaterTransmission() {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                assertNotNull(request);
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                Assert.fail("The web service call should not complete but throw an exception instead");
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "org.springframework.ws.client.WebServiceIOException: Error"));
                assertEquals(ApplicationFormTransferErrorHandlingDecision.RETRY, error.getErrorHandlingStrategy());
                assertEquals(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE, error.getProblemClassification());
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
                Assert.fail("The SFTP transfer should not start");
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
                Assert.fail("The SFTP transfer should not start");
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail("The SFTP transfer should not start");
            }
        };

        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(new WebServiceIOException("Error"));

        EasyMock.expect(commentDAOMock.getValidationCommentForApplication(applicationForm)).andReturn(
                new ValidationCommentBuilder().homeOrOverseas(HomeOrOverseas.HOME).build());

        EasyMock.replay(webServiceTemplateMock, applicationFormTransferServiceMock, commentDAOMock, applicationsServiceMock);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock);

        try {
            exportService.sendWebServiceRequest(applicationForm, applicationFormTransfer, listener);
            fail("UclExportServiceException has not been thrown");
        } catch (ExportServiceException e) {
            assertEquals("The web service is unreachable because of network issues [applicationNumber=TMRMBISING01-2012-999999]", e.getMessage());
        }

        EasyMock.verify(webServiceTemplateMock, commentDAOMock, applicationsServiceMock);
    }

    @Test
    public void shouldReportWebServiceSoapFaultAndGiveUpThisTransferOnly() throws IOException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        TransferListener listener = new TransferListener() {

            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                assertNotNull(request);
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                Assert.fail("The web service call should not complete but throw an exception instead");

            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
                        "org.springframework.ws.soap.client.SoapFaultClientException: Authentication Failed"));
                assertEquals(ApplicationFormTransferErrorHandlingDecision.GIVE_UP, error.getErrorHandlingStrategy());
                assertEquals(ApplicationFormTransferErrorType.WEBSERVICE_SOAP_FAULT, error.getProblemClassification());
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
                Assert.fail("The SFTP transfer should not start");
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
                Assert.fail("The SFTP transfer should not start");
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
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
                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(e);

        EasyMock.expect(commentDAOMock.getValidationCommentForApplication(applicationForm)).andReturn(
                new ValidationCommentBuilder().homeOrOverseas(HomeOrOverseas.HOME).build());

        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();

        EasyMock.replay(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock);

        try {
            exportService.sendWebServiceRequest(applicationForm, applicationFormTransfer, listener);
            fail("UclExportServiceException has not been thrown");
        } catch (ExportServiceException ex) {
            assertEquals("The web service refused our request [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
        }

        EasyMock.verify(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);

        assertEquals(ApplicationTransferStatus.REJECTED_BY_WEBSERVICE, applicationFormTransfer.getStatus());
    }

    @Test
    public void shouldReportWebServiceSoapFaultAndGiveUpCompletelyAfterConfiguredRetries() throws IOException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock);

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

        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andThrow(e);

        EasyMock.expect(commentDAOMock.getValidationCommentForApplication(applicationForm)).andReturn(
                new ValidationCommentBuilder().homeOrOverseas(HomeOrOverseas.HOME).build());

        EasyMock.replay(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);

        try {
            exportService.sendWebServiceRequest(applicationForm, applicationFormTransfer, new DeafListener());
            fail("UclExportServiceException has not been thrown");
        } catch (ExportServiceException ex) {
            assertEquals("The web service refused our request [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
        }

        EasyMock.verify(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSuccessfullyCallWebServiceAndRetrieveAResponse() throws ExportServiceException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        TransferListener listener = new TransferListener() {

            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                assertNotNull(request);
                assertNull(request.getApplication().getCourseApplication().getAtasStatement());
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                assertNotNull(response);
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail("The web service call should succeed");
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
            }
        };

        ReferenceTp referenceTp = new ReferenceTp();
        referenceTp.setApplicantID(uclUserId);
        referenceTp.setApplicationID(uclBookingReferenceNumber);
        AdmissionsApplicationResponse response = new AdmissionsApplicationResponse();
        response.setReference(referenceTp);

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andReturn(response);

        EasyMock.expect(commentDAOMock.getValidationCommentForApplication(applicationForm)).andReturn(
                new ValidationCommentBuilder().homeOrOverseas(HomeOrOverseas.HOME).build());

        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock) {
            @Override
            public void uploadDocuments(final ApplicationForm form, final ApplicationFormTransfer transfer, final TransferListener listener)
                    throws ExportServiceException {
                hasBeenCalled = true;
            }
        };

        EasyMock.expect(applicationContextMock.getBean(EasyMock.isA(Class.class))).andReturn(exportService);

        applicationsServiceMock.save(applicationForm);

        EasyMock.replay(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, applicationContextMock);
        exportService.sendToPortico(applicationForm, applicationFormTransfer, listener);

        EasyMock.verify(webServiceTemplateMock, applicationsServiceMock, commentDAOMock);

//        assertEquals(uclUserId, applicationForm.getApplicant().getUclUserId());
        assertEquals(uclBookingReferenceNumber, applicationForm.getUclBookingReferenceNumber());

        assertEquals(uclUserId, applicationFormTransfer.getUclUserIdReceived());
        assertEquals(uclBookingReferenceNumber, applicationFormTransfer.getUclBookingReferenceReceived());

        assertTrue(hasBeenCalled);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSuccessfullyCallWebServiceWithOverseasStudent() throws ExportServiceException {
        OfferRecommendedComment offerComment = new OfferRecommendedCommentBuilder().id(15).application(applicationForm)
                .comment("").projectAbstract("abstract")
                .recommendedConditionsAvailable(false).recommendedStartDate(new Date()).build();
        applicationForm.getApplicationComments().add(offerComment);
        applicationForm.getProgram().setAtasRequired(true);
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        TransferListener listener = new TransferListener() {

            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                assertNotNull(request);
                assertEquals("abstract", request.getApplication().getCourseApplication().getAtasStatement());
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                assertNotNull(response);
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail("The web service call should succeed");
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
            }
        };

        ReferenceTp referenceTp = new ReferenceTp();
        referenceTp.setApplicantID(uclUserId);
        referenceTp.setApplicationID(uclBookingReferenceNumber);
        AdmissionsApplicationResponse response = new AdmissionsApplicationResponse();
        response.setReference(referenceTp);

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andReturn(response);

        EasyMock.expect(commentDAOMock.getValidationCommentForApplication(applicationForm)).andReturn(
                new ValidationCommentBuilder().homeOrOverseas(HomeOrOverseas.OVERSEAS).build());

        EasyMock.expect(applicationsServiceMock.getById(EasyMock.anyInt())).andReturn(applicationForm).anyTimes();

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock) {
            @Override
            public void uploadDocuments(final ApplicationForm form, final ApplicationFormTransfer transfer, final TransferListener listener)
                    throws ExportServiceException {
                hasBeenCalled = true;
            }
        };

        expect(applicationContextMock.getBean(EasyMock.isA(Class.class))).andReturn(exportService);

        applicationsServiceMock.save(applicationForm);

        EasyMock.replay(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, applicationContextMock);
        exportService.sendToPortico(applicationForm, applicationFormTransfer, listener);

        EasyMock.verify(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, applicationContextMock);

//        assertEquals(uclUserId, applicationForm.getApplicant().getUclUserId());
        assertEquals(uclBookingReferenceNumber, applicationForm.getUclBookingReferenceNumber());

        assertEquals(uclUserId, applicationFormTransfer.getUclUserIdReceived());
        assertEquals(uclBookingReferenceNumber, applicationFormTransfer.getUclBookingReferenceReceived());

        assertTrue(hasBeenCalled);
    }

    @Test
    public void shouldGiveUpSendingDocumentsBecauseOfFailureInLocalConfigurationAndRescheduleForLater() throws ResourceNotFoundException, JSchException,
            ExportServiceException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
                Assert.fail("The sftp transfer should not succeed");
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to configure SSH connection"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION, error.getErrorHandlingStrategy());
            }
        };

        EasyMock.expect(jschfactoryMock.getInstance()).andThrow(new JSchException());

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
                sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, sftpAttachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock);

        EasyMock.replay(jschfactoryMock);

        try {
            exportService.uploadDocuments(applicationForm, applicationFormTransfer, listener);
            fail("UclExportServiceException has not been thrown");
        } catch (ExportServiceException ex) {
            assertEquals("There was an error speaking to the SFTP service due to a misconfiguration in PRISM [applicationNumber=TMRMBISING01-2012-999999]",
                    ex.getMessage());
        }

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock);
    }

    @Test
    public void shoulRescheduleSendingDocumentsBecauseOfNetworkFailure() throws ResourceNotFoundException, JSchException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
                Assert.fail();
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to open SSH connection to PORTICO host"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.RETRY, error.getErrorHandlingStrategy());
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

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
                sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, sftpAttachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock);

        EasyMock.replay(jschfactoryMock, sessionMock);

        try {
            exportService.uploadDocuments(applicationForm, applicationFormTransfer, listener);
            fail("UclExportServiceException has not been thrown");
        } catch (ExportServiceException ex) {
            assertEquals("The SFTP service is unreachable because of network issues [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
        }

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sessionMock);
    }

    @Test
    public void shoulRescheduleSendingDocumentsBecauseOfSftpProtocolFailure() throws ResourceNotFoundException, JSchException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        TransferListener listener = new TransferListener() {

            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
                Assert.fail();
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to open sftp channel over previously established SSH connection"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.RETRY, error.getErrorHandlingStrategy());
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

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
                sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, sftpAttachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock);

        EasyMock.replay(jschfactoryMock, sessionMock, sftpChannelMock);

        try {
            exportService.uploadDocuments(applicationForm, applicationFormTransfer, listener);
            fail("UclExportServiceException has not been thrown");
        } catch (ExportServiceException ex) {
            assertEquals("The SFTP service is unreachable because of network issues [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
        }

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sessionMock, sftpChannelMock);
    }

    @Test
    public void shouldPauseAndRescheduleSendingDocumentsBecauseThereWasAProblemWithTheTargetFolder() throws ResourceNotFoundException, JSchException,
            SftpException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
                Assert.fail();
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Failed to access remote directory for SFTP transmission"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_DIRECTORY_NOT_AVAILABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION, error.getErrorHandlingStrategy());
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

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
                sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, sftpAttachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock);

        EasyMock.replay(jschfactoryMock, sessionMock, sftpChannelMock);

        try {
            exportService.uploadDocuments(applicationForm, applicationFormTransfer, listener);
            fail("UclExportServiceException has not been thrown");
        } catch (ExportServiceException ex) {
            assertEquals("The SFTP target directory is not accessible [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
        }

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sessionMock, sftpChannelMock);
    }

    @Test
    public void shouldPauseAndRescheduleSendingDocumentsBecauseThereWasAProblemWithTheUpload() throws ResourceNotFoundException, JSchException, SftpException,
            ExportServiceException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
                Assert.fail();
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(),
                        "SFTP protocol error during transmission of attachments for application form"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_HOST_UNREACHABLE, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.RETRY, error.getErrorHandlingStrategy());
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

        SftpAttachmentsSendingService sftpAttachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost,
                sftpPort, sftpUsername, sftpPassword, targetFolder);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, sftpAttachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock);

        EasyMock.replay(jschfactoryMock, sessionMock, sftpChannelMock);

        try {
            exportService.uploadDocuments(applicationForm, applicationFormTransfer, listener);
            fail("UclExportServiceException has not been thrown");
        } catch (ExportServiceException ex) {
            assertEquals("The SFTP service is unreachable because of network issues [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
        }

        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, applicationFormTransfer.getStatus());

        EasyMock.verify(jschfactoryMock, sessionMock, sftpChannelMock);
    }

    @Test
    public void shouldCancelTransmitAttachedDocumentsOverSftpAfterFailingToCreateDocumentPack() throws CouldNotCreateAttachmentsPack,
            LocallyDefinedSshConfigurationIsWrong, CouldNotOpenSshConnectionToRemoteHost, SftpTargetDirectoryNotAccessible,
            SftpTransmissionFailedOrProtocolError {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
                Assert.fail();
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                assertNotNull(error);
                assertTrue(StringUtils.containsIgnoreCase(error.getDiagnosticInfo(), "Error"));
                assertTrue(DateUtils.isToday(error.getTimepoint()));
                assertEquals(ApplicationFormTransferErrorType.SFTP_UNEXPECTED_EXCEPTION, error.getProblemClassification());
                assertEquals(ApplicationFormTransferErrorHandlingDecision.GIVE_UP, error.getErrorHandlingStrategy());
            }
        };

        SftpAttachmentsSendingService sftpAttachmentsSendingServiceMock = EasyMock.createMock(SftpAttachmentsSendingService.class);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock,
                sftpAttachmentsSendingServiceMock, applicationFormTransferService, porticoServiceMock,applicationContextMock);

        sftpAttachmentsSendingServiceMock.sendApplicationFormDocuments(applicationForm, listener);

        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new CouldNotCreateAttachmentsPack("Error");
            }
        });

        EasyMock.replay(sftpAttachmentsSendingServiceMock);

        try {
            exportService.uploadDocuments(applicationForm, applicationFormTransfer, listener);
            fail("UclExportServiceException has not been thrown");
        } catch (ExportServiceException ex) {
            assertEquals("There was an error creating the ZIP file for PORTICO [applicationNumber=TMRMBISING01-2012-999999]", ex.getMessage());
        }

        assertEquals(ApplicationTransferStatus.CANCELLED, applicationFormTransfer.getStatus());

        EasyMock.verify(sftpAttachmentsSendingServiceMock);
    }

    @Test
    public void shouldSuccessfullyTransmitDocumentPackOverSftp() throws CouldNotCreateAttachmentsPack, LocallyDefinedSshConfigurationIsWrong,
            CouldNotOpenSshConnectionToRemoteHost, SftpTargetDirectoryNotAccessible, SftpTransmissionFailedOrProtocolError, ExportServiceException {
        ApplicationFormTransfer applicationFormTransfer = exportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        applicationFormTransfer.setUclBookingReferenceReceived(uclBookingReferenceNumber);
        applicationFormTransfer.setUclUserIdReceived(uclUserId);
        applicationForm.setUclBookingReferenceNumber(uclBookingReferenceNumber);
        TransferListener listener = new TransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail();
            }

            @Override
            public void sftpTransferStarted(ApplicationForm form) {
            }

            @Override
            public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
                assertTrue(StringUtils.isNotBlank(zipFileName));
                assertTrue(StringUtils.isNotBlank(uclUserId));
                assertTrue(StringUtils.isNotBlank(uclBookingReferenceNumber));
            }

            @Override
            public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
                Assert.fail();
            }
        };

        SftpAttachmentsSendingService sftpAttachmentsSendingServiceMock = EasyMock.createMock(SftpAttachmentsSendingService.class);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock,
                sftpAttachmentsSendingServiceMock, applicationFormTransferService, porticoServiceMock,applicationContextMock);

        EasyMock.expect(sftpAttachmentsSendingServiceMock.sendApplicationFormDocuments(applicationForm, listener)).andReturn("abc.zip");

        EasyMock.replay(sftpAttachmentsSendingServiceMock, applicationContextMock);

        exportService.uploadDocuments(applicationForm, applicationFormTransfer, listener);

        assertEquals(ApplicationTransferStatus.COMPLETED, applicationFormTransfer.getStatus());

        EasyMock.verify(sftpAttachmentsSendingServiceMock, applicationContextMock);
    }

    @Test
    public void shouldPrepareApplicationIfItIsRejectedOrWithdrawn() throws ExportServiceException {
        applicationForm = new ValidApplicationFormBuilder().build();
        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock) {
            @Override
            public void sendToPortico(final ApplicationForm form, final ApplicationFormTransfer transfer, TransferListener listener)
                    throws ExportServiceException {
                prepareApplicationForm(applicationForm);
            }
        };
//        applicationForm.setStatus(ApplicationFormStatus.REJECTED);
        for (Referee referee : applicationForm.getReferees()) {
            referee.setSendToUCL(false);
        }

        applicationsServiceMock.save(applicationForm);

        EasyMock.replay(applicationsServiceMock);

        exportService.sendToPortico(applicationForm, null);

        EasyMock.verify(applicationsServiceMock);
    }

    @Test
    public void shouldPrepareApplicationIfItIsRejectedOrWithdrawnAndRefereeHasNotProvidedReference() throws ExportServiceException {
        applicationForm = new ValidApplicationFormBuilder().build();
        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock) {
            @Override
            public void sendToPortico(final ApplicationForm form, final ApplicationFormTransfer transfer, TransferListener listener)
                    throws ExportServiceException {
                prepareApplicationForm(applicationForm);
            }
        };
//        applicationForm.setStatus(ApplicationFormStatus.REJECTED);
        for (Referee referee : applicationForm.getReferees()) {
            referee.setSendToUCL(false);
        }

        applicationForm.getReferees().get(0).setComment(null);

        applicationsServiceMock.save(applicationForm);

        EasyMock.replay(applicationsServiceMock);

        exportService.sendToPortico(applicationForm, null);

        EasyMock.verify(applicationsServiceMock);
    }

    @Test
    public void shouldPrepareApplicationFormIfLessThan2RefereesHaveBeenSelected() throws ExportServiceException {
        applicationForm = new ValidApplicationFormBuilder().build();
        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock) {
            @Override
            public void sendToPortico(final ApplicationForm form, final ApplicationFormTransfer transfer, TransferListener listener)
                    throws ExportServiceException {
                prepareApplicationForm(applicationForm);
            }
        };
//        applicationForm.setStatus(ApplicationFormStatus.REJECTED);
        for (Referee referee : applicationForm.getReferees()) {
            referee.setSendToUCL(false);
        }

        applicationForm.getReferees().get(0).setSendToUCL(null);
        applicationForm.getReferees().get(1).setSendToUCL(true);

        applicationsServiceMock.save(applicationForm);

        EasyMock.replay(applicationsServiceMock);

        exportService.sendToPortico(applicationForm, null);

        EasyMock.verify(applicationsServiceMock);
    }

    @Before
    public void prepare() {
        applicationForm = new ValidApplicationFormBuilder().build(sessionFactory);

        jschfactoryMock = EasyMock.createMock(JSchFactory.class);

        applicationFormTransferErrorDAO = new ApplicationFormTransferErrorDAO(sessionFactory);

        applicationFormTransferDAO = new ApplicationFormTransferDAO(sessionFactory);

        attachmentsSendingService = new SftpAttachmentsSendingService(jschfactoryMock, attachmentsZipCreatorMock, sftpHost, sftpPort, sftpUsername,
                sftpPassword, targetFolder);

        webServiceTemplateMock = EasyMock.createMock(WebServiceTemplate.class);

        attachmentsZipCreatorMock = EasyMock.createMock(PorticoAttachmentsZipCreator.class);

        applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);

        applicationsServiceMock = EasyMock.createMock(ApplicationFormService.class);

        applicationFormUserRoleServiceMock = EasyMock.createMock(WorkflowService.class);

        applicationFormTransferService = new ApplicationFormTransferService(applicationFormDAOMock, applicationFormTransferErrorDAO,
                applicationFormTransferDAO, applicationFormUserRoleServiceMock, commentDAOMock, userDAOMock);

        applicationFormTransferServiceMock = EasyMock.createMock(ApplicationFormTransferService.class);

        commentDAOMock = EasyMock.createMock(CommentDAO.class);

        userDAOMock = EasyMock.createMock(UserDAO.class);
        
        porticoServiceMock = EasyMock.createMock(PorticoService.class);

        applicationContextMock = EasyMock.createMock(ApplicationContext.class);

        exportService = new ExportService(webServiceTemplateMock, applicationsServiceMock, commentDAOMock, userDAOMock, attachmentsSendingService,
                applicationFormTransferService, porticoServiceMock,applicationContextMock);

        hasBeenCalled = false;
    }
}