package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReviewEvaluationCommentMappingTest extends AutomaticRollbackTestCase {
    
	@Test
	public void shouldSaveAndLoadReviewEvaluationComment() {

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		save(user);
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(user).advert(testObjectProvider.getEnabledProgram()).build();
		save(applicationForm);
		ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).createdDate(new Date()).build();
		save(reviewRound);

		flushAndClearSession();

		ReviewEvaluationComment reviewEveluationComment = new ReviewEvaluationCommentBuilder().application(applicationForm).comment("hi")				
				.type(CommentType.REVIEW_EVALUATION).user(user).reviewRound(reviewRound).build();
		save(reviewEveluationComment);
		assertNotNull(reviewEveluationComment.getId());
		
		ReviewEvaluationComment reloadedComment = (ReviewEvaluationComment) sessionFactory.getCurrentSession().get(ReviewEvaluationComment.class, reviewEveluationComment.getId());
		assertSame(reviewEveluationComment, reloadedComment);
		
		flushAndClearSession();
		reloadedComment = (ReviewEvaluationComment) sessionFactory.getCurrentSession().get(ReviewEvaluationComment.class, reviewEveluationComment.getId());
		assertNotSame(reviewEveluationComment, reloadedComment);
		assertEquals(reviewEveluationComment.getId(), reloadedComment.getId());
		
		assertEquals(applicationForm.getId(), reloadedComment.getApplication().getId());
		assertEquals("hi", reloadedComment.getComment());
		assertEquals(reviewRound.getId(), reloadedComment.getReviewRound().getId());
		assertEquals(CommentType.REVIEW_EVALUATION, reloadedComment.getType());
		assertEquals(user.getId(), reloadedComment.getUser().getId());
	}
	
}
