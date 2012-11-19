package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.ReferenceTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.utils.DateUtils;
import com.zuehlke.pgadmissions.utils.PausableHibernateCompatibleSequentialTaskExecutor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml")
public class UclExportServiceTest extends UclIntegrationTest {

    @Autowired
    @Qualifier("webservice-calling-queue-executor")
    private PausableHibernateCompatibleSequentialTaskExecutor webserviceCallingQueueExecutor;

    @Autowired
    @Qualifier("webservice-calling-queue-executor")
    private PausableHibernateCompatibleSequentialTaskExecutor sftpCallingQueueExecutor;

    private PausableHibernateCompatibleSequentialTaskExecutor sftpCallingQueueExecutorMock;

    private WebServiceTemplate webServiceTemplateMock;

    @Value("${xml.data.export.webservice.consecutiveSoapFaultsLimit}")
    private int consecutiveSoapFaultsLimit;

    @Value("${xml.data.export.queue_pausing_delay_in_case_of_network_problem}")
    private int queuePausingDelayInCaseOfNetworkProblemsDiscovered;

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
    private JSchFactory jschfactory;

    @Autowired
    private PdfDocumentBuilder pdfDocumentBuilder;

    @Autowired
    @Qualifier("ucl-export-service-scheduler")
    private TaskScheduler scheduler;

    private ProgramInstanceDAO programInstanceDAO;

    private ApplicationFormTransferDAO applicationFormTransferDAO;

    private ApplicationForm applicationForm;

    private ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;

    private UclExportService exportService;

    private SftpAttachmentsSendingService attachmentsSendingService;

    private ProgramInstanceDAO programInstanceDAOMock;

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
                assertTrue(StringUtils.startsWithIgnoreCase(error.getDiagnosticInfo(),
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

        EasyMock.expect(
                programInstanceDAOMock.getCurrentProgramInstanceForStudyOption(EasyMock.anyObject(Program.class),
                        EasyMock.anyObject(String.class)))
                .andReturn(applicationForm.getProgram().getInstances().get(0));

        EasyMock.replay(webServiceTemplateMock, programInstanceDAOMock);

        exportService = new UclExportService(webserviceCallingQueueExecutor, sftpCallingQueueExecutor,
                webServiceTemplateMock, programInstanceDAOMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                queuePausingDelayInCaseOfNetworkProblemsDiscovered, attachmentsSendingService, scheduler);

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        EasyMock.verify(webServiceTemplateMock, programInstanceDAOMock);
    }

    @Test
    public void shouldReportWebServiceSoapFaultAndRetryFor5Times() throws IOException {
        // TODO: Check the scheduler implementation for pause();
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
                assertTrue(StringUtils.startsWithIgnoreCase(error.getDiagnosticInfo(),
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

        EasyMock.expect(
                programInstanceDAOMock.getCurrentProgramInstanceForStudyOption(EasyMock.anyObject(Program.class),
                        EasyMock.anyObject(String.class)))
                .andReturn(applicationForm.getProgram().getInstances().get(0));

        EasyMock.replay(webServiceTemplateMock, programInstanceDAOMock);

        exportService = new UclExportService(webserviceCallingQueueExecutor, sftpCallingQueueExecutor,
                webServiceTemplateMock, programInstanceDAOMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                queuePausingDelayInCaseOfNetworkProblemsDiscovered, attachmentsSendingService, scheduler);

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        EasyMock.verify(webServiceTemplateMock, programInstanceDAOMock);

        assertEquals(ApplicationTransferStatus.REJECTED_BY_WEBSERVICE, applicationFormTransfer.getStatus());
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
        
        String uclUserId = "ucl-user-AX78101";
        String uclBookingReferenceNumber = "b-ref-123456";

        ReferenceTp referenceTp = new ReferenceTp();
        referenceTp.setApplicantID(uclUserId);
        referenceTp.setApplicationID(uclBookingReferenceNumber);
        AdmissionsApplicationResponse response = new AdmissionsApplicationResponse();
        response.setReference(referenceTp);

        EasyMock.expect(
                webServiceTemplateMock.marshalSendAndReceive(
                        EasyMock.anyObject(SubmitAdmissionsApplicationRequest.class),
                        EasyMock.anyObject(WebServiceMessageCallback.class))).andReturn(response);

        EasyMock.expect(
                programInstanceDAOMock.getCurrentProgramInstanceForStudyOption(EasyMock.anyObject(Program.class),
                        EasyMock.anyObject(String.class)))
                .andReturn(applicationForm.getProgram().getInstances().get(0));

        sftpCallingQueueExecutorMock.execute(EasyMock.anyObject(Runnable.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        });
        
        EasyMock.replay(webServiceTemplateMock, programInstanceDAOMock, sftpCallingQueueExecutorMock);

        exportService = new UclExportService(webserviceCallingQueueExecutor, sftpCallingQueueExecutorMock,
                webServiceTemplateMock, programInstanceDAOMock, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                queuePausingDelayInCaseOfNetworkProblemsDiscovered, attachmentsSendingService, scheduler);

        exportService.transactionallyExecuteWebserviceCallAndUpdatePersistentQueue(applicationFormTransfer.getId(),
                listener);

        EasyMock.verify(webServiceTemplateMock, programInstanceDAOMock, sftpCallingQueueExecutorMock);
        
        assertEquals(uclUserId, applicationForm.getApplicant().getUclUserId());
        assertEquals(uclBookingReferenceNumber, applicationForm.getApplication().getUclBookingReferenceNumber());
        
        assertEquals(uclUserId, applicationFormTransfer.getUclUserIdReceived());
        assertEquals(uclBookingReferenceNumber, applicationFormTransfer.getUclBookingReferenceReceived());
    }

    @Before
    public void setup() {
        applicationForm = getValidApplicationForm();

        programInstanceDAO = new ProgramInstanceDAO(sessionFactory);

        programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);

        sftpCallingQueueExecutorMock = EasyMock.createMock(PausableHibernateCompatibleSequentialTaskExecutor.class);

        applicationFormTransferDAO = new ApplicationFormTransferDAO(sessionFactory);

        applicationFormTransferErrorDAO = new ApplicationFormTransferErrorDAO(sessionFactory);

        attachmentsSendingService = new SftpAttachmentsSendingService(jschfactory, pdfDocumentBuilder, sftpHost,
                sftpPort, sftpUsername, sftpPassword, targetFolder);

        webServiceTemplateMock = EasyMock.createMock(WebServiceTemplate.class);

        exportService = new UclExportService(webserviceCallingQueueExecutor, sftpCallingQueueExecutor,
                webServiceTemplateMock, programInstanceDAO, applicationFormTransferDAO,
                applicationFormTransferErrorDAO, consecutiveSoapFaultsLimit,
                queuePausingDelayInCaseOfNetworkProblemsDiscovered, attachmentsSendingService, scheduler);
    }
}
