package com.zuehlke.pgadmissions.controllers;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ViewApplicationFormControllerTest {

	private ViewApplicationFormController controller;
	private RegisteredUser userMock;
	private ApplicationsService applicationsServiceMock;
	private ApplicationReviewService applicationReviewServiceMock;
	private UsernamePasswordAuthenticationToken authenticationToken;
	private RegisteredUser admin;
	private RegisteredUser adminAndReviewer;
	private RegisteredUser reviewer, reviewer2;
	ApplicationForm submittedNonApprovedApplication;
	ApplicationForm submittedApprovedApplication;
	ApplicationForm unsubmittedApplication;
	ApplicationReview applicationReviewForSubmittedNonApproved1, applicationReviewForSubmittedNonApproved2, applicationReviewForSubmittedNonApproved3, applicationReviewForSubmittedNonApproved4;
	private Qualification qual;
	private RegisteredUser applicant;

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getViewApplicationPage("", 1);

	}

	@Test
	public void shouldGetApplicationFormView() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		applicationsServiceMock.save(applicationForm);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(userMock,applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("", 1);
		assertEquals("private/pgStudents/form/main_application_page", modelAndView.getViewName());
	}

	@Test
	public void shouldGetApplicationFormFromIdAndSetOnModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
//		EasyMock.expect(userMock.hasQualifications()).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(userMock, applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("", 1);
		PageModel model = (PageModel) modelAndView.getModel().get("model");
		assertEquals(applicationForm, model.getApplicationForm());
	}
	
	@Test
	public void shouldCreatePersonalDetailsFromApplicationApplicantAndSetOnModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();		
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
//		EasyMock.expect(userMock.hasQualifications()).andReturn(true);
		EasyMock.expect(userMock.getFirstName()).andReturn("bob");
		EasyMock.expect(userMock.getLastName()).andReturn("Smith");
		EasyMock.expect(userMock.getEmail()).andReturn("email@test.com");
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(userMock, applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("", 1);
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertEquals("bob", model.getPersonalDetails().getFirstName());
		assertEquals("Smith", model.getPersonalDetails().getLastName());
		assertEquals("email@test.com", model.getPersonalDetails().getEmail());
	}

	@Test
	public void shouldGetCurrentUserFromSecutrityContextAndSetOnEditModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(userMock, applicationsServiceMock);

		ModelAndView modelAndView = controller.getViewApplicationPage("", 1);
		PageModel model = (PageModel) modelAndView.getModel().get("model");
		assertEquals(userMock, model.getUser());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowExceptionIfCurrentCannotSeeApplicatioForm() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		RegisteredUser userMock =EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(userMock);
		authenticationToken.setDetails(userMock);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		controller.getViewApplicationPage("", 1);
	}

	@Test
	public void shouldShowAllCommentsForAdministrator(){
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		List<ApplicationReview> comments = new ArrayList<ApplicationReview>();
		comments.add(applicationReviewForSubmittedNonApproved1);
		comments.add(applicationReviewForSubmittedNonApproved2);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(submittedNonApprovedApplication);
		EasyMock.expect(applicationReviewServiceMock.getApplicationReviewsByApplication(submittedNonApprovedApplication)).andReturn(comments);
		applicationsServiceMock.save(submittedNonApprovedApplication);
		EasyMock.replay(applicationReviewServiceMock, applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("view", 2);
		List<ApplicationReview> loadedComments = ((PageModel) modelAndView.getModelMap().get("model")).getApplicationComments();
		assertEquals(2, loadedComments.size());
		assertEquals(comments, loadedComments);
		assertEquals("private/staff/application/main_application_page", modelAndView.getViewName());
	}
	
	@Test
	public void shouldShowAllCommentsForReviewerExceptFromOtherReviewersComments(){
		authenticationToken.setDetails(reviewer);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		List<ApplicationReview> comments = new ArrayList<ApplicationReview>();
		comments.add(applicationReviewForSubmittedNonApproved1); //admin
		comments.add(applicationReviewForSubmittedNonApproved2); //reviewer
		comments.add(applicationReviewForSubmittedNonApproved3); //reviewer2
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(submittedNonApprovedApplication);
		EasyMock.expect(applicationReviewServiceMock.getApplicationReviewsByApplication(submittedNonApprovedApplication)).andReturn(comments);
		EasyMock.expect(applicationReviewServiceMock.getVisibleComments(submittedNonApprovedApplication, reviewer)).andReturn(Arrays.asList(applicationReviewForSubmittedNonApproved2, applicationReviewForSubmittedNonApproved1));
		applicationsServiceMock.save(submittedNonApprovedApplication);
		EasyMock.replay(applicationsServiceMock, applicationReviewServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("view", 2);
		List<ApplicationReview> loadedComments = ((PageModel) modelAndView.getModelMap().get("model")).getApplicationComments();
		assertEquals(2, loadedComments.size());
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved2));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved1));
		assertTrue(!loadedComments.contains(applicationReviewForSubmittedNonApproved3));
		
	}
	
	@Test
	public void shouldShowAllCommentsForUserWhoIsBothAdminAndReviewer(){
		authenticationToken.setDetails(adminAndReviewer);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		List<ApplicationReview> comments = new ArrayList<ApplicationReview>();
		comments.add(applicationReviewForSubmittedNonApproved1); //admin
		comments.add(applicationReviewForSubmittedNonApproved2); //reviewer
		comments.add(applicationReviewForSubmittedNonApproved3); //reviewer2
		comments.add(applicationReviewForSubmittedNonApproved4); //adminAndReviewer
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(submittedNonApprovedApplication);
		applicationsServiceMock.save(submittedNonApprovedApplication);
		EasyMock.expect(applicationReviewServiceMock.getApplicationReviewsByApplication(submittedNonApprovedApplication)).andReturn(comments);
		EasyMock.expect(applicationReviewServiceMock.getVisibleComments(submittedNonApprovedApplication, reviewer)).andReturn(Arrays.asList(applicationReviewForSubmittedNonApproved2, applicationReviewForSubmittedNonApproved1));
		EasyMock.replay(applicationsServiceMock, applicationReviewServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage("view", 2);
		List<ApplicationReview> loadedComments = ((PageModel) modelAndView.getModelMap().get("model")).getApplicationComments();
		assertEquals(4, loadedComments.size());
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved2));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved1));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved3));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved4));
	}
	
	@Before
	public void setUp() {
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		userMock =EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		applicationReviewServiceMock = EasyMock.createMock(ApplicationReviewService.class);
		controller = new ViewApplicationFormController(applicationsServiceMock, applicationReviewServiceMock);
//		Qualification qual = new QualificationBuilder().date_taken("2011/2/2").date_taken("sd").grade("ddf").institution("").application(submittedApprovedApplication).toQualification();
		admin = new RegisteredUserBuilder().id(1).username("bob")
								.role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		applicant = new RegisteredUserBuilder().id(1).username("bob")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		reviewer = new RegisteredUserBuilder().id(3).username("jane").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		reviewer2 = new RegisteredUserBuilder().id(3).username("john")
				.role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		adminAndReviewer = new RegisteredUserBuilder().id(6).username("fred")
				.roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole(), new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);
		submittedNonApprovedApplication = new ApplicationFormBuilder().id(2).reviewers(reviewers).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		unsubmittedApplication = new ApplicationFormBuilder().id(3).toApplicationForm();
		applicationReviewForSubmittedNonApproved1 = new ApplicationReviewBuilder().id(1).application(submittedNonApprovedApplication).comment("Amazing Research !!!").user(admin).toApplicationReview();
		applicationReviewForSubmittedNonApproved2 = new ApplicationReviewBuilder().id(2).application(submittedNonApprovedApplication).comment("I'm not interested").user(reviewer).toApplicationReview();
		applicationReviewForSubmittedNonApproved3 = new ApplicationReviewBuilder().id(3).application(submittedNonApprovedApplication).comment("I'm interested").user(reviewer2).toApplicationReview();
		applicationReviewForSubmittedNonApproved4 = new ApplicationReviewBuilder().id(4).application(submittedNonApprovedApplication).comment("Comment By Admin And Reviewer").user(adminAndReviewer).toApplicationReview();
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
