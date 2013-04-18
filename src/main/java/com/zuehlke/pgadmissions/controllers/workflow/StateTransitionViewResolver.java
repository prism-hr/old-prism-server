package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.List;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Component
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
        
        switch(applicationForm.getStatus()) {
        case APPROVAL:
            return resolveViewForApprovalState(applicationForm);
        case APPROVED:
            return MY_APPLICATIONS_VIEW;
        case REVIEW:
            return resolveViewForReviewState(applicationForm);
        case VALIDATION:
            return resolveViewForValidationState(applicationForm);
        default:
            return resolveViewForInterviewState(applicationForm);
        }
    }

    private String resolveViewForApprovalState(ApplicationForm applicationForm) {
        ApprovalEvaluationComment evaluationCommentForLatestApprovalRound = getEvaluationCommentForLatestApprovalRound(applicationForm);
    
        if (evaluationCommentForLatestApprovalRound == null) {
            return STATE_TRANSITION_VIEW;
        }
        
        if (ApplicationFormStatus.APPROVED == evaluationCommentForLatestApprovalRound.getNextStatus()
                && applicationForm.getStatus() == ApplicationFormStatus.APPROVED) {
            return MY_APPLICATIONS_VIEW;
        }
        
        if (ApplicationFormStatus.APPROVED == evaluationCommentForLatestApprovalRound.getNextStatus()
                && applicationForm.getStatus() == ApplicationFormStatus.APPROVAL) {
            return STATE_TRANSITION_VIEW;
        }
        
        return REJECTION_VIEW + applicationForm.getApplicationNumber();
    }
    
    private String resolveViewForInterviewState(ApplicationForm applicationForm) {
        InterviewEvaluationComment evaluationCommentForLatestInterview = getEvaluationCommentForLatestInterview(applicationForm);
        
        if (evaluationCommentForLatestInterview == null) {
            return STATE_TRANSITION_VIEW;
        }
        
        switch(evaluationCommentForLatestInterview.getNextStatus()) {
        case INTERVIEW:
            return INTERVIEW_VIEW + applicationForm.getApplicationNumber();
        case APPROVAL:
            return APPROVAL_VIEW + applicationForm.getApplicationNumber();
        default:
            return REJECTION_VIEW + applicationForm.getApplicationNumber();
        }
    }

    private String resolveViewForReviewState(ApplicationForm applicationForm) {
        ReviewEvaluationComment evaluationCommentForLatestRoundOfReview = getEvaluationCommentForLatestRoundOfReview(applicationForm);
        
        if (evaluationCommentForLatestRoundOfReview == null) {
            return STATE_TRANSITION_VIEW;
        }
        
        switch(evaluationCommentForLatestRoundOfReview.getNextStatus()) {
        case APPROVAL:
            return APPROVAL_VIEW + applicationForm.getApplicationNumber();
        case REVIEW:
            return REVIEW_VIEW + applicationForm.getApplicationNumber();
        case INTERVIEW:
            return INTERVIEW_VIEW + applicationForm.getApplicationNumber();
        default:
            return REJECTION_VIEW + applicationForm.getApplicationNumber();
        }
    }

    private String resolveViewForValidationState(ApplicationForm applicationForm) {
        ValidationComment validationComment = getValidationComment(applicationForm);
        
        if (validationComment == null) {
            return STATE_TRANSITION_VIEW;
        }
        
        switch(validationComment.getNextStatus()) {
        case APPROVAL:
            return APPROVAL_VIEW + applicationForm.getApplicationNumber();
        case REVIEW:
            return REVIEW_VIEW + applicationForm.getApplicationNumber();
        case INTERVIEW:
            return INTERVIEW_VIEW + applicationForm.getApplicationNumber();
        default:
            return REJECTION_VIEW + applicationForm.getApplicationNumber();
        }
    }
    
    private ReviewEvaluationComment getEvaluationCommentForLatestRoundOfReview(final ApplicationForm applicationForm) {
        Integer latestReviewRoundId = applicationForm.getLatestReviewRound().getId();
        for (Comment comment : applicationForm.getApplicationComments()) {
            if (comment instanceof ReviewEvaluationComment) {
                ReviewEvaluationComment reviewEvaluationComment = (ReviewEvaluationComment) comment;
                Integer reviewEvaluationCommentId = reviewEvaluationComment.getReviewRound().getId();
                if (latestReviewRoundId.equals(reviewEvaluationCommentId)) {
                    return reviewEvaluationComment;
                }
            }
        }
        return null;
    }
    
    private ApprovalEvaluationComment getEvaluationCommentForLatestApprovalRound(final ApplicationForm applicationForm) {
        Integer latestApprovalRoundId = applicationForm.getLatestApprovalRound().getId();
        for (Comment comment : applicationForm.getApplicationComments()) {
            if (comment instanceof ApprovalEvaluationComment) {
                ApprovalEvaluationComment approvalEvaluationComment = (ApprovalEvaluationComment) comment;
                Integer approvalEvaluationCommentId = approvalEvaluationComment.getApprovalRound().getId();
                if (latestApprovalRoundId.equals(approvalEvaluationCommentId)) {
                    return approvalEvaluationComment;
                }
            }
        }
        return null;
    }
    
    private InterviewEvaluationComment getEvaluationCommentForLatestInterview(final ApplicationForm applicationForm) {
        Integer latestInterviewId = applicationForm.getLatestInterview().getId();
        for (Comment comment : applicationForm.getApplicationComments()) {
            if (comment instanceof InterviewEvaluationComment) {
                InterviewEvaluationComment interviewEvaluationComment = (InterviewEvaluationComment) comment;
                Integer interviewEvaluationCommentId = interviewEvaluationComment.getInterview().getId();
                if (latestInterviewId.equals(interviewEvaluationCommentId)) {
                    return interviewEvaluationComment;
                }
            }
        }
        return null;
    }
    
    private ValidationComment getValidationComment(final ApplicationForm applicationForm) {
        List<Comment> applicationComments = applicationForm.getApplicationComments();
        for (Comment comment : applicationComments) {
            if (comment instanceof ValidationComment) {
                return (ValidationComment) comment;
            }
        }
        return null;
    }
}
