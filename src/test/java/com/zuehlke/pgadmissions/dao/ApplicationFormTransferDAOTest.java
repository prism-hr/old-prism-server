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
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormTransferBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;

public class ApplicationFormTransferDAOTest extends AutomaticRollbackTestCase {

    private ApplicationFormTransferDAO applicationFormTransferDAO;
    
    private ApplicationForm applicationForm;
    
    private Program program;
    
    @Before
    public void prepare() {
        applicationFormTransferDAO = new ApplicationFormTransferDAO(sessionFactory);
        User user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .userAccount(new UserAccount().withPassword("password")
                .withEnabled(false)).build();

        save(user);
        flushAndClearSession();
        program = testObjectProvider.getEnabledProgram();
        applicationForm = new ApplicationFormBuilder().advert(program).applicant(user)
                .status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
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
        
        User applicant = new UserBuilder().id(Integer.MAX_VALUE).build();
        
        program = testObjectProvider.getEnabledProgram();
        
        ApplicationForm approvedForm1 = new ApplicationFormBuilder().advert(program).applicant(applicant).build();
        ApplicationForm approvedForm2 = new ApplicationFormBuilder().advert(program).applicant(applicant).build();
        ApplicationForm withdrawnForm1 = new ApplicationFormBuilder().advert(program).applicant(applicant).build();
        ApplicationForm rejectedForm1 = new ApplicationFormBuilder().advert(program).applicant(applicant).build();
        
//        approvedForm1.setStatus(ApplicationFormStatus.APPROVED);
//        approvedForm1.setApplicationNumber("1");
//        
//        approvedForm2.setStatus(ApplicationFormStatus.APPROVED);
//        approvedForm2.setApplicationNumber("2");
//        
//        withdrawnForm1.setStatus(ApplicationFormStatus.WITHDRAWN);
//        withdrawnForm1.setApplicationNumber("3");
//        
//        rejectedForm1.setStatus(ApplicationFormStatus.REJECTED);
//        rejectedForm1.setApplicationNumber("4");
        
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
    
    @Test
    public void shouldGetAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds() {
        ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO = new ApplicationFormTransferErrorDAO(sessionFactory);
        for (ApplicationFormTransfer transfer : applicationFormTransferDAO.getAllTransfers()) {
            for (ApplicationFormTransferError error : applicationFormTransferErrorDAO.getByTransfer(transfer)) {
                sessionFactory.getCurrentSession().delete(error);
            }
            sessionFactory.getCurrentSession().delete(transfer);
        }
        flushAndClearSession();
        
        User applicant = new UserBuilder().id(Integer.MAX_VALUE).build();
        
        program = testObjectProvider.getEnabledProgram();
        
        ApplicationForm approvedForm1 = new ApplicationFormBuilder().applicant(applicant).advert(program).build();
        ApplicationForm approvedForm2 = new ApplicationFormBuilder().applicant(applicant).advert(program).build();
        ApplicationForm withdrawnForm1 = new ApplicationFormBuilder().applicant(applicant).advert(program).build();
        ApplicationForm rejectedForm1 = new ApplicationFormBuilder().applicant(applicant).advert(program).build();
        ApplicationForm approvedForm3 = new ApplicationFormBuilder().applicant(applicant).advert(program).build();
        ApplicationForm approvedForm4 = new ApplicationFormBuilder().applicant(applicant).advert(program).build();
        
        // FIXME set statuses
//        approvedForm1.setStatus(ApplicationFormStatus.APPROVED);
//        approvedForm1.setApplicationNumber("1");
//        
//        approvedForm2.setStatus(ApplicationFormStatus.APPROVED);
//        approvedForm2.setApplicationNumber("2");
//        
//        withdrawnForm1.setStatus(ApplicationFormStatus.WITHDRAWN);
//        withdrawnForm1.setApplicationNumber("3");
//        
//        rejectedForm1.setStatus(ApplicationFormStatus.REJECTED);
//        rejectedForm1.setApplicationNumber("4");
//        
//        approvedForm3.setStatus(ApplicationFormStatus.APPROVED);
//        approvedForm3.setApplicationNumber("5");
//        
//        approvedForm4.setStatus(ApplicationFormStatus.APPROVED);
//        approvedForm4.setApplicationNumber("6");

        DateTime now = new DateTime();
        DateTime twoDaysAgo = now.minusDays(2);
        DateTime twoWeeksAgo = now.minusWeeks(2);
        
        ApplicationFormTransfer transferApprovedForm1 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING).transferStartTimepoint(now.plusHours(1).toDate()).applicationForm(approvedForm1).build();
        ApplicationFormTransfer transferApprovedForm2 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL).transferStartTimepoint(now.plusHours(2).toDate()).applicationForm(approvedForm2).build();
        ApplicationFormTransfer transferWithdrawnForm1 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING).transferStartTimepoint(now.plusHours(3).toDate()).applicationForm(withdrawnForm1).build();
        ApplicationFormTransfer transferRejectedForm1 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.CANCELLED).transferStartTimepoint(now.plusHours(4).toDate()).applicationForm(rejectedForm1).build();
        ApplicationFormTransfer transferApprovedForm3 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.REJECTED_BY_WEBSERVICE).transferStartTimepoint(now.plusHours(5).toDate()).applicationForm(approvedForm3).build();
        ApplicationFormTransfer transferApprovedForm4 = new ApplicationFormTransferBuilder().status(ApplicationTransferStatus.REJECTED_BY_WEBSERVICE).transferStartTimepoint(now.plusHours(6).toDate()).applicationForm(approvedForm4).build();
        
        save(applicant, approvedForm1, approvedForm2, withdrawnForm1, rejectedForm1, approvedForm3, approvedForm4, transferApprovedForm1, transferApprovedForm2, transferWithdrawnForm1, transferRejectedForm1, transferApprovedForm3, transferApprovedForm4);
        flushAndClearSession();
        
        sessionFactory.getCurrentSession().createSQLQuery("UPDATE APPLICATION_FORM_TRANSFER SET created_timestamp = ? WHERE id = ?").setTimestamp(0, twoDaysAgo.toDate()).setLong(1, transferApprovedForm3.getId()).executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("UPDATE APPLICATION_FORM_TRANSFER SET created_timestamp = ? WHERE id = ?").setTimestamp(0, twoWeeksAgo.toDate()).setLong(1, transferApprovedForm4.getId()).executeUpdate();
        
        List<Long> transfers = applicationFormTransferDAO.getAllTransfersWaitingToBeSentToPorticoOldestFirstAsIds();
        
        assertEquals(4, transfers.size());
        assertEquals(transferApprovedForm1.getId(), transfers.get(0));
        assertEquals(transferApprovedForm2.getId(), transfers.get(1));
        assertEquals(transferWithdrawnForm1.getId(), transfers.get(2));
        assertEquals(transferApprovedForm3.getId(), transfers.get(3));
    }    
}
