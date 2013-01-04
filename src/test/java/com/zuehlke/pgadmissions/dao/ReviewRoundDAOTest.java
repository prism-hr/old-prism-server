package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ReviewRoundDAOTest extends AutomaticRollbackTestCase {


	private ReviewRoundDAO dao;
	private RegisteredUser applicant;
	private Program program;

	@Test
	public void shouldSaveReviewRound() {
		ApplicationForm application = new ApplicationFormBuilder().id(2).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		flushAndClearSession();
		
		ReviewRound reviewRound = new ReviewRoundBuilder().application(application).build();
		dao.save(reviewRound);
		assertNotNull(reviewRound.getId());
		flushAndClearSession();

		ReviewRound returnedReviewRound = (ReviewRound) sessionFactory.getCurrentSession().get(ReviewRound.class,reviewRound.getId());
		assertEquals(returnedReviewRound.getId(), reviewRound.getId());
		
	}
	
	@Test
	public void shouldGetReviewRounderById() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		flushAndClearSession();
		
		ReviewRound reviewRound = new ReviewRoundBuilder().build();
		dao.save(reviewRound);
		assertNotNull(reviewRound.getId());
		flushAndClearSession();
		assertEquals(reviewRound.getId(), dao.getReviewRoundById(reviewRound.getId()).getId());

	}
	
	
	@Before
	public void setUp() {
		super.setUp();
		applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		program = new ProgramBuilder().code("doesntexist").title("another title").build();
		
		save(applicant, program, applicant);
		
		dao = new ReviewRoundDAO(sessionFactory);
	}
	
}
