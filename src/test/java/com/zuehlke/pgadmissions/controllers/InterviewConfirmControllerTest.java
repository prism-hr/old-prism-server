package com.zuehlke.pgadmissions.controllers;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.InterviewConfirmDTOValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class InterviewConfirmControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private InterviewService interviewServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @Mock
    @InjectIntoByType
    private ActionService actionsProviderMock;

    @Mock
    @InjectIntoByType
    private InterviewConfirmDTOValidator interviewConfirmDTOValidatorMock;

    @TestedObject
    private InterviewConfirmController controller;

//    @Test
//    public void shouldGetApplicationFormFromId() {
//        ApplicationForm applicationForm = new ApplicationForm();
//        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
//
//        EasyMock.replay(applicationsServiceMock);
//        ApplicationForm returnedApplication = controller.getApplicationForm("5");
//        EasyMock.verify(applicationsServiceMock);
//
//        assertEquals(returnedApplication, applicationForm);
//    }
//
//    @Test(expected = MissingApplicationFormException.class)
//    public void shouldThrowExceptionWhenApptlicationIsNull() {
//        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("app1")).andReturn(null);
//
//        EasyMock.replay(applicationsServiceMock);
//        controller.getApplicationForm("app1");
//        EasyMock.verify(applicationsServiceMock);
//    }
//
//    @Test
//    public void shouldSubmitInterviewConfirmation() {
//        Interview interview = new Interview();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).build();
//        RegisteredUser currentUser = new RegisteredUser();
//        InterviewConfirmDTO interviewConfirmDTO = new InterviewConfirmDTO();
//        interviewConfirmDTO.setTimeslotId(2);
//        BindingResult errors = new BeanPropertyBindingResult(interviewConfirmDTO, "interviewConfirmDTO");
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", applicationForm);
//        modelMap.put("user", currentUser);
//
//        interviewServiceMock.confirmInterview(currentUser, interview, interviewConfirmDTO);
//
//        EasyMock.replay(interviewServiceMock, applicationFormUserRoleServiceMock);
//        controller.submitInterviewConfirmation(interviewConfirmDTO, errors, modelMap);
//        EasyMock.verify(interviewServiceMock, applicationFormUserRoleServiceMock);
//
//    }
//
//    @Test
//    public void shouldRejectInterviewConfirmationIfErrors() {
//        InterviewConfirmDTO interviewConfirmDTO = new InterviewConfirmDTO();
//        BindingResult errors = new BeanPropertyBindingResult(interviewConfirmDTO, "interviewConfirmDTO");
//        errors.reject("error");
//
//        String result = controller.submitInterviewConfirmation(interviewConfirmDTO, errors, new ModelMap());
//        Assert.assertEquals("private/staff/interviewers/interview_confirm", result);
//    }

}
