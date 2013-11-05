package com.zuehlke.pgadmissions.controllers.workflow;

import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;

import org.apache.commons.lang.BooleanUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class ValidationTransitionControllerTest {

    private ValidationTransitionController controller;
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
    private BindingResult bindingResultMock;
    private ApplicationFormAccessService accessServiceMock;
    private ActionsProvider actionsProviderMock;
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    private RegisteredUser currentUser;

    @Test
    public void shouldReturnAllValidationQuestionOptions() {
        assertArrayEquals(ValidationQuestionOptions.values(), controller.getValidationQuestionOptions());
    }

    @Test
    public void shouldReturnHomeOrOverseasOptions() {
        assertArrayEquals(HomeOrOverseas.values(), controller.getHomeOrOverseasOptions());
    }

    @Test
    public void shouldResolveViewForApplicationForm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(4).build();
        EasyMock.expect(stateTransitionServiceMock.resolveView(applicationForm, null)).andReturn("view");
        EasyMock.replay(stateTransitionServiceMock, userServiceMock);
        assertEquals("view", controller.getStateTransitionView(applicationForm, null, new ModelMap()));
        EasyMock.verify(stateTransitionServiceMock, userServiceMock);
    }

    @Test
    public void shouldCreateValidationCommentWithQuestionValuesIfNoValidationErrors() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().qualifiedForPhd(ValidationQuestionOptions.NO)
                .englishCompentencyOk(ValidationQuestionOptions.NO).homeOrOverseas(HomeOrOverseas.HOME).nextStatus(ApplicationFormStatus.APPROVAL)
                .comment("comment").type(CommentType.VALIDATION).id(6).fastTrackApplication(false).build();
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();

        commentServiceMock.save(comment);
        controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, accessServiceMock,
                actionsProviderMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        applicationServiceMock.save(applicationForm);
        applicationServiceMock.makeApplicationNotEditable(applicationForm);
        applicationFormUserRoleService.stateChanged(comment);
        
        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, applicationFormUserRoleService);
        String result = controller.addComment(applicationForm.getApplicationNumber(), null, comment, bindingResultMock, new ModelMap(), true,
                delegatedInterviewer);
        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock, applicationFormUserRoleService);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);
    }

    @Test
    public void shouldCreateCommentWithDocumentsAndSaveAndRedirectToResolvedView() {
        Program program = new Program();
        program.setId(1);

        Document documentOne = new DocumentBuilder().id(1).build();
        Document documentTwo = new DocumentBuilder().id(2).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).build();
        controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, accessServiceMock,
                actionsProviderMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }

        };
        ValidationComment comment = new ValidationCommentBuilder().comment("comment").type(CommentType.VALIDATION).documents(documentOne, documentTwo).id(6)
                .fastTrackApplication(false).build();
        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();
        commentServiceMock.save(comment);
        EasyMock.expect(stateTransitionServiceMock.resolveView(applicationForm)).andReturn("view");
        applicationFormUserRoleService.stateChanged(comment);

        EasyMock.replay(commentServiceMock, stateTransitionServiceMock, encryptionHelperMock, documentServiceMock, applicationFormUserRoleService);
        assertEquals("view",
                controller.addComment(applicationForm.getApplicationNumber(), null, comment, bindingResultMock, new ModelMap(), false, delegatedInterviewer));
        EasyMock.verify(commentServiceMock, stateTransitionServiceMock, encryptionHelperMock, documentServiceMock, applicationFormUserRoleService);

        assertEquals(2, comment.getDocuments().size());
        assertTrue(comment.getDocuments().containsAll(Arrays.asList(documentOne, documentTwo)));
    }

    @Test
    public void shouldNotifyRegistryIfHomeOrOverseasIsUnsureAndNextStateIsNotReject() throws ParseException {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().id(6).qualifiedForPhd(ValidationQuestionOptions.NO)
                .englishCompentencyOk(ValidationQuestionOptions.NO).homeOrOverseas(HomeOrOverseas.UNSURE).nextStatus(ApplicationFormStatus.REVIEW)
                .comment("comment").type(CommentType.VALIDATION).fastTrackApplication(false).build();

        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();

        commentServiceMock.save(comment);

        controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, accessServiceMock,
                actionsProviderMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        applicationServiceMock.save(applicationForm);
        EasyMock.expectLastCall().times(2);

        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        String result = controller.addComment(applicationForm.getApplicationNumber(), null, comment, bindingResultMock, new ModelMap(), true,
                delegatedInterviewer);

        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);

        assertNotNull(applicationForm.getAdminRequestedRegistry());
    }

    @Test
    public void shouldNotifyRegistryIfQualifiedForPhdIsUnsureAndNextStateIsNotReject() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().id(6).qualifiedForPhd(ValidationQuestionOptions.UNSURE)
                .englishCompentencyOk(ValidationQuestionOptions.NO).homeOrOverseas(HomeOrOverseas.HOME).nextStatus(ApplicationFormStatus.REVIEW)
                .comment("comment").type(CommentType.VALIDATION).fastTrackApplication(false).build();

        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();

        commentServiceMock.save(comment);

        controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, accessServiceMock,
                actionsProviderMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        applicationServiceMock.save(applicationForm);
        EasyMock.expectLastCall().times(2);

        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        String result = controller.addComment(applicationForm.getApplicationNumber(), null, comment, bindingResultMock, new ModelMap(), true,
                delegatedInterviewer);

        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);

        assertNotNull(applicationForm.getAdminRequestedRegistry());
    }

    @Test
    public void shouldNotifyRegistryIfEnglishCompentencyIsUnsureAndNextStateIsNotReject() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().id(6).qualifiedForPhd(ValidationQuestionOptions.YES)
                .englishCompentencyOk(ValidationQuestionOptions.UNSURE).homeOrOverseas(HomeOrOverseas.HOME).nextStatus(ApplicationFormStatus.REVIEW)
                .comment("comment").type(CommentType.VALIDATION).fastTrackApplication(false).build();

        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();

        commentServiceMock.save(comment);

        controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, accessServiceMock,
                actionsProviderMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        applicationServiceMock.save(applicationForm);
        EasyMock.expectLastCall().times(2);

        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        String result = controller.addComment(applicationForm.getApplicationNumber(), null, comment, bindingResultMock, new ModelMap(), true,
                delegatedInterviewer);

        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);

        assertNotNull(applicationForm.getAdminRequestedRegistry());
    }

    @Test
    public void shouldNotNotifyRegistryIfHomeOrOverseasIsUnsureAndNextStateIsReject() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().id(6).qualifiedForPhd(ValidationQuestionOptions.YES)
                .englishCompentencyOk(ValidationQuestionOptions.NO).homeOrOverseas(HomeOrOverseas.UNSURE).nextStatus(ApplicationFormStatus.REJECTED)
                .comment("comment").type(CommentType.VALIDATION).fastTrackApplication(false).build();

        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();

        commentServiceMock.save(comment);

        controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, accessServiceMock,
                actionsProviderMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        applicationServiceMock.save(applicationForm);

        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        String result = controller.addComment(applicationForm.getApplicationNumber(), null, comment, bindingResultMock, new ModelMap(), true,
                delegatedInterviewer);

        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);

        assertTrue(BooleanUtils.isFalse(applicationForm.isRegistryUsersDueNotification()));
    }

    @Test
    public void shouldNotNotifyRegistryIfQualifiedForPhdIsUnsureAndNextStateIsReject() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().id(6).qualifiedForPhd(ValidationQuestionOptions.UNSURE)
                .englishCompentencyOk(ValidationQuestionOptions.NO).homeOrOverseas(HomeOrOverseas.HOME).nextStatus(ApplicationFormStatus.REJECTED)
                .comment("comment").type(CommentType.VALIDATION).fastTrackApplication(false).build();

        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();

        commentServiceMock.save(comment);

        controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, accessServiceMock,
                actionsProviderMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        applicationServiceMock.save(applicationForm);

        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        String result = controller.addComment(applicationForm.getApplicationNumber(), null, comment, bindingResultMock, new ModelMap(), true,
                delegatedInterviewer);

        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);

        assertTrue(BooleanUtils.isFalse(applicationForm.isRegistryUsersDueNotification()));
    }

    @Test
    public void shouldNotNotifyRegistryIfEnglishCompentencyIsUnsureAndNextStateIsReject() {
        Program program = new ProgramBuilder().id(1).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("1").id(1).program(program).build();
        ValidationComment comment = new ValidationCommentBuilder().id(6).qualifiedForPhd(ValidationQuestionOptions.NO)
                .englishCompentencyOk(ValidationQuestionOptions.UNSURE).homeOrOverseas(HomeOrOverseas.HOME).nextStatus(ApplicationFormStatus.REJECTED)
                .comment("comment").type(CommentType.VALIDATION).fastTrackApplication(false).build();

        RegisteredUser delegatedInterviewer = new RegisteredUserBuilder().id(10).build();

        commentServiceMock.save(comment);

        controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, accessServiceMock,
                actionsProviderMock, null, applicationFormUserRoleService) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                return applicationForm;
            }
        };

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        applicationServiceMock.save(applicationForm);

        EasyMock.replay(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        String result = controller.addComment(applicationForm.getApplicationNumber(), null, comment, bindingResultMock, new ModelMap(), true,
                delegatedInterviewer);

        EasyMock.verify(userServiceMock, applicationServiceMock, commentServiceMock, bindingResultMock);

        assertEquals("redirect:/applications?messageCode=delegate.success&application=1", result);

        assertTrue(BooleanUtils.isFalse(applicationForm.isRegistryUsersDueNotification()));

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
        stateTransitionServiceMock = EasyMock.createMock(StateTransitionService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        documentServiceMock = EasyMock.createMock(DocumentService.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        applicationFormUserRoleService = createMock(ApplicationFormUserRoleService.class);

        controller = new ValidationTransitionController(applicationServiceMock, userServiceMock, commentServiceMock, commentFactoryMock, encryptionHelperMock,
                documentServiceMock, approvalServiceMock, stateChangeValidatorMock, documentPropertyEditorMock, stateTransitionServiceMock, accessServiceMock,
                actionsProviderMock, null, applicationFormUserRoleService);
        currentUser = new RegisteredUser();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
