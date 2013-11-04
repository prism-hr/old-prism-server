package com.zuehlke.pgadmissions.controllers.workflow;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.StateChangeSuggestionComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeSuggestionCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class InterviewDelegateTransitionControllerTest {

    private InterviewDelegateTransitionController controller;
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
    private InterviewService interviewServiceMock;
    private ApplicationFormUserRoleService applicationFormUserRoleService;
    
    private RegisteredUser currentUser = new RegisteredUser();

    @Test
    public void shouldCreateInterviewEvaluationCommentWithLatestInterview() {
        Interview interview = new InterviewBuilder().id(5).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).latestInterview(interview).status(ApplicationFormStatus.INTERVIEW).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.INTERVIEW);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        stateComment.setFastTrackApplication(false);
        controller = new InterviewDelegateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                        encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock,
                        stateTransitionViewServiceMock, accessServiceMock, actionsProviderMock, interviewServiceMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        InterviewEvaluationComment comment = new InterviewEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.INTERVIEW).id(6).build();
        expect(commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(),
                        stateComment.getType(), stateComment.getNextStatus())).andReturn(comment);
        commentServiceMock.save(comment);
        expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        applicationFormUserRoleService.processingDelegated(applicationForm);

        replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, applicationFormUserRoleService);
        String view = controller.addComment(applicationForm.getApplicationNumber(), stateComment.getComment(), stateComment, bindingResultMock);
        verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, applicationFormUserRoleService);

        assertEquals("bob", view);
    }

    @Test
    public void shouldCreateStateChangeSuggestionCommentIfNextStatusRejected() {
        Interview interview = new InterviewBuilder().id(5).stage(InterviewStage.SCHEDULED).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("app1").latestInterview(interview).dueDate(null).build();
        List<Document> documents = Collections.emptyList();
        StateChangeComment stateComment = new StateChangeComment();
        stateComment.setComment("comment");
        stateComment.setDocuments(documents);
        stateComment.setNextStatus(ApplicationFormStatus.REJECTED);
        stateComment.setType(CommentType.APPROVAL_EVALUATION);
        stateComment.setFastTrackApplication(false);
        controller = new InterviewDelegateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                        encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock,
                        stateTransitionViewServiceMock, accessServiceMock, actionsProviderMock, interviewServiceMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        StateChangeSuggestionComment comment = new StateChangeSuggestionCommentBuilder().nextStatus(ApplicationFormStatus.REJECTED).id(6).build();
        expect(commentFactoryMock.createStateChangeSuggestionComment(currentUser, applicationForm, stateComment.getComment(),
                        stateComment.getNextStatus())).andReturn(comment);
        commentServiceMock.save(comment);
        interviewServiceMock.save(interview);
        applicationFormUserRoleService.processingDelegated(applicationForm);

        replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, interviewServiceMock, applicationFormUserRoleService);
        String view = controller.addComment(applicationForm.getApplicationNumber(), "", stateComment, bindingResultMock);
        verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, interviewServiceMock, applicationFormUserRoleService);

        assertEquals("redirect:/applications?messageCode=state.change.suggestion&application=app1", view);
        assertEquals(InterviewStage.INACTIVE, interview.getStage());
        Assert.assertNotNull(applicationForm.getDueDate());
    }

    @Test
    public void shouldReturnViewIfErrors() {
        expect(bindingResultMock.hasErrors()).andReturn(true);

        controller = new InterviewDelegateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                        encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock,
                        stateTransitionViewServiceMock, accessServiceMock, actionsProviderMock, interviewServiceMock, null, applicationFormUserRoleService) {
            public ApplicationForm getApplicationForm(String applicationId) {
                return new ApplicationForm();
            }
        };

        replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, bindingResultMock, applicationServiceMock);
        String view = controller.addComment(null, "", null, bindingResultMock);
        verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, bindingResultMock, applicationServiceMock);

        assertEquals("private/staff/admin/state_transition", view);
    }

    @Before
    public void setUp() {
        bindingResultMock = createMock(BindingResult.class);
        documentPropertyEditorMock = createMock(DocumentPropertyEditor.class);
        stateChangeValidatorMock = createMock(StateChangeValidator.class);
        approvalServiceMock = createMock(ApprovalService.class);
        applicationServiceMock = createMock(ApplicationsService.class);
        userServiceMock = createMock(UserService.class);
        commentFactoryMock = createMock(CommentFactory.class);
        commentServiceMock = createMock(CommentService.class);
        stateTransitionViewServiceMock = createMock(StateTransitionService.class);
        encryptionHelperMock = createMock(EncryptionHelper.class);
        documentServiceMock = createMock(DocumentService.class);
        accessServiceMock = createMock(ApplicationFormAccessService.class);
        actionsProviderMock = createMock(ActionsProvider.class);
        interviewServiceMock = createMock(InterviewService.class);
        applicationFormUserRoleService = createMock(ApplicationFormUserRoleService.class);
        
        controller = new InterviewDelegateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                        encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock,
                        stateTransitionViewServiceMock, accessServiceMock, actionsProviderMock, interviewServiceMock, null, applicationFormUserRoleService);
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
