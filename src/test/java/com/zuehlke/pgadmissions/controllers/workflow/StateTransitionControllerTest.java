package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
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

public class StateTransitionControllerTest {

    private StateTransitionController controller;
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private CommentFactory commentFactoryMock;
    private CommentService commentServiceMock;
    private StateTransitionService stateTransitionServiceMock;
    private EncryptionHelper encryptionHelperMock;
    private DocumentService documentServiceMock;
    private ApprovalService approvalServiceMock;
    private StateChangeValidator stateChangeValidatorMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;
    private ActionsProvider actionsProviderMock;

    @Test
    public void shouldRegisterValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(stateChangeValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor((Class<?>) EasyMock.isNull(), EasyMock.eq("comment"), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerBinders(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldGetApplicationFromIdForAdminUser() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMITTER)).andReturn(false);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test
    public void shouldGetApplicationFromIdForApproverUser() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
        EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
        EasyMock.expect(currentUserMock.isInRole(Authority.ADMITTER)).andReturn(false);
        EasyMock.expect(currentUserMock.isApplicationAdministrator(applicationForm)).andReturn(false);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test
    public void shouldGetApplicationFromIdForApplicationAdministrator() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
        EasyMock.expect(currentUserMock.isApplicationAdministrator(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionIfApplicatioNDoesNotExist() {
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationServiceMock);

        controller.getApplicationForm("5");
    }

    @Test
    public void shouldReturnCurrentUser() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(4).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        assertSame(currentUser, controller.getUser());
    }

    @Test
    public void shouldReturnAvaialableNextStatuses() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).status(ApplicationFormStatus.VALIDATION).build();
        controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, new StateTransitionService(),
                applicationFormUserRoleServiceMock, actionsProviderMock) {

            @Override
            public ApplicationForm getApplicationForm(String application) {
                return applicationForm;
            }
        };
        assertArrayEquals(new StateTransitionService().getAvailableNextStati(ApplicationFormStatus.VALIDATION).toArray(), controller.getAvailableNextStati("5")
                .toArray());
    }

    @Before
    public void setUp() {
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        stateChangeValidatorMock = EasyMock.createMock(StateChangeValidator.class);
        approvalServiceMock = EasyMock.createMock(ApprovalService.class);
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        commentFactoryMock = EasyMock.createMock(CommentFactory.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        stateTransitionServiceMock = EasyMock.createMock(StateTransitionService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        documentServiceMock = EasyMock.createMock(DocumentService.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);

        controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, applicationFormUserRoleServiceMock,
                actionsProviderMock);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
