package com.zuehlke.pgadmissions.controllers.workflow;

import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class StateTransitionControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private CommentService commentServiceMock;

    @Mock
    @InjectIntoByType
    private StateTransitionService stateTransitionServiceMock;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelperMock;

    @Mock
    @InjectIntoByType
    private DocumentService documentServiceMock;

    @Mock
    @InjectIntoByType
    private ApprovalService approvalServiceMock;

    @Mock
    @InjectIntoByType
    private StateChangeValidator stateChangeValidatorMock;

    @Mock
    @InjectIntoByType
    private DocumentPropertyEditor documentPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @Mock
    @InjectIntoByType
    private ActionService actionsProviderMock;

    @TestedObject
    private StateTransitionController controller;

    // @Test
    // public void shouldRegisterValidator() {
    // WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
    // binderMock.setValidator(stateChangeValidatorMock);
    // binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
    // binderMock.registerCustomEditor((Class<?>) EasyMock.isNull(), EasyMock.eq("comment"), EasyMock.anyObject(StringTrimmerEditor.class));
    // binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
    //
    // EasyMock.replay(binderMock);
    // controller.registerBinders(binderMock);
    // EasyMock.verify(binderMock);
    // }
    //
    // @Test
    // public void shouldGetApplicationFromIdForAdminUser() {
    // Program program = new ProgramBuilder().id(6).build();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
    // RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
    // EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
    // EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
    // EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
    // EasyMock.expect(currentUserMock.isInRole(Authority.ADMITTER)).andReturn(false);
    // EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
    // EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);
    //
    // ApplicationForm returnedForm = controller.getApplicationForm("5");
    // assertEquals(applicationForm, returnedForm);
    //
    // }
    //
    // @Test
    // public void shouldGetApplicationFromIdForApproverUser() {
    // Program program = new ProgramBuilder().id(6).build();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
    // RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
    // EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
    // EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
    // EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
    // EasyMock.expect(currentUserMock.isInRole(Authority.ADMITTER)).andReturn(false);
    // EasyMock.expect(currentUserMock.isApplicationAdministrator(applicationForm)).andReturn(false);
    // EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
    // EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);
    //
    // ApplicationForm returnedForm = controller.getApplicationForm("5");
    // assertEquals(applicationForm, returnedForm);
    //
    // }
    //
    // @Test
    // public void shouldGetApplicationFromIdForApplicationAdministrator() {
    // Program program = new ProgramBuilder().id(6).build();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
    // RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
    // EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
    // EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
    // EasyMock.expect(currentUserMock.isApplicationAdministrator(applicationForm)).andReturn(true);
    // EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
    // EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);
    //
    // ApplicationForm returnedForm = controller.getApplicationForm("5");
    // assertEquals(applicationForm, returnedForm);
    //
    // }
    //
    // @Test(expected = MissingApplicationFormException.class)
    // public void shouldThrowExceptionIfApplicatioNDoesNotExist() {
    // EasyMock.expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(null);
    // EasyMock.replay(applicationServiceMock);
    //
    // controller.getApplicationForm("5");
    // }
    //
    // @Test
    // public void shouldReturnCurrentUser() {
    // RegisteredUser currentUser = new RegisteredUserBuilder().id(4).build();
    //
    // EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
    // EasyMock.replay(userServiceMock);
    // assertSame(currentUser, controller.getCurrentUser());
    // }
    //
    // @Test
    // public void shouldReturnAvaialableNextStatuses() {
    // final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).status(new State().withId(ApplicationFormStatus.VALIDATION)).build();
    // final RegisteredUser registeredUser = new RegisteredUserBuilder().id(5).build();
    // userServiceMock.addRoleToUser(registeredUser, Authority.SUPERADMINISTRATOR);
    // controller = new StateTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
    // documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, new StateTransitionService(),
    // applicationFormUserRoleServiceMock, actionsProviderMock) {
    //
    // @Override
    // public ApplicationForm getApplicationForm(String application) {
    // return applicationForm;
    // }
    // };
    // assertArrayEquals(new StateTransitionService().getAvailableNextStati(ApplicationFormStatus.VALIDATION).toArray(),
    // controller.getAvailableNextStati(applicationForm, registeredUser).toArray());
    // }
}
