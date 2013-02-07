package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
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

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        SupervisorDAO supervisorDAO = new SupervisorDAO();
        supervisorDAO.getSupervisorWithId(1);
    }

    @Test
    public void shouldGetSupervisorWithId() {
        Supervisor supervisor = new SupervisorBuilder().id(1).build();
        sessionFactory.getCurrentSession().save(supervisor);
        flushAndClearSession();

        Supervisor returnedSupervisor = dao.getSupervisorWithId(supervisor.getId());
        assertEquals(supervisor.getId(), returnedSupervisor.getId());
    }

    @Test
    public void shouldReturnApprovalRoundForGivenSupervisor() {

        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).build();
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
        Supervisor supervisor = new SupervisorBuilder().user(user).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
        assertTrue(listContainsId(supervisor, supervisors));
    }

    @Test
    public void shouldNotReturnSupervisorInLastNotifiedIsNotNull() {

        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).lastNotified(new Date()).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
        assertFalse(supervisors.contains(supervisor));

    }

    @Test
    public void shouldNotReturnSupervisorIfApplicationNotInApproved() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.setLatestApprovalRound(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
        assertFalse(supervisors.contains(supervisor));

    }

    @Test
    public void shouldNotReturnSupervisorifNotSupervisorOfLatestApprovalRound() {

        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();
        application.getApprovalRounds().add(approvalRound);
        save(application, supervisor, approvalRound);
        flushAndClearSession();

        List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
        assertFalse(supervisors.contains(supervisor));

    }

    @Before
    public void initialise() {
        dao = new SupervisorDAO(sessionFactory);
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(user, program);
        flushAndClearSession();
    }

    private boolean listContainsId(Supervisor supervisor, List<Supervisor> supervisors) {
        for (Supervisor entry : supervisors) {
            if (supervisor.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }
}
