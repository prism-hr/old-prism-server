package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class CommentFactoryTest {

	@Test
	public void shouldReturnCorrectCommentType(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();	
		String strComment ="bob";
		CommentFactory commentFactory = new CommentFactory();
		
		Comment comment = commentFactory.createComment(applicationForm, user, strComment, CommentType.GENERIC, null);
		assertEquals(Comment.class, comment.getClass());
		assertEquals(applicationForm, comment.getApplication());
		assertEquals("bob", comment.getComment());
		assertEquals(user,comment.getUser());
		
		comment = commentFactory.createComment(applicationForm, user, strComment, CommentType.VALIDATION, ApplicationFormStatus.INTERVIEW);
		assertEquals(ValidationComment.class, comment.getClass());
		assertEquals(applicationForm, comment.getApplication());
		assertEquals("bob", comment.getComment());
		assertEquals(CommentType.VALIDATION, comment.getType());
		assertEquals(ApplicationFormStatus.INTERVIEW, ((StateChangeComment)comment).getNextStatus());
		assertEquals(user,comment.getUser());
		
		comment = commentFactory.createComment(applicationForm, user, strComment, CommentType.REVIEW_EVALUATION, ApplicationFormStatus.INTERVIEW);
		assertEquals(ReviewEvaluationComment.class, comment.getClass());
		assertEquals(applicationForm, comment.getApplication());
		assertEquals("bob", comment.getComment());
		assertEquals(CommentType.REVIEW_EVALUATION, comment.getType());
		assertEquals(ApplicationFormStatus.INTERVIEW, ((StateChangeComment)comment).getNextStatus());
		assertEquals(user,comment.getUser());
		
		
		comment = commentFactory.createComment(applicationForm, user, strComment, CommentType.INTERVIEW_EVALUATION, ApplicationFormStatus.APPROVAL);
		assertEquals(InterviewEvaluationComment.class, comment.getClass());
		assertEquals(applicationForm, comment.getApplication());
		assertEquals("bob", comment.getComment());
		assertEquals(CommentType.INTERVIEW_EVALUATION, comment.getType());
		assertEquals(ApplicationFormStatus.APPROVAL, ((StateChangeComment)comment).getNextStatus());
		assertEquals(user,comment.getUser());
		
		comment = commentFactory.createComment(applicationForm, user, strComment, CommentType.REVIEW, null);
		assertEquals(ReviewComment.class, comment.getClass());
		assertEquals(applicationForm, comment.getApplication());
		assertEquals("bob", comment.getComment());
		assertEquals(user,comment.getUser());
		
		comment = commentFactory.createComment(applicationForm, user, strComment, CommentType.APPROVAL, ApplicationFormStatus.APPROVED);
		assertEquals(StateChangeComment.class, comment.getClass());
		assertEquals(applicationForm, comment.getApplication());
		assertEquals("bob", comment.getComment());
		assertEquals(user,comment.getUser());
		assertEquals(ApplicationFormStatus.APPROVED, ((StateChangeComment)comment).getNextStatus());
		
	}
	
	
}
