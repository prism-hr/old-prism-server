package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;

public class GenericCommentControllerTest {
    private ApplicationFormService applicationsServiceMock;
    private UserService userServiceMock;
    private GenericCommentController controller;
    private GenericCommentValidator genericCommentValidatorMock;
    private CommentService commentServiceMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private ActionService actionsProviderMock;
    private WorkflowService applicationFormUserRoleServiceMock;

    @Test
    public void shouldGetApplicationFormFromId() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).advert(program).build();

        User currentUser = EasyMock.createMock(User.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        ApplicationForm returnedApplication = controller.getApplicationForm("5");
        assertEquals(returnedApplication, applicationForm);
    }

    @Test
    public void shouldReturnGenericCommentPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        User user = new User();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, ApplicationFormAction.COMMENT);

        EasyMock.replay(actionsProviderMock);
        assertEquals("private/staff/admin/comment/genericcomment", controller.getGenericCommentPage(modelMap));
        EasyMock.verify(actionsProviderMock);
    }

    @Test
    public void shouldReturnCurrentUser() {
        User currentUser = new UserBuilder().id(8).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        assertEquals(currentUser, controller.getUser());
    }

    @Test
    public void shouldCreateNewCommentForApplicationForm() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        User currentUser = new UserBuilder().id(8).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        controller = new GenericCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, genericCommentValidatorMock,
                documentPropertyEditorMock, actionsProviderMock, applicationFormUserRoleServiceMock) {

            @Override
            public ApplicationForm getApplicationForm(String id) {
                return applicationForm;
            }

        };
        Comment comment = controller.getComment("5");
        assertNull(comment.getId());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUser, comment.getUser());

    }

    @Test
    public void shouldRegisterValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(genericCommentValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor((Class<?>) EasyMock.isNull(), EasyMock.eq("comment"), EasyMock.isA(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerBinders(binderMock);
        EasyMock.verify(binderMock);

    }

    @Test
    public void shouldReturnToCommentsPageIfErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);

        EasyMock.replay(errorsMock);
        assertEquals("private/staff/admin/comment/genericcomment", controller.addComment(null, errorsMock, new ModelMap()));
        EasyMock.verify(errorsMock);
    }

    @Test
    public void shouldSaveCommentAndRedirectBackToPageIfNoErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(6).applicationNumber("ABC").build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);

        Comment comment = new CommentBuilder().id(1).build();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        commentServiceMock.save(comment);

        EasyMock.replay(errorsMock, commentServiceMock);
        assertEquals("redirect:/comment?applicationId=ABC", controller.addComment(comment, errorsMock, modelMap));
        EasyMock.verify(errorsMock, commentServiceMock);

    }

    @Before
    public void setUp() {
        applicationsServiceMock = EasyMock.createMock(ApplicationFormService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        genericCommentValidatorMock = EasyMock.createMock(GenericCommentValidator.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        actionsProviderMock = EasyMock.createMock(ActionService.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(WorkflowService.class);
        controller = new GenericCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, genericCommentValidatorMock,
                documentPropertyEditorMock, actionsProviderMock, applicationFormUserRoleServiceMock);

    }
}
