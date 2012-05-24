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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotApproveApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApproveRejectControllerTest {

	private ApplicationsService applicationsServiceMock;
	private ApproveRejectController controller;
	private RegisteredUser approverMock;
	private UserService userServiceMock;

	@Test
	public void shouldGetApplicationFromFromService() {
		Program program = new ProgramBuilder().id(4).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).program(program).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.expect(approverMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock, applicationsServiceMock);
		assertEquals(applicationForm, controller.getApplicationForm(5));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionApplicationFormDoesNotExist() {

		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm(5);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserCannotSeeApplication() {

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.expect(approverMock.isInRole(Authority.APPROVER)).andReturn(true);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(approverMock, applicationsServiceMock);
		controller.getApplicationForm(5);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserNotApprover() {
		Program program = new ProgramBuilder().id(4).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).program(program).toApplicationForm();
		
		EasyMock.expect(approverMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
		EasyMock.expect(approverMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock);
		controller.applyDecision(applicationForm, ApplicationFormStatus.APPROVED);
	}

	@Test(expected = CannotApproveApplicationException.class)
	public void shouldThrowCannotReviewApprovedApplicationExceptionIfSubmittedApplicationnNotReviewable() {
		Program program = new ProgramBuilder().id(4).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).program(program).toApplicationForm();
		
		EasyMock.expect(approverMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
		EasyMock.expect(approverMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);

		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock);
		controller.applyDecision(applicationForm, ApplicationFormStatus.APPROVED);
	}

	@Test
	public void shouldSetStatusAndApproverAndSaveApplication() {
		Program program = new ProgramBuilder().id(4).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).program(program).toApplicationForm();

		applicationsServiceMock.save(applicationForm);
		EasyMock.expect(approverMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
		EasyMock.expect(approverMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock, applicationsServiceMock);

		controller.applyDecision(applicationForm, ApplicationFormStatus.APPROVED);
		EasyMock.verify(applicationsServiceMock);
		assertEquals(approverMock, applicationForm.getApprover());
		assertEquals(ApplicationFormStatus.APPROVED, applicationForm.getStatus());

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowExceptionIfAdminTriesToApproveApplication() {
		Program program = new ProgramBuilder().id(4).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).program(program).toApplicationForm();
		EasyMock.expect(approverMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);
		EasyMock.expect(approverMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true).anyTimes();
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock);

		controller.applyDecision(applicationForm, ApplicationFormStatus.APPROVED);

	}

	@Test
	public void shouldRedirectToApplicationListOnApproval() {
		Program program = new ProgramBuilder().id(4).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).program(program).toApplicationForm();
		applicationsServiceMock.save(applicationForm);
		EasyMock.expect(approverMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
		EasyMock.expect(approverMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock, applicationsServiceMock);

		ModelAndView modelAndView = controller.applyDecision(applicationForm, ApplicationFormStatus.APPROVED);
		assertEquals("redirect:/applications", modelAndView.getViewName());
		assertEquals("approved", modelAndView.getModelMap().get("decision"));

	}

	@Test
	public void shouldRedirectToApplicationListOnRejection() {
		Program program = new ProgramBuilder().id(4).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).program(program).toApplicationForm();
		applicationsServiceMock.save(applicationForm);
		EasyMock.expect(approverMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
		EasyMock.expect(approverMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(approverMock.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(approverMock, applicationsServiceMock);

		ModelAndView modelAndView = controller.applyDecision(applicationForm, ApplicationFormStatus.REJECTED);
		assertEquals("redirect:/applications", modelAndView.getViewName());
		assertEquals("rejected", modelAndView.getModelMap().get("decision"));

	}

	@Test
	public void shouldReturnApprovedOrRejectedViewWithApplicationAndUser() {

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).toApplicationForm();

		ModelAndView modelAndView = controller.getApprovedOrRejectedPage(applicationForm);
		assertEquals("/reviewer/approvedOrRejected", modelAndView.getViewName());
		PageModel pageModel = (PageModel) modelAndView.getModel().get("model");
		assertEquals(applicationForm, pageModel.getApplicationForm());
		assertEquals(approverMock, pageModel.getUser());

	}

	@Before
	public void setUp() {
		
		approverMock = EasyMock.createMock(RegisteredUser.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(approverMock).anyTimes();
		EasyMock.replay(userServiceMock);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new ApproveRejectController(applicationsServiceMock, userServiceMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
