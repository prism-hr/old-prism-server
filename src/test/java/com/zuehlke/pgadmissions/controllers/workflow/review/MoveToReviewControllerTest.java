package com.zuehlke.pgadmissions.controllers.workflow.review;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Locale;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

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

public class MoveToReviewControllerTest {
	private static final String SECTION_RESULT = "private/staff/admin/assign_reviewers_to_appl_section";
	private static final String AFTER_MOVE_TO_REVIEW_VIEW = "redirect:/applications";
	private static final String VIEW_RESULT = "private/staff/admin/assign_reviewers_to_appl_page";
	private ReviewService reviewServiceMock;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private NewUserByAdminValidator userValidatorMock;
	private MessageSource messageSourceMock;
	private MoveToReviewController controller;
	private RegisteredUser reviewerUser1;
	private RegisteredUser reviewerUser2;
	private Program program;
	private RegisteredUser admin;
	private BindingResult bindingResultMock;
	private RegisteredUser otherReviewerUser;
	private ApplicationForm application;

	@Before
	public void setUp(){
		reviewServiceMock = EasyMock.createMock(ReviewService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);

		messageSourceMock = EasyMock.createMock(MessageSource.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
	
		controller = new MoveToReviewController(applicationServiceMock, reviewServiceMock, userServiceMock,	userValidatorMock, messageSourceMock);
		
		admin = new RegisteredUserBuilder().id(1).username("admin").role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin);
		EasyMock.replay(userServiceMock);
		
		reviewerUser1 = new RegisteredUserBuilder().id(2).username("rev 1").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		reviewerUser2 = new RegisteredUserBuilder().id(3).username("rev 2").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		program = new ProgramBuilder().id(100).administrators(admin).reviewers(reviewerUser1, reviewerUser2).toProgram();
		otherReviewerUser = new RegisteredUserBuilder().id(4).username("i review others").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		application = new ApplicationFormBuilder().id(10).status(ApplicationFormStatus.VALIDATION).program(program).toApplicationForm();
	}
	
	@Test
	public void shouldGetNewReviewRound(){
		assertTrue(controller.getReviewRound(null) instanceof ReviewRound);
		assertNull(controller.getReviewRound(null).getId());
	}
	

	@Test
	public void shouldReturnShowPageTemplate() {
		Assert.assertEquals(VIEW_RESULT, controller.getMoveToReviewPage());
	}

	// -------------------------------------------
		// ------- creating reviewers for programme:
		@Test
		public void shouldCreateNewReviewerUser() {
			RegisteredUser storedUser = new RegisteredUserBuilder().id(52233)//
					.firstName("fresh").lastName("reviewer").username("uname").email("uname@name.com")//
					.toUser();
			EasyMock.reset(userServiceMock);
			EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
			EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bla@blu.com")).andReturn(null);
			EasyMock.expect(userServiceMock.createNewUserForProgramme( "fresh", "reviewer", "bla@blu.com", program, Authority.REVIEWER)).andReturn(storedUser);

			prepareMessageSourceMock("assignReviewer.newReviewer.created", new Object[] { "uname", "uname@name.com" }, "blabla");
			EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

			RegisteredUser inputUser = new RegisteredUserBuilder().firstName("fresh").lastName("reviewer").email("bla@blu.com").toUser();
			ModelMap mmap = new ModelMap();
			String view = controller.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);

			EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
			Assert.assertEquals(SECTION_RESULT, view);
			Assert.assertEquals("blabla", mmap.get("message"));
		}

		@Test
		public void shouldCreateNewReviewerUserAsProgrammeReviewer() {
			EasyMock.reset(userServiceMock);
			EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(reviewerUser1).anyTimes();
			
			RegisteredUser storedUser = new RegisteredUserBuilder().id(534)//
					.firstName("fresh").lastName("reviewer").username("uname").email("uname@name.com")//
					.toUser();

			EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bla@blu.com")).andReturn(null);
			EasyMock.expect(userServiceMock.createNewUserForProgramme( "fresh", "reviewer", "bla@blu.com", program, Authority.REVIEWER)).andReturn(storedUser);

			prepareMessageSourceMock("assignReviewer.newReviewer.created", new Object[] { "uname", "uname@name.com" }, "blabla");
			EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

			RegisteredUser inputUser = new RegisteredUserBuilder().firstName("fresh").lastName("reviewer").email("bla@blu.com").toUser();
			ModelMap mmap = new ModelMap();
			String view = controller.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);

			EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
			Assert.assertEquals(SECTION_RESULT, view);
			Assert.assertEquals("blabla", mmap.get("message"));
		}

		@Test
		public void shouldCreateNewReviewerUserAsSuperAdmin() {
			RegisteredUser superAdmin = new RegisteredUserBuilder().id(541).username("superadmin")//
					.role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole())//
					.toUser();
			EasyMock.reset(userServiceMock);
			EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(superAdmin);
			
			RegisteredUser storedUser = new RegisteredUserBuilder().id(552)//
					.firstName("fresh").lastName("reviewer").username("uname").email("uname@HAHA.com")//
					.toUser();

			EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bla@blu.com")).andReturn(null);
			EasyMock.expect(userServiceMock.createNewUserForProgramme ("fresh", "reviewer", "bla@blu.com", program,Authority.REVIEWER)).andReturn(storedUser);
			prepareMessageSourceMock("assignReviewer.newReviewer.created", new Object[] { "uname", "uname@HAHA.com" }, "BLUBLU");
			EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

			RegisteredUser inputUser = new RegisteredUserBuilder().firstName("fresh").lastName("reviewer").email("bla@blu.com").toUser();
			ModelMap mmap = new ModelMap();
			String view = controller.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);

			EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
			Assert.assertEquals(SECTION_RESULT, view);
			Assert.assertEquals("BLUBLU", mmap.get("message"));
		}

		@Test(expected = ResourceNotFoundException.class)
		public void shouldThrowRNFEIfUserApplicant() {
			RegisteredUser applicant = new RegisteredUserBuilder().id(1546).username("appl")//
					.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole())//
					.toUser();
			EasyMock.reset(userServiceMock);
			EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
			EasyMock.replay(userServiceMock);
			RegisteredUser inputUser = new RegisteredUserBuilder().email("hui@blu.com").toUser();
			ModelMap mmap = new ModelMap();
			controller.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
		}

		@Test(expected = ResourceNotFoundException.class)
		public void shouldThrowRNFEIfReviewerIsNotInProgramme() {
			EasyMock.reset(userServiceMock);
			EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(otherReviewerUser);
			EasyMock.replay(userServiceMock);
			RegisteredUser inputUser = new RegisteredUserBuilder().email("hui@blu.com").toUser();
			ModelMap mmap = new ModelMap();
			controller.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
		}

		@Test
		public void shouldNotAddOrCreateExistingReviewerInProgramme() {
			EasyMock.reset(userServiceMock);
			EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
			reviewerUser1.setEmail("rev1@bla.com");
			EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("hui@blu.com")).andReturn(reviewerUser1);
			prepareMessageSourceMock("assignReviewer.newReviewer.alreadyInProgramme", new Object[] { "rev 1", "rev1@bla.com" }, "SDFSDFSDFSDF");
			EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

			RegisteredUser inputUser = new RegisteredUserBuilder().email("hui@blu.com").toUser();
			ModelMap mmap = new ModelMap();
			String view = controller.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);

			Assert.assertEquals(SECTION_RESULT, view);
			EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
			Assert.assertEquals("SDFSDFSDFSDF", mmap.get("message"));
			Assert.assertNull(mmap.get("newReviewer"));
		}

		@Test
		public void shouldNotAddOrCreateExistingReviewerInApplication() {
			EasyMock.reset(userServiceMock);
			EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
			reviewerUser1.setEmail("rev1@bla.com");
			EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("hui@blu.com")).andReturn(reviewerUser1);
			prepareMessageSourceMock("assignReviewer.reviewer.alreadyExistsInTheApplication", new Object[] { "rev 1", "rev1@bla.com" }, "SDFSDFSDFSDF");
			EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);
			RegisteredUser inputUser = new RegisteredUserBuilder().id(3).email("hui@blu.com").toUser();
			ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser1).toReviewer()).toReviewRound();
			ApplicationForm newApplication = new ApplicationFormBuilder().id(1).latestReviewRound(reviewRound).toApplicationForm();
			ModelMap mmap = new ModelMap();
			String view = controller.createReviewer(program, newApplication, inputUser, bindingResultMock, null, mmap);

			Assert.assertEquals(SECTION_RESULT, view);
			EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);
			Assert.assertEquals("SDFSDFSDFSDF", mmap.get("message"));
			Assert.assertNull(mmap.get("newReviewer"));
		}

		@Test
		public void shouldAddExistingReviewer() {
			EasyMock.reset(userServiceMock);
			EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
			otherReviewerUser.setEmail("woi@blu.com");
			EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("woi@blu.com")).andReturn(otherReviewerUser);
			reviewServiceMock.addUserToProgramme(program, otherReviewerUser);
			EasyMock.expectLastCall();
			prepareMessageSourceMock("assignReviewer.newReviewer.addedToProgramme", new Object[] { "i review others", "woi@blu.com" }, "SDFSDFSDFSDF");
			EasyMock.replay(reviewServiceMock, userServiceMock, messageSourceMock);

			RegisteredUser inputUser = new RegisteredUserBuilder().email("woi@blu.com").toUser();
			ModelMap mmap = new ModelMap();
			String view = controller.createReviewer(program, new ApplicationForm(), inputUser, bindingResultMock, new ArrayList<RegisteredUser>(), mmap);
			EasyMock.verify(reviewServiceMock, userServiceMock, messageSourceMock);

			Assert.assertEquals(SECTION_RESULT, view);
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
						controller.moveApplicationToReviewState(application,null, new ArrayList<RegisteredUser>());
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
			EasyMock.reset(userServiceMock);
			EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
			EasyMock.replay(userServiceMock);		
			controller.moveApplicationToReviewState(application, null,new ArrayList<RegisteredUser>());
		}

		@Test
		public void moveToReviewWithReviewers() {
			ArrayList<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();
			reviewers.add(reviewerUser1);
			reviewers.add(reviewerUser2);
			ReviewRound reviewRound = new ReviewRoundBuilder().id(1).toReviewRound();
			reviewServiceMock.moveApplicationToReview(application, reviewRound,reviewerUser1, reviewerUser2);
			EasyMock.expectLastCall();
			EasyMock.replay(reviewServiceMock);
			String nextView = controller.moveApplicationToReviewState(application,reviewRound, reviewers);

			Assert.assertEquals(AFTER_MOVE_TO_REVIEW_VIEW, nextView);
			EasyMock.verify(reviewServiceMock);
		}

		@Test
		public void dontMoveToReviewWithoutReviewers() {
			EasyMock.replay(reviewServiceMock);
			String nextView = controller.moveApplicationToReviewState(application, new ReviewRoundBuilder().id(1).toReviewRound(), null);
			Assert.assertEquals(AFTER_MOVE_TO_REVIEW_VIEW, nextView);
			EasyMock.verify(reviewServiceMock);
		}

		@Test
		public void dontMoveToReviewEmptyReviewerList() {
			EasyMock.replay(reviewServiceMock);

			String nextView = controller.moveApplicationToReviewState(application, new ReviewRoundBuilder().id(1).toReviewRound(), new  ArrayList<RegisteredUser>());
			Assert.assertEquals(AFTER_MOVE_TO_REVIEW_VIEW, nextView);
			EasyMock.verify(reviewServiceMock);
		}

		private void prepareMessageSourceMock(String code, Object[] objects, String returnString) {
			EasyMock.expect(messageSourceMock.getMessage(EasyMock.eq(code), EasyMock.aryEq(objects), EasyMock.isNull(Locale.class))).andReturn(returnString);
		}


}
