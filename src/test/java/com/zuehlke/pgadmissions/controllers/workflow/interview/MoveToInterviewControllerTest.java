package com.zuehlke.pgadmissions.controllers.workflow.interview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class MoveToInterviewControllerTest {

	private MoveToInterviewController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	private NewUserByAdminValidator userValidatorMock;

	private InterviewService interviewServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;

	private InterviewValidator interviewValidator;
	private DatePropertyEditor datePropertyEditorMock;

	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	private RegisteredUser currentUserMock;

	private InterviewerPropertyEditor interviewerPropertyEditorMock;

	@Test
	public void shouldGetInterviewPageWithOnlyAssignFalseNewInterviewersFunctionality() {
		ModelMap modelMap = new ModelMap();
		String interviewDetailsPage = controller.getInterviewDetailsPage(modelMap);
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, interviewDetailsPage);
		Assert.assertFalse((Boolean) modelMap.get("assignOnly"));

	}

	@Test
	public void shouldGetApplicationFromId() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test
	public void shouldReturnNewInterview() {

		Interview returnedInterview = controller.getInterview(null);
		assertNull(returnedInterview.getId());
	}

	@Test
	public void shouldMoveApplicationToInterview() {
		Interview interview = new InterviewBuilder().id(4).toInterview();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).toApplicationForm();

		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, messageSourceMock, interviewServiceMock,
				interviewValidator, datePropertyEditorMock, interviewerPropertyEditorMock, null) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return application;
			}

		};

		interviewServiceMock.moveApplicationToInterview(interview, application);
		EasyMock.replay(interviewServiceMock);

		String view = controller.moveToInterview(application.getApplicationNumber(), interview, bindingResultMock);
		assertEquals("redirect:/applications", view);
		EasyMock.verify(interviewServiceMock);

	}

	@Test
	public void shouldNotSaveInterviewAndReturnToInterviewPageIfHasErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, messageSourceMock, interviewServiceMock,
				interviewValidator, datePropertyEditorMock, interviewerPropertyEditorMock, null) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

		};
		Interview interview = new InterviewBuilder().application(applicationForm).toInterview();
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock, applicationServiceMock);
		assertEquals(INTERVIEW_DETAILS_VIEW_NAME, controller.moveToInterview("1", interview, errorsMock));

	}

	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);

		interviewServiceMock = EasyMock.createMock(InterviewService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		interviewValidator = EasyMock.createMock(InterviewValidator.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		interviewerPropertyEditorMock = EasyMock.createMock(InterviewerPropertyEditor.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);

		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, messageSourceMock, interviewServiceMock,
				interviewValidator, datePropertyEditorMock, interviewerPropertyEditorMock, null);

	}

}
