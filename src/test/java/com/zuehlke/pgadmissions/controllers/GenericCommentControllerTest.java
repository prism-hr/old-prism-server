package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;

public class GenericCommentControllerTest {
    private ApplicationsService applicationsServiceMock;
    private UserService userServiceMock;
    private GenericCommentController controller;
    private GenericCommentValidator genericCommentValidatorMock;
    private CommentService commentServiceMock;
    private DocumentPropertyEditor documentPropertyEditorMock;

    @Test
    public void shouldGetApplicationFormFromId() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(false);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        ApplicationForm returnedApplication = controller.getApplicationForm("5");
        assertEquals(returnedApplication, applicationForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfCurrentUserApplicant() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
        EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");

    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfCurrentUserReferee() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");

    }

    @Test
    public void shouldNotThrowResourceNotFoundExceptionIfCurrentUserIsRefereeAndAdministrator() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(currentUser.getId()).andReturn(99).anyTimes();
        applicationForm.getProgram().getAdministrators().add(currentUser);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
        EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);

        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");
    }

    @Test
    public void shouldNotThrowResourceNotFoundExceptionIfCurrentUserIsRefereeAndSuperAdministrator() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        applicationForm.getProgram().getAdministrators().add(currentUser);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
        EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
        EasyMock.expect(currentUser.getId()).andReturn(99).anyTimes();

        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfCurrentUserCannotSeeApplication() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
        EasyMock.expect(currentUser.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(false);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
        EasyMock.replay(currentUser, userServiceMock);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");

    }

    @Test
    public void shouldReturnGenericCommentPage() {
        assertEquals("private/staff/admin/comment/genericcomment", controller.getGenericCommentPage());
    }

    @Test
    public void shouldReturnCurrentUser() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(8).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        assertEquals(currentUser, controller.getUser());
    }

    @Test
    public void shouldCreateNewCommentForApplicationForm() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        RegisteredUser currentUser = new RegisteredUserBuilder().id(8).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        controller = new GenericCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, genericCommentValidatorMock,
                documentPropertyEditorMock) {

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
        assertEquals("private/staff/admin/comment/genericcomment", controller.addComment(null, errorsMock));
    }

    @Test
    public void shouldSaveCommentAndRedirectBackToPageIfNoErrors() {
        Comment comment = new CommentBuilder().id(1).application(new ApplicationFormBuilder().id(6).applicationNumber("ABC").build()).build();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        commentServiceMock.save(comment);
        EasyMock.replay(errorsMock, commentServiceMock);
        assertEquals("redirect:/comment?applicationId=ABC", controller.addComment(comment, errorsMock));
    }

    @Before
    public void setUp() {

        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        genericCommentValidatorMock = EasyMock.createMock(GenericCommentValidator.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        controller = new GenericCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, genericCommentValidatorMock,
                documentPropertyEditorMock);

    }
}
