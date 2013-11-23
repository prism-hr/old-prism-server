package com.zuehlke.pgadmissions.controllers.workflow;

import static org.easymock.EasyMock.createMock;
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
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
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
    private ActionsProvider actionsProviderMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    private RegisteredUser currentUser = new RegisteredUser();

    @Test
    public void shouldCreateApprovalEvaluationCommentWithLatestReviewRound() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).status(ApplicationFormStatus.APPROVAL).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.REJECTED);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        stateComment.setFastTrackApplication(false);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        controller.postStateChangeComment(applicationForm, currentUser, stateComment, null, false);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");

        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, applicationFormUserRoleServiceMock);
        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, null);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, applicationFormUserRoleServiceMock);

        assertEquals("bob", view);
    }

    @Test
    public void shouldCreateApprovalEvaluationCommentWithLatestReviewRoundAndNotMoveToApprovedIdNextStageIsRejected() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestApprovalRound(approvalRound).status(ApplicationFormStatus.APPROVAL).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.REJECTED);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        stateComment.setFastTrackApplication(false);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        controller.postStateChangeComment(applicationForm, currentUser, stateComment, null, false);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        EasyMock.replay(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewServiceMock, userServiceMock,
                applicationFormUserRoleServiceMock);
        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, null);
        EasyMock.verify(commentFactoryMock, commentServiceMock, approvalServiceMock, stateTransitionViewServiceMock, userServiceMock,
                applicationFormUserRoleServiceMock);

        assertEquals("bob", view);
    }

    @Test
    public void shouldCreateInterviewEvaluationCommentWithLatestInterview() {
        Interview interview = new InterviewBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestInterview(interview).status(ApplicationFormStatus.APPROVAL).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        stateComment.setFastTrackApplication(false);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        controller.postStateChangeComment(applicationForm, currentUser, stateComment, null, false);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);

        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, null);
        assertEquals("bob", view);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);

    }

    @Test
    public void shouldCreateReviewEvaluationCommentWithNextStageApproval() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVAL).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.APPROVAL);
        stateComment.setType(CommentType.REVIEW_EVALUATION);
        stateComment.setFastTrackApplication(false);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };

        ReviewEvaluationComment comment = new ReviewEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.APPROVAL).id(6).build();
        EasyMock.expect(
                commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(), stateComment.getType(),
                        stateComment.getNextStatus(), null)).andReturn(comment);
        controller.postStateChangeComment(applicationForm, currentUser, stateComment, null, false);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, applicationServiceMock, userServiceMock,
                applicationFormUserRoleServiceMock);
        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, null);
        EasyMock.verify(commentServiceMock, commentServiceMock, stateTransitionViewServiceMock, applicationServiceMock, userServiceMock,
                applicationFormUserRoleServiceMock);

        assertEquals("bob", view);
    }

    @Test
    public void shouldReturnToApplicationsViewIfDelegated() {
        Interview interview = new InterviewBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABC").status(ApplicationFormStatus.APPROVAL).latestInterview(interview).build();
        final RegisteredUser delegateUser = new RegisteredUserBuilder().id(1).firstName("test").lastName("test").email("test@test.com").build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        stateComment.setFastTrackApplication(false);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("test@test.com")).andReturn(delegateUser).anyTimes();
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);
        
        controller.postStateChangeComment(applicationForm, currentUser, stateComment, delegateUser, true);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(delegateUser.getEmail())).andReturn(delegateUser);
        ModelMap modelMap = new ModelMap();

        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);
        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, modelMap, null, true, null);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=ABC", view);
        assertTrue((Boolean) modelMap.get("delegate"));
    }

    @Test
    public void shouldCreateReviewEvaluationCommentWithLatestReviewRound() {
        ReviewRound reviewRound = new ReviewRoundBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestReviewRound(reviewRound).status(ApplicationFormStatus.REVIEW).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.REVIEW_EVALUATION);
        stateComment.setFastTrackApplication(false);
        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        controller.postStateChangeComment(applicationForm, currentUser, stateComment, null, false);
        EasyMock.expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");    
        EasyMock.replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, applicationFormUserRoleServiceMock);
        controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, null);
        EasyMock.verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, applicationFormUserRoleServiceMock);
    }

    @Test
    public void shouldReturnViewIfErrors() {
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);

        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            @ModelAttribute("applicationForm")
            public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
                return new ApplicationForm();
            }
        };

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).build();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setType(CommentType.REVIEW_EVALUATION);
        stateComment.setFastTrackApplication(false);

        actionsProviderMock.validateAction(applicationForm, null, ApplicationFormAction.COMPLETE_REVIEW_STAGE);
        
        EasyMock.replay(commentServiceMock, stateTransitionViewServiceMock, bindingResultMock, applicationServiceMock, actionsProviderMock);
        String view = controller.addComment(applicationForm, stateComment, bindingResultMock, new ModelMap(), null, null, null);

        EasyMock.verify(commentServiceMock, stateTransitionViewServiceMock, bindingResultMock, applicationServiceMock, actionsProviderMock);
        assertEquals("private/staff/admin/state_transition", view);
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
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);

        controller = new EvaluationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionViewServiceMock,
                applicationFormUserRoleServiceMock, actionsProviderMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
