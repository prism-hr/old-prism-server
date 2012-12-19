package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ApprovalEvaluationCommentMappingTest extends AutomaticRollbackTestCase {

	
	@Test
	public void shouldSaveAndLoadInterviewEvaluationComment() {
		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(user).program(program).toApplicationForm();

		ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).toApprovalRound();
		save(program, user, applicationForm, approvalRound);

		flushAndClearSession();

		ApprovalEvaluationComment approvalEvaluationComment = new ApprovalEvaluationCommentBuilder().application(applicationForm).comment("hi")
				.type(CommentType.APPROVAL_EVALUATION).user(user).approvalRound(approvalRound).toApprovalEvaluationComment();
		save(approvalEvaluationComment);
		assertNotNull(approvalEvaluationComment.getId());

		ApprovalEvaluationComment reloadedComment = (ApprovalEvaluationComment) sessionFactory.getCurrentSession().get(ApprovalEvaluationComment.class,
				approvalEvaluationComment.getId());
		assertSame(approvalEvaluationComment, reloadedComment);

		flushAndClearSession();
		reloadedComment = (ApprovalEvaluationComment) sessionFactory.getCurrentSession()
				.get(ApprovalEvaluationComment.class, approvalEvaluationComment.getId());
		assertNotSame(approvalEvaluationComment, reloadedComment);
		assertEquals(approvalEvaluationComment.getId(), reloadedComment.getId());

		assertEquals(applicationForm.getId(), reloadedComment.getApplication().getId());
		assertEquals("hi", reloadedComment.getComment());
		assertEquals(approvalRound.getId(), reloadedComment.getApprovalRound().getId());
		assertEquals(CommentType.APPROVAL_EVALUATION, reloadedComment.getType());
		assertEquals(user.getId(), reloadedComment.getUser().getId());
	}

}
