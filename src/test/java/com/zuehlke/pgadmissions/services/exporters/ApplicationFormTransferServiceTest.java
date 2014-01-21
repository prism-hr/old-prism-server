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
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormTransferBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.utils.DateUtils;

public class ApplicationFormTransferServiceTest {

    private ApplicationFormTransferErrorDAO applicationFormTransferErrorDAOMock;
    
    private ApplicationFormTransferDAO applicationFormTransferDAOMock;
    
    private ApplicationFormTransferService service;
    
    @Before
    public void prepare() {
        applicationFormTransferDAOMock = EasyMock.createMock(ApplicationFormTransferDAO.class);
        applicationFormTransferErrorDAOMock = EasyMock.createMock(ApplicationFormTransferErrorDAO.class);
        service = new ApplicationFormTransferService(applicationFormTransferErrorDAOMock, applicationFormTransferDAOMock);
    }

    @Test
    public void shouldUpdateTransferStatus() {
        DateTime dateInThePast = new DateTime(1984, 9, 29, 8, 0);
        ApplicationFormTransfer transfer = new ApplicationFormTransferBuilder()
            .status(ApplicationTransferStatus.CANCELLED)
            .transferStartTimepoint(dateInThePast.toDate()).build();
        
        service.updateTransferStatus(transfer, ApplicationTransferStatus.COMPLETED);
        
        assertEquals(ApplicationTransferStatus.COMPLETED, transfer.getStatus());
        assertTrue(DateUtils.isToday(transfer.getTransferFinishTimepoint()));
    }
    
    @Test
    public void shouldUpdateTransferPorticoIds() {
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        form.setUclBookingReferenceNumber("");
        form.getApplicant().setUclUserId("");
        
        AdmissionsApplicationResponse response = new AdmissionsApplicationResponse();
        ReferenceTp referenceTp = new ReferenceTp();
        referenceTp.setApplicantID("applicantId");
        referenceTp.setApplicationID("applicationId");
        response.setReference(referenceTp);
        
        service.updateApplicationFormPorticoIds(form, response);
        
        assertEquals("applicationId", form.getUclBookingReferenceNumber());
        assertEquals("applicantId", form.getApplicant().getUclUserId());
    }
    
    @Test
    public void shouldCreateTransferError() {
        ApplicationFormTransfer transfer = new ApplicationFormTransferBuilder()
                .status(ApplicationTransferStatus.CANCELLED).transferStartTimepoint(new Date()).build();

        ApplicationFormTransferErrorBuilder builder = new ApplicationFormTransferErrorBuilder()
                .errorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.RETRY)
                .problemClassification(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE).requestCopy("")
                .transfer(transfer);
        
        ApplicationFormTransferError expectedError = builder.build();
        
        applicationFormTransferErrorDAOMock.save(EasyMock.isA(ApplicationFormTransferError.class));
        
        EasyMock.replay(applicationFormTransferErrorDAOMock);
        
        ApplicationFormTransferError actualError = service.createTransferError(builder);
        
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
    
    @Test
    public void shouldReturnExistingApplicationFormTransferObject() {
        ApplicationFormTransfer transfer = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.CANCELLED).transferStartTimepoint(new Date()).build();
        ApplicationForm form = new ValidApplicationFormBuilder().build();

        EasyMock.expect(applicationFormTransferDAOMock.getByApplicationForm(form)).andReturn(transfer);
        
        EasyMock.replay(applicationFormTransferDAOMock);
        
        assertEquals(transfer, service.createOrReturnExistingApplicationFormTransfer(form));
        
        EasyMock.verify(applicationFormTransferDAOMock);
    }
    
    @Test
    public void shouldCreateNewApplicationFormTransfer() {
        ApplicationForm form = new ValidApplicationFormBuilder().build();

        EasyMock.expect(applicationFormTransferDAOMock.getByApplicationForm(form)).andReturn(null);
        applicationFormTransferDAOMock.save(EasyMock.isA(ApplicationFormTransfer.class));
        
        EasyMock.replay(applicationFormTransferDAOMock);
        
        ApplicationFormTransfer actualTransfer = service.createOrReturnExistingApplicationFormTransfer(form);
        
        assertEquals(form, actualTransfer.getApplicationForm());
        assertEquals(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL, actualTransfer.getStatus());
        assertTrue(DateUtils.isToday(actualTransfer.getTransferStartTimepoint()));
        
        EasyMock.verify(applicationFormTransferDAOMock);
    }
    
}