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
	public void shouldGetSupervisorWithId(){
		Supervisor supervisor = new SupervisorBuilder().id(1).toSupervisor();
		sessionFactory.getCurrentSession().save(supervisor);
		flushAndClearSession();		

		Supervisor returnedSupervisor = dao.getSupervisorWithId(supervisor.getId());
		assertEquals(supervisor, returnedSupervisor);
	}
	
	
	@Test
	public void shouldReturnSupervisorIfLastNotifiedIsNull() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		Supervisor supervisor = new SupervisorBuilder().user(user).toSupervisor();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).toApprovalRound();
		application.setLatestApprovalRound(approvalRound);
		save(application, supervisor, approvalRound);
		flushAndClearSession();

		List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
		assertTrue(supervisors.contains(supervisor));

	}

	@Test
	public void shouldNotReturnSupervisorInLastNotifiedIsNotNull() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		Supervisor supervisor = new SupervisorBuilder().user(user).lastNotified(new Date()).toSupervisor();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).toApprovalRound();
		application.setLatestApprovalRound(approvalRound);
		save(application, supervisor, approvalRound);
		flushAndClearSession();

		List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
		assertFalse(supervisors.contains(supervisor));

	}

	@Test
	public void shouldNotReturnSupervisorIfApplicationNotInApproved() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		Supervisor supervisor = new SupervisorBuilder().user(user).toSupervisor();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).toApprovalRound();
		application.setLatestApprovalRound(approvalRound);
		save(application, supervisor, approvalRound);
		flushAndClearSession();

		List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
		assertFalse(supervisors.contains(supervisor));

		
	}
	@Test
	public void shouldNotReturnSupervisorifNotSupervisorOfLatestApprovalRound() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		Supervisor supervisor = new SupervisorBuilder().user(user).toSupervisor();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).toApprovalRound();
		application.getApprovalRounds().add(approvalRound);
		save(application, supervisor, approvalRound);
		flushAndClearSession();

		List<Supervisor> supervisors = dao.getSupervisorsDueNotification();
		assertFalse(supervisors.contains(supervisor));

	}
	
	@Before
	public void setUp(){
		super.setUp();
		dao = new SupervisorDAO(sessionFactory);
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		save(user, program);
		flushAndClearSession();
	}
}
