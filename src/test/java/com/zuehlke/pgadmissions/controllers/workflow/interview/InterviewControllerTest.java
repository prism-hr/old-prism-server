package com.zuehlke.pgadmissions.controllers.workflow.interview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class InterviewControllerTest {
	private InterviewController controller;
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
	public void shouldAddRegisteredUserValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(userValidatorMock);
		EasyMock.replay(binderMock);
		controller.registerInterviewerValidators(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldReturnExistingInterviewersBelongingToApplication() {
		Program program = new ProgramBuilder().id(6).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		controller = new InterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(Integer applicationId) {
				return applicationForm;
			}

			@Override
			public Interview getInterview(Integer applicationId) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();

		Interviewer inter1 = new InterviewerBuilder().user(interUser1).id(4).toInterviewer();
		Interviewer inter2 = new InterviewerBuilder().user(interUser2).id(5).toInterviewer();
		Interview interview = new InterviewBuilder().id(1).toInterview();
		interview.setInterviewers(Arrays.asList(inter1, inter2));
		applicationForm.setLatestInterview(interview);

		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		Set<RegisteredUser> interviewersUsers = controller.getApplicationInterviewersAsUsers(applicationForm.getId());
		assertEquals(2, interviewersUsers.size());
	}

	@Test
	public void shouldGetProgrammeInterviewers() {
		final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();
		final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();
		final Program program = new ProgramBuilder().interviewers(interUser1, interUser2).id(6).toProgram();
		Interview interview = new InterviewBuilder().id(1).toInterview();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().interviews(interview).latestInterview(interview).acceptedTerms(CheckedStatus.NO)
				.id(5).program(program).toApplicationForm();
		controller = new InterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(Integer applicationId) {
				applicationForm.setProgram(program);
				return applicationForm;
			}

			public List<RegisteredUser> unsavedInterviewers(String unsavedInterviewersRaw) {
				return null;
			}

			@Override
			public Interview getInterview(Integer applicationId) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);

		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		List<RegisteredUser> interviewersUsers = controller.getProgrammeInterviewers(5, null);
		assertEquals(2, interviewersUsers.size());
	}


	@Test
	public void shouldRegisterInterviewValidatorAndPropertyEditor() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(interviewValidator);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerInterviewValidatorsAndPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	

	@Test
	public void shouldReturnNewUser() {
		assertNotNull(controller.getInterviewer());
		assertNull(controller.getInterviewer().getId());
	}

	@Test
	public void shouldTransformStringIdsToUserObjects() {
		EasyMock.reset(userServiceMock);
		RegisteredUser user_1 = new RegisteredUserBuilder().id(5).toUser();
		RegisteredUser user_2 = new RegisteredUserBuilder().id(6).toUser();
		EasyMock.expect(userServiceMock.getUser(5)).andReturn(user_1);
		EasyMock.expect(userServiceMock.getUser(6)).andReturn(user_2);

		EasyMock.replay(userServiceMock);
		List<RegisteredUser> unsavedInterviewers = controller.unsavedInterviewers("5|6");

		EasyMock.verify(userServiceMock);
		Assert.assertNotNull(unsavedInterviewers);
		Assert.assertEquals(2, unsavedInterviewers.size());
		Assert.assertTrue(unsavedInterviewers.contains(user_1));
		Assert.assertTrue(unsavedInterviewers.contains(user_2));

	}

	@Test
	public void shouldNotFailIntUnsavedReviewersIsNull() {
		EasyMock.reset(userServiceMock);
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> unsavedInterviewers = controller.unsavedInterviewers(null);

		EasyMock.verify(userServiceMock);
		Assert.assertNotNull(unsavedInterviewers);
		Assert.assertTrue(unsavedInterviewers.isEmpty());
	}
	@Test
	public void shouldGetApplicationFromIdForAdmin() {
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
	public void shouldGetApplicationFromIdForInterviewer() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(false);
		EasyMock.expect(currentUserMock.isInterviewerOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm(5);
		assertEquals(applicationForm, returnedForm);
		

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controller.getApplicationForm(5);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminOrInterviewerOfApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.ADMINISTRATOR, program)).andReturn(false);
		EasyMock.expect(currentUserMock.isInterviewerOfApplicationForm(applicationForm)).andReturn(false);
		
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		controller.getApplicationForm(5);
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

		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		
		controller = new InterviewController(applicationServiceMock, userServiceMock, userValidatorMock, interviewerServiceMock, messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock) {
			

			@Override
			public Interview getInterview(Integer applicationId) {
				// TODO Auto-generated method stub
				return null;
			}
		};

	}
}
