package com.zuehlke.pgadmissions.controllers.workflow.interview;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class MoveToInterviewControllerTest {

	private MoveToInterviewController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	private NewUserByAdminValidator userValidatorMock;
	private InterviewerService interviewerServiceMock;
	private InterviewService interviewServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;

	private InterviewValidator interviewValidator;
	private DatePropertyEditor datePropertyEditorMock;

	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	private RegisteredUser currentUserMock;
	private ApplicationForm applicationForm;

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

		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm(5);
		assertEquals(applicationForm, returnedForm);

	}

	@Test
	public void shouldReturnNewInterview() {

		Interview returnedInterview = controller.getInterview(null);
		assertNull(returnedInterview.getId());
	}

	@Test
	public void shouldSaveInterview() {
		final ApplicationForm application = new ApplicationFormBuilder().id(2).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(Integer applicationId) {
				return application;
			}

		};
		ModelMap mmap = new ModelMap();
		Interview interview = new InterviewBuilder().id(4).furtherDetails("9 pm").locationURL("pgadmissions.com").dueDate(new Date()).toInterview();
		interviewServiceMock.save(interview);
		applicationServiceMock.save(application);
		EasyMock.replay(interviewerServiceMock, applicationServiceMock);
		controller.moveToInterview(application.getId(), interview, bindingResultMock, mmap, new ArrayList<RegisteredUser>());
		EasyMock.verify(interviewerServiceMock, applicationServiceMock);
		assertEquals(interview, application.getLatestInterview());
	}



	@Test
	public void shouldNotSaveInterviewAndReturnToInterviewPageIfHasErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}

		};
		Interview interview = new InterviewBuilder().application(applicationForm).toInterview();
		EasyMock.expect(applicationServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock, applicationServiceMock);
		assertEquals(INTERVIEW_DETAILS_VIEW_NAME, controller.moveToInterview(1, interview, errorsMock, new ModelMap(), new ArrayList<RegisteredUser>()));

	}

	@Test
	public void shouldSaveInterviewIfNoErrorsAndMoveApplicationToInterviewStage() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = calendar.getTime();

		Interview interview = new InterviewBuilder().furtherDetails("further").dueDate(new Date()).id(1).application(applicationForm).toInterview();
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		interviewServiceMock.save(interview);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(errorsMock, interviewServiceMock, applicationServiceMock);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}
		};
		assertEquals("redirect:/applications", controller.moveToInterview(1, interview, errorsMock, new ModelMap(), new ArrayList<RegisteredUser>()));

		EasyMock.verify(errorsMock, interviewServiceMock, applicationServiceMock);
		Assert.assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getStatus());
		assertEquals(DateUtils.truncate(tomorrow, Calendar.DATE), DateUtils.truncate(interview.getInterviewDueDate(), Calendar.DATE));

	}

	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
		interviewerServiceMock = EasyMock.createMock(InterviewerService.class);
		interviewServiceMock = EasyMock.createMock(InterviewService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		interviewValidator = EasyMock.createMock(InterviewValidator.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);

		controller = new MoveToInterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock);

	}

}
