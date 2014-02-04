package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
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
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        save(user, institution, program);
        flushAndClearSession();
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        SupervisorDAO supervisorDAO = new SupervisorDAO();
        supervisorDAO.getSupervisorWithId(1);
    }

    @Test
    public void shouldGetSupervisorWithId() {
        RegisteredUser supervisorUser = new RegisteredUserBuilder().firstName("Super").lastName("Visor").email("super@test.com").username("supervisor")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        Supervisor supervisor = new SupervisorBuilder().id(1).isPrimary(false).user(supervisorUser).build();
        save(supervisorUser, supervisor);
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

    private boolean isSame(Supervisor supervisor, Supervisor other) {
        return supervisor.getId().equals(other.getId());
    }
}
