package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewConfirmDTOValidator;

public class InterviewConfirmControllerTest {

    private ApplicationsService applicationsServiceMock;

    private InterviewService interviewServiceMock;

    private UserService userServiceMock;

    private InterviewConfirmController controller;

    private ApplicationFormAccessService accessServiceMock;

    private ActionsProvider actionsProviderMock;

    private InterviewConfirmDTOValidator interviewConfirmDTOValidatorMock;

    @Test
    public void shouldGetApplicationFormFromId() {
        ApplicationForm applicationForm = new ApplicationForm();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);

        EasyMock.replay(applicationsServiceMock);
        ApplicationForm returnedApplication = controller.getApplicationForm("5");
        EasyMock.verify(applicationsServiceMock);

        assertEquals(returnedApplication, applicationForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionWhenApptlicationIsNull() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(null);

        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(applicationsServiceMock);
    }

    @Test
    public void shouldSubmitInterviewConfirmation() {
        Interview interview = new Interview();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).build();
        RegisteredUser currentUser = new RegisteredUser();
        InterviewConfirmDTO interviewConfirmDTO = new InterviewConfirmDTO();
        interviewConfirmDTO.setTimeslotId(2);
        BindingResult errors = new BeanPropertyBindingResult(interviewConfirmDTO, "interviewConfirmDTO");
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", currentUser);

        interviewServiceMock.confirmInterview(currentUser, interview, interviewConfirmDTO);
        accessServiceMock.updateAccessTimestamp(eq(applicationForm), eq(currentUser), isA(Date.class));

        EasyMock.replay(interviewServiceMock, accessServiceMock);
        controller.submitInterviewConfirmation(interviewConfirmDTO, errors, modelMap);
        EasyMock.verify(interviewServiceMock, accessServiceMock);

    }

    @Test
    public void shouldRejectInterviewConfirmationIfErrors() {
        InterviewConfirmDTO interviewConfirmDTO = new InterviewConfirmDTO();
        BindingResult errors = new BeanPropertyBindingResult(interviewConfirmDTO, "interviewConfirmDTO");
        errors.reject("error");

        String result = controller.submitInterviewConfirmation(interviewConfirmDTO, errors, new ModelMap());
        Assert.assertEquals("private/staff/interviewers/interview_confirm", result);
    }

    @Before
    public void prepare() {
        interviewServiceMock = EasyMock.createMock(InterviewService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        interviewConfirmDTOValidatorMock = EasyMock.createMock(InterviewConfirmDTOValidator.class);

        controller = new InterviewConfirmController(applicationsServiceMock, userServiceMock, interviewServiceMock, accessServiceMock, actionsProviderMock,
                interviewConfirmDTOValidatorMock);
    }

}
