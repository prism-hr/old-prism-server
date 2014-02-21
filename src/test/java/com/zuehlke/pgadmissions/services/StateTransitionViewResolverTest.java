package com.zuehlke.pgadmissions.services;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class StateTransitionViewResolverTest {
    
    @TestedObject
    private StateTransitionViewResolver resolver;
    
    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceServiceMock;

    private ApplicationForm applicationForm;

//    @Test
//    public void shouldReturnRedirectToRejectViewIfProgrammeIsNotAvailableAnyMore() {
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder.getApplicationFormBuilder()
//                .applicationNumber("ABC").id(1).status(ApplicationFormStatus.VALIDATION)
//                .comments(new ValidationCommentBuilder().nextStatus(ApplicationFormStatus.REVIEW).build()).build();
//        applicationForm.getProgram().getInstances().get(0).setEnabled(false);
//        
//        EasyMockUnitils.reset();
//        EasyMock.expect(programInstanceServiceMock.isProgrammeStillAvailable(applicationForm)).andReturn(false);
//        EasyMockUnitils.replay();
//        
//        assertEquals("redirect:/rejectApplication?applicationId=ABC&rejectionId=7&rejectionIdForced=true", resolver.resolveView(applicationForm));
//    }
//    
//    @Test
//    public void shouldReturnRedirectToReviewIfValidationAndReviewNextStatus() {
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder.getApplicationFormBuilder()
//                .applicationNumber("ABC").id(1).status(ApplicationFormStatus.VALIDATION)
//                .comments(new ValidationCommentBuilder().nextStatus(ApplicationFormStatus.REVIEW).build()).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.REVIEW);
//        assertEquals("redirect:/review/moveToReview?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToIntervewIfValidationAndInterviewNextStatus() {
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder.getApplicationFormBuilder()
//        .applicationNumber("ABC").id(1).status(ApplicationFormStatus.VALIDATION)
//                .comments(new ValidationCommentBuilder().nextStatus(ApplicationFormStatus.INTERVIEW).build()).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.INTERVIEW);
//        assertEquals("redirect:/interview/moveToInterview?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToApprovalIfValidationAndApprovalNextStatus() {
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder.getApplicationFormBuilder()
//                .applicationNumber("ABC").id(1).status(ApplicationFormStatus.VALIDATION)
//                .comments(new ValidationCommentBuilder().nextStatus(ApplicationFormStatus.APPROVAL).build()).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.APPROVAL);
//        assertEquals("redirect:/approval/moveToApproval?action=firstLoad&applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToRejectionIfValidationAndRejectedNextStatus() {
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder.getApplicationFormBuilder()
//                .applicationNumber("ABC").id(1).status(ApplicationFormStatus.VALIDATION)
//                .comments(new ValidationCommentBuilder().nextStatus(ApplicationFormStatus.REJECTED).build()).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.REJECTED);
//        assertEquals("redirect:/rejectApplication?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToReviewIfReviewAndReviewNextStatus() {
//        ReviewRound previousReviewRound = new ReviewRoundBuilder().id(3).build();
//        ReviewRound latestReviewRound = new ReviewRoundBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .comments(
//                        new ReviewEvaluationCommentBuilder().id(1).reviewRound(previousReviewRound)
//                                .nextStatus(ApplicationFormStatus.INTERVIEW).build(),
//                        new ReviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REVIEW)
//                                .reviewRound(latestReviewRound).build()).status(ApplicationFormStatus.REVIEW)
//                .reviewRounds(previousReviewRound, latestReviewRound).latestReviewRound(latestReviewRound).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.REVIEW);
//        assertEquals("redirect:/review/moveToReview?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToIntervewIfReviewAndInterviewNextStatus() {
//        ReviewRound previousReviewRound = new ReviewRoundBuilder().id(3).build();
//        ReviewRound latestReviewRound = new ReviewRoundBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .comments(
//                        new ReviewEvaluationCommentBuilder().id(1).reviewRound(previousReviewRound)
//                                .nextStatus(ApplicationFormStatus.REVIEW).build(),
//                        new ReviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.INTERVIEW)
//                                .reviewRound(latestReviewRound).build()).status(ApplicationFormStatus.REVIEW)
//                .reviewRounds(previousReviewRound, latestReviewRound).latestReviewRound(latestReviewRound).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.INTERVIEW);
//        assertEquals("redirect:/interview/moveToInterview?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToApprovalIfVReviewnAndAppovalNextStatus() {
//        ReviewRound previousReviewRound = new ReviewRoundBuilder().id(3).build();
//        ReviewRound latestReviewRound = new ReviewRoundBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .comments(
//                        new ReviewEvaluationCommentBuilder().id(1).reviewRound(previousReviewRound)
//                                .nextStatus(ApplicationFormStatus.REVIEW).build(),
//                        new ReviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.APPROVAL)
//                                .reviewRound(latestReviewRound).build()).status(ApplicationFormStatus.REVIEW)
//                .reviewRounds(previousReviewRound, latestReviewRound).latestReviewRound(latestReviewRound).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.APPROVAL);
//        assertEquals("redirect:/approval/moveToApproval?action=firstLoad&applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToRejectionIfReviewAndRejectedNextStatus() {
//        ReviewRound previousReviewRound = new ReviewRoundBuilder().id(3).build();
//        ReviewRound latestReviewRound = new ReviewRoundBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .comments(
//                        new ReviewEvaluationCommentBuilder().id(1).reviewRound(previousReviewRound)
//                                .nextStatus(ApplicationFormStatus.REVIEW).build(),
//                        new ReviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REJECTED)
//                                .reviewRound(latestReviewRound).build()).status(ApplicationFormStatus.REVIEW)
//                .reviewRounds(previousReviewRound, latestReviewRound).latestReviewRound(latestReviewRound).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.REJECTED);
//        assertEquals("redirect:/rejectApplication?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToIntervewIfInterviewAndInterviewNextStatus() {
//        Interview previousInterview = new InterviewBuilder().id(3).build();
//        Interview latestInterview = new InterviewBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .comments(
//                        new InterviewEvaluationCommentBuilder().id(1).interview(previousInterview)
//                                .nextStatus(ApplicationFormStatus.REVIEW).build(),
//                        new InterviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.INTERVIEW)
//                                .interview(latestInterview).build()).status(ApplicationFormStatus.INTERVIEW)
//                .interviews(previousInterview, latestInterview).latestInterview(latestInterview).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.INTERVIEW);
//        assertEquals("redirect:/interview/moveToInterview?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToApprovalIfInterviewAndAppovalNextStatus() {
//        Interview previousInterview = new InterviewBuilder().id(3).build();
//        Interview latestInterview = new InterviewBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .comments(
//                        new InterviewEvaluationCommentBuilder().id(1).interview(previousInterview)
//                                .nextStatus(ApplicationFormStatus.REVIEW).build(),
//                        new InterviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.APPROVAL)
//                                .interview(latestInterview).build()).status(ApplicationFormStatus.INTERVIEW)
//                .interviews(previousInterview, latestInterview).latestInterview(latestInterview).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.APPROVAL);
//        assertEquals("redirect:/approval/moveToApproval?action=firstLoad&applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToRejectionIfInterviewAndRejectedNextStatus() {
//        Interview previousInterview = new InterviewBuilder().id(3).build();
//        Interview latestInterview = new InterviewBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .comments(
//                        new InterviewEvaluationCommentBuilder().id(1).interview(previousInterview)
//                                .nextStatus(ApplicationFormStatus.REVIEW).build(),
//                        new InterviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REJECTED)
//                                .interview(latestInterview).build()).status(ApplicationFormStatus.INTERVIEW)
//                .interviews(previousInterview, latestInterview).latestInterview(latestInterview).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.REJECTED);
//        assertEquals("redirect:/rejectApplication?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldReturnRedirectToRejectionIfApprovalAndRejectedNextStatus() {
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(3).build();
//        ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .comments(
//                        new ApprovalEvaluationCommentBuilder().id(1).approvalRound(latestApprovalRound)
//                                .nextStatus(ApplicationFormStatus.REJECTED).build(),
//                        new ApprovalEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REJECTED)
//                                .approvalRound(latestApprovalRound).build()).status(ApplicationFormStatus.APPROVAL)
//                .approvalRounds(approvalRound, latestApprovalRound).latestApprovalRound(latestApprovalRound).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.REJECTED);
//        assertEquals("redirect:/rejectApplication?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//
//    @Test
//    public void shouldMoveToConfirmRecommendationViewIfApprovalAndApprovedNextStatus() {
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(3).build();
//        ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .status(ApplicationFormStatus.APPROVAL)
//                .comments(
//                        new ApprovalEvaluationCommentBuilder().id(1).approvalRound(latestApprovalRound)
//                                .nextStatus(ApplicationFormStatus.APPROVED).build(),
//                        new ApprovalEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.APPROVED)
//                                .approvalRound(latestApprovalRound).build())
//                .approvalRounds(approvalRound, latestApprovalRound).latestApprovalRound(latestApprovalRound).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.APPROVED);
//        assertEquals("redirect:/offerRecommendation?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//    
//    @Test
//    public void shouldReturnToReviewTransitionViewIfApprovalAndReviewNextStatus() {
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(3).build();
//        ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .status(ApplicationFormStatus.APPROVAL)
//                .comments(
//                        new ApprovalEvaluationCommentBuilder().id(1).approvalRound(latestApprovalRound)
//                                .nextStatus(ApplicationFormStatus.REVIEW).build(),
//                        new ApprovalEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REVIEW)
//                                .approvalRound(latestApprovalRound).build())
//                .approvalRounds(approvalRound, latestApprovalRound).latestApprovalRound(latestApprovalRound).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.REVIEW);
//        assertEquals("redirect:/review/moveToReview?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//    
//    @Test
//    public void shouldReturnToInterviewTransitionViewIfApprovalAndInterviewNextStatus() {
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(3).build();
//        ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .id(1)
//                .status(ApplicationFormStatus.APPROVAL)
//                .comments(
//                        new ApprovalEvaluationCommentBuilder().id(1).approvalRound(latestApprovalRound)
//                                .nextStatus(ApplicationFormStatus.INTERVIEW).build(),
//                        new ApprovalEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.INTERVIEW)
//                                .approvalRound(latestApprovalRound).build())
//                .approvalRounds(approvalRound, latestApprovalRound).latestApprovalRound(latestApprovalRound).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.INTERVIEW);
//        assertEquals("redirect:/interview/moveToInterview?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
//    
//    @Test
//    public void shouldReturnRedirectToReviewIfInterviewAndReviewNextStatus() {
//        Interview previousInterview = new InterviewBuilder().id(3).build();
//        Interview latestInterview = new InterviewBuilder().id(4).build();
//        ValidApplicationFormBuilder validApplicationFormBuilder = new ValidApplicationFormBuilder();
//        validApplicationFormBuilder.build();
//        applicationForm = validApplicationFormBuilder
//                .getApplicationFormBuilder()
//                .applicationNumber("ABC")
//                .status(ApplicationFormStatus.INTERVIEW)
//                .id(1)
//                .comments(
//                        new InterviewEvaluationCommentBuilder().id(1).interview(previousInterview)
//                                .nextStatus(ApplicationFormStatus.REVIEW).build(),
//                        new InterviewEvaluationCommentBuilder().id(2).nextStatus(ApplicationFormStatus.REVIEW)
//                                .interview(latestInterview).build()).status(ApplicationFormStatus.REVIEW)
//                .interviews(previousInterview, latestInterview).latestInterview(latestInterview).build();
//        applicationForm.setNextStatus(ApplicationFormStatus.REVIEW);
//        assertEquals("redirect:/review/moveToReview?applicationId=ABC", resolver.resolveView(applicationForm));
//    }
    
    @Before
    public void setUp(){
        EasyMock.expect(programInstanceServiceMock.isProgrammeStillAvailable(applicationForm)).andReturn(true);
        EasyMockUnitils.replay();
    }
}
