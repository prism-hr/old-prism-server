package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

public class ReferenceControllerTest {

    private ApplicationsService applicationsServiceMock;
    private ReferenceController controller;
    private RegisteredUser currentUser;
    private DocumentPropertyEditor documentPropertyEditor;
    private FeedbackCommentValidator referenceValidator;
    private RefereeService refereeServiceMock;
    private CommentService commentServiceMock;
    private UserService userServiceMock;

    @Test
    public void shouldReturnApplicationForm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        Referee referee = new RefereeBuilder().toReferee();
        
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);

        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);
        
        assertEquals(applicationForm, returnedApplicationForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("1");
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowResourceNoFoundExceptionIfUserNotRefereeForForm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(false);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);

        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
        controller.getApplicationForm("1");
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);
    }

    @Test
    public void shouldReturnUploadReferencePage() {
        assertEquals("private/referees/upload_references", controller.getUploadReferencesPage());
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldReturnExpiredViewIfApplicationAlreadyDecided() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        EasyMock.replay(currentUser, userServiceMock, applicationsServiceMock);
        assertSame(applicationForm, controller.getApplicationForm("app1"));
        EasyMock.verify(currentUser, userServiceMock, applicationsServiceMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shoulThrowExceptionIfRefereeHasAlreadyProvidedReferecne() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).build();
        Referee referee = new RefereeBuilder().reference(new ReferenceComment()).toReferee();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
        
        EasyMock.replay(currentUser, userServiceMock, applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(currentUser, userServiceMock, applicationsServiceMock);
    }

    @Test
    public void shouldReturnNewReferenceComment() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        Referee referee = new RefereeBuilder().id(8).toReferee();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee).anyTimes();
        
        EasyMock.replay(currentUser, userServiceMock, applicationsServiceMock);
        ReferenceComment returnedReference = controller.getComment("1");
        EasyMock.verify(currentUser, userServiceMock, applicationsServiceMock);
        
        assertNull(returnedReference.getId());
        assertEquals(referee, returnedReference.getReferee());
        assertEquals(applicationForm, returnedReference.getApplication());
        assertEquals(currentUser, returnedReference.getUser());
        assertEquals(CommentType.REFERENCE, returnedReference.getType());
    }

    @Test
    public void shouldBindPRopertyEditorAndValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(referenceValidator);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        binderMock.registerCustomEditor(Document.class, documentPropertyEditor);
        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldReturnToFormViewIfValidationErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock);
        assertEquals("private/referees/upload_references", controller.handleReferenceSubmission(new ReferenceComment(), errorsMock));
    }

    @Test
    public void shouldSaveReferenceAndRedirectToSaveViewIfNoErrors() {
        Referee referee = new RefereeBuilder().id(1).toReferee();
        ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("12").build();
        ReferenceComment reference = new ReferenceCommentBuilder().application(application).referee(referee).id(4).build();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        commentServiceMock.save(reference);
        refereeServiceMock.saveReferenceAndSendMailNotifications(referee);
        EasyMock.replay(errorsMock, commentServiceMock, refereeServiceMock);
        assertEquals("redirect:/applications?messageCode=reference.uploaded&application=12", controller.handleReferenceSubmission(reference, errorsMock));
        EasyMock.verify(commentServiceMock, refereeServiceMock);
    }

    @Test
    public void shouldPreventFromSavingDuplicateReferences() {
        Referee referee = new RefereeBuilder().id(1).toReferee();
        ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("12").build();
        ReferenceComment reference = new ReferenceCommentBuilder().application(application).referee(referee).id(4).build();
        referee.setReference(reference);
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        EasyMock.replay(errorsMock, commentServiceMock, refereeServiceMock);
        assertEquals("redirect:/applications?messageCode=reference.uploaded&application=12", controller.handleReferenceSubmission(reference, errorsMock));
        EasyMock.verify(commentServiceMock, refereeServiceMock);
    }

    @Before
    public void setUp() {
        commentServiceMock = EasyMock.createMock(CommentService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        documentPropertyEditor = EasyMock.createMock(DocumentPropertyEditor.class);
        referenceValidator = EasyMock.createMock(FeedbackCommentValidator.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator,
                commentServiceMock);

        currentUser = EasyMock.createMock(RegisteredUser.class);
    }

}
