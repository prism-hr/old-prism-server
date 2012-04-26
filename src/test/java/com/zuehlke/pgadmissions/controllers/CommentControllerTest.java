package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotCommentException;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

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
		submittedNonApprovedApplication = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		submittedApprovedApplication = new ApplicationFormBuilder().id(2).status(ApplicationFormStatus.APPROVED).approver(approver).toApplicationForm();
		unsubmittedApplication = new ApplicationFormBuilder().id(3).toApplicationForm();
		applicationReviewForSubmittedNonApproved1 = new ApplicationReviewBuilder().id(1).application(submittedNonApprovedApplication).comment("Amazing Research !!!").user(admin).toApplicationReview();
		applicationReviewForSubmittedNonApproved2 = new ApplicationReviewBuilder().id(2).application(submittedNonApprovedApplication).comment("I'm not interested").user(reviewer).toApplicationReview();
		applicationReviewForSubmittedNonApproved3 = new ApplicationReviewBuilder().id(3).application(submittedNonApprovedApplication).comment("I'm interested").user(reviewer2).toApplicationReview();
		applicationReviewForSubmittedNonApproved4 = new ApplicationReviewBuilder().id(4).application(submittedNonApprovedApplication).comment("Comment By Admin And Reviewer").user(adminAndReviewer).toApplicationReview();
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
		assertEquals("redirect:/application?view=comments", modelAndView.getViewName());
		EasyMock.verify(applicationsServiceMock);
	}
	
	@Test
	public void shouldGetAllCommentsForApplication(){
		ApplicationForm appForm = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(appForm.getId()).andReturn(23);
		EasyMock.expect(applicationsServiceMock.getApplicationById(23)).andReturn(appForm);
		EasyMock.replay(appForm, applicationsServiceMock);
		ModelAndView modelAndView = controller.getAllCommentsForApplication(23);
		Assert.assertEquals("redirect:/application?view=comments", modelAndView.getViewName());
	}
	
	@Test(expected = CannotCommentException.class)
	public void shouldNotSaveCommentByAdminOnSubmittedApprovedApplication(){
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(submittedApprovedApplication);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getCommentedApplicationPage(2, "Amazing Research !!!");
		assertEquals("redirect:/application", modelAndView.getViewName());
		EasyMock.verify(applicationsServiceMock);
	}
	
	@Test(expected = CannotCommentException.class)
	public void shouldNotSaveCommentByAdminOnUnSubmittedApplication(){
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(3)).andReturn(unsubmittedApplication);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getCommentedApplicationPage(3, "Amazing Research !!!");
		assertEquals("redirect:/application", modelAndView.getViewName());
		EasyMock.verify(applicationsServiceMock);
		
	}
	
	@Test(expected = CannotCommentException.class)
	public void shouldNotAllowApplicantCommenting(){
		authenticationToken.setDetails(applicant);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(unsubmittedApplication);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getCommentedApplicationPage(1, "Amazing Research !!!");
		assertEquals("redirect:/application", modelAndView.getViewName());
		EasyMock.verify(applicationsServiceMock);
		
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
