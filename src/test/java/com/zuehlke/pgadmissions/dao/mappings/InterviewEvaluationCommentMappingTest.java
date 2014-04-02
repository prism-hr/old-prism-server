package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.CompleteInterviewComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class InterviewEvaluationCommentMappingTest extends AutomaticRollbackTestCase {
    
	@Test
	public void shouldSaveAndLoadInterviewEvaluationComment() {
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(user).advert(testObjectProvider.getEnabledProgram()).build();

		save(user, applicationForm);

		flushAndClearSession();

		CompleteInterviewComment interviewEvaluationComment = new InterviewEvaluationCommentBuilder().application(applicationForm).comment("hi")
				.user(user).build();
		save(interviewEvaluationComment);
		assertNotNull(interviewEvaluationComment.getId());

		CompleteInterviewComment reloadedComment = (CompleteInterviewComment) sessionFactory.getCurrentSession().get(CompleteInterviewComment.class,
				interviewEvaluationComment.getId());
		assertSame(interviewEvaluationComment, reloadedComment);

		flushAndClearSession();
		reloadedComment = (CompleteInterviewComment) sessionFactory.getCurrentSession()
				.get(CompleteInterviewComment.class, interviewEvaluationComment.getId());
		assertNotSame(interviewEvaluationComment, reloadedComment);
		assertEquals(interviewEvaluationComment.getId(), reloadedComment.getId());

		assertEquals(applicationForm.getId(), reloadedComment.getApplication().getId());
		assertEquals("hi", reloadedComment.getContent());
		assertEquals(user.getId(), reloadedComment.getUser().getId());

	}

}
