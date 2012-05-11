package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class AssignReviewerControllerTest {
	private static final String VIEW_RESULT = "private/staff/admin/assign_reviewers_to_appl_page";
	private static final String AFTER_MOVE_TO_REVIEW_VIEW = "redirect:/applications";
	private AssignReviewerController controllerUT;

	private ApplicationForm application;
	private ApplicationsService applicationServiceMock;
	private ReviewService reviewServiceMock;
	private UserService userServiceMock;
	private NewUserByAdminValidator userValidatorMock;
	private BindingResult bindingResultMock;
	private MessageSource messageSourceMock;

	private UsernamePasswordAuthenticationToken authenticationToken;
	private Program program;
	private RegisteredUser admin;
	private RegisteredUser reviewerUser1;
	private RegisteredUser reviewerUser2;
	private RegisteredUser otherReviewerUser;

	@Before
	public void setUp() {
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		admin = new RegisteredUserBuilder().id(1).username("admin").role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		reviewServiceMock = EasyMock.createMock(ReviewService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		controllerUT = new AssignReviewerController(applicationServiceMock, reviewServiceMock, userServiceMock,//
				userValidatorMock, messageSourceMock);

		reviewerUser1 = new RegisteredUserBuilder().id(2).username("rev 1").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		reviewerUser2 = new RegisteredUserBuilder().id(3).username("rev 2").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		otherReviewerUser = new RegisteredUserBuilder().id(4).username("i review others")//
				.role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		program = new ProgramBuilder().id(100).administrators(admin).reviewers(reviewerUser1, reviewerUser2).toProgram();

		application = new ApplicationFormBuilder().id(10).status(ApplicationFormStatus.VALIDATION)//
				.program(program)//
				.toApplicationForm();

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void shouldAddRegisteredUserValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(userValidatorMock);
		EasyMock.replay(binderMock);
		controllerUT.registerValidators(binderMock);
		EasyMock.verify(binderMock);
	}

	// -------------------------------------------
	// ------- status checks on application form:
	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsInApproval() {
		application.setStatus(ApplicationFormStatus.APPROVAL);
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		controllerUT.getApplicationForm(10);
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsApproved() {
		application.setStatus(ApplicationFormStatus.APPROVED);
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		controllerUT.getApplicationForm(10);
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsRejected() {
		application.setStatus(ApplicationFormStatus.REJECTED);
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		controllerUT.getApplicationForm(10);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfApplicationIsUnsubmitted() {
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		application.setStatus(ApplicationFormStatus.UNSUBMITTED);
		controllerUT.getApplicationForm(10);
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsWithdrawn() {
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		application.setStatus(ApplicationFormStatus.WITHDRAWN);
		controllerUT.getApplicationForm(10);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfApplicationDoesntExist() {
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controllerUT.getApplicationForm(10);
	}

	@Test
	public void returnApplicationIfApplicationIsInReview() {
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		application.setStatus(ApplicationFormStatus.REVIEW);
		ApplicationForm applicationForm = controllerUT.getApplicationForm(10);
		Assert.assertNotNull(applicationForm);
		Assert.assertEquals(application, applicationForm);
		EasyMock.verify(applicationServiceMock);
	}

	// -----------------------------------------
	// ------ Programme for an application:
	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfApplicantUser() {
		admin.getRoles().clear();
		admin.getRoles().add(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole());

		controllerUT.getProgrammeForApplication(application);
	}

	@Test
	public void getProgrammeFromApplicationAsSuperUser() {
		admin.getRoles().clear();
		admin.getRoles().add(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole());

		Program returnedProgram = controllerUT.getProgrammeForApplication(application);
		Assert.assertNotNull(returnedProgram);
		Assert.assertEquals(program, returnedProgram);
	}

	@Test
	public void getProgrammeFromApplication() {
		Program returnedProgram = controllerUT.getProgrammeForApplication(application);
		Assert.assertNotNull(returnedProgram);
		Assert.assertEquals(program, returnedProgram);
	}

	// -------------------------------------------
	// ------- available reviewers of a program:
	@Test
	public void getAvailableReviewers() {
		List<RegisteredUser> availableReviewers = controllerUT.getAvailableReviewers(program, application, null);
		Assert.assertNotNull(availableReviewers);
		Assert.assertEquals(2, availableReviewers.size());
		Assert.assertTrue(availableReviewers.contains(reviewerUser1));
		Assert.assertTrue(availableReviewers.contains(reviewerUser2));
	}

	@Test
	public void getAvailableReviewersMinusUnsavedReviewers() {
		ArrayList<RegisteredUser> unsavedReviewers = new ArrayList<RegisteredUser>();
		unsavedReviewers.add(reviewerUser1);
		List<RegisteredUser> availableReviewers = controllerUT.getAvailableReviewers(program, application, unsavedReviewers);
		Assert.assertNotNull(availableReviewers);
		Assert.assertEquals(1, availableReviewers.size());
		Assert.assertTrue(availableReviewers.contains(reviewerUser2));
	}

	// -------------------------------------------
	// ------- existing reviewers of an application:
	@Test
	public void getEmptyApplicationReviewerList() {
		Set<RegisteredUser> applReviewers = controllerUT.getApplicationReviewers(application);
		Assert.assertNotNull(applReviewers);
		Assert.assertTrue(applReviewers.isEmpty());
	}

	@Test
	public void getAvailableReviewersMinusAlreadyReviewerOfApplication() {
		application.getReviewers().add(new ReviewerBuilder().user(reviewerUser1).toReviewer());

		List<RegisteredUser> availableReviewers = controllerUT.getAvailableReviewers(program, application, null);
		Assert.assertNotNull(availableReviewers);
		Assert.assertEquals(1, availableReviewers.size());
		Assert.assertTrue(availableReviewers.contains(reviewerUser2));
	}

	@Test
	public void getExistingApplicationReviewerList() {
		Reviewer reviewer = new ReviewerBuilder().user(reviewerUser2).toReviewer();
		application.setReviewers(Arrays.asList(reviewer));

		Set<RegisteredUser> applReviewers = controllerUT.getApplicationReviewers(application);
		Assert.assertNotNull(applReviewers);
		Assert.assertTrue(applReviewers.contains(reviewerUser2));
	}

	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser user = controllerUT.getUser();
		Assert.assertNotNull(user);
		Assert.assertEquals(admin, user);
	}

	@Test
	public void shouldReturnShowPageTemplate() {
		Assert.assertEquals(VIEW_RESULT, controllerUT.getAssignReviewerPage());
	}

	// -------------------------------------------
	// ------- creating reviewers for programme:
	@Test
	public void shouldCreateNewReviewerUser() {
		RegisteredUser storedUser = new RegisteredUserBuilder().id(52233)//
				.firstName("fresh").lastName("reviewer").username("uname").email("uname@name.com")//
				.toUser();

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bla@blu.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserForProgramme( "fresh", "reviewer", "bla@blu.com", program, Authority.REVIEWER)).andReturn(storedUser);

		prepareMessageSourceMock("assignReviewer.newReviewer.created", new Object[] { "uname", "uname@name.com" }, "blabla");
		EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

		RegisteredUser inputUser = new RegisteredUserBuilder().firstName("fresh").lastName("reviewer").email("bla@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		String view = controllerUT.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);

		EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
		Assert.assertEquals(VIEW_RESULT, view);
		Assert.assertEquals("blabla", mmap.get("message"));
	}

	@Test
	public void shouldCreateNewReviewerUserAsProgrammeReviewer() {
		authenticationToken.setDetails(reviewerUser1);
		RegisteredUser storedUser = new RegisteredUserBuilder().id(534)//
				.firstName("fresh").lastName("reviewer").username("uname").email("uname@name.com")//
				.toUser();

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bla@blu.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserForProgramme( "fresh", "reviewer", "bla@blu.com", program, Authority.REVIEWER)).andReturn(storedUser);

		prepareMessageSourceMock("assignReviewer.newReviewer.created", new Object[] { "uname", "uname@name.com" }, "blabla");
		EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

		RegisteredUser inputUser = new RegisteredUserBuilder().firstName("fresh").lastName("reviewer").email("bla@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		String view = controllerUT.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);

		EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
		Assert.assertEquals(VIEW_RESULT, view);
		Assert.assertEquals("blabla", mmap.get("message"));
	}

	@Test
	public void shouldCreateNewReviewerUserAsSuperAdmin() {
		RegisteredUser superAdmin = new RegisteredUserBuilder().id(541).username("superadmin")//
				.role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole())//
				.toUser();
		authenticationToken.setDetails(superAdmin);
		RegisteredUser storedUser = new RegisteredUserBuilder().id(552)//
				.firstName("fresh").lastName("reviewer").username("uname").email("uname@HAHA.com")//
				.toUser();

		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bla@blu.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserForProgramme ("fresh", "reviewer", "bla@blu.com", program,Authority.REVIEWER)).andReturn(storedUser);
		prepareMessageSourceMock("assignReviewer.newReviewer.created", new Object[] { "uname", "uname@HAHA.com" }, "BLUBLU");
		EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

		RegisteredUser inputUser = new RegisteredUserBuilder().firstName("fresh").lastName("reviewer").email("bla@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		String view = controllerUT.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);

		EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
		Assert.assertEquals(VIEW_RESULT, view);
		Assert.assertEquals("BLUBLU", mmap.get("message"));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowRNFEIfUserApplicant() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1546).username("appl")//
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole())//
				.toUser();
		authenticationToken.setDetails(applicant);

		RegisteredUser inputUser = new RegisteredUserBuilder().email("hui@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		controllerUT.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowRNFEIfReviewerIsNotInProgramme() {
		authenticationToken.setDetails(otherReviewerUser);
		RegisteredUser inputUser = new RegisteredUserBuilder().email("hui@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		controllerUT.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
	}

	@Test
	public void shouldNotAddOrCreateExistingReviewerInProgramme() {
		reviewerUser1.setEmail("rev1@bla.com");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("hui@blu.com")).andReturn(reviewerUser1);
		prepareMessageSourceMock("assignReviewer.newReviewer.alreadyInProgramme", new Object[] { "rev 1", "rev1@bla.com" }, "SDFSDFSDFSDF");
		EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

		RegisteredUser inputUser = new RegisteredUserBuilder().email("hui@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		String view = controllerUT.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);

		Assert.assertEquals(VIEW_RESULT, view);
		EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
		Assert.assertEquals("SDFSDFSDFSDF", mmap.get("message"));
		Assert.assertNull(mmap.get("newReviewer"));
	}

	@Test
	public void shouldNotAddOrCreateExistingReviewerInApplication() {
		reviewerUser1.setEmail("rev1@bla.com");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("hui@blu.com")).andReturn(reviewerUser1);
		prepareMessageSourceMock("assignReviewer.reviewer.alreadyExistsInTheApplication", new Object[] { "rev 1", "rev1@bla.com" }, "SDFSDFSDFSDF");
		EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);
		RegisteredUser inputUser = new RegisteredUserBuilder().id(3).email("hui@blu.com").toUser();
		ApplicationForm newApplication = new ApplicationFormBuilder().id(1).reviewers(new ReviewerBuilder().user(reviewerUser1).toReviewer()).toApplicationForm();
		ModelMap mmap = new ModelMap();
		String view = controllerUT.createReviewer(program, newApplication, inputUser, bindingResultMock, null, mmap);

		Assert.assertEquals(VIEW_RESULT, view);
		EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
		Assert.assertEquals("SDFSDFSDFSDF", mmap.get("message"));
		Assert.assertNull(mmap.get("newReviewer"));
	}

	@Test
	public void shouldAddExistingReviewer() {
		otherReviewerUser.setEmail("woi@blu.com");
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("woi@blu.com")).andReturn(otherReviewerUser);
		reviewServiceMock.addUserToProgramme(program, otherReviewerUser);
		EasyMock.expectLastCall();
		prepareMessageSourceMock("assignReviewer.newReviewer.addedToProgramme", new Object[] { "i review others", "woi@blu.com" }, "SDFSDFSDFSDF");
		EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

		RegisteredUser inputUser = new RegisteredUserBuilder().email("woi@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		String view = controllerUT.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
		EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);

		Assert.assertEquals(VIEW_RESULT, view);
		Assert.assertEquals("SDFSDFSDFSDF", mmap.get("message"));
	}

	// -------------------------------------------
	// ------- move application to review:

	@Test
	public void moveToReviewThrowCUpadateWhenApplicationInInvalidState() {
		ApplicationFormStatus[] values = ApplicationFormStatus.values();
		for (ApplicationFormStatus status : values) {
			if (status != ApplicationFormStatus.VALIDATION && status != ApplicationFormStatus.REVIEW) {
				application.setStatus(status);
				boolean threwException = false;
				try {
					controllerUT.moveApplicationToReviewState(application, new ArrayList<RegisteredUser>());
				} catch (CannotUpdateApplicationException cuae) {
					threwException = true;
				}
				Assert.assertTrue("expected exception not thrown for status: " + status, threwException);
			}
		}
	}

	@Test(expected = ResourceNotFoundException.class)
	public void moveToReviewThrowRNFEWhenInvalidUser() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(156).username("appl")//
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole())//
				.toUser();
		authenticationToken.setDetails(applicant);
		controllerUT.moveApplicationToReviewState(application, new ArrayList<RegisteredUser>());
	}

	@Test
	public void moveToReviewWithReviewers() {
		ArrayList<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();
		reviewers.add(reviewerUser1);
		reviewers.add(reviewerUser2);

		reviewServiceMock.moveApplicationToReview(application, reviewerUser1, reviewerUser2);
		EasyMock.expectLastCall();

		EasyMock.replay(reviewServiceMock);
		String nextView = controllerUT.moveApplicationToReviewState(application, reviewers);

		Assert.assertEquals(AFTER_MOVE_TO_REVIEW_VIEW, nextView);
		EasyMock.verify(reviewServiceMock);
	}

	@Test
	public void dontMoveToReviewWithoutReviewers() {
		EasyMock.replay(reviewServiceMock);

		String nextView = controllerUT.moveApplicationToReviewState(application, null);
		Assert.assertEquals(AFTER_MOVE_TO_REVIEW_VIEW, nextView);
		EasyMock.verify(reviewServiceMock);
	}

	@Test
	public void dontMoveToReviewEmptyReviewerList() {
		EasyMock.replay(reviewServiceMock);

		String nextView = controllerUT.moveApplicationToReviewState(application, new ArrayList<RegisteredUser>());
		Assert.assertEquals(AFTER_MOVE_TO_REVIEW_VIEW, nextView);
		EasyMock.verify(reviewServiceMock);
	}

	private void prepareMessageSourceMock(String code, Object[] objects, String returnString) {
		EasyMock.expect(messageSourceMock.getMessage(EasyMock.eq(code), EasyMock.aryEq(objects), EasyMock.isNull(Locale.class))).andReturn(returnString);
	}

	// -------------------------------------------
	// ------- transform ids to users:

	@Test
	public void transformUserIdsToUsers() {
		EasyMock.expect(userServiceMock.getUser(5)).andReturn(reviewerUser1);
		EasyMock.expect(userServiceMock.getUser(6)).andReturn(reviewerUser2);
		EasyMock.expect(userServiceMock.getUser(9)).andReturn(otherReviewerUser);

		EasyMock.replay(userServiceMock);
		List<RegisteredUser> unsavedReviewers = controllerUT.unsavedReviewers("5|6|9");

		EasyMock.verify(userServiceMock);
		Assert.assertNotNull(unsavedReviewers);
		Assert.assertEquals(3, unsavedReviewers.size());
		Assert.assertTrue(unsavedReviewers.contains(reviewerUser1));
		Assert.assertTrue(unsavedReviewers.contains(reviewerUser2));
		Assert.assertTrue(unsavedReviewers.contains(otherReviewerUser));
	}

	@Test
	public void dontTransformUserIdsToUsers() {
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> unsavedReviewers = controllerUT.unsavedReviewers(null);

		EasyMock.verify(userServiceMock);
		Assert.assertNotNull(unsavedReviewers);
		Assert.assertTrue(unsavedReviewers.isEmpty());
	}
}
