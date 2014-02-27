package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class InterviewEvaluationCommentMappingTest extends AutomaticRollbackTestCase {
	@Test
	public void shouldSaveAndLoadInterviewEvaluationComment() {
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(user).advert(testObjectProvider.getEnabledProgram()).build();

		Interview interview = new InterviewBuilder().application(applicationForm).dueDate(new Date()).build();
		save(user, applicationForm, interview);

		flushAndClearSession();

		InterviewEvaluationComment interviewEvaluationComment = new InterviewEvaluationCommentBuilder().application(applicationForm).comment("hi")
				.type(CommentType.INTERVIEW_EVALUATION).user(user).interview(interview).build();
		save(interviewEvaluationComment);
		assertNotNull(interviewEvaluationComment.getId());

		InterviewEvaluationComment reloadedComment = (InterviewEvaluationComment) sessionFactory.getCurrentSession().get(InterviewEvaluationComment.class,
				interviewEvaluationComment.getId());
		assertSame(interviewEvaluationComment, reloadedComment);

		flushAndClearSession();
		reloadedComment = (InterviewEvaluationComment) sessionFactory.getCurrentSession()
				.get(InterviewEvaluationComment.class, interviewEvaluationComment.getId());
		assertNotSame(interviewEvaluationComment, reloadedComment);
		assertEquals(interviewEvaluationComment.getId(), reloadedComment.getId());

		assertEquals(applicationForm.getId(), reloadedComment.getApplication().getId());
		assertEquals("hi", reloadedComment.getComment());
		assertEquals(interview.getId(), reloadedComment.getInterview().getId());
		assertEquals(CommentType.INTERVIEW_EVALUATION, reloadedComment.getType());
		assertEquals(user.getId(), reloadedComment.getUser().getId());

	}

}
