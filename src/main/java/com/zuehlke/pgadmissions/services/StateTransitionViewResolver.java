package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Service
public class StateTransitionViewResolver {

    private static final String REJECTION_VIEW = "redirect:/rejectApplication?applicationId=";
    private static final String APPROVAL_VIEW = "redirect:/approval/moveToApproval?applicationId=";
    private static final String INTERVIEW_VIEW = "redirect:/interview/moveToInterview?applicationId=";
    private static final String REVIEW_VIEW = "redirect:/review/moveToReview?applicationId=";
    private static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";
    private static final String MY_APPLICATIONS_VIEW = "redirect:/applications";

    public String resolveView(ApplicationForm applicationForm) {

        if (!applicationForm.isProgrammeStillAvailable()) {
            return REJECTION_VIEW + applicationForm.getApplicationNumber() + "&rejectionId=7&rejectionIdForced=true";
        }

        if (applicationForm.isInState(ApplicationFormStatus.APPROVED)) {
            return MY_APPLICATIONS_VIEW;
        }

        ApplicationFormStatus nextStatus = getNextStatus(applicationForm);
        if (nextStatus == null) {
            return STATE_TRANSITION_VIEW;
        }

        switch (nextStatus) {
        case REVIEW:
            return REVIEW_VIEW + applicationForm.getApplicationNumber();
        case INTERVIEW:
            return INTERVIEW_VIEW + applicationForm.getApplicationNumber();
        case APPROVAL:
            return APPROVAL_VIEW + applicationForm.getApplicationNumber();
        case REJECTED:
            return REJECTION_VIEW + applicationForm.getApplicationNumber();
        default:
            return STATE_TRANSITION_VIEW;
        }

    }

    public ApplicationFormStatus getNextStatus(ApplicationForm applicationForm) {
        StateChangeComment stateChangeComment = null;
        switch (applicationForm.getStatus()) {
        case APPROVAL:
            stateChangeComment = getEvaluationCommentForLatestApprovalRound(applicationForm);
            break;
        case REVIEW:
            stateChangeComment = getEvaluationCommentForLatestRoundOfReview(applicationForm);
            break;
        case VALIDATION:
            stateChangeComment = getValidationComment(applicationForm);
            break;
        case INTERVIEW:
            stateChangeComment = getEvaluationCommentForLatestInterview(applicationForm);
            break;
        default:
        }
        if (stateChangeComment != null) {
            return stateChangeComment.getNextStatus();
        }
        return null;
    }

    private ReviewEvaluationComment getEvaluationCommentForLatestRoundOfReview(final ApplicationForm applicationForm) {
        ReviewRound latestReviewRound = applicationForm.getLatestReviewRound();
        if (latestReviewRound != null) {
            Integer latestReviewRoundId = latestReviewRound.getId();
            for (Comment comment : applicationForm.getApplicationComments()) {
                if (comment instanceof ReviewEvaluationComment) {
                    ReviewEvaluationComment reviewEvaluationComment = (ReviewEvaluationComment) comment;
                    Integer reviewEvaluationCommentId = reviewEvaluationComment.getReviewRound().getId();
                    if (latestReviewRoundId.equals(reviewEvaluationCommentId)) {
                        return reviewEvaluationComment;
                    }
                }
            }
        }
        return null;
    }

    private ApprovalEvaluationComment getEvaluationCommentForLatestApprovalRound(final ApplicationForm applicationForm) {
        ApprovalRound latestApprovalRound = applicationForm.getLatestApprovalRound();
        if (latestApprovalRound != null) {
            Integer latestApprovalRoundId = latestApprovalRound.getId();
            for (Comment comment : applicationForm.getApplicationComments()) {
                if (comment instanceof ApprovalEvaluationComment) {
                    ApprovalEvaluationComment approvalEvaluationComment = (ApprovalEvaluationComment) comment;
                    Integer approvalEvaluationCommentId = approvalEvaluationComment.getApprovalRound().getId();
                    if (latestApprovalRoundId.equals(approvalEvaluationCommentId)) {
                        return approvalEvaluationComment;
                    }
                }
            }
        }
        return null;
    }

    private InterviewEvaluationComment getEvaluationCommentForLatestInterview(final ApplicationForm applicationForm) {
        Interview latestInterview = applicationForm.getLatestInterview();
        if (latestInterview != null) {
            Integer latestInterviewId = latestInterview.getId();
            for (Comment comment : applicationForm.getApplicationComments()) {
                if (comment instanceof InterviewEvaluationComment) {
                    InterviewEvaluationComment interviewEvaluationComment = (InterviewEvaluationComment) comment;
                    Integer interviewEvaluationCommentId = interviewEvaluationComment.getInterview().getId();
                    if (latestInterviewId.equals(interviewEvaluationCommentId)) {
                        return interviewEvaluationComment;
                    }
                }
            }
        }
        return null;
    }

    private ValidationComment getValidationComment(final ApplicationForm applicationForm) {
        List<Comment> applicationComments = applicationForm.getApplicationComments();
        for (Comment comment : applicationComments) {
            if (comment instanceof ValidationComment && comment.getType() != CommentType.ADMITTER_COMMENT) {
                return (ValidationComment) comment;
            }
        }
        return null;
    }
}
