package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.controllers.workflow.interview.InterviewController;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class ApprovalControllerTest {
	private ApprovalController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	private NewUserByAdminValidator userValidatorMock;
	private ApprovalService approvalServiceMock;
	private SupervisorPropertyEditor supervisorProertyEditorMock;

	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;

	private RegisteredUser currentUserMock;

	private EncryptionHelper encryptionHelperMock;

	@Test
	public void shouldAddRegisteredUserValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(userValidatorMock);
		EasyMock.replay(binderMock);
		controller.registerSupervisorValidators(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldReturnExistingInterviewersBelongingToApplication() {
		Program program = new ProgramBuilder().id(6).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		controller = new ApprovalController(applicationServiceMock, userServiceMock, userValidatorMock, null, approvalServiceMock, messageSourceMock,
				supervisorProertyEditorMock, encryptionHelperMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

			@Override
			public ApprovalRound getApprovalRound(String applicationId) {
				return null;
			}
		};
		RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();
		RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();

		Supervisor super1 = new SupervisorBuilder().user(interUser1).id(4).toSupervisor();
		Supervisor super2 = new SupervisorBuilder().user(interUser2).id(5).toSupervisor();

		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).toApprovalRound();
		approvalRound.setSupervisors(Arrays.asList(super1, super2));
		applicationForm.setLatestApprovalRound(approvalRound);

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		Set<RegisteredUser> interviewersUsers = controller.getApplicationSupervisorsAsUsers(applicationForm.getApplicationNumber());
		assertEquals(2, interviewersUsers.size());
	}

	@Test
	public void shouldGetProgrammeSupervisorsAndRemovePendingAndAssignedSupervisorUsers() {
		final RegisteredUser superUser1 = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser superUser2 = new RegisteredUserBuilder().id(6).toUser();
		final RegisteredUser superUser3 = new RegisteredUserBuilder().id(8).toUser();
		final RegisteredUser superUser4 = new RegisteredUserBuilder().id(9).toUser();

		final Program program = new ProgramBuilder().supervisors(superUser1, superUser2, superUser3, superUser4).id(6).toProgram();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(new SupervisorBuilder().user(superUser4).toSupervisor()).id(1).toApprovalRound();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().approvalRounds(approvalRound).latestApprovalRound(approvalRound)
				.acceptedTerms(CheckedStatus.NO).id(5).program(program).toApplicationForm();
		controller = new ApprovalController(applicationServiceMock, userServiceMock, userValidatorMock, null, approvalServiceMock, messageSourceMock,
				supervisorProertyEditorMock, encryptionHelperMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId == "5") {
					return applicationForm;
				}
				return null;
			}

			@Override
			public ApprovalRound getApprovalRound(String applicationId) {
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public List<RegisteredUser> getPendingSupervisors(List<String> pendingSupervisors, String applicationId) {
				if (pendingSupervisors.size() == 1 && pendingSupervisors.get(0) == "3") {
					return Arrays.asList(superUser3);
				}
				return Collections.EMPTY_LIST;
			}

		};

		List<RegisteredUser> supervisorsUsers = controller.getProgrammeSupervisors("5", Arrays.asList("3"));
		assertEquals(2, supervisorsUsers.size());
	}

	@Test
	public void shouldReturnNewUser() {
		assertNotNull(controller.getSupervisor());
		assertNull(controller.getSupervisor().getId());
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
	public void shouldGetApplicationFromIdForSupervisor() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isSupervisorOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);
	}

	@Test
	public void shouldGetApplicationFromIdForApprover() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isSupervisorOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
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
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminOrSupervisorOrApproverOfApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isSupervisorOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);

		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		controller.getApplicationForm("5");
	}

	@Test
	public void shouldReturnPendingSueprvisorsAndRemoveExistingSuprvisorsFromList() {
		List<String> ids = Arrays.asList("abc", "def");
		EasyMock.expect(encryptionHelperMock.decryptToInteger("abc")).andReturn(1);
		EasyMock.expect(encryptionHelperMock.decryptToInteger("def")).andReturn(8);
		EasyMock.replay(encryptionHelperMock);
		EasyMock.reset(userServiceMock);
		RegisteredUser newUser1 = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser newUser2 = new RegisteredUserBuilder().id(8).toUser();

		EasyMock.expect(userServiceMock.getUser(1)).andReturn(newUser1);
		EasyMock.expect(userServiceMock.getUser(8)).andReturn(newUser2);
		EasyMock.replay(userServiceMock);

		String applicationNumber = "5";
		final ApplicationForm applicationForm = new ApplicationFormBuilder()
				.latestApprovalRound(new ApprovalRoundBuilder().supervisors(new SupervisorBuilder().user(newUser2).toSupervisor()).toApprovalRound()).id(2)
				.applicationNumber(applicationNumber).toApplicationForm();
		controller = new ApprovalController(applicationServiceMock, userServiceMock, userValidatorMock, null, approvalServiceMock, messageSourceMock,
				supervisorProertyEditorMock, encryptionHelperMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if ("5".equals(applicationId)) {
					return applicationForm;
				}
				return null;
			}

			@Override
			public ApprovalRound getApprovalRound(String applicationId) {
				return null;
			}
		};
		List<RegisteredUser> newUsers = controller.getPendingSupervisors(ids, applicationNumber);
		assertEquals(1, newUsers.size());
		assertEquals(newUser1, newUsers.get(0));

	}

	@Test
	public void shouldGetListOfPreviousSupervisorsAndRemovePendingAssignedOrDefaultSupervisors() {
		EasyMock.reset(userServiceMock);
		final RegisteredUser defaultSupervisor = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser supervisor = new RegisteredUserBuilder().id(6).toUser();
		final RegisteredUser pendingSupervisorUser = new RegisteredUserBuilder().id(8).toUser();
		final RegisteredUser assignedSupervisor = new RegisteredUserBuilder().id(9).toUser();

		final Program program = new ProgramBuilder().supervisors(defaultSupervisor).id(6).toProgram();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).supervisors(new SupervisorBuilder().user(assignedSupervisor).toSupervisor())
				.toApprovalRound();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(approvalRound).id(5).program(program).toApplicationForm();
		controller = new ApprovalController(applicationServiceMock, userServiceMock, userValidatorMock, null, approvalServiceMock, messageSourceMock,
				supervisorProertyEditorMock, encryptionHelperMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId == "5") {
					return applicationForm;
				}
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public List<RegisteredUser> getPendingSupervisors(List<String> pendingSupervisor, String applicationId) {
				if (pendingSupervisor.size() == 1 && pendingSupervisor.get(0) == "3") {
					return Arrays.asList(pendingSupervisorUser);
				}
				return Collections.EMPTY_LIST;
			}

			@Override
			public ApprovalRound getApprovalRound(String applicationId) {

				return null;
			}

		};

		EasyMock.expect(userServiceMock.getAllPreviousSupervisorsOfProgram(program)).andReturn(
				Arrays.asList(defaultSupervisor, supervisor, pendingSupervisorUser, assignedSupervisor));
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> supervisorsUsers = controller.getPreviousSupervisors("5", Arrays.asList("3"));
		assertTrue(supervisorsUsers.contains(supervisor));
		assertEquals(1, supervisorsUsers.size());
		assertTrue(supervisorsUsers.contains(supervisor));
	}

	@Test
	public void shouldGetListOfInterviewersWillingSuperviseAndRemovePendingAndAssignedSupervisors() {
		EasyMock.reset(userServiceMock);

		final RegisteredUser supervisor = EasyMock.createMock(RegisteredUser.class);
		final RegisteredUser pendingSupervisorUser = EasyMock.createMock(RegisteredUser.class);
		RegisteredUser assigneSupervisorMock = EasyMock.createMock(RegisteredUser.class);

		final ApplicationForm applicationForm = EasyMock.createMock(ApplicationForm.class);

		controller = new ApprovalController(applicationServiceMock, userServiceMock, userValidatorMock, null, approvalServiceMock, messageSourceMock,
				supervisorProertyEditorMock, encryptionHelperMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId == "5") {
					return applicationForm;
				}
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public List<RegisteredUser> getPendingSupervisors(List<String> pendingSupervisor, String applicationId) {
				if (pendingSupervisor.size() == 1 && pendingSupervisor.get(0) == "enc3") {
					return Arrays.asList(pendingSupervisorUser);
				}
				return Collections.EMPTY_LIST;
			}

			@Override
			public ApprovalRound getApprovalRound(String applicationId) {

				return null;
			}

		};

		EasyMock.expect(applicationForm.getUsersWillingToSupervise()).andReturn(Arrays.asList(supervisor, pendingSupervisorUser, assigneSupervisorMock));
		EasyMock.expect(supervisor.isSupervisorOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(pendingSupervisorUser.isSupervisorOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(assigneSupervisorMock.isSupervisorOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.replay(applicationForm, assigneSupervisorMock, supervisor, pendingSupervisorUser);
		List<RegisteredUser> supervisorUsers = controller.getWillingToSuperviseusers("5", Arrays.asList("enc3"));
		assertEquals(1, supervisorUsers.size());
		assertTrue(supervisorUsers.contains(supervisor));
	}

	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
		approvalServiceMock = EasyMock.createMock(ApprovalService.class);
		supervisorProertyEditorMock = EasyMock.createMock(SupervisorPropertyEditor.class);

		messageSourceMock = EasyMock.createMock(MessageSource.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);

		controller = new ApprovalController(applicationServiceMock, userServiceMock, userValidatorMock, null, approvalServiceMock, messageSourceMock,
				supervisorProertyEditorMock, encryptionHelperMock) {
			@Override
			public ApprovalRound getApprovalRound(String applicationId) {
				return null;
			}
		};

	}
}
