package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;

public class CreateNewSupervisorControllerTest {
	protected final String APROVAL_DETAILS_VIEW_NAME = "/private/staff/supervisors/approval_details";
	private CreateNewSupervisorController controller;
	private UserService userServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;
	private ApprovalService approvalServiceMock;
	private SupervisorPropertyEditor supervisorProertyEditorMock;
	private ApprovalRoundValidator approvalroundValidator;

	@Test
	@SuppressWarnings("unchecked")
	public void shouldCreateNewApprovalRoundForNewSupervisorUserIfUserDoesNotExists() {
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", Authority.SUPERVISOR)).andReturn(user);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignSupervisor.user.created"), EasyMock.aryEq(new Object[] { "bob bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createSupervisorForNewApprovalRound(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/approval/moveToApproval", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals(2, modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingSupervisors");
		assertEquals(1, newUser.size());
		assertTrue(newUser.containsAll(Arrays.asList(5)));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldCreateNewApprovalRoundForExistingSupervisorUserIfUserDoesNotExists() {
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", Authority.SUPERVISOR)).andReturn(user);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignSupervisor.user.created"), EasyMock.aryEq(new Object[] { "bob bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createSupervisorForNewApprovalRound(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/approval/moveToApproval", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals(2, modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingSupervisors");
		assertEquals(1, newUser.size());
		assertTrue(newUser.containsAll(Arrays.asList(5)));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	
	@Test
	@SuppressWarnings("unchecked")
	public void shouldRetainPreviousListOfPedningSupervisorsWhenCreatingNewUser() {
		List<RegisteredUser> pedningSupervisors = new ArrayList<RegisteredUser>(Arrays.asList(new RegisteredUserBuilder().id(1).toUser(),
				new RegisteredUserBuilder().id(2).toUser()));
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", Authority.SUPERVISOR)).andReturn(user);
		EasyMock.replay(userServiceMock);

		ModelAndView modelAndView = controller.createSupervisorForNewApprovalRound(user, bindingResultMock, application, pedningSupervisors, Collections.EMPTY_LIST);

		Assert.assertEquals("redirect:/approval/moveToApproval", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals(2, modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingSupervisors");
		assertEquals(3, newUser.size());
		assertTrue(newUser.containsAll(Arrays.asList(1, 2, 5)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldDoNothingIfUserExistsAndIsAlreadySupervisorOfLatestApprovalRoundOnApp() {

		EasyMock.reset(userServiceMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		ApplicationForm application = new ApplicationFormBuilder().id(2).program(new ProgramBuilder().toProgram())
				.latestApprovalRound(new ApprovalRoundBuilder().supervisors(new SupervisorBuilder().user(user).toSupervisor()).toApprovalRound()).toApplicationForm();

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(user);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignSupervisor.user.alreadyExistsInTheApplication"),
						EasyMock.aryEq(new Object[] { "bob bobson", "bobson@bob.com" }), EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createSupervisorForNewApprovalRound(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);

		Assert.assertEquals("redirect:/approval/moveToApproval", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals(2, modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingSupervisors");
		assertTrue(newUser.isEmpty());
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldDoNothingIfUserExistsAndIsAlreadyPedningSupervisorOfApp() {
		RegisteredUser existingPendingSupervisors = new RegisteredUserBuilder().id(1).firstName("Robert").lastName("Bobson").email("bobson@bob.com").toUser();
		List<RegisteredUser> pedningSupervisors = new ArrayList<RegisteredUser>(Arrays.asList(existingPendingSupervisors));
		EasyMock.reset(userServiceMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		ApplicationForm application = new ApplicationFormBuilder().id(2).program(new ProgramBuilder().toProgram())
				.latestApprovalRound(new ApprovalRoundBuilder().supervisors(new SupervisorBuilder().user(user).toSupervisor()).toApprovalRound()).toApplicationForm();

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingPendingSupervisors);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignSupervisor.user.pending"), EasyMock.aryEq(new Object[] { "Robert Bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createSupervisorForNewApprovalRound(user, bindingResultMock, application, pedningSupervisors, Collections.EMPTY_LIST);

		Assert.assertEquals("redirect:/approval/moveToApproval", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals(2, modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingSupervisors");
		assertEquals(1, newUser.size());
		assertEquals((Integer) 1, newUser.get(0));
		assertEquals("message", modelAndView.getModel().get("message"));
	}
 
	@Before
	public void setup() {
		userServiceMock = EasyMock.createMock(UserService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		approvalServiceMock = EasyMock.createMock(ApprovalService.class);
		supervisorProertyEditorMock = EasyMock.createMock(SupervisorPropertyEditor.class);
		approvalroundValidator = EasyMock.createMock(ApprovalRoundValidator.class);
		
		controller = new CreateNewSupervisorController(null, userServiceMock, null,null, approvalServiceMock, messageSourceMock, supervisorProertyEditorMock);
		}
}
