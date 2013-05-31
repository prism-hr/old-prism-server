package com.zuehlke.pgadmissions.utils;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.InterviewScheduleComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Component
public class CommentFactory {

    public Comment createComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        Comment comment;
        if (commentType == CommentType.INTERVIEW_EVALUATION) {
            comment = createInterviewEvaluationComment(applicationForm, user, strComment, commentType, nextStatus);
        } else if (commentType == CommentType.REVIEW_EVALUATION) {
            comment = createReviewEvaluationComment(applicationForm, user, strComment, commentType, nextStatus);
        } else if (commentType == CommentType.APPROVAL_EVALUATION) {
            comment = createApprovalEvaluationComment(applicationForm, user, strComment, commentType, nextStatus);
        } else if (commentType == CommentType.VALIDATION) {
            comment = createValidationComment(applicationForm, user, strComment, commentType, nextStatus);
        } else if (commentType == CommentType.REVIEW) {
            comment = createReviewComment(applicationForm, user, strComment, commentType);
        } else if (commentType == CommentType.APPROVAL) {
            comment = creatStateChangeComment(applicationForm, user, strComment, commentType, nextStatus);
        } else {
            comment = new Comment();
        }
        comment.setComment(strComment);
        comment.setUser(user);
        comment.setApplication(applicationForm);
        return comment;
    }
    
    public InterviewScheduleComment createInterviewScheduleComment(RegisteredUser user, ApplicationForm application, String furtherDetails, String furtherInterviewerDetails){
        InterviewScheduleComment scheduleComment = new InterviewScheduleComment();
        scheduleComment.setFurtherDetails(furtherDetails);
        scheduleComment.setFurtherInterviewerDetails(furtherInterviewerDetails);
        scheduleComment.setUser(user);
        scheduleComment.setApplication(application);
        scheduleComment.setComment("");
        return scheduleComment;
    }

    private Comment creatStateChangeComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        StateChangeComment stateChangeComment = new StateChangeComment();
        stateChangeComment.setComment(strComment);
        stateChangeComment.setType(commentType);
        stateChangeComment.setNextStatus(nextStatus);
        return stateChangeComment;
    }

    private Comment createReviewComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType) {
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setType(commentType);
        return reviewComment;
    }

    private Comment createValidationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        ValidationComment validationComment = new ValidationComment();
        validationComment.setType(commentType);
        validationComment.setNextStatus(nextStatus);
        return validationComment;
    }

    private Comment createReviewEvaluationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        ReviewEvaluationComment comment = new ReviewEvaluationComment();
        comment.setType(commentType);
        comment.setNextStatus(nextStatus);
        return comment;
    }

    private Comment createInterviewEvaluationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        InterviewEvaluationComment comment = new InterviewEvaluationComment();
        comment.setType(commentType);
        comment.setNextStatus(nextStatus);
        return comment;
    }

    private Comment createApprovalEvaluationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        ApprovalEvaluationComment comment = new ApprovalEvaluationComment();
        comment.setType(commentType);
        comment.setNextStatus(nextStatus);
        return comment;
    }
}
