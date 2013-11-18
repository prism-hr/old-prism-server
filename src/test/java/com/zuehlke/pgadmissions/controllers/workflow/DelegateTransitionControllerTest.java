package com.zuehlke.pgadmissions.controllers.workflow;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.After;
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
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
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

public class DelegateTransitionControllerTest {

    private DelegateTransitionController controller;
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
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;
    private ActionsProvider actionsProviderMock;

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
        controller = new DelegateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock,
                stateTransitionViewServiceMock, applicationFormUserRoleServiceMock, actionsProviderMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        InterviewEvaluationComment comment = new InterviewEvaluationCommentBuilder().nextStatus(ApplicationFormStatus.INTERVIEW).id(6).build();
        expect(
                commentFactoryMock.createComment(applicationForm, currentUser, stateComment.getComment(), stateComment.getDocuments(), stateComment.getType(),
                        stateComment.getNextStatus(), currentUser)).andReturn(comment);
        commentServiceMock.save(comment);
        expect(stateTransitionViewServiceMock.resolveView(applicationForm)).andReturn("bob");
        applicationFormUserRoleServiceMock.registerApplicationUpdate(applicationForm, currentUser, ApplicationUpdateScope.INTERNAL);

        replay(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, applicationFormUserRoleServiceMock);
        String view = controller.addComment(applicationForm.getApplicationNumber(), stateComment.getComment(), stateComment, bindingResultMock);
        verify(commentFactoryMock, commentServiceMock, stateTransitionViewServiceMock, userServiceMock, applicationFormUserRoleServiceMock);

        assertEquals("bob", view);
    }

    @Test
    public void shouldReturnViewIfErrors() {
        expect(bindingResultMock.hasErrors()).andReturn(true);

        controller = new DelegateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock,
                stateTransitionViewServiceMock, applicationFormUserRoleServiceMock, actionsProviderMock) {
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
        applicationFormUserRoleServiceMock = createMock(ApplicationFormUserRoleService.class);
        actionsProviderMock = createMock(ActionsProvider.class);

        controller = new DelegateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock,
                encryptionHelperMock, documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock,
                stateTransitionViewServiceMock, applicationFormUserRoleServiceMock, actionsProviderMock);
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}