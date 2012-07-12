package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ApprovalRoundDAOTest extends AutomaticRollbackTestCase {


	private ApprovalRoundDAO dao;
	private RegisteredUser applicant;
	private Program program;

	@Test
	public void shouldSaveApprovalRound() {
		ApplicationForm application = new ApplicationFormBuilder().id(2).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		ApprovalRound approvalRound = new ApprovalRoundBuilder().application(application).toApprovalRound();
		dao.save(approvalRound);
		assertNotNull(approvalRound.getId());
		flushAndClearSession();

		ApprovalRound returnedApprovalRound = (ApprovalRound) sessionFactory.getCurrentSession().get(ApprovalRound.class,approvalRound.getId());
		assertEquals(returnedApprovalRound, approvalRound);
		
	}
	
	@Test
	public void shouldGetApprovalRoundById() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		ApprovalRound approvalRound = new ApprovalRoundBuilder().toApprovalRound();
		dao.save(approvalRound);
		assertNotNull(approvalRound.getId());
		flushAndClearSession();
		assertEquals(approvalRound, dao.getApprovalRoundById(approvalRound.getId()));

	}
	
	
	@Before
	public void setUp() {
		super.setUp();
		applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		
		save(applicant, program, applicant);
		
		dao = new ApprovalRoundDAO(sessionFactory);
	}
	
}
