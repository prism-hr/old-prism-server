package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class StateTransitionViewResolverTest {

	@Test
	public void shouldReturnStateTransitionViewIfInValidationAndNoValidationComment() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertEquals("private/staff/admin/state_transition", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToReviewIfValidationAndReviewNextStatus() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").id(1).status(ApplicationFormStatus.VALIDATION)
				.comments(new ValidationCommentBuilder().nextStatus(ApplicationFormStatus.REVIEW).toValidationComment()).toApplicationForm();
		assertEquals("redirect:review/moveToReview?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToIntervewIfValidationAndInterviewNextStatus() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").id(1).status(ApplicationFormStatus.VALIDATION)
				.comments(new ValidationCommentBuilder().nextStatus(ApplicationFormStatus.INTERVIEW).toValidationComment()).toApplicationForm();
		assertEquals("redirect:interview/moveToInterview?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToApprovalIfValidationAndAppovalNextStatus() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").id(1).status(ApplicationFormStatus.VALIDATION)
				.comments(new ValidationCommentBuilder().nextStatus(ApplicationFormStatus.APPROVAL).toValidationComment()).toApplicationForm();
		assertEquals("redirect:approval/moveToApproval?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToRejectionIfValidationAndRejectedNextStatus() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").id(1).status(ApplicationFormStatus.VALIDATION)
				.comments(new ValidationCommentBuilder().nextStatus(ApplicationFormStatus.REJECTED).toValidationComment()).toApplicationForm();
		assertEquals("redirect:rejectApplication?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnStateTransitionViewIfInReviewAndNoEvaluationCommentForLatestRoundOfReview() {
		ReviewRound previousReviewRound = new ReviewRoundBuilder().id(3).toReviewRound();
		ReviewRound latestReviewRound = new ReviewRoundBuilder().id(4).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABC")
				.comments(new ReviewEvaluationCommentBuilder().id(1).reviewRound(previousReviewRound).toReviewEvaluationComment())
				.status(ApplicationFormStatus.REVIEW).reviewRounds(previousReviewRound, latestReviewRound).latestReviewRound(latestReviewRound)
				.toApplicationForm();
		assertEquals("private/staff/admin/state_transition", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToReviewIfReviewAndReviewNextStatus() {
		ReviewRound previousReviewRound = new ReviewRoundBuilder().id(3).toReviewRound();
		ReviewRound latestReviewRound = new ReviewRoundBuilder().id(4).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.applicationNumber("ABC")
				.id(1)
				.comments(
						new ReviewEvaluationCommentBuilder().id(1).reviewRound(previousReviewRound).nextStatus(ApplicationFormStatus.INTERVIEW)
								.toReviewEvaluationComment(),
						new ReviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REVIEW).reviewRound(latestReviewRound)
								.toReviewEvaluationComment()).status(ApplicationFormStatus.REVIEW).reviewRounds(previousReviewRound, latestReviewRound)
				.latestReviewRound(latestReviewRound).toApplicationForm();
		assertEquals("redirect:review/moveToReview?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToIntervewIfReviewAndInterviewNextStatus() {
		ReviewRound previousReviewRound = new ReviewRoundBuilder().id(3).toReviewRound();
		ReviewRound latestReviewRound = new ReviewRoundBuilder().id(4).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.applicationNumber("ABC")
				.id(1)
				.comments(
						new ReviewEvaluationCommentBuilder().id(1).reviewRound(previousReviewRound).nextStatus(ApplicationFormStatus.REVIEW)
								.toReviewEvaluationComment(),
						new ReviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.INTERVIEW).reviewRound(latestReviewRound)
								.toReviewEvaluationComment()).status(ApplicationFormStatus.REVIEW).reviewRounds(previousReviewRound, latestReviewRound)
				.latestReviewRound(latestReviewRound).toApplicationForm();
		assertEquals("redirect:interview/moveToInterview?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToApprovalIfVReviewnAndAppovalNextStatus() {
		ReviewRound previousReviewRound = new ReviewRoundBuilder().id(3).toReviewRound();
		ReviewRound latestReviewRound = new ReviewRoundBuilder().id(4).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.applicationNumber("ABC")
				.id(1)
				.comments(
						new ReviewEvaluationCommentBuilder().id(1).reviewRound(previousReviewRound).nextStatus(ApplicationFormStatus.REVIEW)
								.toReviewEvaluationComment(),
						new ReviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.APPROVAL).reviewRound(latestReviewRound)
								.toReviewEvaluationComment()).status(ApplicationFormStatus.REVIEW).reviewRounds(previousReviewRound, latestReviewRound)
				.latestReviewRound(latestReviewRound).toApplicationForm();
		assertEquals("redirect:approval/moveToApproval?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToRejectionIfReviewAndRejectedNextStatus() {
		ReviewRound previousReviewRound = new ReviewRoundBuilder().id(3).toReviewRound();
		ReviewRound latestReviewRound = new ReviewRoundBuilder().id(4).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.applicationNumber("ABC")
				.id(1)
				.comments(
						new ReviewEvaluationCommentBuilder().id(1).reviewRound(previousReviewRound).nextStatus(ApplicationFormStatus.REVIEW)
								.toReviewEvaluationComment(),
						new ReviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REJECTED).reviewRound(latestReviewRound)
								.toReviewEvaluationComment()).status(ApplicationFormStatus.REVIEW).reviewRounds(previousReviewRound, latestReviewRound)
				.latestReviewRound(latestReviewRound).toApplicationForm();
		assertEquals("redirect:rejectApplication?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}
	
	@Test
	public void shouldReturnStateTransitionViewIfInInterviewAndNoEvaluationCommentForLatestInterview() {
		Interview previousInterview = new InterviewBuilder().id(3).toInterview();
		Interview latestInterview = new InterviewBuilder().id(4).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABC")
				.comments(new InterviewEvaluationCommentBuilder().id(1).interview(previousInterview).toInterviewEvaluationComment())
				.status(ApplicationFormStatus.INTERVIEW).interviews(previousInterview, latestInterview).latestInterview(latestInterview)
				.toApplicationForm();
		assertEquals("private/staff/admin/state_transition", new StateTransitionViewResolver().resolveView(applicationForm));
	}



	@Test
	public void shouldReturnRedirectToIntervewIfInterviewAndInterviewNextStatus() {
		Interview previousInterview = new InterviewBuilder().id(3).toInterview();
		Interview latestInterview = new InterviewBuilder().id(4).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.applicationNumber("ABC")
				.id(1)
				.comments(
						new InterviewEvaluationCommentBuilder().id(1).interview(previousInterview).nextStatus(ApplicationFormStatus.REVIEW)
								.toInterviewEvaluationComment(),
						new InterviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.INTERVIEW).interview(latestInterview)
								.toInterviewEvaluationComment()).status(ApplicationFormStatus.INTERVIEW).interviews(previousInterview, latestInterview)
				.latestInterview(latestInterview).toApplicationForm();
		assertEquals("redirect:interview/moveToInterview?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToApprovalIfInterviewAndAppovalNextStatus() {
		Interview previousInterview = new InterviewBuilder().id(3).toInterview();
		Interview latestInterview = new InterviewBuilder().id(4).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.applicationNumber("ABC")
				.id(1)
				.comments(
						new InterviewEvaluationCommentBuilder().id(1).interview(previousInterview).nextStatus(ApplicationFormStatus.REVIEW)
								.toInterviewEvaluationComment(),
						new InterviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.APPROVAL).interview(latestInterview)
								.toInterviewEvaluationComment()).status(ApplicationFormStatus.INTERVIEW).interviews(previousInterview, latestInterview)
				.latestInterview(latestInterview).toApplicationForm();
		assertEquals("redirect:approval/moveToApproval?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}

	@Test
	public void shouldReturnRedirectToRejectionIfInterviewAndRejectedNextStatus() {
		Interview previousInterview = new InterviewBuilder().id(3).toInterview();
		Interview latestInterview = new InterviewBuilder().id(4).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.applicationNumber("ABC")
				.id(1)
				.comments(
						new InterviewEvaluationCommentBuilder().id(1).interview(previousInterview).nextStatus(ApplicationFormStatus.REVIEW)
								.toInterviewEvaluationComment(),
						new InterviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REJECTED).interview(latestInterview)
								.toInterviewEvaluationComment()).status(ApplicationFormStatus.INTERVIEW).interviews(previousInterview, latestInterview)
				.latestInterview(latestInterview).toApplicationForm();
		assertEquals("redirect:rejectApplication?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}
	
	@Test
	public void shouldReturnRedirectToRejectionIfApprovalAndRejectedNextStatus() {
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(3).toApprovalRound();
		ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(4).toApprovalRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
		.applicationNumber("ABC")
		.id(1)
		.comments(
				new ApprovalEvaluationCommentBuilder().id(1).approvalRound(latestApprovalRound).nextStatus(ApplicationFormStatus.REJECTED)
				.toApprovalEvaluationComment(),
				new ApprovalEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REJECTED).approvalRound(latestApprovalRound)
				.toApprovalEvaluationComment()).status(ApplicationFormStatus.APPROVAL).approvalRounds(approvalRound, latestApprovalRound)
				.latestApprovalRound(latestApprovalRound).toApplicationForm();
		assertEquals("redirect:rejectApplication?applicationId=ABC", new StateTransitionViewResolver().resolveView(applicationForm));
	}
	
	@Test
	public void shouldReturnToApplicationsListIfApprovalAndApprovedNextStatus() {
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(3).toApprovalRound();
		ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(4).toApprovalRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
		.applicationNumber("ABC")
		.id(1)
		.comments(
				new ApprovalEvaluationCommentBuilder().id(1).approvalRound(latestApprovalRound).nextStatus(ApplicationFormStatus.APPROVED)
				.toApprovalEvaluationComment(),
				new ApprovalEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.APPROVED).approvalRound(latestApprovalRound)
				.toApprovalEvaluationComment()).status(ApplicationFormStatus.APPROVAL).approvalRounds(approvalRound, latestApprovalRound)
				.latestApprovalRound(latestApprovalRound).toApplicationForm();
		assertEquals("redirect:applications", new StateTransitionViewResolver().resolveView(applicationForm));
	}
	@Test
	public void shouldReturnToStateChangePageListIfApprovalAndEmptyComment() {
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(3).toApprovalRound();
		ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(4).toApprovalRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABC")
				.comments(new ApprovalEvaluationCommentBuilder().id(1).approvalRound(approvalRound).toApprovalEvaluationComment())
				.status(ApplicationFormStatus.APPROVAL).approvalRounds(approvalRound, latestApprovalRound).latestApprovalRound(latestApprovalRound)
				.toApplicationForm();
		assertEquals("private/staff/admin/state_transition", new StateTransitionViewResolver().resolveView(applicationForm));
	}
}
