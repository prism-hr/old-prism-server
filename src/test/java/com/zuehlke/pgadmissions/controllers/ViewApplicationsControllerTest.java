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
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ViewApplicationsControllerTest {

	private RegisteredUser user;
	private ViewApplicationsController controller;
	private ApplicationFormDAO applicationFormDAOMock;

	@Test
	public void shouldAddUserFromSecurityContextObjectToModel() {

		ModelMap modelMap = new ModelMap();
		controller.createApplicationsView(modelMap);
		assertNotNull(modelMap.get("user"));
		assertEquals(user, modelMap.get("user"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldAddAllApplicationsToModel() {
		ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).toApplicationForm();
		ApplicationForm applicationTwo = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getAllApplications()).andReturn(Arrays.asList(applicationOne, applicationTwo));
		EasyMock.replay(applicationFormDAOMock);
		ModelMap modelMap = new ModelMap();
		controller.createApplicationsView(modelMap);

	
		List<ApplicationForm> applications = (List<ApplicationForm>) modelMap.get("applications");
		assertEquals(2, applications.size());
		assertTrue(applications.containsAll(Arrays.asList(applicationOne, applicationTwo)));
	}

	@Test
	public void shouldReturnApplicaionsViewName() {

		assertEquals("applications/applications", controller.createApplicationsView(new ModelMap()));

	}

	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		controller = new ViewApplicationsController(applicationFormDAOMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
