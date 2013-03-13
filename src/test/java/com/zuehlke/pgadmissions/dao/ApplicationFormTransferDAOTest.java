package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;

public class ApplicationFormTransferDAOTest extends AutomaticRollbackTestCase {

    private ApplicationFormTransferDAO applicationFormTransferDAO;
    
    private ApplicationForm applicationForm;
    
    @Before
    public void setup() {
        applicationFormTransferDAO = new ApplicationFormTransferDAO(sessionFactory);
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        
        save(user, program);
        flushAndClearSession();

        applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.VALIDATION).build();
        
        save(applicationForm);
        flushAndClearSession();
    }
    
    @Test
    public void shouldReturnTransferBasedOnBookingReference() {
        String bookingReferenceNumber = "97c69350-79d3-11e2-b92a-0800200c9a66";
        String uclUserIdReceived = "97c69350";

        ApplicationFormTransfer transfer = new ApplicationFormTransfer();
        transfer.setApplicationForm(applicationForm);
        transfer.setStatus(ApplicationTransferStatus.COMPLETED);
        transfer.setTransferFinishTimepoint(new Date());
        transfer.setTransferStartTimepoint(new Date());
        transfer.setUclBookingReferenceReceived(bookingReferenceNumber);
        transfer.setUclUserIdReceived(uclUserIdReceived);
        
        save(transfer);
        flushAndClearSession();
        
        ApplicationFormTransfer transferWithBookingRef = applicationFormTransferDAO.getByReceivedBookingReferenceNumber(bookingReferenceNumber);
        assertEquals(bookingReferenceNumber, transferWithBookingRef.getUclBookingReferenceReceived());
    }
}
