package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReviewEvaluationCommentMappingTest extends AutomaticRollbackTestCase {
	@Test
	public void shouldSaveAndLoadReviewEvaluationComment() {
		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(user).program(program).toApplicationForm();
		
		ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).createdDate(new Date()).toReviewRound();
		save(program,  user, applicationForm,reviewRound);

		flushAndClearSession();

		ReviewEvaluationComment reviewEveluationComment = new ReviewEvaluationCommentBuilder().application(applicationForm).comment("hi")				
				.type(CommentType.REVIEW_EVALUATION).user(user).reviewRound(reviewRound).toReviewEvaluationComment();
		save(reviewEveluationComment);
		assertNotNull(reviewEveluationComment.getId());
		
		ReviewEvaluationComment reloadedComment = (ReviewEvaluationComment) sessionFactory.getCurrentSession().get(ReviewEvaluationComment.class, reviewEveluationComment.getId());
		assertSame(reviewEveluationComment, reloadedComment);
		
		flushAndClearSession();
		reloadedComment = (ReviewEvaluationComment) sessionFactory.getCurrentSession().get(ReviewEvaluationComment.class, reviewEveluationComment.getId());
		assertNotSame(reviewEveluationComment, reloadedComment);
		assertEquals(reviewEveluationComment, reloadedComment);
		
		assertEquals(applicationForm, reloadedComment.getApplication());
		assertEquals("hi", reloadedComment.getComment());
		assertEquals(reviewRound, reloadedComment.getReviewRound());
		assertEquals(CommentType.REVIEW_EVALUATION, reloadedComment.getType());
		assertEquals(user, reloadedComment.getUser());
		
		
	}

}
