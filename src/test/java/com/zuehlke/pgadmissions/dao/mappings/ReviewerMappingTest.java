package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
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
		Reviewer reviewer = new ReviewerBuilder().user(rewiewerUser).build();
		save(reviewer);
		assertNotNull(reviewer.getId());
		Reviewer reloadedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class,reviewer.getId());
		assertSame(reviewer, reloadedReviewer);
		
		flushAndClearSession();
		reloadedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class,reviewer.getId());
		
		assertNotSame(reviewer, reloadedReviewer);
		assertEquals(reviewer.getId(), reloadedReviewer.getId());
		assertEquals(rewiewerUser.getId(), reloadedReviewer.getUser().getId());
	}
	
	@Test	
	public void shouldLoadReviewerWithReviewRound() throws ParseException{
		Reviewer reviewer = new ReviewerBuilder().user(rewiewerUser).build();
		save(reviewer);
		
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).application(applicationForm).build();
		
		sessionFactory.getCurrentSession().save(reviewRound);
		flushAndClearSession();
		
		Reviewer reloadedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class,reviewer.getId());
		assertEquals(reviewRound.getId(), reloadedReviewer.getReviewRound().getId());
	}
	@Test
	public void shoulLoadReviewWithReviewer() throws ParseException{
		Reviewer reviewer = new ReviewerBuilder().user(rewiewerUser).build();
		ReviewComment review = new ReviewCommentBuilder().application(applicationForm).user(rewiewerUser).comment("hi").reviewer(reviewer).commentType(CommentType.REVIEW).build();
		
		
		save(reviewer, review);		
		assertNotNull(review.getId());
		flushAndClearSession();
		
		Reviewer reloadedReviewer = (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class,reviewer.getId());	
		assertEquals(review.getId(), reloadedReviewer.getReview().getId());
		
	}
	
	@Before
	public void prepare() {
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		rewiewerUser = new RegisteredUserBuilder().firstName("hanna").lastName("hoopla").email("hoopla@test.com").username("username1")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		save(applicant, rewiewerUser);
		applicationForm = new ApplicationFormBuilder().applicant(applicant).advert(testObjectProvider.getEnabledProgram()).build();
		save(applicationForm);
		flushAndClearSession();
	}
	
}
