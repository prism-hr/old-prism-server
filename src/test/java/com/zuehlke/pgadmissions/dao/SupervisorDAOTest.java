package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class SupervisorDAOTest extends AutomaticRollbackTestCase {

    private SupervisorDAO dao;
    private RegisteredUser user;
    private Program program;

    @Before
    public void initialise() {
        dao = new SupervisorDAO(sessionFactory);
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(user, program);
        flushAndClearSession();
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        SupervisorDAO supervisorDAO = new SupervisorDAO();
        supervisorDAO.getSupervisorWithId(1);
    }

    @Test
    public void shouldGetSupervisorWithId() {
        Supervisor supervisor = new SupervisorBuilder().id(1).isPrimary(false).build();
        sessionFactory.getCurrentSession().save(supervisor);
        flushAndClearSession();

        Supervisor returnedSupervisor = dao.getSupervisorWithId(supervisor.getId());
        assertTrue(isSame(supervisor, returnedSupervisor));
    }

    @Test
    public void shouldReturnApprovalRoundForGivenSupervisor() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(false).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).projectTitle("title").application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, approvalRound);
        flushAndClearSession();

        supervisor = dao.getSupervisorWithId(supervisor.getId());
        approvalRound = supervisor.getApprovalRound();
        assertEquals("title", approvalRound.getProjectTitle());
    }

    @Test
    public void shouldReturnSupervisorIfLastNotifiedIsNull() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(false).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
        assertTrue(listContainsSupervisor(supervisor, supervisors));
    }

    @Test
    public void shouldNotReturnSupervisorInLastNotifiedIsNotNull() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(false).lastNotified(new Date()).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }

    @Test
    public void shouldNotReturnSupervisorIfApplicationNotInApproved() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(false).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }

    @Test
    public void shouldNotReturnSupervisorifNotSupervisorOfLatestApprovalRound() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(false).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.getApprovalRounds().add(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldNotReturnPrimarySupervisorIfTheyHaveAlreadyConfirmed() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).confirmedSupervision(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueNotification();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldReturnPrimarySupervisorIfLastNotifiedIsNull() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueNotification();
        assertTrue(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldNotReturnPrimarySupervisorIfLastNotifiedIsNullAndIsPrimaryIsFalse() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(false).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueNotification();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldNotReturnPrimarySupervisorInLastNotifiedIsNotNull() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).lastNotified(new Date()).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueNotification();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }

    @Test
    public void shouldNotReturnPrimarySupervisorIfApplicationNotInApproved() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueNotification();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }

    @Test
    public void shouldNotReturnPrimarySupervisorifNotSupervisorOfLatestApprovalRound() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.getApprovalRounds().add(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueNotification();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldReturnPrimarySupervisorsDueNotificationReminder() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).confirmedSupervision(null).lastNotified(DateUtils.addDays(new Date(), -10)).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueReminder();
        assertTrue(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldNotReturnPrimarySupervisorsDueNotificationReminder() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(false).lastNotified(DateUtils.addDays(new Date(), -10)).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueReminder();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldNotReturnPrimarySupervisorsDueNotificationIfIntervalIsSmallerThanReminder() {
        ReminderIntervalDAO reminderIntervalDAO = new ReminderIntervalDAO(sessionFactory);
        ReminderInterval reminderInterval = reminderIntervalDAO.getReminderInterval();
        reminderInterval.setDuration(5);
        save(reminderInterval);
        flushAndClearSession();
        
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).lastNotified(DateUtils.addDays(new Date(), -2)).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueReminder();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldNotReturnPrimarySupervisorsDueNotificationReminderIfApplicationInWrongState() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).lastNotified(DateUtils.addDays(new Date(), -2)).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueReminder();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldNotReturnPrimarySupervisorDueReminderIfNotSupervisorOfLatestApprovalRound() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).lastNotified(DateUtils.addDays(new Date(), -10)).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.getApprovalRounds().add(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueNotification();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldReturnPrimarySupervisorDueReminderIfSupervisionHasNotYetBeenConfirmed() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).confirmedSupervision(null).lastNotified(DateUtils.addDays(new Date(), -10)).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueReminder();
        assertTrue(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldNotReturnPrimarySupervisorDueReminderIfSupervisionHasBeenConfirmed() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).confirmedSupervision(true).lastNotified(DateUtils.addDays(new Date(), -10)).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueReminder();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    @Test
    public void shouldNotReturnPrimarySupervisorDueReminderIfSupervisionHasBeenDeclined() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).confirmedSupervision(false).declinedSupervisionReason("Hello").lastNotified(DateUtils.addDays(new Date(), -10)).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getPrimarySupervisorsDueReminder();
        assertFalse(listContainsSupervisor(supervisor, supervisors));
    }
    
    private boolean listContainsSupervisor(Supervisor supervisor, List<Supervisor> supervisors) {
        for (Supervisor entry : supervisors) {
            if (supervisor.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSame(Supervisor supervisor, Supervisor other) {
        return supervisor.getId().equals(other.getId());
    }
}
