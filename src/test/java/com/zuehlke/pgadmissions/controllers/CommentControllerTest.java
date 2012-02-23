package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
import com.zuehlke.pgadmissions.domain.CommentModel;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewerAssignedModel;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotViewCommentsException;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

import cucumber.annotation.en_pirate.Aye;

public class CommentControllerTest {
	
	private RegisteredUser admin;
	private RegisteredUser approver;
	private RegisteredUser reviewer, reviewer2;
	private RegisteredUser applicant;
	private RegisteredUser adminAndReviewer;
	private ApplicationsService applicationsServiceMock;
	private ApplicationReviewService applicationReviewServiceMock;
	private CommentController controller;
	ApplicationForm submittedNonApprovedApplication;
	ApplicationForm submittedApprovedApplication;
	ApplicationForm unsubmittedApplication;
	ApplicationReview applicationReviewForSubmittedNonApproved1, applicationReviewForSubmittedNonApproved2, applicationReviewForSubmittedNonApproved3, applicationReviewForSubmittedNonApproved4;
	UsernamePasswordAuthenticationToken authenticationToken;
	
	
	@Before
	public void setUp() {
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		admin = new RegisteredUserBuilder().id(1).username("bob")
								.role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		approver = new RegisteredUserBuilder().id(2).username("mark")
				.role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		reviewer = new RegisteredUserBuilder().id(3).username("jane")
				.role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		reviewer2 = new RegisteredUserBuilder().id(3).username("john")
				.role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		applicant = new RegisteredUserBuilder().id(5).username("fred")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		adminAndReviewer = new RegisteredUserBuilder().id(6).username("fred")
				.roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole(), new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		applicationReviewServiceMock = EasyMock.createMock(ApplicationReviewService.class);
		controller = new CommentController(applicationReviewServiceMock, applicationsServiceMock);
		submittedNonApprovedApplication = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		submittedApprovedApplication = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).approvedSatus(ApprovalStatus.APPROVED).approver(approver).toApplicationForm();
		unsubmittedApplication = new ApplicationFormBuilder().id(3).toApplicationForm();
		applicationReviewForSubmittedNonApproved1 = new ApplicationReviewBuilder().id(1).application(submittedNonApprovedApplication).comment("Amazing Research !!!").user(admin).toApplicationReview();
		applicationReviewForSubmittedNonApproved2 = new ApplicationReviewBuilder().id(2).application(submittedNonApprovedApplication).comment("I'm not interested").user(reviewer).toApplicationReview();
		applicationReviewForSubmittedNonApproved3 = new ApplicationReviewBuilder().id(3).application(submittedNonApprovedApplication).comment("I'm interested").user(reviewer2).toApplicationReview();
		applicationReviewForSubmittedNonApproved4 = new ApplicationReviewBuilder().id(4).application(submittedNonApprovedApplication).comment("Comment By Admin And Reviewer").user(adminAndReviewer).toApplicationReview();
	}

	@Test
	public void shouldGetCommentPage(){
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(submittedNonApprovedApplication);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getCommentPage(1, "Comment");
		assertEquals("commentForm", modelAndView.getViewName());
		assertEquals(submittedNonApprovedApplication, ((CommentModel)modelAndView.getModelMap().get("model")).getApplication());
	}
	
	@Test
	public void shouldSaveCommentByAdminOnSubmittedNonApprovedApplication(){
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(submittedNonApprovedApplication);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getCommentedApplicationPage(1, "Amazing Research !!!");
		assertEquals("comment", modelAndView.getViewName());
		assertEquals("Amazing Research !!!", ((CommentModel)modelAndView.getModel().get("model")).getComment());
		assertEquals("Your comment is submitted successful", ((CommentModel)modelAndView.getModel().get("model")).getMessage());
		
	}
	
	@Test
	public void shouldNotSaveCommentByAdminOnSubmittedApprovedApplication(){
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(submittedApprovedApplication);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getCommentedApplicationPage(2, "Amazing Research !!!");
		assertEquals("comment", modelAndView.getViewName());
		assertNull(((CommentModel)modelAndView.getModel().get("model")).getComment());
		assertEquals("You cannot comment on a completed application", ((CommentModel)modelAndView.getModel().get("model")).getMessage());
		
	}
	@Test
	public void shouldNotSaveCommentByAdminOnUnSubmittedApplication(){
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(3)).andReturn(unsubmittedApplication);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getCommentedApplicationPage(3, "Amazing Research !!!");
		assertEquals("comment", modelAndView.getViewName());
		assertNull(((CommentModel)modelAndView.getModel().get("model")).getComment());
		assertEquals("You cannot comment on a non submitted application", ((CommentModel)modelAndView.getModel().get("model")).getMessage());
		
	}
	@Test
	public void shouldNotAllowApplicantCommenting(){
		authenticationToken.setDetails(applicant);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(unsubmittedApplication);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getCommentedApplicationPage(1, "Amazing Research !!!");
		assertEquals("comment", modelAndView.getViewName());
		assertNull(((CommentModel)modelAndView.getModel().get("model")).getComment());
		assertEquals("You are not authorized to comment on the application", ((CommentModel)modelAndView.getModel().get("model")).getMessage());
		
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
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(submittedNonApprovedApplication);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.expect(applicationReviewServiceMock.getApplicationReviewsByApplication(submittedNonApprovedApplication)).andReturn(comments);
		EasyMock.replay(applicationReviewServiceMock);
		ModelAndView modelAndView = controller.getAllCommentsForApplication(1);
		List<ApplicationReview> loadedComments = ((CommentModel) modelAndView.getModelMap().get("model")).getComments();
		assertEquals(2, loadedComments.size());
		assertEquals(comments, loadedComments);
		assertEquals("comments", modelAndView.getViewName());
		
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
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(submittedNonApprovedApplication);
		EasyMock.expect(applicationReviewServiceMock.getApplicationReviewsByApplication(submittedNonApprovedApplication)).andReturn(comments);
		EasyMock.expect(applicationReviewServiceMock.getVisibleComments(submittedNonApprovedApplication, reviewer)).andReturn(Arrays.asList(applicationReviewForSubmittedNonApproved2, applicationReviewForSubmittedNonApproved1));
		EasyMock.replay(applicationsServiceMock, applicationReviewServiceMock);
		ModelAndView modelAndView = controller.getAllCommentsForApplication(1);
		List<ApplicationReview> loadedComments = ((CommentModel) modelAndView.getModelMap().get("model")).getComments();
		assertEquals(2, loadedComments.size());
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved2));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved1));
		assertTrue(!loadedComments.contains(applicationReviewForSubmittedNonApproved3));
		
		assertEquals("comments", modelAndView.getViewName());
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
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(submittedNonApprovedApplication);
		EasyMock.expect(applicationReviewServiceMock.getApplicationReviewsByApplication(submittedNonApprovedApplication)).andReturn(comments);
		EasyMock.expect(applicationReviewServiceMock.getVisibleComments(submittedNonApprovedApplication, reviewer)).andReturn(Arrays.asList(applicationReviewForSubmittedNonApproved2, applicationReviewForSubmittedNonApproved1));
		EasyMock.replay(applicationsServiceMock, applicationReviewServiceMock);
		ModelAndView modelAndView = controller.getAllCommentsForApplication(1);
		List<ApplicationReview> loadedComments = ((CommentModel) modelAndView.getModelMap().get("model")).getComments();
		assertEquals(4, loadedComments.size());
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved2));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved1));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved3));
		assertTrue(loadedComments.contains(applicationReviewForSubmittedNonApproved4));
		
		assertEquals("comments", modelAndView.getViewName());
	}
	
	@Test(expected = CannotViewCommentsException.class)
	public void applicantShouldNotSeeAnyComments(){
		authenticationToken.setDetails(applicant);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		List<ApplicationReview> comments = new ArrayList<ApplicationReview>();
		comments.add(applicationReviewForSubmittedNonApproved1);
		controller.getAllCommentsForApplication(1);
	}
	

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
