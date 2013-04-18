package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormTransferBuilder;
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
    
    @Test
    public void shouldGetAllTransfersWaitingToBeSentToPorticoOldestFirst() {
        ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO = new ApplicationFormTransferErrorDAO(sessionFactory);
        for (ApplicationFormTransfer transfer : applicationFormTransferDAO.getAllTransfers()) {
            for (ApplicationFormTransferError error : applicationFormTransferErrorDAO.getByTransfer(transfer)) {
                sessionFactory.getCurrentSession().delete(error);
            }
            sessionFactory.getCurrentSession().delete(transfer);
        }
        flushAndClearSession();
        
        RegisteredUser applicant = new RegisteredUserBuilder().id(Integer.MAX_VALUE).username("ked9999@zuhlke.com").email("ked@zuhlke.com").build();
        
        ApplicationForm approvedForm1 = new ApplicationFormBuilder().applicant(applicant).build();
        ApplicationForm approvedForm2 = new ApplicationFormBuilder().applicant(applicant).build();
        ApplicationForm withdrawnForm1 = new ApplicationFormBuilder().applicant(applicant).build();
        ApplicationForm rejectedForm1 = new ApplicationFormBuilder().applicant(applicant).build();
        
        approvedForm1.setStatus(ApplicationFormStatus.APPROVED);
        approvedForm1.setApplicationNumber("1");
        
        approvedForm2.setStatus(ApplicationFormStatus.APPROVED);
        approvedForm2.setApplicationNumber("2");
        
        withdrawnForm1.setStatus(ApplicationFormStatus.WITHDRAWN);
        withdrawnForm1.setApplicationNumber("3");
        
        rejectedForm1.setStatus(ApplicationFormStatus.REJECTED);
        rejectedForm1.setApplicationNumber("4");
        
        DateTime now = new DateTime();
        
        ApplicationFormTransfer transferApprovedForm1 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING).transferStartTimepoint(now.plusHours(1).toDate()).applicationForm(approvedForm1).build();
        ApplicationFormTransfer transferApprovedForm2 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL).transferStartTimepoint(now.plusHours(2).toDate()).applicationForm(approvedForm2).build();
        ApplicationFormTransfer transferWithdrawnForm1 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING).transferStartTimepoint(now.plusHours(3).toDate()).applicationForm(withdrawnForm1).build();
        ApplicationFormTransfer transferRejectedForm1 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.CANCELLED).transferStartTimepoint(now.plusHours(4).toDate()).applicationForm(rejectedForm1).build();
        
        save(applicant, approvedForm1, approvedForm2, withdrawnForm1, rejectedForm1, transferApprovedForm1, transferApprovedForm2, transferWithdrawnForm1, transferRejectedForm1);
        flushAndClearSession();
        
        List<ApplicationFormTransfer> transfers = applicationFormTransferDAO.getAllTransfersWaitingToBeSentToPorticoOldestFirst();
        
        assertEquals(3, transfers.size());
        assertEquals(now.plusHours(1).toString("yyy-MM-dd HH:mm:ss.0"), transfers.get(0).getTransferStartTimepoint().toString());
        assertEquals(now.plusHours(2).toString("yyy-MM-dd HH:mm:ss.0"), transfers.get(1).getTransferStartTimepoint().toString());
        assertEquals(now.plusHours(3).toString("yyy-MM-dd HH:mm:ss.0"), transfers.get(2).getTransferStartTimepoint().toString());
    }
}
