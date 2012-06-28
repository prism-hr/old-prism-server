package com.zuehlke.pgadmissions.controllers.workflow.interview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;

public class OldCreateNewInterviewerControllerTest {
	private static final String INTERVIEW_DETAILS_VIEW_NAME = "/private/staff/interviewers/interview_details";
	private OldCreateNewInterviewerController controller;
	private UserService userServiceMock;
	private MessageSource messageSourceMock;
	private BindingResult bindingResultMock;
	private InterviewService interviewServiceMock;
	private EncryptionHelper encryptionHelper;
	private ApplicationsService applicationsServiceMock;

	@Test
	@SuppressWarnings("unchecked")
	public void shouldCreateNewInterviewForNewInterviewUserIfUserDoesNotExists() {
		final List<String> encryptedList = new ArrayList<String>();
		encryptedList.add("encrypted5");
		controller = new OldCreateNewInterviewerController(applicationsServiceMock, userServiceMock, null,  messageSourceMock, interviewServiceMock, null, null, encryptionHelper){
			@Override
			public List<String> getEncryptedUserIds(List<Integer> newUserIds) {
				return encryptedList;
			}
		};
		
		
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", Authority.INTERVIEWER, DirectURLsEnum.ADD_INTERVIEW, application)).andReturn(user);
		EasyMock.replay(userServiceMock);
		interviewServiceMock.addInterviewerInPreviousInterview(application, user);
		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignInterviewer.user.created"), EasyMock.aryEq(new Object[] { "bob bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock, interviewServiceMock);
		ModelAndView modelAndView = controller.createInterviewerForNewInterview(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/interview/moveToInterview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingInterviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.containsAll(encryptedList));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldCreateNewInterviewForExistingInterviewUserIfUserDoesNotExists() {
		final List<String> encryptedList = new ArrayList<String>();
		encryptedList.add("encrypted5");
		controller = new OldCreateNewInterviewerController(applicationsServiceMock, userServiceMock, null,  messageSourceMock, interviewServiceMock, null, null, encryptionHelper){
			@Override
			public List<String> getEncryptedUserIds(List<Integer> newUserIds) {
				return encryptedList;
			}
		};
		
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", Authority.INTERVIEWER, DirectURLsEnum.ADD_INTERVIEW, application)).andReturn(user);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignInterviewer.user.created"), EasyMock.aryEq(new Object[] { "bob bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createInterviewerForExistingInterview(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/interview/assignInterviewers", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingInterviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.containsAll(encryptedList));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	
	@Test
	@SuppressWarnings("unchecked")
	public void shouldRetainPreviousListOfPedningInterviewersWhenCreatingNewUser() {
		List<RegisteredUser> pedningInterviewers = new ArrayList<RegisteredUser>(Arrays.asList(new RegisteredUserBuilder().id(1).toUser(),
				new RegisteredUserBuilder().id(2).toUser()));
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", Authority.INTERVIEWER, DirectURLsEnum.ADD_INTERVIEW, application)).andReturn(user);
		EasyMock.expect(encryptionHelper.encrypt(1)).andReturn("encryptedOne");
		EasyMock.expect(encryptionHelper.encrypt(2)).andReturn("encryptedTwo");
		EasyMock.expect(encryptionHelper.encrypt(5)).andReturn("encryptedFive");
		EasyMock.replay(userServiceMock, encryptionHelper);

		ModelAndView modelAndView = controller.createInterviewerForNewInterview(user, bindingResultMock, application, pedningInterviewers, Collections.EMPTY_LIST);

		Assert.assertEquals("redirect:/interview/moveToInterview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingInterviewer");
		assertEquals(3, newUser.size());
		assertTrue(newUser.containsAll(Arrays.asList("encryptedOne", "encryptedTwo", "encryptedFive")));
	}
	
	@Test
	public void shouldGetEncryptedIds(){
		List<Integer> newUserIds = new ArrayList<Integer>();
		newUserIds.add(1);
		newUserIds.add(5);
		newUserIds.add(10);
		EasyMock.expect(encryptionHelper.encrypt(1)).andReturn("encryptedOne");
		EasyMock.expect(encryptionHelper.encrypt(5)).andReturn("encryptedFive");
		EasyMock.expect(encryptionHelper.encrypt(10)).andReturn("encryptedTen");
		EasyMock.replay(encryptionHelper);
		List<String> encryptedUserIds = controller.getEncryptedUserIds(newUserIds);
		assertEquals(3, encryptedUserIds.size());
		assertTrue(encryptedUserIds.containsAll(Arrays.asList("encryptedOne", "encryptedFive", "encryptedTen")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldDoNothingIfUserExistsAndIsAlreadyInterviewerOfLatestInterviewOnApp() {

		EasyMock.reset(userServiceMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").program(new ProgramBuilder().toProgram())
				.latestInterview(new InterviewBuilder().interviewers(new InterviewerBuilder().user(user).toInterviewer()).toInterview()).toApplicationForm();

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(user);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignInterviewer.user.alreadyExistsInTheApplication"),
						EasyMock.aryEq(new Object[] { "bob bobson", "bobson@bob.com" }), EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createInterviewerForNewInterview(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);

		Assert.assertEquals("redirect:/interview/moveToInterview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingInterviewer");
		assertTrue(newUser.isEmpty());
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldDoNothingIfUserExistsAndIsAlreadyPedningInterviewerOfApp() {
		final List<String> encryptedList = new ArrayList<String>();
		encryptedList.add("encryptedOne");
		controller = new OldCreateNewInterviewerController(null, userServiceMock, null,  messageSourceMock, interviewServiceMock, null, null, encryptionHelper){
			@Override
			public List<String> getEncryptedUserIds(List<Integer> newUserIds) {
				return encryptedList;
			}
		};
		
		
		RegisteredUser existingPendingInterviewer = new RegisteredUserBuilder().id(1).firstName("Robert").lastName("Bobson").email("bobson@bob.com").toUser();
		List<RegisteredUser> pedningInterviewers = new ArrayList<RegisteredUser>(Arrays.asList(existingPendingInterviewer));
		EasyMock.reset(userServiceMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").program(new ProgramBuilder().toProgram())
				.latestInterview(new InterviewBuilder().interviewers(new InterviewerBuilder().user(user).toInterviewer()).toInterview()).toApplicationForm();

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingPendingInterviewer);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignInterviewer.user.pending"), EasyMock.aryEq(new Object[] { "Robert Bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);
		ModelAndView modelAndView = controller.createInterviewerForNewInterview(user, bindingResultMock, application, pedningInterviewers, Collections.EMPTY_LIST);

		Assert.assertEquals("redirect:/interview/moveToInterview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingInterviewer");
		assertEquals(1, newUser.size());
		assertEquals("encryptedOne", newUser.get(0));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldAddUserToPendingInterviewersIfExistingUserInPreviousInterviewerOfProgram() {
		final List<String> encryptedList = new ArrayList<String>();
		encryptedList.add("encryptedEight");
		controller = new OldCreateNewInterviewerController(null, userServiceMock, null,  messageSourceMock, interviewServiceMock, null, null, encryptionHelper){
			@Override
			public List<String> getEncryptedUserIds(List<Integer> newUserIds) {
				return encryptedList;
			}
		};
		
		
		RegisteredUser existingPreviousInterviewer = new RegisteredUserBuilder().id(8).firstName("Robert").lastName("Bobson").email("bobson@bob.com").toUser();
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingPreviousInterviewer);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignInterviewer.user.previous"),
						EasyMock.aryEq(new Object[] { "Robert Bobson", "bobson@bob.com" }), EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);

		ModelAndView modelAndView = controller.createInterviewerForNewInterview(user, bindingResultMock, application, Collections.EMPTY_LIST,
				Arrays.asList(existingPreviousInterviewer));
		Assert.assertEquals("redirect:/interview/moveToInterview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingInterviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.containsAll(encryptedList));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldAddUserToPendingInterviewersIfExistingUserIsDefaultInterviewOfProgram() {
		final List<String> encryptedList = new ArrayList<String>();
		encryptedList.add("encryptedEight");
		controller = new OldCreateNewInterviewerController(null, userServiceMock, null,  messageSourceMock, interviewServiceMock, null, null, encryptionHelper){
			@Override
			public List<String> getEncryptedUserIds(List<Integer> newUserIds) {
				return encryptedList;
			}
		};
		
		RegisteredUser existingDefaultInterviewer = new RegisteredUserBuilder().id(8).firstName("Robert").lastName("Bobson").email("bobson@bob.com").toUser();
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").program(new ProgramBuilder().interviewers(existingDefaultInterviewer).toProgram())
				.toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingDefaultInterviewer);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignInterviewer.user.alreadyInProgramme"),
						EasyMock.aryEq(new Object[] { "Robert Bobson", "bobson@bob.com" }), EasyMock.isNull(Locale.class))).andReturn("message");
		EasyMock.replay(messageSourceMock);

		ModelAndView modelAndView = controller.createInterviewerForNewInterview(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/interview/moveToInterview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingInterviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.containsAll(encryptedList));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldAddExistingUserToPendingIfUserExistsAndIsNewToAppAndProgram() {
		final List<String> encryptedList = new ArrayList<String>();
		encryptedList.add("encryptedEight");
		controller = new OldCreateNewInterviewerController(applicationsServiceMock, userServiceMock, null,  messageSourceMock, interviewServiceMock, null, null, encryptionHelper){
			@Override
			public List<String> getEncryptedUserIds(List<Integer> newUserIds) {
				return encryptedList;
			}
		};
		
		RegisteredUser existingUser = new RegisteredUserBuilder().id(8).firstName("Robert").lastName("Bobson").email("bobson@bob.com").toUser();
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().applicationNumber("ABC").id(2).program(new ProgramBuilder().toProgram()).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingUser);
		EasyMock.replay(userServiceMock);

		EasyMock.expect(
				messageSourceMock.getMessage(EasyMock.eq("assignInterviewer.user.added"), EasyMock.aryEq(new Object[] { "Robert Bobson", "bobson@bob.com" }),
						EasyMock.isNull(Locale.class))).andReturn("message");
		interviewServiceMock.addInterviewerInPreviousInterview(application, existingUser);
		applicationsServiceMock.save(application);
		EasyMock.replay(messageSourceMock, applicationsServiceMock, interviewServiceMock);

		ModelAndView modelAndView = controller.createInterviewerForNewInterview(user, bindingResultMock, application, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals("redirect:/interview/moveToInterview", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals("ABC", modelAndView.getModel().get("applicationId"));
		List<Integer> newUser = (List<Integer>) modelAndView.getModel().get("pendingInterviewer");
		assertEquals(1, newUser.size());
		assertTrue(newUser.containsAll(encryptedList));
		assertEquals("message", modelAndView.getModel().get("message"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldReturnToViewAndAssignValueIfValidationErrorsForCreateNew() {
		EasyMock.reset(bindingResultMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		ModelAndView modelAndView = controller.createInterviewerForNewInterview(user, bindingResultMock, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, modelAndView.getViewName());	
		assertEquals(false, modelAndView.getModel().get("assignOnly"));
	}
	@Test
	@SuppressWarnings("unchecked")
	public void shouldReturnToViewAndAssignValueIfValidationErrorsForaAssign(){
		EasyMock.reset(bindingResultMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").toUser();
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		ModelAndView modelAndView = controller.createInterviewerForExistingInterview(user, bindingResultMock, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Assert.assertEquals(INTERVIEW_DETAILS_VIEW_NAME, modelAndView.getViewName());	
		assertEquals(true, modelAndView.getModel().get("assignOnly"));
	}
	
	@Test
	public void shouldReturnNewInterviewIfNoInterviewId(){
		Interview interview = controller.getInterview(null);
		assertNull(interview.getId());
	}
	
	@Test
	public void shouldReturnNewInterviewIfInterviewIdIsBlank(){
		Interview interview = controller.getInterview("");
		assertNull(interview.getId());
	}
	@Test
	public void shouldReturnInterviewIfIdGiven(){
		Interview interview = new InterviewBuilder().id(4).toInterview();
		EasyMock.expect(encryptionHelper.decryptToInteger("23")).andReturn(4);
		EasyMock.expect(interviewServiceMock.getInterviewById(4)).andReturn(interview);
		EasyMock.replay(interviewServiceMock, encryptionHelper);
		
		assertEquals(interview, controller.getInterview(23));

		EasyMock.verify(interviewServiceMock, encryptionHelper);
	}

	@Before
	public void setup() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		interviewServiceMock = EasyMock.createMock(InterviewService.class);
		encryptionHelper = EasyMock.createMock(EncryptionHelper.class);
		
		controller = new OldCreateNewInterviewerController(applicationsServiceMock, userServiceMock, null,  messageSourceMock, interviewServiceMock, null, null, encryptionHelper);
	}
}
