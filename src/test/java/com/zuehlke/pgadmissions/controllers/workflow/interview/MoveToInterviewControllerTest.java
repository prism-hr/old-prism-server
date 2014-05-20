package com.zuehlke.pgadmissions.controllers.workflow.interview;

import org.springframework.validation.BindingResult;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;

public class MoveToInterviewControllerTest {
    private ApplicationFormService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private User currentUserMock;

    @Mock
    @InjectIntoByType
    private InterviewService interviewServiceMock;

    @Mock
    @InjectIntoByType
    private BindingResult bindingResultMock;

    @Mock
    @InjectIntoByType
    private LocalDatePropertyEditor datePropertyEditorMock;

    @Mock
    @InjectIntoByType
    private InterviewTimeslotsPropertyEditor interviewTimeslotsPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private ActionService actionsProviderMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @TestedObject
    private MoveToInterviewController controller;

//    @Test
//    public void shouldGetInterviewPage() {
//        ApplicationForm applicationForm = new ApplicationForm();
//        RegisteredUser user = new RegisteredUser();
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", applicationForm);
//        modelMap.put("user", user);
//
//        Assert.assertEquals("/private/staff/interviewers/interview_details", controller.getInterviewDetailsPage(modelMap));
//    }
//
//    @Test
//    public void shouldGetInterviewersSection() {
//        Assert.assertEquals("/private/staff/interviewers/interviewer_section", controller.getInterviewersSection());
//    }
//
//    @Test
//    public void shouldReturnNewInterviewWithEmtpyInterviewersIfNoLatestInterview() {
//
//        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();
//        expect(applicationServiceMock.getByApplicationNumber("abc")).andReturn(application);
//
//        replay(applicationServiceMock);
//        Interview returnedInterview = controller.getInterview("abc");
//        verify(applicationServiceMock);
//
//        assertNull(returnedInterview.getId());
//        assertTrue(returnedInterview.getInterviewers().isEmpty());
//
//    }
//
//    @Test
//    public void shouldGetApplicationFromIdForAdmin() {
//        Program program = new Program().id(6).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
//
//        expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
//        expect(currentUserMock.canSee(applicationForm)).andReturn(true);
//        expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
//        replay(applicationServiceMock, currentUserMock);
//
//        ApplicationForm returnedForm = controller.getApplicationForm("5");
//        assertEquals(applicationForm, returnedForm);
//
//    }
//
//    @Test(expected = MissingApplicationFormException.class)
//    public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
//        expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(null);
//
//        replay(applicationServiceMock);
//        controller.getApplicationForm("5");
//    }
//
//    @Test
//    public void shouldGetCurrentUserAsUser() {
//        assertEquals(currentUserMock, controller.getUser());
//    }
//
//    @Test
//    public void shouldMoveApplicationToInterview() {
//        Interview interview = new InterviewBuilder().id(4).build();
//        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", application);
//        modelMap.put("user", currentUserMock);
//
//        interviewServiceMock.moveApplicationToInterview(currentUserMock, interview, application);
//
//        replay(interviewServiceMock, applicationFormUserRoleServiceMock);
//        String view = controller.moveToInterview(interview, bindingResultMock, modelMap);
//        verify(interviewServiceMock, applicationFormUserRoleServiceMock);
//
//        assertEquals("/private/common/ajax_OK", view);
//    }
//
//    @Test
//    public void shouldMoveApplicationToInterviewAndRedirectToVotePage() {
//        InterviewParticipant participant = new InterviewParticipantBuilder().user(currentUserMock).build();
//        Interview interview = new InterviewBuilder().id(4).participants(participant).build();
//        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", application);
//        modelMap.put("user", currentUserMock);
//
//        expect(currentUserMock.getId()).andReturn(3).anyTimes();
//        interviewServiceMock.moveApplicationToInterview(currentUserMock, interview, application);
//
//        replay(interviewServiceMock, currentUserMock, applicationFormUserRoleServiceMock);
//        String view = controller.moveToInterview(interview, bindingResultMock, modelMap);
//        verify(interviewServiceMock, currentUserMock, applicationFormUserRoleServiceMock);
//
//        assertEquals("/private/common/simpleResponse", view);
//        assertEquals("redirectToVote", modelMap.get("message"));
//    }
//
//    @Test
//    public void shouldNotSaveInterviewAndReturnToInterviewPageIfHasErrors() {
//        BindingResult errorsMock = createMock(BindingResult.class);
//        final ApplicationForm application = new ApplicationFormBuilder().id(1).build();
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", application);
//        modelMap.put("user", currentUserMock);
//
//        Interview interview = new InterviewBuilder().application(application).build();
//        expect(errorsMock.hasErrors()).andReturn(true);
//
//        replay(errorsMock);
//        assertEquals("/private/staff/interviewers/interviewer_section", controller.moveToInterview(interview, errorsMock, modelMap));
//        verify(errorsMock);
//
//    }
//
//    @Test
//    public void shouldAddInterviewValidatorAndInterviewerPropertyEditor() {
//        WebDataBinder binderMock = createMock(WebDataBinder.class);
//        binderMock.setValidator(interviewValidatorMock);
//        binderMock.registerCustomEditor(eq(String.class), anyObject(StringTrimmerEditor.class));
//        binderMock.registerCustomEditor(Interviewer.class, interviewerPropertyEditorMock);
//        binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
//        binderMock.registerCustomEditor(null, "timeslots", interviewTimeslotsPropertyEditorMock);
//        binderMock.registerCustomEditor(EasyMock.<Class<?>> eq(null), eq("duration"), anyObject(CustomNumberEditor.class));
//        replay(binderMock);
//        controller.registerValidatorAndPropertyEditor(binderMock);
//        verify(binderMock);
//    }

}