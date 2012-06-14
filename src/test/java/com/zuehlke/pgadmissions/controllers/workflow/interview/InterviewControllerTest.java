package com.zuehlke.pgadmissions.controllers.workflow.interview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
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
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class InterviewControllerTest {
	private InterviewController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	private NewUserByAdminValidator userValidatorMock;
	
	private InterviewService interviewServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;

	private InterviewValidator interviewValidator;
	private DatePropertyEditor datePropertyEditorMock;

	private RegisteredUser currentUserMock;
	private InterviewerPropertyEditor interviewerPropertyEditorMock;

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
		controller = new InterviewController(applicationServiceMock, userServiceMock, userValidatorMock,  messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock, interviewerPropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

			@Override
			public Interview getInterview(Object applicationId) {
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

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		Set<RegisteredUser> interviewersUsers = controller.getApplicationInterviewersAsUsers(applicationForm.getApplicationNumber());
		assertEquals(2, interviewersUsers.size());
	}

	@Test
	public void shouldGetProgrammeInterviewersAndRemovePendingAndAssignedInterviewersUsers() {
		final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();
		final RegisteredUser interUser3 = new RegisteredUserBuilder().id(8).toUser();
		final RegisteredUser interUser4 = new RegisteredUserBuilder().id(9).toUser();

		final Program program = new ProgramBuilder().interviewers(interUser1, interUser2, interUser3, interUser4).id(6).toProgram();
		Interview interview = new InterviewBuilder().id(1).interviewers(new InterviewerBuilder().user(interUser4).toInterviewer()).toInterview();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().interviews(interview).latestInterview(interview).acceptedTerms(CheckedStatus.NO)
				.id(5).program(program).toApplicationForm();
		controller = new InterviewController(applicationServiceMock, userServiceMock, userValidatorMock,  messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock, interviewerPropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId == "5") {
					return applicationForm;
				}
				return null;
			}

			@Override
			public Interview getInterview(Object applicationId) {
				// TODO Auto-generated method stub
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public List<RegisteredUser> getPendingInterviewers(List<Integer> pendingInterviewer, String applicationId) {
				if (pendingInterviewer.size() == 1 && pendingInterviewer.get(0) == 3) {
					return Arrays.asList(interUser3);
				}
				return Collections.EMPTY_LIST;
			}

		};

		List<RegisteredUser> interviewersUsers = controller.getProgrammeInterviewers("5", Arrays.asList(3));
		assertEquals(2, interviewersUsers.size());
	}

	@Test
	public void shouldRegisterInterviewValidatorAndPropertyEditor() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(interviewValidator);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Interviewer.class, interviewerPropertyEditorMock);
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
	public void shouldGetApplicationFromIdForAdmin() {
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
	public void shouldGetApplicationFromIdForInterviewer() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isInterviewerOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controller.getApplicationForm("5");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminOrInterviewerOfApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isInterviewerOfApplicationForm(applicationForm)).andReturn(false);

		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		controller.getApplicationForm("5");
	}

	@Test
	public void shouldReturnPendingInterviewersAndRemoveExistingInterviewersFromList() {
		List<Integer> ids = Arrays.asList(1, 8);
		EasyMock.reset(userServiceMock);
		RegisteredUser newUser1 = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser newUser2 = new RegisteredUserBuilder().id(8).toUser();

		EasyMock.expect(userServiceMock.getUser(1)).andReturn(newUser1);
		EasyMock.expect(userServiceMock.getUser(8)).andReturn(newUser2);
		EasyMock.replay(userServiceMock);

		String applicationNumber = "5";
		final ApplicationForm applicationForm = new ApplicationFormBuilder()
				.latestInterview(new InterviewBuilder().interviewers(new InterviewerBuilder().user(newUser2).toInterviewer()).toInterview()).id(5).applicationNumber(applicationNumber)
				.toApplicationForm();
		controller = new InterviewController(applicationServiceMock, userServiceMock, userValidatorMock,  messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock, interviewerPropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if ("5" == applicationId) {
					return applicationForm;
				}
				return null;
			}

			@Override
			public Interview getInterview(Object applicationId) {
				return null;
			}
		};
		List<RegisteredUser> newUsers = controller.getPendingInterviewers(ids, applicationNumber);
		assertEquals(1, newUsers.size());
		assertEquals(newUser1, newUsers.get(0));

	}

	@Test
	public void shouldGetListOfPreviousInterviewersAndRemovePendingAssignedOrDefaultInterviewers() {
		EasyMock.reset(userServiceMock);
		final RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser interviewer = new RegisteredUserBuilder().id(6).toUser();
		final RegisteredUser pendingInterviewerUser = new RegisteredUserBuilder().id(8).toUser();
		final RegisteredUser assignedInterviewer = new RegisteredUserBuilder().id(9).toUser();

		final Program program = new ProgramBuilder().interviewers(defaultInterviewer).id(6).toProgram();
		Interview interview = new InterviewBuilder().id(1).interviewers(new InterviewerBuilder().user(assignedInterviewer).toInterviewer()).toInterview();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).id(5).program(program).toApplicationForm();
		controller = new InterviewController(applicationServiceMock, userServiceMock, userValidatorMock,  messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock, interviewerPropertyEditorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId == "5") {
					return applicationForm;
				}
				return null;
			}

			@Override
			public Interview getInterview(Object applicationId) {
				// TODO Auto-generated method stub
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public List<RegisteredUser> getPendingInterviewers(List<Integer> pendingInterviewer, String applicationId) {
				if (pendingInterviewer.size() == 1 && pendingInterviewer.get(0) == 3) {
					return Arrays.asList(pendingInterviewerUser);
				}
				return Collections.EMPTY_LIST;
			}

		};

		EasyMock.expect(userServiceMock.getAllPreviousInterviewersOfProgram(program)).andReturn(
				Arrays.asList(defaultInterviewer, interviewer, pendingInterviewerUser, assignedInterviewer));
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> interviewersUsers = controller.getPreviousInterviewers("5", Arrays.asList(3));
		assertEquals(1, interviewersUsers.size());
		assertTrue(interviewersUsers.contains(interviewer));
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
		interviewerPropertyEditorMock = EasyMock.createMock(InterviewerPropertyEditor.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);

		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);

		controller = new InterviewController(applicationServiceMock, userServiceMock, userValidatorMock, messageSourceMock,
				interviewServiceMock, interviewValidator, datePropertyEditorMock, interviewerPropertyEditorMock) {

			@Override
			public Interview getInterview(Object applicationId) {
				// TODO Auto-generated method stub
				return null;
			}
		};

	}
}
