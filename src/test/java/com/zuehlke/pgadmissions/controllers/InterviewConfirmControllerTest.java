package com.zuehlke.pgadmissions.controllers;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
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
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).build();
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

        interviewServiceMock.confirmInterview(interview, 2);
        
        EasyMock.replay(interviewServiceMock);
        controller.submitInterviewConfirmation(applicationForm, 2);
        EasyMock.verify(interviewServiceMock);
    }

    @Before
    public void prepare() {
        interviewServiceMock = EasyMock.createMock(InterviewService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);

        controller = new InterviewConfirmController(applicationsServiceMock, userServiceMock, interviewServiceMock);
    }

}
