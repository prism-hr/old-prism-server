package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
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
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class EvaluationTransitionControllerTest {

    private EvaluationTransitionController controller;
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private CommentFactory commentFactoryMock;
    private CommentService commentServiceMock;
    private StateTransitionService stateTransitionViewServiceMock;
    private EncryptionHelper encryptionHelperMock;
    private DocumentService documentServiceMock;
    private ApprovalService approvalServiceMock;
    private StateChangeValidator stateChangeValidatorMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private BindingResult bindingResultMock;
    private ApplicationFormAccessService accessServiceMock;
    private ActionsProvider actionsProviderMock;
    private RegisteredUser currentUser = new RegisteredUser();

    @Test
    public void shouldCreateApprovalEvaluationCommentWithLatestReviewRound() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.REJECTED);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        ApprovalEvaluationComment comment = new ApprovalEvaluationCommentBuilder().id(6).build();
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(), stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);

        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, false);

        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);
        assertEquals("bob", view);
    }

    @Test
    public void shouldCreateApprovalEvaluationCommentWithLatestReviewRoundAndMoveToApprovedIdNextStageIsApproved() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).applicationNumber("abc").build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.APPROVED);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        ApprovalEvaluationComment comment = new ApprovalEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.APPROVED).id(6).build();
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(), stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(approvalServiceMock.moveToApproved(applicationForm)).andReturn(true);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        approvalServiceMock.sendToPortico(applicationForm);
        EasyMock.replay(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewServiceMock, userServiceMock);

        ModelMap modelMap = new ModelMap();
        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, modelMap, null, null, false);

        EasyMock.verify(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewServiceMock, userServiceMock);
        assertEquals("move.approved", modelMap.get("messageCode"));
        assertEquals("abc", modelMap.get("application"));
        assertEquals("bob", view);
    }

    @Test
    public void shouldCreateApprovalEvaluationCommentWithLatestReviewRoundAndNotMoveToApprovedIdNextStageIsRejected() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.REJECTED);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        ApprovalEvaluationComment comment = new ApprovalEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.REJECTED).id(6).build();
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(),stateComment.getType(), ApplicationFormStatus.REJECTED))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");

        EasyMock.replay(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewServiceMock, userServiceMock);

        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, false);
        assertEquals("bob", view);
        EasyMock.verify(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewServiceMock, userServiceMock);
    }

    @Test
    public void shouldCreateInterviewEvaluationCommentWithLatestInterview() {
        Interview interview = new InterviewBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestInterview(interview).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        InterviewEvaluationComment comment = new InterviewEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.INTERVIEW).id(6).build();
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(),stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);

        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, false);
        assertEquals("bob", view);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);

    }

    @Test
    public void shouldCreateReviewEvaluationCommentWithNextStageApproval() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.APPROVAL);
        stateComment.setType(CommentType.REVIEW_EVALUATION);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };

        ReviewEvaluationComment comment = new ReviewEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.APPROVAL).id(6).build();
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(),stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        applicationServiceMock.makeApplicationNotEditable(applicationForm);
        EasyMock.expectLastCall();
        applicationServiceMock.save(applicationForm);
        applicationServiceMock.refresh(applicationForm);

        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, applicationServiceMock, userServiceMock);
        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, false);
        EasyMock.verify(commentServiceMock, commentServiceMock, stateTransitionViewServiceMock, applicationServiceMock, userServiceMock);

        assertEquals("bob", view);
    }

    @Test
    public void shouldReturnToApplicationsViewIdDelegated() {
        Interview interview = new InterviewBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABC").latestInterview(interview).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        InterviewEvaluationComment comment = new InterviewEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.INTERVIEW).id(6).build();
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(),stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        ModelMap modelMap = new ModelMap();

        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);
        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, modelMap, true, null, false);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=ABC", view);
        assertTrue((Boolean) modelMap.get("delegate"));
    }

    @Test
    public void shouldCreateReviewEvaluationCommentWithLatestReviewRound() {
        ReviewRound reviewRound = new ReviewRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestReviewRound(reviewRound).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.REVIEW_EVALUATION);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        ReviewEvaluationComment comment = new ReviewEvaluationCommentBuilder().id(6).build();
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(),stateComment.getType(), stateComment.getNextStatus()))
                .andReturn(comment);
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);

        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, false);
        assertEquals("bob", view);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);

    }

    @Test
    public void shouldReturnViewIfErrors() {
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, bindingResultMock, applicationServiceMock);
        
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock, accessServiceMock, actionsProviderMock) {
            @Override
            @ModelAttribute("applicationForm")
            public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
                return new ApplicationForm();
            }
        };

        String view = controller.addComment(null, null, bindingResultMock, new ModelMap(), null, null, false);

        EasyMock.verify(commentServiceMock, applicationServiceMock);
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
                encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock,
                stateTransitionViewServiceMock, accessServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };
        EasyMock.expect(approvalServiceMock.moveToApproved(applicationForm)).andReturn(false);

        commentServiceMock.save(EasyMock.isA(Comment.class));

        EasyMock.replay(approvalServiceMock, commentServiceMock, userServiceMock);

        String resultView = controller.addComment(applicationForm, stateChangeComment, bindingResultMock, new ModelMap(), null, null, false);

        assertEquals("redirect:/rejectApplication?applicationId=" + applicationForm.getApplicationNumber() + "&rejectionId=7", resultView);
        EasyMock.verify(approvalServiceMock, commentServiceMock, userServiceMock);
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
        stateTransitionViewServiceMock = EasyMock.createMock(StateTransitionService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        documentServiceMock = EasyMock.createMock(DocumentService.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                accessServiceMock, actionsProviderMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
