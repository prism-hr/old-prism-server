package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;


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
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class CommentControllerTest {
	
	private RegisteredUser admin;
	private RegisteredUser approver;
	private RegisteredUser reviewer;
	private RegisteredUser applicant;
	private ApplicationsService applicationsServiceMock;
	private ApplicationReviewService applicationReviewServiceMock;
	private CommentController controller;
	ApplicationForm submittedNonApprovedApplication;
	ApplicationForm submittedApprovedApplication;
	ApplicationForm unsubmittedApplication;
	ApplicationReview applicationReviewForSubmittedNonApproved;
	UsernamePasswordAuthenticationToken authenticationToken;
	
	
	@Before
	public void setUp() {
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		admin = new RegisteredUserBuilder().id(1).username("bob")
								.role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		approver = new RegisteredUserBuilder().id(1).username("mark")
				.role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		reviewer = new RegisteredUserBuilder().id(1).username("jane")
				.role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		applicant = new RegisteredUserBuilder().id(1).username("fred")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		applicationReviewServiceMock = EasyMock.createMock(ApplicationReviewService.class);
		controller = new CommentController(applicationReviewServiceMock, applicationsServiceMock);
		submittedNonApprovedApplication = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		submittedApprovedApplication = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).approvedSatus(ApprovalStatus.APPROVED).approver(approver).toApplicationForm();
		unsubmittedApplication = new ApplicationFormBuilder().id(3).toApplicationForm();
		applicationReviewForSubmittedNonApproved = new ApplicationReviewBuilder().id(1).application(submittedNonApprovedApplication).comment("Amazing Research !!!").user(admin).toApplicationReview();
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
		System.out.println(((CommentModel)modelAndView.getModel().get("model")).getComment());
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

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
