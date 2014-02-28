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
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ApprovalRoundDAOTest extends AutomaticRollbackTestCase {


	private ApprovalRoundDAO dao;
	private RegisteredUser applicant;
	private Program program;

	@Test
	public void shouldSaveApprovalRound() {
		ApplicationForm application = new ApplicationFormBuilder().id(2).advert(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		flushAndClearSession();
		
		ApprovalRound approvalRound = new ApprovalRoundBuilder().application(application).build();
		dao.save(approvalRound);
		assertNotNull(approvalRound.getId());
		flushAndClearSession();

		ApprovalRound returnedApprovalRound = (ApprovalRound) sessionFactory.getCurrentSession().get(ApprovalRound.class,approvalRound.getId());
		assertEquals(returnedApprovalRound.getId(), approvalRound.getId());
		
	}
	
	@Test
	public void shouldGetApprovalRoundById() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).advert(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		flushAndClearSession();
		
		ApprovalRound approvalRound = new ApprovalRoundBuilder().build();
		dao.save(approvalRound);
		assertNotNull(approvalRound.getId());
		flushAndClearSession();
		assertEquals(approvalRound.getId(), dao.getApprovalRoundById(approvalRound.getId()).getId());

	}
	
	
	@Before
	public void prepare() {
		applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		save(applicant);
		dao = new ApprovalRoundDAO(sessionFactory);
		program = testObjectProvider.getEnabledProgram();
	}
	
}
