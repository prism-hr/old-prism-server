package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.SearchContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.temporary.User;



public class DemoControllerTest {

	private RegisteredUser user;

	@Test
	public void shouldReturnJspViewForEmptyPath() {
		String jspViewName = "view";
		DemoController controller = new DemoController(jspViewName, null);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/");
		assertEquals(jspViewName, controller.getPage(request, new ModelMap()));
	}
	
	@Test
	public void shouldReturnVeloCityViewForHomePath() {
		String velocityViewName = "view";
		DemoController controller = new DemoController(null, velocityViewName);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/home");
		assertEquals(velocityViewName, controller.getPage(request, new ModelMap()));
	}


	@Test
	public void shoudAddUserFromSecurityContextObjectToModel(){
		
		
		DemoController controller = new DemoController(null, null);
		ModelMap modelMap = new ModelMap();
		controller.getPage(new MockHttpServletRequest(), modelMap);
		assertNotNull(modelMap.get("user"));
		assertEquals(user, modelMap.get("user"));
	}
	@Before
	public void setUp(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}
	
	@After
	public void tearDown(){
		SecurityContextHolder.clearContext();
	}
	
}
