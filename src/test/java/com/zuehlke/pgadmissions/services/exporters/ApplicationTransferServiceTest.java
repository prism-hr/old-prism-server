package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ReferenceTp;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;
import com.zuehlke.pgadmissions.domain.builders.ApplicationTransferBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferState;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.utils.DateUtils;

public class ApplicationTransferServiceTest {

    private ApplicationFormDAO applicationFormDAOMock;

    private ApplicationTransferErrorDAO applicationFormTransferErrorDAOMock;

    private ApplicationTransferDAO applicationFormTransferDAOMock;

    private WorkflowService applicationFormUserRoleServiceMock;

    private CommentDAO commentDAOMock;

    private UserDAO userDAOMock;

    private ApplicationTransferService service;

    @Before
    public void prepare() {
        applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
        applicationFormTransferDAOMock = EasyMock.createMock(ApplicationTransferDAO.class);
        applicationFormTransferErrorDAOMock = EasyMock.createMock(ApplicationTransferErrorDAO.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(WorkflowService.class);
        commentDAOMock = EasyMock.createMock(CommentDAO.class);
        userDAOMock = EasyMock.createMock(UserDAO.class);
        service = new ApplicationTransferService(applicationFormDAOMock, applicationFormTransferErrorDAOMock, applicationFormTransferDAOMock,
                applicationFormUserRoleServiceMock, commentDAOMock, userDAOMock);
    }

    @Test
    public void shouldUpdateTransferStatus() {
        DateTime dateInThePast = new DateTime(1984, 9, 29, 8, 0);
        ApplicationTransfer transfer = new ApplicationTransferBuilder().status(ApplicationTransferState.CANCELLED)
                .transferStartTimepoint(dateInThePast.toDate()).build();

        service.updateTransferStatus(transfer, ApplicationTransferState.COMPLETED);

        assertEquals(ApplicationTransferState.COMPLETED, transfer.getState());
        assertTrue(DateUtils.isToday(transfer.getEndedTimestamp()));
    }

    @Test
    public void shouldUpdateTransferPorticoIds() {
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        form.setUclBookingReferenceNumber("");
        // form.getApplicant().setUclUserId("");

        AdmissionsApplicationResponse response = new AdmissionsApplicationResponse();
        ReferenceTp referenceTp = new ReferenceTp();
        referenceTp.setApplicantID("applicantId");
        referenceTp.setApplicationID("applicationId");
        response.setReference(referenceTp);

        service.updateApplicationFormPorticoIds(form, response);

        assertEquals("applicationId", form.getUclBookingReferenceNumber());
        // assertEquals("applicantId", form.getApplicant().getUclUserId());
    }

    @Test
    public void shouldCreateTransferError() {
        ApplicationTransfer transfer = new ApplicationTransferBuilder().status(ApplicationTransferState.CANCELLED).transferStartTimepoint(new Date()).build();

        ApplicationTransferErrorBuilder builder = new ApplicationTransferErrorBuilder().errorHandlingStrategy(ApplicationTransferErrorHandlingDecision.RETRY)
                .problemClassification(ApplicationTransferErrorType.WEBSERVICE_UNREACHABLE).requestCopy("").transfer(transfer);

        ApplicationTransferError expectedError = builder.build();

        applicationFormTransferErrorDAOMock.save(EasyMock.isA(ApplicationTransferError.class));

        EasyMock.replay(applicationFormTransferErrorDAOMock);

        ApplicationTransferError actualError = service.createTransferError(builder);

        EasyMock.verify(applicationFormTransferErrorDAOMock);

        assertEquals(expectedError.getDiagnosticInfo(), actualError.getDiagnosticInfo());
        assertEquals(expectedError.getErrorHandlingStrategy(), actualError.getErrorHandlingStrategy());
        assertEquals(expectedError.getId(), actualError.getId());
        assertEquals(expectedError.getProblemClassification(), actualError.getProblemClassification());
        assertEquals(expectedError.getRequestCopy(), actualError.getRequestCopy());
        assertEquals(expectedError.getResponseCopy(), actualError.getResponseCopy());
        assertEquals(expectedError.getTimepoint(), actualError.getTimepoint());
        assertEquals(expectedError.getTransfer(), actualError.getTransfer());
    }

}