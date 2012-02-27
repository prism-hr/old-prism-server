package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ApproveRejectControllerTest {

	private RegisteredUser admin;
	private RegisteredUser approver;
	private ApplicationsService applicationsServiceMock;
	private ApproveRejectController controller;
	ApplicationForm submittedApplication;
	ApplicationForm approvedApplication;
	UsernamePasswordAuthenticationToken authenticationToken;
	
	
	@Before
	public void setUp() {
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		admin = new RegisteredUserBuilder().id(1).username("bob")
				.role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		approver = new RegisteredUserBuilder().id(1).username("mark")
				.role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		authenticationToken.setDetails(admin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new ApproveRejectController(applicationsServiceMock);
		submittedApplication = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		approvedApplication = new ApplicationFormBuilder().id(2).approvedSatus(ApprovalStatus.APPROVED).approver(approver).toApplicationForm();
	}
	
	@Test
	public void shouldSaveApproveToApplication(){
		authenticationToken.setDetails(approver);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(submittedApplication);
		applicationsServiceMock.save(submittedApplication);
		EasyMock.replay(applicationsServiceMock);
		String view = controller.getDecidedApplicationPage(1, "Approve", new ModelMap());
		EasyMock.verify(applicationsServiceMock);
		assertEquals("mark", submittedApplication.getApprover().getUsername());
		assertEquals(ApprovalStatus.APPROVED, submittedApplication.getApprovalStatus());
		assertEquals("approveRejectSuccess", view);
	}
	
	@Test
	public void shouldSaveRejectToApplication(){
		submittedApplication.setApprovalStatus(null);
		authenticationToken.setDetails(approver);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(submittedApplication);
		applicationsServiceMock.save(submittedApplication);
		EasyMock.replay(applicationsServiceMock);
		String view = controller.getDecidedApplicationPage(1, "Reject", new ModelMap());
		EasyMock.verify(applicationsServiceMock);
		assertEquals("mark", submittedApplication.getApprover().getUsername());
		assertEquals(ApprovalStatus.REJECTED, submittedApplication.getApprovalStatus());
		assertEquals("approveRejectSuccess", view);
	}
	
	@Test
	public void shouldNotSaveAlreadyApprovedOrRejectedApplication(){
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(approvedApplication);
		EasyMock.replay(applicationsServiceMock);
		controller.getDecidedApplicationPage(2, "0", new ModelMap());
	}

	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
