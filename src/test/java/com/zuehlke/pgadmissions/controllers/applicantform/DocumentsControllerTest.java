package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.DocumentSectionValidator;

public class DocumentsControllerTest {

    private RegisteredUser currentUser;

    private ApplicationsService applicationsServiceMock;

    private DocumentSectionValidator documentSectionValidatorMock;

    private DocumentsController controller;

    private DocumentPropertyEditor documentPropertyEditorMock;

    private UserService userServiceMock;

    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    @Before
    public void setUp() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        documentSectionValidatorMock = EasyMock.createMock(DocumentSectionValidator.class);
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);
        controller = new DocumentsController(applicationsServiceMock, userServiceMock, documentSectionValidatorMock, documentPropertyEditorMock,
                applicationFormUserRoleServiceMock);
        currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);
    }

    @Test(expected = CannotUpdateApplicationException.class)
    public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).status(ApplicationFormStatus.APPROVED).build();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.replay(applicationsServiceMock, errors);
        controller.editDocuments(applicationForm, errors);
        EasyMock.verify(applicationsServiceMock);
    }

    @Test
    public void shouldReturnApplicationFormView() {
        assertEquals("/private/pgStudents/form/components/documents", controller.getDocumentsView());
    }

    @Test
    public void shouldReturnApplicationForm() {
        currentUser = EasyMock.createMock(RegisteredUser.class);

        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser);
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
        assertEquals(applicationForm, returnedApplicationForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("1");
    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(documentSectionValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldReturnMessage() {
        assertEquals("bob", controller.getMessage("bob"));
    }

    @Test
    public void shouldSaveAppplicationFormAndRedirectIfNoErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").personalStatement(new DocumentBuilder().build()).build();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(false);
        applicationsServiceMock.save(applicationForm);
        EasyMock.replay(applicationsServiceMock, errors);

        String view = controller.editDocuments(applicationForm, errors);

        EasyMock.verify(applicationsServiceMock);
        assertEquals("redirect:/update/getDocuments?applicationId=ABC", view);
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        errors.rejectValue("personalStatement", "file.upload.empty");
        EasyMock.expect(errors.hasErrors()).andReturn(true);
        EasyMock.replay(applicationsServiceMock, errors);

        String view = controller.editDocuments(applicationForm, errors);

        EasyMock.verify(applicationsServiceMock);
        assertEquals("/private/pgStudents/form/components/documents", view);
    }
}