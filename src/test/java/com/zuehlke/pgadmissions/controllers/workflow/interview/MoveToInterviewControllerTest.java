package com.zuehlke.pgadmissions.controllers.workflow.interview;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;

public class MoveToInterviewControllerTest {
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private RegisteredUser currentUserMock;
    private MoveToInterviewController controller;
    private InterviewValidator interviewValidatorMock;
    private InterviewerPropertyEditor interviewerPropertyEditorMock;
    private InterviewService interviewServiceMock;
    private BindingResult bindingResultMock;
    private DatePropertyEditor datePropertyEditorMock;
    private InterviewTimeslotsPropertyEditor interviewTimeslotsPropertyEditorMock;
    private ApplicationFormAccessService accessServiceMock;
    private ActionsProvider actionsProviderMock;

    @Test
    public void shouldGetInterviewPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        RegisteredUser user = new RegisteredUser();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        Assert.assertEquals("/private/staff/interviewers/interview_details", controller.getInterviewDetailsPage(modelMap));
    }

    @Test
    public void shouldGetInterviewersSection() {
        Assert.assertEquals("/private/staff/interviewers/interviewer_section", controller.getInterviewersSection());
    }

    @Test
    public void shouldGetNominatedInterviewers() {
        EasyMock.reset(userServiceMock);
        final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).build();

        String emailOfSupervisor1 = "1@ucl.ac.uk";
        String emailOfSupervisor2 = "2@ucl.ac.uk";
        SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
        SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
                .build();

        final Program program = new ProgramBuilder().reviewers(interUser1, interUser2).id(6).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("abc")).andReturn(application);

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(interUser1);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(interUser2);

        EasyMock.replay(userServiceMock, applicationServiceMock);
        List<RegisteredUser> interviewers = controller.getNominatedSupervisors("abc");
        EasyMock.verify(userServiceMock, applicationServiceMock);

        assertEquals(2, interviewers.size());
        assertTrue(interviewers.containsAll(Arrays.asList(interUser1, interUser2)));
    }

    @Test
    public void shouldGetProgrammeInterviewersAndRemoveApplicantNominatedSupervisors() {
        EasyMock.reset(userServiceMock);
        final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).build();
        final RegisteredUser interUser3 = new RegisteredUserBuilder().id(5).build();

        String emailOfSupervisor1 = "1@ucl.ac.uk";
        String emailOfSupervisor2 = "2@ucl.ac.uk";
        SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
        SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
                .build();

        final Program program = new ProgramBuilder().interviewers(interUser1, interUser2, interUser3).id(6).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("abc")).andReturn(application).anyTimes();

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(interUser1);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(interUser2);

        EasyMock.replay(userServiceMock, applicationServiceMock);
        List<RegisteredUser> interviewersUsers = controller.getProgrammeInterviewers("abc");
        EasyMock.verify(userServiceMock, applicationServiceMock);

        assertEquals(1, interviewersUsers.size());
        assertTrue(interviewersUsers.containsAll(Arrays.asList(interUser3)));
    }

    @Test
    public void shouldGetListOfPreviousInterviewersAndAddReviewersWillingToInterviewWithDefaultInterviewersAndApplicantNominatedSupervisorsRemoved() {
        EasyMock.reset(userServiceMock);
        String emailOfSupervisor1 = "1@ucl.ac.uk";
        String emailOfSupervisor2 = "2@ucl.ac.uk";
        SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
        SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
                .build();

        final RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(9).build();
        final RegisteredUser reviewerWillingToIntergviewOne = new RegisteredUserBuilder().id(8).build();
        final RegisteredUser reviewerWillingToIntergviewTwo = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser previousInterviewer = new RegisteredUserBuilder().id(6).build();
        ReviewComment reviewOne = new ReviewCommentBuilder().id(1).user(reviewerWillingToIntergviewOne).willingToInterview(true).build();
        ReviewComment reviewTwo = new ReviewCommentBuilder().id(1).user(defaultInterviewer).willingToInterview(true).build();
        ReviewComment reviewThree = new ReviewCommentBuilder().id(1).user(reviewerWillingToIntergviewTwo).willingToInterview(true).build();

        final Program program = new ProgramBuilder().id(6).interviewers(defaultInterviewer).build();

        final ApplicationForm application = new ApplicationFormBuilder().id(5).program(program).comments(reviewOne, reviewTwo, reviewThree)
                .programmeDetails(programmeDetails).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("abc")).andReturn(application).anyTimes();

        List<RegisteredUser> previousInterviewers = new ArrayList<RegisteredUser>();
        previousInterviewers.add(previousInterviewer);
        previousInterviewers.add(defaultInterviewer);
        previousInterviewers.add(reviewerWillingToIntergviewOne);
        EasyMock.expect(userServiceMock.getAllPreviousInterviewersOfProgram(program)).andReturn(previousInterviewers);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(defaultInterviewer).times(2);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(reviewerWillingToIntergviewOne).times(2);

        EasyMock.replay(userServiceMock, applicationServiceMock);
        List<RegisteredUser> interviewerUsers = controller.getPreviousInterviewersAndReviewersWillingToInterview("abc");
        EasyMock.verify(userServiceMock, applicationServiceMock);

        assertEquals(2, interviewerUsers.size());
        assertTrue(interviewerUsers.containsAll(Arrays.asList(reviewerWillingToIntergviewTwo, previousInterviewer)));
    }

    @Test
    public void shouldReturnNewInterviewWithExistingRoundsInterviewersIfAny() {
        Interviewer interviewerOne = new InterviewerBuilder().id(1).build();
        Interviewer interviewerTwo = new InterviewerBuilder().id(2).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc")
                .latestInterview(new InterviewBuilder().interviewers(interviewerOne, interviewerTwo).build()).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("abc")).andReturn(application);

        EasyMock.replay(applicationServiceMock);
        Interview returnedInterview = controller.getInterview("abc");
        EasyMock.verify(applicationServiceMock);

        assertNull(returnedInterview.getId());
        assertThat(returnedInterview.getInterviewers(), hasItems(interviewerOne, interviewerTwo));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnNewInterviewWithApplicationAdministratorIfAny() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").applicationAdministrator(user).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("abc")).andReturn(application);

        EasyMock.replay(applicationServiceMock);
        Interview returnedInterview = controller.getInterview("abc");
        EasyMock.verify(applicationServiceMock);

        assertNull(returnedInterview.getId());
        assertThat(returnedInterview.getInterviewers(), Matchers.<Interviewer> hasItems(hasProperty("user", sameInstance(user))));
    }

    @Test
    public void shouldReturnInterviewWithWillingToInterviewWithInterviewersOfPreviousInterviewRemoved() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
        ReviewComment reviewOne = new ReviewCommentBuilder().id(1).user(userOne).willingToInterview(true).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(2).build();
        ReviewComment reviewTwo = new ReviewCommentBuilder().id(2).user(userTwo).willingToInterview(true).build();
        RegisteredUser userThree = new RegisteredUserBuilder().id(3).build();
        ReviewComment reviewThree = new ReviewCommentBuilder().id(3).user(userThree).willingToInterview(true).build();
        Interviewer interviewerOne = new InterviewerBuilder().id(1).user(userOne).build();
        Interviewer interviewerTwo = new InterviewerBuilder().id(2).user(userTwo).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").comments(reviewOne, reviewTwo, reviewThree)
                .latestInterview(new InterviewBuilder().interviewers(interviewerOne, interviewerTwo).build()).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("abc")).andReturn(application);

        EasyMock.replay(applicationServiceMock);
        Interview returnedInterview = controller.getInterview("abc");
        EasyMock.verify(applicationServiceMock);

        assertNull(returnedInterview.getId());
        assertEquals(3, returnedInterview.getInterviewers().size());
        assertTrue(returnedInterview.getInterviewers().containsAll(Arrays.asList(interviewerOne, interviewerTwo)));
        assertNull(returnedInterview.getInterviewers().get(2).getId());
        assertEquals(userThree, returnedInterview.getInterviewers().get(2).getUser());
    }

    @Test
    public void shouldReturnNewInterviewWithEmtpyInterviewersIfNoLatestInterview() {

        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("abc")).andReturn(application);

        EasyMock.replay(applicationServiceMock);
        Interview returnedInterview = controller.getInterview("abc");
        EasyMock.verify(applicationServiceMock);

        assertNull(returnedInterview.getId());
        assertTrue(returnedInterview.getInterviewers().isEmpty());

    }

    @Test
    public void shouldGetApplicationFromIdForAdmin() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);

        EasyMock.replay(applicationServiceMock);
        controller.getApplicationForm("5");
    }

    @Test
    public void shouldGetCurrentUserAsUser() {
        assertEquals(currentUserMock, controller.getUser());
    }

    @Test
    public void shouldMoveApplicationToInterview() {
        Interview interview = new InterviewBuilder().id(4).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", currentUserMock);

        interviewServiceMock.moveApplicationToInterview(currentUserMock, interview, application);

        EasyMock.replay(interviewServiceMock);
        String view = controller.moveToInterview(interview, bindingResultMock, modelMap);
        EasyMock.verify(interviewServiceMock);

        assertEquals("/private/common/ajax_OK", view);
    }

    @Test
    public void shouldMoveApplicationToInterviewAndRedirectToVotePage() {
        InterviewParticipant participant = new InterviewParticipantBuilder().user(currentUserMock).build();
        Interview interview = new InterviewBuilder().id(4).participants(participant).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", currentUserMock);

        EasyMock.expect(currentUserMock.getId()).andReturn(3).anyTimes();
        interviewServiceMock.moveApplicationToInterview(currentUserMock, interview, application);

        EasyMock.replay(interviewServiceMock, currentUserMock);
        String view = controller.moveToInterview(interview, bindingResultMock, modelMap);
        EasyMock.verify(interviewServiceMock, currentUserMock);

        assertEquals("/private/common/simpleResponse", view);
        assertEquals("redirectToVote", modelMap.get("message"));
    }

    @Test
    public void shouldNotSaveInterviewAndReturnToInterviewPageIfHasErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        final ApplicationForm application = new ApplicationFormBuilder().id(1).build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", currentUserMock);

        Interview interview = new InterviewBuilder().application(application).build();
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);

        EasyMock.replay(errorsMock);
        assertEquals("/private/staff/interviewers/interviewer_section", controller.moveToInterview(interview, errorsMock, modelMap));
        EasyMock.verify(errorsMock);

    }

    @Test
    public void shouldAddInterviewValidatorAndInterviewerPropertyEditor() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(interviewValidatorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(Interviewer.class, interviewerPropertyEditorMock);
        binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
        binderMock.registerCustomEditor(null, "timeslots", interviewTimeslotsPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.<Class<?>> eq(null), EasyMock.eq("duration"), EasyMock.anyObject(CustomNumberEditor.class));
        EasyMock.replay(binderMock);
        controller.registerValidatorAndPropertyEditor(binderMock);
        EasyMock.verify(binderMock);
    }

    @Before
    public void setUp() {
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        currentUserMock = EasyMock.createMock(RegisteredUser.class);
        interviewValidatorMock = EasyMock.createMock(InterviewValidator.class);
        interviewerPropertyEditorMock = EasyMock.createMock(InterviewerPropertyEditor.class);
        datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
        interviewServiceMock = EasyMock.createMock(InterviewService.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        interviewTimeslotsPropertyEditorMock = EasyMock.createMock(InterviewTimeslotsPropertyEditor.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        EasyMock.replay(userServiceMock);

        bindingResultMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResultMock);

        controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, interviewServiceMock, interviewValidatorMock,
                interviewerPropertyEditorMock, datePropertyEditorMock, interviewTimeslotsPropertyEditorMock, accessServiceMock, actionsProviderMock);

    }
}
