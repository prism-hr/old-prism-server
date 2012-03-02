package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.pagemodels.ApplicationListModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ApplicationListControllerTest {

	private RegisteredUser user;
	private ApplicationsService applicationsServiceMock;
	private ApplicationListController controller;

	@Test
	public void shouldReturnCorrectViewForApplicant() {		
		
		ModelAndView modelAndView = controller.getApplicationListPage(false, null);

		assertEquals("application/application_list", modelAndView.getViewName());
	}
	
	@Test
	public void shouldAddUserFromSecurityContextObjectToModel() {
		
		ModelAndView modelAndView = controller.getApplicationListPage(false, null);
		
		ApplicationListModel model = (ApplicationListModel) modelAndView.getModel().get("model");
		
		assertNotNull(model.getUser());
		assertEquals(user, model.getUser());
	}
	
	@Test
	public void shouldAddSubmissionSuccesMessageIfRequired() {
		
		ModelAndView modelAndView = controller.getApplicationListPage(true, null);
		
		ApplicationListModel model = (ApplicationListModel) modelAndView.getModel().get("model");
		
		assertEquals("Your application is submitted successfully. <b>Coming soon: </b> email confirmation.", model.getMessage());
	}
	
	@Test
	public void shouldAddDecissionMessageIfRequired() {
		
		ModelAndView modelAndView = controller.getApplicationListPage(true, "bobbed");
		
		ApplicationListModel model = (ApplicationListModel) modelAndView.getModel().get("model");
		
		assertEquals("The application was successfully bobbed.", model.getMessage());
		
		
	}
	@Test
	public void shouldAddAllApplicationsToModel() {
		ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationForm applicationTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getVisibleApplications(user)).andReturn(Arrays.asList(applicationOne, applicationTwo));
		EasyMock.replay(applicationsServiceMock);
		
		ModelAndView modelAndView = controller.getApplicationListPage(false, null);
		ApplicationListModel model = (ApplicationListModel) modelAndView.getModel().get("model");
	
		List<ApplicationForm> applications = model.getApplications();
		assertEquals(2, applications.size());
		assertTrue(applications.containsAll(Arrays.asList(applicationOne, applicationTwo)));
	}

	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob")
								.role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new ApplicationListController(applicationsServiceMock);
	}

	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
