package com.zuehlke.pgadmissions.controllers.workflow;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
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
        String view = controller.addComment(null, "", null, null, bindingResultMock);
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