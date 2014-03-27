package com.zuehlke.pgadmissions.controllers.workflow.interview;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.easymock.EasyMock;
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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;

public class MoveToInterviewControllerTest {
    private ApplicationFormService applicationServiceMock;
    private UserService userServiceMock;
    private RegisteredUser currentUserMock;
    private MoveToInterviewController controller;
    private InterviewValidator interviewValidatorMock;
    private InterviewerPropertyEditor interviewerPropertyEditorMock;
    private InterviewService interviewServiceMock;
    private BindingResult bindingResultMock;
    private DatePropertyEditor datePropertyEditorMock;
    private InterviewTimeslotsPropertyEditor interviewTimeslotsPropertyEditorMock;
    private ActionsProvider actionsProviderMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

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
    public void shouldReturnNewInterviewWithEmtpyInterviewersIfNoLatestInterview() {

        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();
        expect(applicationServiceMock.getByApplicationNumber("abc")).andReturn(application);

        replay(applicationServiceMock);
        Interview returnedInterview = controller.getInterview("abc");
        verify(applicationServiceMock);

        assertNull(returnedInterview.getId());
        assertTrue(returnedInterview.getInterviewers().isEmpty());

    }

    @Test
    public void shouldGetApplicationFromIdForAdmin() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).advert(program).build();

        expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        expect(currentUserMock.canSee(applicationForm)).andReturn(true);
        expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
        replay(applicationServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
        expect(applicationServiceMock.getByApplicationNumber("5")).andReturn(null);

        replay(applicationServiceMock);
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

        replay(interviewServiceMock, applicationFormUserRoleServiceMock);
        String view = controller.moveToInterview(interview, bindingResultMock, modelMap);
        verify(interviewServiceMock, applicationFormUserRoleServiceMock);

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

        expect(currentUserMock.getId()).andReturn(3).anyTimes();
        interviewServiceMock.moveApplicationToInterview(currentUserMock, interview, application);
        
        replay(interviewServiceMock, currentUserMock, applicationFormUserRoleServiceMock);
        String view = controller.moveToInterview(interview, bindingResultMock, modelMap);
        verify(interviewServiceMock, currentUserMock, applicationFormUserRoleServiceMock);

        assertEquals("/private/common/simpleResponse", view);
        assertEquals("redirectToVote", modelMap.get("message"));
    }

    @Test
    public void shouldNotSaveInterviewAndReturnToInterviewPageIfHasErrors() {
        BindingResult errorsMock = createMock(BindingResult.class);
        final ApplicationForm application = new ApplicationFormBuilder().id(1).build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", currentUserMock);

        Interview interview = new InterviewBuilder().application(application).build();
        expect(errorsMock.hasErrors()).andReturn(true);

        replay(errorsMock);
        assertEquals("/private/staff/interviewers/interviewer_section", controller.moveToInterview(interview, errorsMock, modelMap));
        verify(errorsMock);

    }

    @Test
    public void shouldAddInterviewValidatorAndInterviewerPropertyEditor() {
        WebDataBinder binderMock = createMock(WebDataBinder.class);
        binderMock.setValidator(interviewValidatorMock);
        binderMock.registerCustomEditor(eq(String.class), anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(Interviewer.class, interviewerPropertyEditorMock);
        binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
        binderMock.registerCustomEditor(null, "timeslots", interviewTimeslotsPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.<Class<?>> eq(null), eq("duration"), anyObject(CustomNumberEditor.class));
        replay(binderMock);
        controller.registerValidatorAndPropertyEditor(binderMock);
        verify(binderMock);
    }

    @Before
    public void setUp() {
        applicationServiceMock = createMock(ApplicationFormService.class);
        userServiceMock = createMock(UserService.class);
        currentUserMock = createMock(RegisteredUser.class);
        interviewValidatorMock = createMock(InterviewValidator.class);
        interviewerPropertyEditorMock = createMock(InterviewerPropertyEditor.class);
        datePropertyEditorMock = createMock(DatePropertyEditor.class);
        interviewServiceMock = createMock(InterviewService.class);
        interviewTimeslotsPropertyEditorMock = createMock(InterviewTimeslotsPropertyEditor.class);
        actionsProviderMock = createMock(ActionsProvider.class);
        applicationFormUserRoleServiceMock = createMock(ApplicationFormUserRoleService.class);

        expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        replay(userServiceMock);

        bindingResultMock = createMock(BindingResult.class);
        expect(bindingResultMock.hasErrors()).andReturn(false);
        replay(bindingResultMock);

        controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, interviewServiceMock, interviewValidatorMock,
                interviewerPropertyEditorMock, datePropertyEditorMock, interviewTimeslotsPropertyEditorMock, actionsProviderMock, applicationFormUserRoleServiceMock);

    }
}