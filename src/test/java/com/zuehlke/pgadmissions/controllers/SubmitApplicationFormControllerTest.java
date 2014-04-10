package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.SubmitApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class SubmitApplicationFormControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormValidator applicationFormValidatorMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private ActionService actionsProviderMock;
    
    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;
    
    @Mock
    @InjectIntoByType
    private ProgramService programsService;

    @Mock
    @InjectIntoByType
    private SubmitApplicationFormService submitApplicationFormServiceMock;

    @TestedObject
    private SubmitApplicationFormController controller;

    @Test
    public void shouldSubmitApplicationForm() throws UnknownHostException {
        User applicant = new UserBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(applicant).applicationNumber("abc").build();
        BindingResult bindingResult = new BeanPropertyBindingResult(applicationForm, "applicationForm");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("localhost");

        expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        actionsProviderMock.validateAction(applicationForm, applicant, ApplicationFormAction.COMPLETE_APPLICATION);
        submitApplicationFormServiceMock.submitApplication(applicationForm);

        replay();
        controller.submitApplication(applicationForm, bindingResult, request);

    }

    @Test
    public void shouldRegisterValidator() {
        WebDataBinder binderMock = createMock(WebDataBinder.class);
        binderMock.setValidator(applicationFormValidatorMock);
        replay(binderMock);
        controller.registerValidator(binderMock);
        verify(binderMock);
    }

    @Test
    public void shouldGetApplicationFormFromService() {
        ApplicationForm applicationForm = new ApplicationForm();
        expect(applicationsServiceMock.getByApplicationNumber("2")).andReturn(applicationForm);
        
        replay();
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("2");
        
        assertEquals(applicationForm, returnedApplicationForm);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfSubmittedApplicationFormDoesNotExist() {
        expect(applicationsServiceMock.getByApplicationNumber("2")).andReturn(null);
        
        replay();
        controller.getApplicationForm("2");
    }

}
