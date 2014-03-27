package com.zuehlke.pgadmissions.controllers.applicantform;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdditionalInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.BooleanPropertyEditor;
import com.zuehlke.pgadmissions.services.AdditionalInfoService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AdditionalInformationValidator;

public class AdditionalInformationControllerTest {
    private RegisteredUser currentUser;
    private AdditionalInfoService addInfoServiceMock;
    private ApplicationFormService applicationServiceMock;
    private AdditionalInformationValidator validatorMock;
    private ApplicationFormPropertyEditor applFormPropertyEditorMock;
    private BooleanPropertyEditor booleanPropertyEditorMock;
    private AdditionalInformationController controller;
    private UserService userServiceMock;
    private WorkflowService applicationFormUserRoleServiceMock;

    @Before
    public void setUp() {
        addInfoServiceMock = EasyMock.createMock(AdditionalInfoService.class);
        applicationServiceMock = EasyMock.createMock(ApplicationFormService.class);
        applFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
        booleanPropertyEditorMock = EasyMock.createMock(BooleanPropertyEditor.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        validatorMock = EasyMock.createMock(AdditionalInformationValidator.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(WorkflowService.class);
        controller = new AdditionalInformationController(applicationServiceMock, userServiceMock, applFormPropertyEditorMock,//
                booleanPropertyEditorMock, addInfoServiceMock, validatorMock, applicationFormUserRoleServiceMock);

        currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test(expected = CannotUpdateApplicationException.class)
    public void throwExceptionWhenApplicationFormAlreadySubmitted() {
        ApplicationForm applForm = new ApplicationFormBuilder().id(1)//
                .status(ApplicationFormStatus.APPROVED)//
                .build();
        AdditionalInformation info = new AdditionalInformationBuilder().id(1)//
                .applicationForm(applForm).build();
        controller.editAdditionalInformation(info, null, applForm);
    }

    @Test
    public void shouldReturnApplicationFormViewWhenErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        AdditionalInformation info = new AdditionalInformationBuilder().id(1).applicationForm(applicationForm).build();

        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(true);

        EasyMock.replay(errors, applicationServiceMock, addInfoServiceMock);
        String viewID = controller.editAdditionalInformation(info, errors, applicationForm);
        EasyMock.verify(errors, applicationServiceMock, addInfoServiceMock);
        Assert.assertEquals("/private/pgStudents/form/components/additional_information", viewID);
    }

    @Test
    public void shouldReturnApplicationFormView() {
        Assert.assertEquals("/private/pgStudents/form/components/additional_information",
                controller.getAdditionalInformationView(new ApplicationForm(), new ExtendedModelMap()));
    }

    @Test
    public void shouldReturnMessage() {
        Assert.assertEquals("bob", controller.getMessage("bob"));
    }

    @Test
    public void shouldReturnErrorCode() {
        Assert.assertEquals("bob", controller.getErrorCode("bob"));
    }

    @Test
    public void shouldSaveAdditionalInfoAndRedirect() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").build();
        AdditionalInformation info = new AdditionalInformationBuilder().id(1).applicationForm(applicationForm).build();

        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(false);

        addInfoServiceMock.save(info);

        EasyMock.replay(errors, applicationServiceMock, addInfoServiceMock);
        String viewID = controller.editAdditionalInformation(info, errors, applicationForm);
        EasyMock.verify(errors, applicationServiceMock, addInfoServiceMock);
        Assert.assertEquals("redirect:/update/getAdditionalInformation?applicationId=ABC", viewID);
    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(validatorMock);
        binderMock.registerCustomEditor(ApplicationForm.class, applFormPropertyEditorMock);
        binderMock.registerCustomEditor(Boolean.class, booleanPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerValidatorsEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldReturnApplicationForm() {
        currentUser = EasyMock.createMock(RegisteredUser.class);

        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("100")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUser);
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("100");
        Assert.assertEquals(applicationForm, returnedApplicationForm);
        EasyMock.verify(applicationServiceMock);
    }

}