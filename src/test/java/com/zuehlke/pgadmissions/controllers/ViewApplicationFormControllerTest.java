package com.zuehlke.pgadmissions.controllers;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ViewApplicationFormControllerTest {
	
	private ApplicationFormDAO applicationFormDAOMock;
	private ViewApplicationFormController viewApplicationFormController;
	private ApplicationForm form;
	private MockHttpServletRequest request;
	private RegisteredUser user;


	@Test
	public void shouldReturnViewApplicationViewName(){
		assertEquals("viewApplication", viewApplicationFormController.getViewApplicationPage(request, new ModelMap()));
	}
	
	@Test
	public void shouldGetApplicationFromApplicationId(){
	
		EasyMock.expect(applicationFormDAOMock.get(1)).andReturn(form);
		EasyMock.replay(applicationFormDAOMock);
		ModelMap modelMap = new ModelMap();
		viewApplicationFormController.getViewApplicationPage(request, modelMap);
		assertEquals(form, modelMap.get("application"));
	}

	
	@Before
	public void setUp(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		request = new MockHttpServletRequest();
		request.setParameter("id", "1");
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		viewApplicationFormController = new ViewApplicationFormController(applicationFormDAOMock);
		form = new ApplicationFormBuilder().id(1).toApplicationForm();
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
	
}
