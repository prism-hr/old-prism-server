package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.ArrayList;
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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
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
	

	private AssignReviewerController controllerUT;

	private ApplicationForm application;
	private ApplicationsService applicationServiceMock;
	private ReviewService reviewServiceMock;
	private UserService userServiceMock;
	private NewUserByAdminValidator userValidatorMock;
	private BindingResult bindingResultMock;
	private MessageSource messageSourceMock;

	
	private Program program;
	private RegisteredUser admin;
	private RegisteredUser reviewerUser1;
	private RegisteredUser reviewerUser2;
	private RegisteredUser otherReviewerUser;

	@Before
	public void setUp() {
		admin = new RegisteredUserBuilder().id(1).username("admin").role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();	

		reviewServiceMock = EasyMock.createMock(ReviewService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin);
		EasyMock.replay(userServiceMock);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		EasyMock.replay(bindingResultMock);
		messageSourceMock = EasyMock.createMock(MessageSource.class);
		controllerUT = new AssignReviewerController(applicationServiceMock, reviewServiceMock, userServiceMock,	userValidatorMock, messageSourceMock){

			@Override
			public ReviewRound getReviewRound(Integer applicationId) {
				return null;
			}
			
		};
		
		

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
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		controllerUT.getProgrammeForApplication(5);
	}

	@Test
	public void getProgrammeFromApplicationAsSuperUser() {
		admin.getRoles().clear();
		admin.getRoles().add(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole());
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		Program returnedProgram = controllerUT.getProgrammeForApplication(5);
		Assert.assertNotNull(returnedProgram);
		Assert.assertEquals(program, returnedProgram);
	}

	@Test
	public void getProgrammeFromApplication() {
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		Program returnedProgram = controllerUT.getProgrammeForApplication(5);
		Assert.assertNotNull(returnedProgram);
		Assert.assertEquals(program, returnedProgram);
	}

	// -------------------------------------------
	// ------- available reviewers of a program:
	@Test
	public void getAvailableReviewers() {
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		List<RegisteredUser> availableReviewers = controllerUT.getAvailableReviewers(5, null);
		Assert.assertNotNull(availableReviewers);
		Assert.assertEquals(2, availableReviewers.size());
		Assert.assertTrue(availableReviewers.contains(reviewerUser1));
		Assert.assertTrue(availableReviewers.contains(reviewerUser2));
	}

	@Test
	public void getAvailableReviewersMinusUnsavedReviewers() {
		EasyMock.reset(userServiceMock);
	
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
		EasyMock.expect(userServiceMock.getUser(6)).andReturn(reviewerUser1);
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> availableReviewers = controllerUT.getAvailableReviewers(5, "6");
		Assert.assertNotNull(availableReviewers);
		Assert.assertEquals(1, availableReviewers.size());
		Assert.assertTrue(availableReviewers.contains(reviewerUser2));
	}

	// -------------------------------------------
	// ------- existing reviewers of an application:
	@Test
	public void getEmptyApplicationReviewerIfNoLatestsReviewRoundList() {
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		Set<RegisteredUser> applReviewers = controllerUT.getApplicationReviewers(5);
		Assert.assertNotNull(applReviewers);
		Assert.assertTrue(applReviewers.isEmpty());
	}

	@Test
	public void getAvailableReviewersMinusAlreadyReviewerOfApplication() {
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser1).toReviewer()).toReviewRound();
		application.setLatestReviewRound(reviewRound);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		List<RegisteredUser> availableReviewers = controllerUT.getAvailableReviewers(5, null);
		Assert.assertNotNull(availableReviewers);
		Assert.assertEquals(1, availableReviewers.size());
		Assert.assertTrue(availableReviewers.contains(reviewerUser2));
	}

	@Test
	public void getExistingApplicationReviewerList() {
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser2).toReviewer()).toReviewRound();
		application.setLatestReviewRound(reviewRound);
		
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		Set<RegisteredUser> applReviewers = controllerUT.getApplicationReviewers(5);
		Assert.assertNotNull(applReviewers);
		Assert.assertTrue(applReviewers.contains(reviewerUser2));
	}

	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser user = controllerUT.getUser();
		Assert.assertNotNull(user);
		Assert.assertEquals(admin, user);
	}

		// -------------------------------------------
	// ------- transform ids to users:

	@Test
	public void transformUserIdsToUsers() {
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
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
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
		EasyMock.replay(userServiceMock);
		List<RegisteredUser> unsavedReviewers = controllerUT.unsavedReviewers(null);

		EasyMock.verify(userServiceMock);
		Assert.assertNotNull(unsavedReviewers);
		Assert.assertTrue(unsavedReviewers.isEmpty());
	}
}
