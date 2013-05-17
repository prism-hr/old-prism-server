package com.zuehlke.pgadmissions.controllers;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.AcceptedTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewParticipantValidator;

public class InterviewVoteControllerTest {

    private InterviewVoteController controller;

    private ApplicationsService applicationsServiceMock;

    private UserService userServiceMock;

    private InterviewService interviewServiceMock;

    private InterviewParticipantValidator interviewParticipantValidatorMock;

    private AcceptedTimeslotsPropertyEditor acceptedTimeslotsPropertyEditorMock;

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
    public void shouldThrowExceptionWhenUserNotParticipant() {
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
    public void shouldThrowExceptionWhenParticipantAlreadyResponded() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        InterviewParticipant interviewParticipant = new InterviewParticipantBuilder().user(user).responded(true).build();
        Interview interview = new InterviewBuilder().participants(interviewParticipant).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).latestInterview(interview).applicant(user)
                .build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(userServiceMock, applicationsServiceMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldThrowExceptionWhenApplicationNotInInterviewStage() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        InterviewParticipant interviewParticipant = new InterviewParticipantBuilder().user(user).build();
        Interview interview = new InterviewBuilder().participants(interviewParticipant).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).latestInterview(interview).applicant(user)
                .build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);

        EasyMock.replay(userServiceMock, applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(userServiceMock, applicationsServiceMock);
    }

    @Test
    public void shouldregisterParticipantValidatorAndPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(interviewParticipantValidatorMock);
        binderMock.registerCustomEditor(null, "acceptedTimeslots", acceptedTimeslotsPropertyEditorMock);
        EasyMock.replay(binderMock);
        controller.registerValidatorAndPropertyEditor(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldNotSubmitInterviewVotesIfValidationErrors() {
        InterviewParticipant participant = new InterviewParticipant();
        BindingResult errors = new DirectFieldBindingResult(participant, "interviewParticipant");
        errors.reject("88");

        controller.submitInterviewVotes(participant, errors, null, "my comment");
    }

    @Test
    public void shouldSubmitInterviewVotes() {
        EasyMock.reset(interviewServiceMock);
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("22").build();
        InterviewParticipant participant = new InterviewParticipant();
        InterviewVoteComment interviewVoteComment = new InterviewVoteComment();
        interviewVoteComment.setComment("my comment");
        interviewVoteComment.setApplication(applicationForm);
        interviewVoteComment.setUser(participant.getUser());
        BindingResult errors = new DirectFieldBindingResult(participant, "interviewParticipant");

        interviewServiceMock.postVote(EasyMock.isA(InterviewParticipant.class), EasyMock.isA(InterviewVoteComment.class));

        EasyMock.replay(interviewServiceMock);
        String result = controller.submitInterviewVotes(participant, errors, applicationForm, "my comment");
        EasyMock.verify(interviewServiceMock);

        Assert.assertEquals("redirect:/applications?messageCode=interview.vote.feedback&application=22", result);
    }

    @Before
    public void prepare() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        interviewServiceMock = EasyMock.createMock(InterviewService.class);
        interviewParticipantValidatorMock = EasyMock.createMock(InterviewParticipantValidator.class);
        acceptedTimeslotsPropertyEditorMock = EasyMock.createMock(AcceptedTimeslotsPropertyEditor.class);

        controller = new InterviewVoteController(applicationsServiceMock, userServiceMock, interviewParticipantValidatorMock, interviewServiceMock,
                acceptedTimeslotsPropertyEditorMock);

    }

}
