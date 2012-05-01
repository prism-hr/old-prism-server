package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotCommentException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class CommentControllerTest {
	
	private RegisteredUser admin;
	private RegisteredUser approver;
	private RegisteredUser reviewer, reviewer2;
	private RegisteredUser applicant;
	private RegisteredUser adminAndReviewer;
	private ApplicationsService applicationsServiceMock;
	private CommentService commentServiceMock;
	private CommentController controller;
	ApplicationForm inValidationApplication;
	ApplicationForm submittedApprovedApplication;

	Comment commentForSubmittedNonApproved1, commentForSubmittedNonApproved2, commentForSubmittedNonApproved3, commentForSubmittedNonApproved4;
	UsernamePasswordAuthenticationToken authenticationToken;
	private UserService userServiceMock;
	
	
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
		commentServiceMock = EasyMock.createMock(CommentService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new CommentController(commentServiceMock, applicationsServiceMock, userServiceMock);
		
		inValidationApplication = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		submittedApprovedApplication = new ApplicationFormBuilder().id(2).status(ApplicationFormStatus.APPROVED).approver(approver).toApplicationForm();
		
		commentForSubmittedNonApproved1 = new CommentBuilder().id(1).application(inValidationApplication).comment("Amazing Research !!!").user(admin).toComment();
		commentForSubmittedNonApproved2 = new CommentBuilder().id(2).application(inValidationApplication).comment("I'm not interested").user(reviewer).toComment();
		commentForSubmittedNonApproved3 = new CommentBuilder().id(3).application(inValidationApplication).comment("I'm interested").user(reviewer2).toComment();
		commentForSubmittedNonApproved4 = new CommentBuilder().id(4).application(inValidationApplication).comment("Comment By Admin And Reviewer").user(adminAndReviewer).toComment();
	}
	
	@Test
	public void shouldGetApplicationFormFromId(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(currentUser, userServiceMock);
	
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		ApplicationForm returnedApplication = controller.getApplicationForm(5);
		assertEquals(returnedApplication, applicationForm);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist(){		
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(5);		
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserIsApplicant(){		
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.replay(currentUser, userServiceMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(5);		

	}
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentCannotSeeApplicationForm(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(currentUser, userServiceMock);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(5);

	}
	
	@Test
	public void shouldSaveCommentByAdminOnSubmittedNonApprovedApplication(){
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(inValidationApplication);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getCommentedApplicationPage(1, "Amazing Research !!!");
		assertEquals("redirect:/application?view=comments", modelAndView.getViewName());
		EasyMock.verify(applicationsServiceMock);
	}
	
	@Test
	public void shouldGetAllVisibleCommentsForApplication(){
		RegisteredUser currentUser = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
				
		final ApplicationForm applicationForm = EasyMock.createMock(ApplicationForm.class);
		List<Comment> commentsList = Arrays.asList(new CommentBuilder().id(1).toComment(), new CommentBuilder().id(2).toComment());
		EasyMock.expect(applicationForm.getVisibleComments(currentUser)).andReturn(commentsList);
		EasyMock.replay(userServiceMock, applicationForm);
		
		controller = new CommentController(commentServiceMock, applicationsServiceMock, userServiceMock){

			@Override
			public ApplicationForm getApplicationForm(Integer id) {			
				return applicationForm;
			}
			
		};
		assertSame(commentsList, controller.getComments(5));
	}
	
	@Test
	public void shouldReturnTimeLine(){
		assertEquals("private/staff/admin/timeline", controller.getCommentsView());
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
	public void shouldNotSaveCommentByAdminOnNonModifiableApplication(){
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(3)).andReturn(new ApplicationFormBuilder().status(ApplicationFormStatus.WITHDRAWN).toApplicationForm());
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
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(inValidationApplication);
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
