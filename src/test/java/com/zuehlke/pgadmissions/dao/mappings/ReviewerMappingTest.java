package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReviewerMappingTest extends AutomaticRollbackTestCase{

	private ApplicationForm applicationForm;
	private RegisteredUser rewiewerUser;

	@Test
	public void shouldSaveAndLoadReviewer() throws ParseException{
		Date lastNotified = new SimpleDateFormat("dd MM yyyy HH:mm:ss").parse("01 05 2012 13:08:45");		
		Reviewer reviewer = new ReviewerBuilder().user(rewiewerUser).lastNotified(lastNotified).toReviewer();
		save(reviewer);
		assertNotNull(reviewer.getId());
		Reviewer reloadedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class,reviewer.getId());
		assertSame(reviewer, reloadedReviewer);
		
		flushAndClearSession();
		reloadedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class,reviewer.getId());
		
		assertNotSame(reviewer, reloadedReviewer);
		assertEquals(reviewer, reloadedReviewer);
		assertEquals(rewiewerUser, reloadedReviewer.getUser());
		assertEquals(lastNotified, reloadedReviewer.getLastNotified());
	}
	
	@Test	
	public void shouldLoadReviewerWithReviewRound() throws ParseException{
		Date lastNotified = new SimpleDateFormat("dd MM yyyy HH:mm:ss").parse("01 05 2012 13:08:45");		
		Reviewer reviewer = new ReviewerBuilder().user(rewiewerUser).lastNotified(lastNotified).toReviewer();
		save(reviewer);
		
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).application(applicationForm).toReviewRound();
		
		sessionFactory.getCurrentSession().save(reviewRound);
		flushAndClearSession();
		
		Reviewer reloadedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class,reviewer.getId());
		assertEquals(reviewRound, reloadedReviewer.getReviewRound());
	}
	@Test
	public void shoulLoadReviewWithReviewer() throws ParseException{
		Reviewer reviewer = new ReviewerBuilder().user(rewiewerUser).toReviewer();
		ReviewComment review = new ReviewCommentBuilder().application(applicationForm).user(rewiewerUser).comment("hi").reviewer(reviewer).commentType(CommentType.REVIEW).toReviewComment();
		
		
		save(reviewer, review);		
		assertNotNull(review.getId());
		flushAndClearSession();
		
		Reviewer reloadedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class,reviewer.getId());	
		assertEquals(review, reloadedReviewer.getReview());
		
	}
	
	@Before
	public void setUp() {
		super.setUp();

		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		
		save(program);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		
		rewiewerUser = new RegisteredUserBuilder().firstName("hanna").lastName("hoopla").email("hoopla@test.com").username("username1")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		save(applicant, rewiewerUser);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}
	
}
