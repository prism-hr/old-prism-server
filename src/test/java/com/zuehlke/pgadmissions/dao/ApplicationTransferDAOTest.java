package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferState;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class ApplicationTransferDAOTest extends AutomaticRollbackTestCase {

    private ApplicationTransferDAO applicationTransferDAO;
    
    private ApplicationForm applicationForm;
    
    @Test
    public void shouldReturnTransferBasedOnBookingReference() {
        applicationForm = testObjectProvider.getApplication(PrismState.APPLICATION_APPROVED);
        
        String bookingReferenceNumber = "97c69350-79d3-11e2-b92a-0800200c9a66";
        String uclUserIdReceived = "97c69350";

        ApplicationTransfer transfer = new ApplicationTransfer();
        transfer.setApplicationForm(applicationForm);
        transfer.setState(ApplicationTransferState.COMPLETED);
        transfer.setEndedTimestamp(new Date());
        transfer.setBeganTimestamp(new Date());
        transfer.setExternalTransferReference(bookingReferenceNumber);
        transfer.setExternalApplicantReference(uclUserIdReceived);
        
        save(transfer);
        flushAndClearSession();
        
        ApplicationTransfer transferWithBookingRef = applicationTransferDAO.getByExternalTransferReference(bookingReferenceNumber);
        assertEquals(bookingReferenceNumber, transferWithBookingRef.getExternalTransferReference());
    }
    
    public void setup(){
        super.setup();
        applicationTransferDAO = new ApplicationTransferDAO(sessionFactory);
    }
    
}
