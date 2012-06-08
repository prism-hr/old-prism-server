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
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.utils.ApplicationPageModelBuilder;

public class ViewApplicationFormControllerTest {

	private ViewApplicationFormController controller;
	private RegisteredUser userMock;
	private ApplicationsService applicationsServiceMock;	
	private ApplicationPageModelBuilder applicationPageModelBuilderMock;

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getViewApplicationPage(null, "1", null, null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowExceptionIfCurrentCannotSeeApplicatioForm() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(applicationsServiceMock, userMock);

		controller.getViewApplicationPage(null,  "1", null, null, null);
	}

	@Test
	public void shouldGetApplicationFormViewWithApplicationPageModelForApplicationApplicant() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).toApplicationForm();
		String uploadErrorCode = "abc";
		String view = "def";
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		ApplicationPageModel model = new ApplicationPageModel();
		
		EasyMock.expect(applicationPageModelBuilderMock.createAndPopulatePageModel(applicationForm, uploadErrorCode, view, null, null)).andReturn(model);
		
		EasyMock.replay(applicationsServiceMock, userMock, applicationPageModelBuilderMock);

		ModelAndView modelAndView = controller.getViewApplicationPage(view, "1", uploadErrorCode, null, null);
		
		assertEquals("private/pgStudents/form/main_application_page", modelAndView.getViewName());
		assertEquals(model, modelAndView.getModel().get("model"));
	}

	@Test
	public void shouldGetApplicationFormViewWithApplicationPageModelForStaff() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		String uploadErrorCode = "abc";
		String view = "def";
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);

		ApplicationPageModel model = new ApplicationPageModel();
		EasyMock.expect(applicationPageModelBuilderMock.createAndPopulatePageModel(applicationForm, uploadErrorCode, view, null, null)).andReturn(model);
		EasyMock.replay(applicationsServiceMock, userMock, applicationPageModelBuilderMock);
		
		ModelAndView modelAndView = controller.getViewApplicationPage(view,  "1", uploadErrorCode, null, null);
		
		assertEquals("private/staff/application/main_application_page", modelAndView.getViewName());
		assertEquals(model, modelAndView.getModel().get("model"));
	}


	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		userMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		applicationPageModelBuilderMock = EasyMock.createMock(ApplicationPageModelBuilder.class);
		controller = new ViewApplicationFormController(applicationsServiceMock, applicationPageModelBuilderMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
