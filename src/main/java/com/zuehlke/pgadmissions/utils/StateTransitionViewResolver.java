package com.zuehlke.pgadmissions.utils;

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

	private static final String REJECTION_VIEW = "redirect:rejectApplication?applicationId=";
	private static final String APPROVAL_VIEW = "redirect:approval/moveToApproval?applicationId=";
	private static final String INTERVIEW_VIEW = "redirect:interview/moveToInterview?applicationId=";
	private static final String REVIEW_VIEW = "redirect:review/moveToReview?applicationId=";
	private static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";
	private static final String MY_APPLICATIONS_VIEW = "redirect:applications";

	public String resolveView(ApplicationForm applicationForm) {
		if (ApplicationFormStatus.VALIDATION == applicationForm.getStatus()) {
			return resolveViewForValidationState(applicationForm);
		}
		 if (ApplicationFormStatus.REVIEW == applicationForm.getStatus()) {
			return resolveViewForReviewState(applicationForm);
		}
		 if (ApplicationFormStatus.APPROVAL == applicationForm.getStatus()) {
			 return resolveViewForApprovalState(applicationForm);
		 }
		return  resolveViewForInterviewState(applicationForm);
	}

	private String resolveViewForApprovalState(ApplicationForm applicationForm) {
		ApprovalEvaluationComment evaluationCommentForLatestApprovalRound = null;
		List<Comment> applicationComments = applicationForm.getApplicationComments();
		for (Comment comment : applicationComments) {
			if (comment instanceof ApprovalEvaluationComment && applicationForm.getLatestApprovalRound().equals(((ApprovalEvaluationComment) comment).getApprovalRound())) {				
				evaluationCommentForLatestApprovalRound = (ApprovalEvaluationComment) comment;
				break;
			}
		}
		System.out.println("APPROVAL COMMENT ::: " + evaluationCommentForLatestApprovalRound);
		if (evaluationCommentForLatestApprovalRound == null) {
			return STATE_TRANSITION_VIEW;
		}
		if (ApplicationFormStatus.APPROVED == evaluationCommentForLatestApprovalRound.getNextStatus()) {
			return MY_APPLICATIONS_VIEW;
		}
		return REJECTION_VIEW + applicationForm.getApplicationNumber();
	}

	private String resolveViewForInterviewState(ApplicationForm applicationForm) {
		InterviewEvaluationComment evaluationCommentForLatestInterview = null;
		List<Comment> applicationComments = applicationForm.getApplicationComments();
		for (Comment comment : applicationComments) {
			if (comment instanceof InterviewEvaluationComment && applicationForm.getLatestInterview().equals(((InterviewEvaluationComment) comment).getInterview())) {				
				evaluationCommentForLatestInterview = (InterviewEvaluationComment) comment;
				break;
			}
		}
		if (evaluationCommentForLatestInterview == null) {
			return STATE_TRANSITION_VIEW;
		}
		if (ApplicationFormStatus.INTERVIEW == evaluationCommentForLatestInterview.getNextStatus()) {
			return INTERVIEW_VIEW + applicationForm.getApplicationNumber();
		}
		if (ApplicationFormStatus.APPROVAL == evaluationCommentForLatestInterview.getNextStatus()) {
			return APPROVAL_VIEW + applicationForm.getApplicationNumber();
		}
		return REJECTION_VIEW + applicationForm.getApplicationNumber();
	}

	private String resolveViewForReviewState(ApplicationForm applicationForm) {
		ReviewEvaluationComment evaluationCommentForLatestRoundOfReview = null;
		List<Comment> applicationComments = applicationForm.getApplicationComments();
		for (Comment comment : applicationComments) {
			if (comment instanceof ReviewEvaluationComment && applicationForm.getLatestReviewRound().equals(((ReviewEvaluationComment) comment).getReviewRound())) {				
				evaluationCommentForLatestRoundOfReview = (ReviewEvaluationComment) comment;
				break;
			}
		}
		if (evaluationCommentForLatestRoundOfReview == null) {
			return STATE_TRANSITION_VIEW;
		}
		if (ApplicationFormStatus.REVIEW == evaluationCommentForLatestRoundOfReview.getNextStatus()) {
			return REVIEW_VIEW + applicationForm.getApplicationNumber();
		}
		if (ApplicationFormStatus.INTERVIEW == evaluationCommentForLatestRoundOfReview.getNextStatus()) {
			return INTERVIEW_VIEW + applicationForm.getApplicationNumber();
		}
		if (ApplicationFormStatus.APPROVAL == evaluationCommentForLatestRoundOfReview.getNextStatus()) {
			return APPROVAL_VIEW + applicationForm.getApplicationNumber();
		}
		return REJECTION_VIEW + applicationForm.getApplicationNumber();
	}

	private String resolveViewForValidationState(ApplicationForm applicationForm) {
		ValidationComment validationComment = null;
		List<Comment> applicationComments = applicationForm.getApplicationComments();
		for (Comment comment : applicationComments) {
			if (comment instanceof ValidationComment) {
				validationComment = (ValidationComment) comment;
				break;
			}
		}
		if (validationComment == null) {
			return STATE_TRANSITION_VIEW;
		}
		if (ApplicationFormStatus.REVIEW == validationComment.getNextStatus()) {
			return REVIEW_VIEW + applicationForm.getApplicationNumber();
		}
		if (ApplicationFormStatus.INTERVIEW == validationComment.getNextStatus()) {
			return INTERVIEW_VIEW + applicationForm.getApplicationNumber();
		}
		if (ApplicationFormStatus.APPROVAL == validationComment.getNextStatus()) {
			return APPROVAL_VIEW + applicationForm.getApplicationNumber();
		}
		return REJECTION_VIEW + applicationForm.getApplicationNumber();
	
	}

}
