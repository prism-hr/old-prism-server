package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
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

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionWhenApptlicationIsNull() {
        RegisteredUser user = new RegisteredUser();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(null);

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(userServiceMock, applicationsServiceMock);
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionWhenUserNotInterviewer() {
        Interview interview = new Interview();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).build();
        RegisteredUser user = new RegisteredUser();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(userServiceMock, applicationsServiceMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldThrowExceptionWhenApplicationAlreadyScheduled() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        Interviewer interviewer = new InterviewerBuilder().user(user).build();
        Interview interview = new InterviewBuilder().interviewers(interviewer).stage(InterviewStage.SCHEDULED).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).applicationAdministrator(user).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(userServiceMock, applicationsServiceMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldThrowExceptionWhenApplicationNotInInterviewStage() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        Interviewer interviewer = new InterviewerBuilder().user(user).build();
        Interview interview = new InterviewBuilder().interviewers(interviewer).stage(InterviewStage.SCHEDULING).build();
        Program program = new ProgramBuilder().build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).status(ApplicationFormStatus.APPROVAL).program(program)
                .applicationAdministrator(user).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(userServiceMock, applicationsServiceMock);
    }

    @Test
    public void shouldSubmitInterviewConfirmation() {
        Interview interview = new Interview();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).build();
        RegisteredUser currentUser = new RegisteredUser();
        InterviewConfirmDTO interviewConfirmDTO = new InterviewConfirmDTO();
        interviewConfirmDTO.setTimeslotId(2);
        BindingResult errors = new BeanPropertyBindingResult(interviewConfirmDTO, "interviewConfirmDTO");

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        interviewServiceMock.confirmInterview(currentUser, interview, interviewConfirmDTO);
        accessServiceMock.updateAccessTimestamp(eq(applicationForm), eq(currentUser), isA(Date.class));

        EasyMock.replay(interviewServiceMock, userServiceMock, accessServiceMock);
        controller.submitInterviewConfirmation(applicationForm, interviewConfirmDTO, errors);
        EasyMock.verify(interviewServiceMock, accessServiceMock, userServiceMock);

    }

    @Test
    public void shouldRejectInterviewConfirmationIfErrors() {
        ApplicationForm applicationForm = new ApplicationForm();
        InterviewConfirmDTO interviewConfirmDTO = new InterviewConfirmDTO();
        BindingResult errors = new BeanPropertyBindingResult(interviewConfirmDTO, "interviewConfirmDTO");
        errors.reject("error");

        String result = controller.submitInterviewConfirmation(applicationForm, interviewConfirmDTO, errors);
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
