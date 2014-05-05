package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class ApplicationTransferDAOTest extends AutomaticRollbackTestCase {

    private ApplicationTransferDAO applicationFormTransferDAO;
    
    private ApplicationForm applicationForm;
    
    @Test
    public void shouldReturnTransferBasedOnBookingReference() {
        applicationForm = testObjectProvider.getApplication(PrismState.APPLICATION_APPROVED);
        
        String bookingReferenceNumber = "97c69350-79d3-11e2-b92a-0800200c9a66";
        String uclUserIdReceived = "97c69350";

        ApplicationTransfer transfer = new ApplicationTransfer();
        transfer.setApplicationForm(applicationForm);
        transfer.setStatus(ApplicationTransferStatus.COMPLETED);
        transfer.setTransferFinishTimepoint(new Date());
        transfer.setTransferStartTimepoint(new Date());
        transfer.setUclBookingReferenceReceived(bookingReferenceNumber);
        transfer.setUclUserIdReceived(uclUserIdReceived);
        
        save(transfer);
        flushAndClearSession();
        
        ApplicationTransfer transferWithBookingRef = applicationFormTransferDAO.getByReceivedBookingReferenceNumber(bookingReferenceNumber);
        assertEquals(bookingReferenceNumber, transferWithBookingRef.getUclBookingReferenceReceived());
    }
    
}
