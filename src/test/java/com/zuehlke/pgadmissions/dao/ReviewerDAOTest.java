package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ReviewerDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private ReviewerDAO dao;
	private Program program;

	@Test
	public void shouldReturnReviewerIfLastModifiedIsNull(){
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		save(application);
		flushAndClearSession();
		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).toReviewer();
		save(reviewer);
		flushAndClearSession();
		
		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertTrue(reviewers.contains(reviewer));
		
	}
	
	@Test
	public void shouldNotReturnReviwerInLastNotifiedIsNotNull(){
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		save(application);
		flushAndClearSession();
		Reviewer reviewer = new ReviewerBuilder().user(user).lastNotified(new Date()).application(application).toReviewer();
		save(reviewer);
		flushAndClearSession();
		
		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertFalse(reviewers.contains(reviewer));
		
	}
	
	@Test
	public void shouldNotReturnReviewerIfApplicationNotInReview(){
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).toReviewer();
		save(reviewer);
		flushAndClearSession();
		
		List<Reviewer> reviewers = dao.getReviewersDueNotification();
		assertFalse(reviewers.contains(reviewer));
		
	}
	@Test
	public void shouldSaveReviewer(){
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Reviewer reviewer = new ReviewerBuilder().user(user).application(application).toReviewer();

		dao.save(reviewer);
		assertNotNull(reviewer.getId());
		flushAndClearSession();
		Reviewer returnedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class,reviewer.getId());
		assertEquals(returnedReviewer, reviewer);
		
	}
	


	@Before
	public void setUp() {
		super.setUp();
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		save(user, program);
		
		dao = new ReviewerDAO(sessionFactory);
	}
}
