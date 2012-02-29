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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ViewApplicationFormControllerTest {

	private ViewApplicationFormController controller;
	private RegisteredUser userMock;
	private ApplicationsService applicationsServiceMock;
	private ApplicationReviewService applicationReviewServiceMock;

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getViewApplicationPage(1);

	}

	@Test
	public void shouldGetApplicationFormView() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(userMock,applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage(1);
		assertEquals("application/applicationForm_applicant", modelAndView.getViewName());
	}

	@Test
	public void shouldGetApplicationFormFromIdAndSetOnModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(userMock, applicationsServiceMock);
		ModelAndView modelAndView = controller.getViewApplicationPage(1);
		PageModel model = (PageModel) modelAndView.getModel().get("model");
		assertEquals(applicationForm, model.getApplicationForm());
	}

	@Test
	public void shouldGetCurrentUserFromSecutrityContextAndSetOnEditModel() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.APPROVER)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.REVIEWER)).andReturn(false);
		EasyMock.replay(userMock, applicationsServiceMock);

		ModelAndView modelAndView = controller.getViewApplicationPage(1);
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
		
		controller.getViewApplicationPage(1);
	}

	
	
	
	
	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		userMock =EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new ViewApplicationFormController(applicationsServiceMock, applicationReviewServiceMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
