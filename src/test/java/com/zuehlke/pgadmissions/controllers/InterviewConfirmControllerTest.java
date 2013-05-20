package com.zuehlke.pgadmissions.controllers;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

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
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;

public class InterviewConfirmControllerTest {

    private ApplicationsService applicationsServiceMock;

    private InterviewService interviewServiceMock;

    private UserService userServiceMock;

    private InterviewConfirmController controller;

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
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).status(ApplicationFormStatus.APPROVAL).program(program).applicationAdministrator(user).build();
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
        Model model = new ExtendedModelMap();
        
        interviewServiceMock.confirmInterview(interview, 2);
        
        EasyMock.replay(interviewServiceMock);
        controller.submitInterviewConfirmation(applicationForm, 2, model);
        EasyMock.verify(interviewServiceMock);
        
        Assert.assertFalse(model.containsAttribute("timeslotIdError"));
    }
    
    @Test
    public void shouldRejectInterviewConfirmationIfNoTimeslotId() {
        ApplicationForm applicationForm = new ApplicationForm();
        Model model = new ExtendedModelMap();
        
        controller.submitInterviewConfirmation(applicationForm, null, model);
        
        Assert.assertEquals("dropdown.radio.select.none", model.asMap().get("timeslotIdError"));
    }

    @Before
    public void prepare() {
        interviewServiceMock = EasyMock.createMock(InterviewService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);

        controller = new InterviewConfirmController(applicationsServiceMock, userServiceMock, interviewServiceMock);
    }

}
