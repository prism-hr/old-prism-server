package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ReviewRoundMappingTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;
	private RegisteredUser reviewerUser;
	private ApplicationForm application;
	
	@Test
	public void shouldSaveLoadReviewRoundWithReviewer() {
		
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).toReviewer()).application(application).toReviewRound();
		
		sessionFactory.getCurrentSession().save(reviewRound);
		
		flushAndClearSession();
		
		ReviewRound reloadedReviewRound = (ReviewRound) sessionFactory.getCurrentSession().get(ReviewRound.class, reviewRound.getId());
		assertNotSame(reviewRound, reloadedReviewRound);
		assertEquals(reviewRound, reloadedReviewRound);
		
		Assert.assertEquals(1, reloadedReviewRound.getReviewers().size());
		Reviewer reviewer = reloadedReviewRound.getReviewers().get(0);
		assertEquals(reviewerUser, reviewer.getUser());
		assertEquals(reloadedReviewRound, reviewer.getReviewRound());
		assertNotNull(reloadedReviewRound.getCreatedDate());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),DateUtils.truncate(reloadedReviewRound.getCreatedDate(), Calendar.DATE));
	}
	
	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		reviewerUser = new RegisteredUserBuilder().firstName("brad").lastName("brady").email("brady@test.com").username("brady").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		application = new ApplicationFormBuilder().program(program).applicant(user).toApplicationForm();
		save(user, program, reviewerUser, application);

		flushAndClearSession();
	}
}
