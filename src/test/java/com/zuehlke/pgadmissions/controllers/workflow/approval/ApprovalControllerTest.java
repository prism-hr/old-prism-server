package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RequestRestartComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RequestRestartCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;

public class ApprovalControllerTest {
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private RegisteredUser currentUserMock;
	private ApprovalController controller;
	private ApprovalRoundValidator approvalRoundValidatorMock;
	private SupervisorPropertyEditor supervisorPropertyEditorMock;
	private ApprovalService approvalServiceMock;
	private BindingResult bindingResultMock;
	private DocumentPropertyEditor documentPropertyEditorMock;
	private GenericCommentValidator commentValidatorMock;

	@Test
	public void shouldGetApprovalPage() {
		Assert.assertEquals("/private/staff/supervisors/approval_details", controller.getMoveToApprovalPage());
	}

	@Test
	public void shouldGetSupervisorsSection() {
		Assert.assertEquals("/private/staff/supervisors/supervisors_section", controller.getSupervisorSection());
	}

//	@Test
//	public void shouldGetRequestApprovalPage() {
//		Assert.assertEquals("/private/staff/approver/request_restart_approve_page", controller.getRequestRestartPage());
//	}

	@Test
	public void shouldGetProgrammeSupervisors() {
		final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).toUser();

