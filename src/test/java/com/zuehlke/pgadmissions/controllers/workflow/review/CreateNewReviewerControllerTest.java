package com.zuehlke.pgadmissions.controllers.workflow.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;

public class CreateNewReviewerControllerTest {
	protected static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/admin/assign_reviewers_to_appl_page";
	private CreateNewReviewerController controller;
	private UserService userServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;
	private EncryptionHelper encryptionHelperMock;
	private ApplicationsService applicationsServiceMock;
	private ReviewService reviewServiceMock;

	@Test
	@SuppressWarnings("unchecked")
	public void shouldCreateNewReviewRoundForNewReviewRoundUserIfUserDoesNotExists() {
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("bob");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", Authority.REVIEWER, DirectURLsEnum.ADD_REVIEW, application)).andReturn(user);
		EasyMock.replay(userServiceMock, encryptionHelperMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignReviewer.user.created"), EasyMock.aryEq(new Object[] { "bob bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createReviewerForNewReviewRound(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/review/moveToReview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<String> newUser = (List<String>) modelAndView.getModel().get("pendingReviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.contains("bob"));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldCreateNewReviewRoundForExistingReviewRoundUserIfUserDoesNotExists() {
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("bob");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", Authority.REVIEWER, DirectURLsEnum.ADD_REVIEW, application)).andReturn(user);
		
		EasyMock.replay(userServiceMock, encryptionHelperMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignReviewer.user.created"), EasyMock.aryEq(new Object[] { "bob bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createReviewerForExistingReviewRound(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/review/assignReviewers", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingReviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.contains("bob"));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	
	@Test
	@SuppressWarnings("unchecked")
	public void shouldRetainPreviousListOfPedningReviewersWhenCreatingNewUser() {
		List<RegisteredUser> pedningReviewers = new ArrayList<RegisteredUser>(Arrays.asList(new RegisteredUserBuilder().id(1).toUser(),
				new RegisteredUserBuilder().id(2).toUser()));
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(encryptionHelperMock.encrypt(1)).andReturn("ab");
		EasyMock.expect(encryptionHelperMock.encrypt(2)).andReturn("cd");
		EasyMock.expect(encryptionHelperMock.encrypt(5)).andReturn("ef");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", Authority.REVIEWER, DirectURLsEnum.ADD_REVIEW, application)).andReturn(user);
		
		EasyMock.replay(userServiceMock, encryptionHelperMock);

		ModelAndView modelAndView = controller.createReviewerForNewReviewRound(user, bindingResultMock, application, pedningReviewers, Collections.EMPTY_LIST);

		Assert.assertEquals("redirect:/review/moveToReview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<String> newUser = (List<String>) modelAndView.getModel().get("pendingReviewer");
		assertEquals(3, newUser.size());
		assertTrue(newUser.containsAll(Arrays.asList("ab", "cd", "ef")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldDoNothingIfUserExistsAndIsAlreadyReviewerOfLatestReviewRoundOnApp() {

		EasyMock.reset(userServiceMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").program(new ProgramBuilder().toProgram())
				.latestReviewRound(new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(user).toReviewer()).toReviewRound()).toApplicationForm();

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(user);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignReviewer.user.alreadyExistsInTheApplication"),
						EasyMock.aryEq(new Object[] { "bob bobson", "bobson@bob.com" }), EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createReviewerForNewReviewRound(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);

		Assert.assertEquals("redirect:/review/moveToReview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingReviewer");
		assertTrue(newUser.isEmpty());
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldDoNothingIfUserExistsAndIsAlreadyPedningReviewerOfApp() {
		RegisteredUser existingPendingReviewer = new RegisteredUserBuilder().id(1).firstName("Robert").lastName("Bobson").email("bobson@bob.com").toUser();
		List<RegisteredUser> pedningReviewers = new ArrayList<RegisteredUser>(Arrays.asList(existingPendingReviewer));
		EasyMock.reset(userServiceMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(encryptionHelperMock.encrypt(1)).andReturn("bob");
		ApplicationForm application = new ApplicationFormBuilder().applicationNumber("ABC").id(2).program(new ProgramBuilder().toProgram())
				.latestReviewRound(new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(user).toReviewer()).toReviewRound()).toApplicationForm();

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingPendingReviewer);
		EasyMock.replay(userServiceMock, encryptionHelperMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignReviewer.user.pending"), EasyMock.aryEq(new Object[] { "Robert Bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createReviewerForNewReviewRound(user, bindingResultMock, application, pedningReviewers, Collections.EMPTY_LIST);

		Assert.assertEquals("redirect:/review/moveToReview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingReviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.contains("bob"));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldAddUserToPendingReviewersIfExistingUserInPreviousReviewerOfProgram() {
		RegisteredUser existingPreviousReviewer = new RegisteredUserBuilder().id(8).firstName("Robert").lastName("Bobson").email("bobson@bob.com").toUser();
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(encryptionHelperMock.encrypt(8)).andReturn("bob");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingPreviousReviewer);
		EasyMock.replay(userServiceMock,encryptionHelperMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignReviewer.user.previous"),
						EasyMock.aryEq(new Object[] { "Robert Bobson", "bobson@bob.com" }), EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);

		ModelAndView modelAndView = controller.createReviewerForNewReviewRound(user, bindingResultMock, application, Collections.EMPTY_LIST,
				Arrays.asList(existingPreviousReviewer));
		Assert.assertEquals("redirect:/review/moveToReview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingReviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.contains("bob"));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldAddUserToPendingReviewersIfExistingUserIsDefaultReviewRoundOfProgram() {
		RegisteredUser existingDefaultReviewer = new RegisteredUserBuilder().id(8).firstName("Robert").lastName("Bobson").email("bobson@bob.com").toUser();
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").program(new ProgramBuilder().reviewers(existingDefaultReviewer).toProgram())
				.toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(encryptionHelperMock.encrypt(8)).andReturn("bob");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingDefaultReviewer);
		EasyMock.replay(userServiceMock, encryptionHelperMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignReviewer.user.alreadyInProgramme"),
						EasyMock.aryEq(new Object[] { "Robert Bobson", "bobson@bob.com" }), EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);

		ModelAndView modelAndView = controller.createReviewerForNewReviewRound(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/review/moveToReview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingReviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.contains("bob"));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldAddExistingUserToDefaultIfUserExistsAndIsNewToAppAndProgram() {
		RegisteredUser existingUser = new RegisteredUserBuilder().id(8).firstName("Robert").lastName("Bobson").email("bobson@bob.com").toUser();
		EasyMock.reset(userServiceMock);
		Program program = new ProgramBuilder().id(3).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().id(3).program(program).id(2).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(encryptionHelperMock.encrypt(8)).andReturn("bob");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingUser);
		EasyMock.replay(userServiceMock,encryptionHelperMock);

		
		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignReviewer.user.added"), EasyMock.aryEq(new Object[] { "Robert Bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);

		ModelAndView modelAndView = controller.createReviewerForNewReviewRound(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/review/moveToReview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingReviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.contains("bob"));
		assertEquals("message", modelAndView.getModel().get("message"));
	}
	
	@Test
	public void shouldAddReviewerRoleToExistingUserNotInProgrammeNotInAnyReviewRound(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).email("jo@jo.com").toUser();
		Program program = new ProgramBuilder().reviewers(new RegisteredUserBuilder().id(17).toUser()).id(3).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).id(3).toApplicationForm();
		List<RegisteredUser> pendingUsers = new ArrayList<RegisteredUser>();
		List<RegisteredUser> previousUsers = new ArrayList<RegisteredUser>();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("jo@jo.com")).andReturn(user);
		userServiceMock.updateUserWithNewRoles(user, program, Authority.REVIEWER);
		EasyMock.replay(userServiceMock);
		controller.createReviewerForNewReviewRound(user, bindingResultMock, application, pendingUsers, previousUsers);
		
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldReturnToViewIfValidationErrorsForNewReviewRound() {
		EasyMock.reset(bindingResultMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		ModelAndView modelAndView = controller.createReviewerForNewReviewRound(user, bindingResultMock, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals(REVIEW_DETAILS_VIEW_NAME, modelAndView.getViewName());
		assertEquals(false, modelAndView.getModel().get("assignOnly"));

	}
	@Test
	@SuppressWarnings("unchecked")
	public void shouldReturnToViewIfValidationErrorsForExistingReviewRound() {
		EasyMock.reset(bindingResultMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		ModelAndView modelAndView = controller.createReviewerForExistingReviewRound(user, bindingResultMock, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals(REVIEW_DETAILS_VIEW_NAME, modelAndView.getViewName());
		assertEquals(true, modelAndView.getModel().get("assignOnly"));

	}

	@Before
	public void setup() {
		reviewServiceMock = EasyMock.createMock(ReviewService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		controller = new CreateNewReviewerController(applicationsServiceMock, userServiceMock, null, null, reviewServiceMock, messageSourceMock, null, encryptionHelperMock);
	}
}
