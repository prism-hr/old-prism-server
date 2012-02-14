package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ViewApplicationsControllerTest {

	private RegisteredUser user;

	@Test
	public void shouldAddUserFromSecurityContextObjectToModel() {
		ViewApplicationsController controller = new ViewApplicationsController();
		ModelMap modelMap = new ModelMap();
		controller.createApplicationsView(modelMap);
		assertNotNull(modelMap.get("user"));
		assertEquals(user, modelMap.get("user"));
	}


	@Test
	public void shouldReturnApplicaionsViewName() {
		ViewApplicationsController controller = new ViewApplicationsController();
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
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
