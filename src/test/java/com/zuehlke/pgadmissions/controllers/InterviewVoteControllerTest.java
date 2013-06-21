package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
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

    private ActionsProvider actionsProviderMock;

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionWhenApptlicationIsNull() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(null);

        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(applicationsServiceMock);
    }

    @Test
    public void shouldGetApplicationFormFromId() {
        ApplicationForm applicationForm = new ApplicationForm();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);

        EasyMock.replay(applicationsServiceMock);
        ApplicationForm returnedApplication = controller.getApplicationForm("5");
        EasyMock.verify(applicationsServiceMock);

        assertEquals(returnedApplication, applicationForm);
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

        controller.submitInterviewVotes(participant, errors, "my comment", new ModelMap());
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
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);

        interviewServiceMock.postVote(EasyMock.isA(InterviewParticipant.class), EasyMock.isA(InterviewVoteComment.class));

        EasyMock.replay(interviewServiceMock);
        String result = controller.submitInterviewVotes(participant, errors, "my comment", modelMap);
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
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);

        controller = new InterviewVoteController(applicationsServiceMock, userServiceMock, interviewParticipantValidatorMock, interviewServiceMock,
                acceptedTimeslotsPropertyEditorMock, actionsProviderMock);

    }

}
