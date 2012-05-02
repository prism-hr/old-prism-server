package com.zuehlke.pgadmissions.controllers;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;

public class AssignReviewerControllerTest {
	private AssignReviewerController controllerUT;
	private RegisteredUser admin;

	private ApplicationForm application;
	private ApplicationsService applicationServiceMock;
	private ReviewService reviewServiceMock;
	private UserService userServiceMock;

	private UsernamePasswordAuthenticationToken authenticationToken;
	private Program program;
	private RegisteredUser reviewer1;
	private RegisteredUser reviewer2;
	private RegisteredUser otherReviewer;
	private BindingResult bindingResultMock;

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

		controllerUT = new AssignReviewerController(applicationServiceMock, reviewServiceMock, userServiceMock);

		reviewer1 = new RegisteredUserBuilder().id(2).username("rev 1").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		reviewer2 = new RegisteredUserBuilder().id(3).username("rev 2").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		otherReviewer = new RegisteredUserBuilder().id(4).username("i review others")//
				.role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		program = new ProgramBuilder().id(100).administrators(admin).reviewers(reviewer1, reviewer2).toProgram();

		application = new ApplicationFormBuilder().id(10).status(ApplicationFormStatus.VALIDATION)//
				.program(program)//
				.toApplicationForm();

		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);

		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock, bindingResultMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	// -------------------------------------------
	// ------- status checks on application form:
	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsInApproval() {
		application.setStatus(ApplicationFormStatus.APPROVAL);
		controllerUT.getApplicationForm(10);
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsApproved() {
		application.setStatus(ApplicationFormStatus.APPROVED);
		controllerUT.getApplicationForm(10);
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsRejected() {
		application.setStatus(ApplicationFormStatus.REJECTED);
		controllerUT.getApplicationForm(10);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfApplicationIsUnsubmitted() {
		application.setStatus(ApplicationFormStatus.UNSUBMITTED);
		controllerUT.getApplicationForm(10);
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsWithdrawn() {
		application.setStatus(ApplicationFormStatus.WITHDRAWN);
		controllerUT.getApplicationForm(10);
	}

	public void returnApplicationIfApplicationIsInReview() {
		application.setStatus(ApplicationFormStatus.REVIEW);
		ApplicationForm applicationForm = controllerUT.getApplicationForm(10);
		Assert.assertNotNull(applicationForm);
		Assert.assertEquals(application, applicationForm);
	}

	// -----------------------------------------
	// ------ Programme for an application:
	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfApplicationDoesntExist() {
		EasyMock.reset(applicationServiceMock);
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(null);
		EasyMock.replay(applicationServiceMock);
		controllerUT.getProgrammeForApplication(10);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfApplicantUser() {
		admin.getRoles().clear();
		admin.getRoles().add(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole());

		controllerUT.getProgrammeForApplication(10);
	}

	@Test
	public void getProgrammeFromApplicationAsSuperUser() {
		admin.getRoles().clear();
		admin.getRoles().add(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole());

		Program returnedProgram = controllerUT.getProgrammeForApplication(10);
		Assert.assertNotNull(returnedProgram);
		Assert.assertEquals(program, returnedProgram);
		EasyMock.verify(applicationServiceMock);
	}

	@Test
	public void getProgrammeFromApplication() {
		Program returnedProgram = controllerUT.getProgrammeForApplication(10);
		Assert.assertNotNull(returnedProgram);
		Assert.assertEquals(program, returnedProgram);
		EasyMock.verify(applicationServiceMock);
	}

	// -------------------------------------------
	// ------- available reviewers of a program:
	@Test
	public void getAvailableReviewers() {
		List<RegisteredUser> availableReviewers = controllerUT.getAvailableReviewers(10);
		Assert.assertNotNull(availableReviewers);
		Assert.assertEquals(2, availableReviewers.size());
		Assert.assertTrue(availableReviewers.contains(reviewer1));
		Assert.assertTrue(availableReviewers.contains(reviewer2));
		EasyMock.verify(applicationServiceMock);
	}

	// -------------------------------------------
	// ------- existing reviewers of an application:
	@Test
	public void getEmptyApplicationReviewerList() {
		List<RegisteredUser> applReviewers = controllerUT.getApplicationReviewers(10);
		Assert.assertNotNull(applReviewers);
		Assert.assertTrue(applReviewers.isEmpty());
		EasyMock.verify(applicationServiceMock);
	}

	@Test
	public void getExistingApplicationReviewerList() {
		application.setReviewers(Arrays.asList(reviewer2));

		List<RegisteredUser> applReviewers = controllerUT.getApplicationReviewers(10);
		Assert.assertNotNull(applReviewers);
		Assert.assertTrue(applReviewers.contains(reviewer2));
		EasyMock.verify(applicationServiceMock);
	}

	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser user = controllerUT.getUser();
		Assert.assertNotNull(user);
		Assert.assertEquals(admin, user);
	}

	@Test
	public void shouldReturnShowPageTemplate() {
		Assert.assertEquals("/private/staff/admin/assign_reviewers_to_appl_page", controllerUT.getAssignReviewerPage());
	}

	@Test
	public void shouldCreateNewReviewerUser() {
		RegisteredUser storedUser = new RegisteredUserBuilder().id(5)//
				.firstName("fresh").lastName("reviewer").username("uname").email("uname@name.com")//
				.toUser();

		EasyMock.expect(userServiceMock.getUserByEmail("bla@blu.com")).andReturn(null);
		EasyMock.expect(reviewServiceMock.createNewReviewerForProgramme(program, "fresh", "reviewer", "bla@blu.com")).andReturn(storedUser);
		EasyMock.replay(reviewServiceMock, userServiceMock);

		RegisteredUser inputUser = new RegisteredUserBuilder().firstName("fresh").lastName("reviewer").email("bla@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		String view = controllerUT.createReviewer(program, inputUser, mmap);

		EasyMock.verify(reviewServiceMock, userServiceMock);
		Assert.assertEquals("/private/staff/admin/reviewer_as_JSON", view);
		Assert.assertEquals(storedUser, mmap.get("newReviewer"));
		Assert.assertEquals("Created user 'uname' (e-mail: uname@name.com) and added as a reviewer for this programme.", mmap.get("message"));
	}

	@Test
	public void shouldNotAddOrCreateExistingReviewerInProgramme() {
		reviewer1.setEmail("rev1@bla.com");
		EasyMock.expect(userServiceMock.getUserByEmail("hui@blu.com")).andReturn(reviewer1);
		EasyMock.replay(reviewServiceMock, userServiceMock);

		RegisteredUser inputUser = new RegisteredUserBuilder().email("hui@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		String view = controllerUT.createReviewer(program, inputUser, mmap);

		Assert.assertEquals("/private/staff/admin/reviewer_as_JSON", view);
		EasyMock.verify(reviewServiceMock, userServiceMock);
		Assert.assertEquals("User 'rev 1' (e-mail: rev1@bla.com) is already a reviewer for this programme.", mmap.get("message"));
		Assert.assertNull(mmap.get("newReviewer"));
	}

	@Test
	public void shouldAddExistingReviewer() {
		otherReviewer.setEmail("woi@blu.com");
		EasyMock.expect(userServiceMock.getUserByEmail("woi@blu.com")).andReturn(otherReviewer);
		reviewServiceMock.addUserToProgramme(program, otherReviewer);
		EasyMock.expectLastCall();
		EasyMock.replay(reviewServiceMock, userServiceMock);

		RegisteredUser inputUser = new RegisteredUserBuilder().email("woi@blu.com").toUser();
		ModelMap mmap = new ModelMap();
		String view = controllerUT.createReviewer(program, inputUser, mmap);
		EasyMock.verify(reviewServiceMock, userServiceMock);

		Assert.assertEquals("/private/staff/admin/reviewer_as_JSON", view);
		Object message = mmap.get("message");
		Assert.assertEquals("User 'i review others' (e-mail: woi@blu.com) added as reviewer for this programme.", message);
		Assert.assertEquals(otherReviewer, mmap.get("newReviewer"));
	}

	@Test
	public void assignReviewerToApplication() {
		//		controllerUT.assignReviewer(application.getId(), otherReviewer.getId());
	}
}
