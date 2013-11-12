package com.zuehlke.pgadmissions.utils;

import java.util.List;

import org.springframework.stereotype.Component;

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
import com.zuehlke.pgadmissions.domain.StateChangeSuggestionComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Component
public class CommentFactory {

    public Comment createComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, List<Document> documents, CommentType commentType,
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
        comment.setDocuments(documents);
        comment.setUser(user);
        comment.setApplication(applicationForm);
        return comment;
    }

    public InterviewScheduleComment createInterviewScheduleComment(RegisteredUser user, ApplicationForm application, String furtherDetails,
            String furtherInterviewerDetails, String locationUrl) {
        InterviewScheduleComment scheduleComment = new InterviewScheduleComment();
        scheduleComment.setFurtherDetails(furtherDetails);
        scheduleComment.setFurtherInterviewerDetails(furtherInterviewerDetails);
        scheduleComment.setLocationUrl(locationUrl);
        scheduleComment.setUser(user);
        scheduleComment.setApplication(application);
        return scheduleComment;
    }

    public StateChangeSuggestionComment createStateChangeSuggestionComment(RegisteredUser user, ApplicationForm application, String comment,
            ApplicationFormStatus nextStatus) {
        StateChangeSuggestionComment stateChangeSuggestionComment = new StateChangeSuggestionComment();
        stateChangeSuggestionComment.setUser(user);
        stateChangeSuggestionComment.setApplication(application);
        stateChangeSuggestionComment.setComment(comment);
        stateChangeSuggestionComment.setNextStatus(nextStatus);
        return stateChangeSuggestionComment;

    }

    private Comment creatStateChangeComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        StateChangeComment stateChangeComment = new StateChangeComment();
        stateChangeComment.setComment(strComment);
        stateChangeComment.setType(commentType);
        setNextStatus(stateChangeComment, applicationForm, nextStatus);
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
        setNextStatus(validationComment, applicationForm, nextStatus);
        return validationComment;
    }

    private Comment createReviewEvaluationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        ReviewEvaluationComment reviewEvaluationComment = new ReviewEvaluationComment();
        reviewEvaluationComment.setType(commentType);
        setNextStatus(reviewEvaluationComment, applicationForm, nextStatus);
        reviewEvaluationComment.setReviewRound(applicationForm.getLatestReviewRound());
        return reviewEvaluationComment;
    }

    private Comment createInterviewEvaluationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        InterviewEvaluationComment interviewEvaluationComment = new InterviewEvaluationComment();
        interviewEvaluationComment.setType(commentType);
        setNextStatus(interviewEvaluationComment, applicationForm, nextStatus);
        interviewEvaluationComment.setInterview(applicationForm.getLatestInterview());
        return interviewEvaluationComment;
    }

    private Comment createApprovalEvaluationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType,
            ApplicationFormStatus nextStatus) {
        ApprovalEvaluationComment approvalEvaluationComment = new ApprovalEvaluationComment();
        approvalEvaluationComment.setType(commentType);
        setNextStatus(approvalEvaluationComment, applicationForm, nextStatus);
        approvalEvaluationComment.setApprovalRound(applicationForm.getLatestApprovalRound());
        return approvalEvaluationComment;
    }
    
    private void setNextStatus(StateChangeComment comment, ApplicationForm applicationForm, ApplicationFormStatus nextStatus) {
    	comment.setNextStatus(nextStatus);
    	applicationForm.setNextStatus(nextStatus);
    }
}