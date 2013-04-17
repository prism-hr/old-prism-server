package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.StateTransitionViewResolver;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class EvaluationTransitionControllerTest {

    private EvaluationTransitionController controller;
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private CommentFactory commentFactoryMock;
    private CommentService commentServiceMock;
    private StateTransitionViewResolver stateTransitionViewResolverMock;
    private EncryptionHelper encryptionHelperMock;
    private DocumentService documentServiceMock;
    private ApprovalService approvalServiceMock;
    private StateChangeValidator stateChangeValidatorMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private BindingResult bindingResultMock;

    @Test
    public void shouldCreateApprovalEvaluationCommentWithLatestReviewRound() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).build();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setNextStatus(ApplicationFormStatus.REJECTED);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        ApprovalEvaluationComment comment = new ApprovalEvaluationCommentBuilder().id(6).build();
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock,
                documentPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("bob");
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock);

        String view = controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, new ModelMap(), null, null);

        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock);
        assertEquals(approvalRound, comment.getApprovalRound());
        assertEquals("bob", view);
    }

    @Test
    public void shouldCreateApprovalEvaluationCommentWithLatestReviewRoundAndMoveToApprovedIdNextStageIsApproved() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).applicationNumber("abc").build();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setNextStatus(ApplicationFormStatus.APPROVED);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        ApprovalEvaluationComment comment = new ApprovalEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.APPROVED).id(6).build();
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock,
                documentPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(approvalServiceMock.moveToApproved(applicationForm)).andReturn(true);
        EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("bob");
        approvalServiceMock.sendToPortico(applicationForm);
        EasyMock.replay(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewResolverMock);

        ModelMap modelMap = new ModelMap();
        String view = controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, modelMap, null, null);

        EasyMock.verify(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewResolverMock);
        assertEquals(approvalRound, comment.getApprovalRound());
        assertEquals("move.approved", modelMap.get("messageCode"));
        assertEquals("abc", modelMap.get("application"));
        assertEquals("bob", view);
    }

    @Test
    public void shouldCreateApprovalEvaluationCommentWithLatestReviewRoundAndNotMoveToApprovedIdNextStageIsRejected() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).build();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setNextStatus(ApplicationFormStatus.REJECTED);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        ApprovalEvaluationComment comment = new ApprovalEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.REJECTED).id(6).build();
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock,
                documentPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), ApplicationFormStatus.REJECTED))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("bob");

        EasyMock.replay(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewResolverMock);

        String view = controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, new ModelMap(), null, null);
        assertEquals("bob", view);
        EasyMock.verify(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewResolverMock);
        assertEquals(approvalRound, comment.getApprovalRound());
    }

    @Test
    public void shouldCreateInterviewEvaluationCommentWithLatestInterview() {
        Interview interview = new InterviewBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestInterview(interview).build();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock,
                documentPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        InterviewEvaluationComment comment = new InterviewEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.INTERVIEW).id(6).build();
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("bob");
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock);

        String view = controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, new ModelMap(), null, null);
        assertEquals("bob", view);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock);
        assertEquals(interview, comment.getInterview());

    }

    @Test
    public void shouldCreateReviewEvaluationCommentWithNextStageApproval() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setNextStatus(ApplicationFormStatus.APPROVAL);
        stateComment.setType(CommentType.REVIEW_EVALUATION);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock,
                documentPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };

        ReviewEvaluationComment comment = new ReviewEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.APPROVAL).id(6).build();
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("bob");
        applicationServiceMock.makeApplicationNotEditable(applicationForm);
        EasyMock.expectLastCall();
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock, applicationServiceMock);

        String view = controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, new ModelMap(), null, null);
        assertEquals("bob", view);
        EasyMock.verify(commentServiceMock, commentServiceMock, stateTransitionViewResolverMock, applicationServiceMock);
    }

    @Test
    public void shouldReturnToApplicationsViewIdDelegated() {
        Interview interview = new InterviewBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABC").latestInterview(interview).build();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock,
                documentPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        InterviewEvaluationComment comment = new InterviewEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.INTERVIEW).id(6).build();
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        ModelMap modelMap = new ModelMap();

        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock);
        String view = controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, modelMap, true, null);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=ABC", view);
        assertEquals(interview, comment.getInterview());
        assertTrue((Boolean)modelMap.get("delegate"));
    }

    @Test
    public void shouldCreateReviewEvaluationCommentWithLatestReviewRound() {
        ReviewRound reviewRound = new ReviewRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestReviewRound(reviewRound).build();

        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.REVIEW_EVALUATION);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock,
                documentPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        ReviewEvaluationComment comment = new ReviewEvaluationCommentBuilder().id(6).build();
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, null, stateComment.getComment(), stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewResolverMock.resolveView(applicationForm)).andReturn("bob");
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock);

        String view = controller.addComment(applicationForm.getApplicationNumber(), stateComment, bindingResultMock, new ModelMap(), null, null);
        assertEquals("bob", view);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock);
        assertEquals(reviewRound, comment.getReviewRound());

    }

    @Test
    public void shouldReturnViewIfErrors() {
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewResolverMock, bindingResultMock);

        String view = controller.addComment(null, null, bindingResultMock, new ModelMap(), null, null);

        EasyMock.verify(commentServiceMock);
        assertEquals("private/staff/admin/state_transition", view);
    }

    @Test
    public void shouldCreateGenericCommentIfPreferredStartDateIsNotInBoundsForApproval() {
        StateChangeComment stateChangeComment = new StateChangeComment();
        stateChangeComment.setComment("comment");
        stateChangeComment.setNextStatus(ApplicationFormStatus.APPROVED);
        stateChangeComment.setType(CommentType.APPROVAL_EVALUATION);
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABCD-EFG").build();

        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, new CommentFactory(),
                stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock,
                documentPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(approvalServiceMock.moveToApproved(applicationForm)).andReturn(false);

        commentServiceMock.save(EasyMock.isA(Comment.class));

        EasyMock.replay(approvalServiceMock, commentServiceMock);

        String resultView = controller.addComment(applicationForm.getApplicationNumber(), stateChangeComment, bindingResultMock, new ModelMap(), null, null);

        assertEquals("redirect:/rejectApplication?applicationId=" + applicationForm.getApplicationNumber() + "&rejectionId=7", resultView);
        EasyMock.verify(approvalServiceMock, commentServiceMock);
    }

    @Before
    public void setUp() {
        bindingResultMock = EasyMock.createMock(BindingResult.class);
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        stateChangeValidatorMock = EasyMock.createMock(StateChangeValidator.class);
        approvalServiceMock = EasyMock.createMock(ApprovalService.class);
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        commentFactoryMock = EasyMock.createMock(CommentFactory.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        stateTransitionViewResolverMock = EasyMock.createMock(StateTransitionViewResolver.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        documentServiceMock = EasyMock.createMock(DocumentService.class);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                stateTransitionViewResolverMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock,
                documentPropertyEditorMock);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
