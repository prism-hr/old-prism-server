package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.InterviewScheduleComment;
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

    private CommentFactory commentFactory;
    private ApplicationForm applicationForm;
    private RegisteredUser user;

    @Before
    public void prepare() {
        commentFactory = new CommentFactory();
        applicationForm = new ApplicationFormBuilder().id(1).build();
        user = new RegisteredUserBuilder().id(8).build();
    }

    @Test
    public void shouldReturnCorrectCommentType() {
        String strComment = "bob";
        List<Document> documents = Collections.emptyList();

        Comment comment = commentFactory.createComment(applicationForm, user, strComment, documents, CommentType.GENERIC, null);
        assertEquals(Comment.class, comment.getClass());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals("bob", comment.getComment());
        assertEquals(user, comment.getUser());
        assertSame(documents, comment.getDocuments());

        comment = commentFactory.createComment(applicationForm, user, strComment, documents, CommentType.VALIDATION, ApplicationFormStatus.INTERVIEW);
        assertEquals(ValidationComment.class, comment.getClass());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals("bob", comment.getComment());
        assertEquals(CommentType.VALIDATION, comment.getType());
        assertEquals(ApplicationFormStatus.INTERVIEW, ((StateChangeComment) comment).getNextStatus());
        assertEquals(user, comment.getUser());
        assertSame(documents, comment.getDocuments());

        comment = commentFactory.createComment(applicationForm, user, strComment, documents, CommentType.REVIEW_EVALUATION, ApplicationFormStatus.INTERVIEW);
        assertEquals(ReviewEvaluationComment.class, comment.getClass());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals("bob", comment.getComment());
        assertEquals(CommentType.REVIEW_EVALUATION, comment.getType());
        assertEquals(ApplicationFormStatus.INTERVIEW, ((StateChangeComment) comment).getNextStatus());
        assertEquals(user, comment.getUser());
        assertSame(documents, comment.getDocuments());

        comment = commentFactory.createComment(applicationForm, user, strComment, documents, CommentType.INTERVIEW_EVALUATION, ApplicationFormStatus.APPROVAL);
        assertEquals(InterviewEvaluationComment.class, comment.getClass());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals("bob", comment.getComment());
        assertEquals(CommentType.INTERVIEW_EVALUATION, comment.getType());
        assertEquals(ApplicationFormStatus.APPROVAL, ((StateChangeComment) comment).getNextStatus());
        assertEquals(user, comment.getUser());
        assertSame(documents, comment.getDocuments());

        comment = commentFactory.createComment(applicationForm, user, strComment, documents, CommentType.REVIEW, null);
        assertEquals(ReviewComment.class, comment.getClass());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals("bob", comment.getComment());
        assertEquals(user, comment.getUser());
        assertSame(documents, comment.getDocuments());

        comment = commentFactory.createComment(applicationForm, user, strComment, documents, CommentType.APPROVAL, ApplicationFormStatus.APPROVED);
        assertEquals(StateChangeComment.class, comment.getClass());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals("bob", comment.getComment());
        assertEquals(user, comment.getUser());
        assertSame(documents, comment.getDocuments());
        assertEquals(ApplicationFormStatus.APPROVED, ((StateChangeComment) comment).getNextStatus());

        comment = commentFactory.createComment(applicationForm, user, strComment, documents, CommentType.APPROVAL_EVALUATION, ApplicationFormStatus.APPROVED);
        assertEquals(ApprovalEvaluationComment.class, comment.getClass());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals("bob", comment.getComment());
        assertSame(documents, comment.getDocuments());
        assertEquals(CommentType.APPROVAL_EVALUATION, comment.getType());
        assertEquals(ApplicationFormStatus.APPROVED, ((StateChangeComment) comment).getNextStatus());
        assertEquals(user, comment.getUser());

        comment = commentFactory.createComment(applicationForm, user, strComment, documents, CommentType.APPROVAL_EVALUATION, ApplicationFormStatus.REJECTED);
        assertEquals(ApprovalEvaluationComment.class, comment.getClass());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals("bob", comment.getComment());
        assertSame(documents, comment.getDocuments());
        assertEquals(CommentType.APPROVAL_EVALUATION, comment.getType());
        assertEquals(ApplicationFormStatus.REJECTED, ((StateChangeComment) comment).getNextStatus());
        assertEquals(user, comment.getUser());

    }

    @Test
    public void shouldCreateInterviewScheduleComment() {
        InterviewScheduleComment comment = commentFactory.createInterviewScheduleComment(user, applicationForm, "applicant!", "interviewer!");
        assertSame(applicationForm, comment.getApplication());
        assertEquals("", comment.getComment());
        assertEquals("applicant!", comment.getFurtherDetails());
        assertEquals("interviewer!", comment.getFurtherInterviewerDetails());
        assertEquals(CommentType.INTERVIEW_SCHEDULE, comment.getType());
        assertSame(user, comment.getUser());

    }

}
