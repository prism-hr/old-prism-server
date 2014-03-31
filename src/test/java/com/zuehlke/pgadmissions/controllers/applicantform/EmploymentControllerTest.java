package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;

public class EmploymentControllerTest {

    private RegisteredUser currentUser;
    private EmploymentPositionService employmentServiceMock;
    private EmploymentPositionController controller;
    private LanguageService languageServiceMock;
    private ApplicationFormService applicationsServiceMock;
    private LanguagePropertyEditor languagePropertyEditorMock;
    private DatePropertyEditor datePropertyEditorMock;
    private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
    private EmploymentPositionValidator employmentValidatorMock;
    private UserService userServiceMock;
    private EncryptionHelper encryptionHelperMock;
    private WorkflowService applicationFormUserRoleServiceMock;
    private DomicileService domicileServiceMock;
    private DomicilePropertyEditor domicilePropertyEditorMock;
    private FullTextSearchService fullTextSearchServiceMock;

    @Test(expected = CannotUpdateApplicationException.class)
    public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
        EmploymentPosition employment = new EmploymentPositionBuilder().id(1)
                .application(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).build()).toEmploymentPosition();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.replay(employmentServiceMock, errors);
        controller.editEmployment(employment, errors);
        EasyMock.verify(employmentServiceMock);

    }

    @Test
    public void shouldReturnEmploymentView() {
        assertEquals("/private/pgStudents/form/components/employment_position_details", controller.getEmploymentView());
    }

    @Test
    public void shouldReturnAllEnabledLanguages() {
        List<Language> languageList = Arrays.asList(new LanguageBuilder().id(1).enabled(true).build(), new LanguageBuilder().id(2).enabled(false).build());
        EasyMock.expect(languageServiceMock.getAllEnabledLanguages()).andReturn(Collections.singletonList(languageList.get(0)));
        EasyMock.replay(languageServiceMock);
        List<Language> allLanguages = controller.getAllEnabledLanguages();
        assertEquals(1, allLanguages.size());
        assertEquals(languageList.get(0), allLanguages.get(0));
    }

    @Test
    public void shouldReturnApplicationForm() {
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser);
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
        assertEquals(applicationForm, returnedApplicationForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("1");
    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(employmentValidatorMock);
        binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
        binderMock.registerCustomEditor(Language.class, languagePropertyEditorMock);
        binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditorMock);
        binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldGetEmploymentFromServiceIfIdProvided() {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
        EmploymentPosition employment = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
        EasyMock.expect(employmentServiceMock.getById(1)).andReturn(employment);
        EasyMock.replay(employmentServiceMock, encryptionHelperMock);
        EmploymentPosition returnedEmploymentPosition = controller.getEmploymentPosition("bob");
        assertEquals(employment, returnedEmploymentPosition);
    }

    @Test
    public void shouldReturnNewEmploymentIfIdIsNull() {
        EmploymentPosition returnedEmploymentPosition = controller.getEmploymentPosition(null);
        assertNull(returnedEmploymentPosition.getId());
    }

    @Test
    public void shouldReturnNewEmploymentIfIdIsBlank() {
        EmploymentPosition returnedEmploymentPosition = controller.getEmploymentPosition("");
        assertNull(returnedEmploymentPosition.getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfEmploymentDoesNotExist() {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
        EasyMock.expect(employmentServiceMock.getById(1)).andReturn(null);
        EasyMock.replay(employmentServiceMock, encryptionHelperMock);
        controller.getEmploymentPosition("bob");

    }

    @Test
    public void shouldReturnMessage() {
        assertEquals("bob", controller.getMessage("bob"));

    }

    @Test
    public void shouldSaveEmploymentAndRedirectIfNoErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").build();
        EmploymentPosition employment = new EmploymentPositionBuilder().id(1).application(applicationForm).toEmploymentPosition();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(false);
        employmentServiceMock.save(employment);
        applicationsServiceMock.save(applicationForm);
        EasyMock.replay(employmentServiceMock, applicationsServiceMock, errors);
        String view = controller.editEmployment(employment, errors);
        EasyMock.verify(employmentServiceMock, applicationsServiceMock);
        assertEquals("redirect:/update/getEmploymentPosition?applicationId=ABC", view);
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfErrors() {
        EmploymentPosition employment = new EmploymentPositionBuilder().id(1).application(new ApplicationFormBuilder().id(5).build()).toEmploymentPosition();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(true);

        EasyMock.replay(employmentServiceMock, errors);
        String view = controller.editEmployment(employment, errors);
        EasyMock.verify(employmentServiceMock);
        assertEquals(EmploymentPositionController.STUDENTS_EMPLOYMENT_DETAILS_VIEW, view);
    }

    @Before
    public void setUp() {

        employmentServiceMock = EasyMock.createMock(EmploymentPositionService.class);
        languageServiceMock = EasyMock.createMock(LanguageService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationFormService.class);

        languagePropertyEditorMock = EasyMock.createMock(LanguagePropertyEditor.class);
        datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
        applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);

        employmentValidatorMock = EasyMock.createMock(EmploymentPositionValidator.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(WorkflowService.class);
        domicileServiceMock = EasyMock.createMock(DomicileService.class);
        domicilePropertyEditorMock = EasyMock.createMock(DomicilePropertyEditor.class);
        fullTextSearchServiceMock = EasyMock.createMock(FullTextSearchService.class);

        controller = new EmploymentPositionController(employmentServiceMock, languageServiceMock, applicationsServiceMock, languagePropertyEditorMock,
                datePropertyEditorMock, applicationFormPropertyEditorMock, employmentValidatorMock, userServiceMock, encryptionHelperMock,
                applicationFormUserRoleServiceMock, domicileServiceMock, domicilePropertyEditorMock, fullTextSearchServiceMock);

        currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);

    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}