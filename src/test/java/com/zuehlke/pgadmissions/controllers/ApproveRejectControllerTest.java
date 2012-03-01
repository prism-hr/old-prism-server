package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotApproveApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ApproveRejectControllerTest {


	private ApplicationsService applicationsServiceMock;
	private ApproveRejectController controller;	
	private RegisteredUser approverMock;
	
	

	@Test
	public void shouldGetApplicationFromFromService() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.expect(approverMock.isInRole(Authority.APPROVER)).andReturn(true);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock, applicationsServiceMock);
		assertEquals(applicationForm, controller.getApplicationForm(5));
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionApplicationFormDoesNotExist(){
			
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(5);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserCannotSeeApplication(){
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.expect(approverMock.isInRole(Authority.APPROVER)).andReturn(true);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(approverMock, applicationsServiceMock);
		controller.getApplicationForm(5);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserNotApprover(){			
		ApplicationForm applicationFormMock = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormMock.isReviewable()).andReturn(true);
		EasyMock.expect(approverMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(approverMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(approverMock.canSee(applicationFormMock)).andReturn(true);
		EasyMock.replay(approverMock, applicationFormMock);		
		controller.applyDecision(applicationFormMock, ApprovalStatus.APPROVED);	
	}

	
	@Test(expected=CannotApproveApplicationException.class)
	public void shouldThrowCannotReviewApprovedApplicationExceptionIfSubmittedApplicationnNotReviewable() {
		
		ApplicationForm applicationFormMock = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormMock.isReviewable()).andReturn(false);
		EasyMock.expect(approverMock.isInRole(Authority.APPROVER)).andReturn(true);
		EasyMock.expect(approverMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		
		EasyMock.expect(approverMock.canSee(applicationFormMock)).andReturn(true);
		EasyMock.replay(approverMock, applicationFormMock);		
		controller.applyDecision(applicationFormMock, ApprovalStatus.APPROVED);	
	}
	@Test
	public void shouldSetApprovalStatusAndApproverAndSaveApplication(){
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		applicationsServiceMock.save(applicationForm);
		EasyMock.expect(approverMock.isInRole(Authority.APPROVER)).andReturn(true);
		EasyMock.expect(approverMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock, applicationsServiceMock);
		
		controller.applyDecision(applicationForm, ApprovalStatus.APPROVED);
		EasyMock.verify(applicationsServiceMock);
		assertEquals(approverMock, applicationForm.getApprover());
		assertEquals(ApprovalStatus.APPROVED, applicationForm.getApprovalStatus());
		
	}
	
	@Test
	public void shouldRedirectToApplicationListOnApproval(){
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		applicationsServiceMock.save(applicationForm);
		EasyMock.expect(approverMock.isInRole(Authority.APPROVER)).andReturn(true);
		EasyMock.expect(approverMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock, applicationsServiceMock);
		
		
		ModelAndView modelAndView = controller.applyDecision(applicationForm, ApprovalStatus.APPROVED);
		assertEquals("redirect:/applications", modelAndView.getViewName());
		assertEquals("approved", modelAndView.getModelMap().get("decision"));	
		
		
	}
	@Test
	public void shouldRedirectToApplicationListOnRejection(){
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		applicationsServiceMock.save(applicationForm);
		EasyMock.expect(approverMock.isInRole(Authority.APPROVER)).andReturn(true);
		EasyMock.expect(approverMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock, applicationsServiceMock);
		
		
		ModelAndView modelAndView = controller.applyDecision(applicationForm, ApprovalStatus.REJECTED);
		assertEquals("redirect:/applications", modelAndView.getViewName());
		assertEquals("rejected", modelAndView.getModelMap().get("decision"));	
		
	}
	@Test
	public void shouldReturnApprovedOrRejectedViewWithApplicationAndUser(){
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
				
		ModelAndView modelAndView = controller.getApprovedOrRejectedPage(applicationForm);
		assertEquals("/reviewer/approvedOrRejected", modelAndView.getViewName());
		PageModel pageModel = (PageModel) modelAndView.getModel().get("model");
		assertEquals(applicationForm, pageModel.getApplicationForm());
		assertEquals(approverMock, pageModel.getUser());		
		
	}

	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		approverMock = EasyMock.createMock(RegisteredUser.class);		
		authenticationToken.setDetails(approverMock);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new ApproveRejectController(applicationsServiceMock);
		

	}	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
