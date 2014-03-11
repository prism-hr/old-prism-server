package com.zuehlke.pgadmissions.controllers;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.propertyeditors.AcceptedTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.CommentAssignedUserValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class InterviewVoteControllerTest {


    @Mock
    @InjectIntoByType
    private ApplicationsService applicationsService;
    
    @Mock
    @InjectIntoByType
    private UserService userService;
    
    @Mock
    @InjectIntoByType
    private InterviewService interviewService;
    
    @Mock
    @InjectIntoByType
    private CommentAssignedUserValidator assignedUserValidator;
    
    @Mock
    @InjectIntoByType
    private AcceptedTimeslotsPropertyEditor acceptedTimeslotsPropertyEditor;
    
    @Mock
    @InjectIntoByType
    private ActionsProvider actionsProvider;
    
    @Mock
    @InjectIntoByType
    private ApplicationFormUserRoleService applicationFormUserRoleService;
    
    @TestedObject
    private InterviewVoteController controller;

//    @Test(expected = MissingApplicationFormException.class)
//    public void shouldThrowExceptionWhenApptlicationIsNull() {
//        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(null);
//
//        EasyMock.replay(applicationsServiceMock);
//        controller.getApplicationForm("app1");
//        EasyMock.verify(applicationsServiceMock);
//    }
//
//    @Test
//    public void shouldGetApplicationFormFromId() {
//        ApplicationForm applicationForm = new ApplicationForm();
//        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
//
//        EasyMock.replay(applicationsServiceMock);
//        ApplicationForm returnedApplication = controller.getApplicationForm("5");
//        EasyMock.verify(applicationsServiceMock);
//
//        assertEquals(returnedApplication, applicationForm);
//    }
//
//    @Test
//    public void shouldregisterParticipantValidatorAndPropertyEditors() {
//        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
//        binderMock.setValidator(interviewParticipantValidatorMock);
//        binderMock.registerCustomEditor(null, "acceptedTimeslots", acceptedTimeslotsPropertyEditorMock);
//        EasyMock.replay(binderMock);
//        controller.registerValidatorAndPropertyEditor(binderMock);
//        EasyMock.verify(binderMock);
//    }
//
//    @Test
//    public void shouldNotSubmitInterviewVotesIfValidationErrors() {
//        InterviewParticipant participant = new InterviewParticipant();
//        BindingResult errors = new DirectFieldBindingResult(participant, "interviewParticipant");
//        errors.reject("88");
//
//        controller.submitInterviewVotes(participant, errors, "my comment", new ModelMap());
//    }
//
//    @Test
//    public void shouldSubmitInterviewVotes() {
//        EasyMock.reset(interviewServiceMock);
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("22").build();
//        InterviewParticipant participant = new InterviewParticipant();
//        InterviewVoteComment interviewVoteComment = new InterviewVoteComment();
//        interviewVoteComment.setComment("my comment");
//        interviewVoteComment.setApplication(applicationForm);
//        interviewVoteComment.setUser(participant.getUser());
//        BindingResult errors = new DirectFieldBindingResult(participant, "interviewParticipant");
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", applicationForm);
//
//        interviewServiceMock.postVote(EasyMock.isA(InterviewParticipant.class), EasyMock.isA(InterviewVoteComment.class));
//
//        EasyMock.replay(interviewServiceMock);
//        String result = controller.submitInterviewVotes(participant, errors, "my comment", modelMap);
//        EasyMock.verify(interviewServiceMock);
//
//        Assert.assertEquals("redirect:/applications?messageCode=interview.vote.feedback&application=22", result);
//    }

}