		final Program program = new ProgramBuilder().supervisors(interUser1, interUser2).id(6).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
		controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
				supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("5")) {
					return applicationForm;
				}
				return null;
			}

		};

		List<RegisteredUser> supervisorsUsers = controller.getProgrammeSupervisors("5");
		assertEquals(2, supervisorsUsers.size());
		assertTrue(supervisorsUsers.containsAll(Arrays.asList(interUser1, interUser2)));
	}

	@Test
	public void shouldGetListOfPreviousSupervisorsAndAddReviewersWillingToApprovalRoundWitDefaultSupervisorsRemoved() {
		EasyMock.reset(userServiceMock);
		final RegisteredUser defaultSupervisor = new RegisteredUserBuilder().id(9).toUser();
		final RegisteredUser interviewerWillingToSuperviseOne = new RegisteredUserBuilder().id(8).toUser();
		final RegisteredUser interviewerWillingToSuperviseTwo = new RegisteredUserBuilder().id(7).toUser();
		final RegisteredUser previousSupervisor = new RegisteredUserBuilder().id(6).toUser();
		InterviewComment interviewOne = new InterviewCommentBuilder().id(1).user(interviewerWillingToSuperviseOne).willingToSupervise(true)
				.toInterviewComment();
		InterviewComment interviewTwo = new InterviewCommentBuilder().id(1).user(defaultSupervisor).willingToSupervise(true).toInterviewComment();
		InterviewComment interviewThree = new InterviewCommentBuilder().id(1).user(interviewerWillingToSuperviseTwo).willingToSupervise(true)
				.toInterviewComment();

		final Program program = new ProgramBuilder().id(6).supervisors(defaultSupervisor).toProgram();

		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).comments(interviewOne, interviewTwo, interviewThree)
				.toApplicationForm();
		controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
				supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("5")) {
					return applicationForm;
				}
				return null;
			}

		};

		EasyMock.expect(userServiceMock.getAllPreviousSupervisorsOfProgram(program)).andReturn(
				Arrays.asList(previousSupervisor, defaultSupervisor, interviewerWillingToSuperviseOne));
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> interviewerUsers = controller.getPreviousSupervisorsAndInterviewersWillingToSupervise("5");
		assertEquals(3, interviewerUsers.size());
		assertTrue(interviewerUsers.containsAll(Arrays.asList(previousSupervisor, interviewerWillingToSuperviseOne, interviewerWillingToSuperviseTwo)));
	}

	@Test
	public void shouldReturnNewApprovalRoundWithExistingRoundsSupervisorsIfAny() {
		Supervisor supervisorOne = new SupervisorBuilder().id(1).toSupervisor();
		Supervisor suprvisorTwo = new SupervisorBuilder().id(2).toSupervisor();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc")
				.latestApprovalRound(new ApprovalRoundBuilder().supervisors(supervisorOne, suprvisorTwo).toApprovalRound()).toApplicationForm();

		controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
				supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("bob")) {
					return application;
				}
				return null;
			}

		};
		ApprovalRound returnedApprovalRound = controller.getApprovalRound("bob");
		assertNull(returnedApprovalRound.getId());
		assertEquals(2, returnedApprovalRound.getSupervisors().size());
		assertTrue(returnedApprovalRound.getSupervisors().containsAll(Arrays.asList(supervisorOne, suprvisorTwo)));
	}

	@Test
	public void shouldReturnApprovalRoundWithWillingToApprovalRoundWithSupervisorsOfPreviousApprovalRoundRemoved() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		InterviewComment interviewOne = new InterviewCommentBuilder().id(1).user(userOne).willingToSupervise(true).toInterviewComment();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		InterviewComment interviewTwo = new InterviewCommentBuilder().id(2).user(userTwo).willingToSupervise(true).toInterviewComment();
		RegisteredUser userThree = new RegisteredUserBuilder().id(3).toUser();
		InterviewComment interviewThree = new InterviewCommentBuilder().id(3).user(userThree).willingToSupervise(true).toInterviewComment();
		Supervisor interviewerOne = new SupervisorBuilder().id(1).user(userOne).toSupervisor();
		Supervisor interviewerTwo = new SupervisorBuilder().id(2).user(userTwo).toSupervisor();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").comments(interviewOne, interviewTwo, interviewThree)
				.latestApprovalRound(new ApprovalRoundBuilder().supervisors(interviewerOne, interviewerTwo).toApprovalRound()).toApplicationForm();

		controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
				supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("bob")) {
					return application;
				}
				return null;
			}

		};
		ApprovalRound returnedApprovalRound = controller.getApprovalRound("bob");
		assertNull(returnedApprovalRound.getId());
		assertEquals(3, returnedApprovalRound.getSupervisors().size());
		assertTrue(returnedApprovalRound.getSupervisors().containsAll(Arrays.asList(interviewerOne, interviewerTwo)));
		assertNull(returnedApprovalRound.getSupervisors().get(2).getId());
		assertEquals(userThree, returnedApprovalRound.getSupervisors().get(2).getUser());
	}

	@Test
	public void shouldReturnNewApprovalRoundWithEmtpySupervisorsIfNoLatestApprovalRound() {

		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").toApplicationForm();

		controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
				supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if (applicationId.equals("bob")) {
					return application;
				}
				return null;
			}

		};
		ApprovalRound returnedApprovalRound = controller.getApprovalRound("bob");
		assertNull(returnedApprovalRound.getId());
		assertTrue(returnedApprovalRound.getSupervisors().isEmpty());

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
	public void shouldGetApplicationFromIdForApprover() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
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
	public void shouldThrowResourceNotFoundExceptionIfUserNotAdminOrApproverOfApplicationProgram() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);

		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUserMock);

		controller.getApplicationForm("5");
	}

	@Test
	public void shouldGetCurrentUserAsUser() {
		assertEquals(currentUserMock, controller.getUser());
	}

	@Test
	public void shouldMoveApplicationToApprovalRound() {
		ApprovalRound interview = new ApprovalRoundBuilder().id(4).toApprovalRound();
		final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").toApplicationForm();

		controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
				supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return application;
			}

		};

		approvalServiceMock.moveApplicationToApproval(application, interview);
		EasyMock.replay(approvalServiceMock);

		String view = controller.moveToApproval("abc", interview, bindingResultMock);
		assertEquals("/private/common/ajax_OK", view);
		EasyMock.verify(approvalServiceMock);

	}

	@Test
	public void shouldNotSaveApprovalRoundAndReturnToApprovalPageIfHasErrors() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
				supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				return applicationForm;
			}

		};
		ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).toApprovalRound();
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		EasyMock.replay(errorsMock, applicationServiceMock);
		assertEquals("/private/staff/supervisors/supervisors_section", controller.moveToApproval("abc", approvalRound, errorsMock));

	}

	@Test
	public void shouldAddApprovalRoundValidatorAndSupervisorPropertyEditor() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(approvalRoundValidatorMock);
		binderMock.registerCustomEditor(Supervisor.class, supervisorPropertyEditorMock);

		EasyMock.replay(binderMock);
		controller.registerValidatorAndPropertyEditorForApprovalRound(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetRequestRequestRestartCommentWithApplicationAndCurrentUser() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
				supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock) {
			@Override
			public ApplicationForm getApplicationForm(String applicationId) {
				if ("5".equals(applicationId)) {
					return applicationForm;
				}
				return null;
			}
		};
		RequestRestartComment comment = controller.getRequestRestartComment("5");
		assertEquals(applicationForm, comment.getApplication());
		assertEquals(currentUserMock, comment.getUser());
	}

	@Test
	public void shouldAddCommentValidatorAndDocumentPropertyEditor() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(commentValidatorMock);
		binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);

		EasyMock.replay(binderMock);
		controller.registerValidatorAndPropertyEditorForComment(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Test
	public void shouldRequestRestartOfApproval() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").toApplicationForm();
		
		RequestRestartComment comment = new RequestRestartCommentBuilder().id(9).comment("request restart").toComment();


		approvalServiceMock.requestApprovalRestart(applicationForm, currentUserMock, comment);
		EasyMock.replay(approvalServiceMock);
		assertEquals("redirect:/applications?messageCode=request.approval.restart&application=LALALA", controller.requestRestart(applicationForm, comment, bindingResultMock));
		EasyMock.verify(approvalServiceMock);
	
	}
	@Test
	public void shouldReturnToReequestRestartPageIfErrors() {
		EasyMock.reset(bindingResultMock);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").toApplicationForm();		
		RequestRestartComment comment = new RequestRestartCommentBuilder().id(9).comment("request restart").toComment();

		EasyMock.replay(approvalServiceMock);
		assertEquals("/private/staff/approver/request_restart_approve_page", controller.requestRestart(applicationForm, comment, bindingResultMock));
		EasyMock.verify(approvalServiceMock);
	
	}
	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		approvalRoundValidatorMock = EasyMock.createMock(ApprovalRoundValidator.class);
		supervisorPropertyEditorMock = EasyMock.createMock(SupervisorPropertyEditor.class);
		approvalServiceMock = EasyMock.createMock(ApprovalService.class);
		documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
		commentValidatorMock = EasyMock.createMock(GenericCommentValidator.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);

		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);

		controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
				supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock);

	}
}
