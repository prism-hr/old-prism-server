package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.ui.ModelMap;

public class LoginControllerTest {

	private LoginController loginController;

	@Test
	public void shouldReturnLoginPageViewName(){
		assertEquals("public/login/login_page", loginController.getLoginPage(new MockHttpServletRequest(), new ModelMap()));
	}
	
	@Test
	public void shouldGetProjectIdFromSavedRequestIfnewProjectRequest(){
		MockHttpSession session = new MockHttpSession();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		DefaultSavedRequest defaultSavedRequestMock = EasyMock.createMock(DefaultSavedRequest.class);
		EasyMock.expect(defaultSavedRequestMock.getRequestURL()).andReturn("/apply/new").anyTimes();
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("program")).andReturn(new String[]{"4"});
		EasyMock.replay(defaultSavedRequestMock);
		session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);
		ModelMap modelMap = new ModelMap();
		loginController.getLoginPage(request, modelMap);
		assertEquals("4", modelMap.get("program"));
	}
	
	@Test
	public void shouldNotFailIfNoProjectIdsProvided(){
		MockHttpSession session = new MockHttpSession();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		DefaultSavedRequest defaultSavedRequestMock = EasyMock.createMock(DefaultSavedRequest.class);
		EasyMock.expect(defaultSavedRequestMock.getRequestURL()).andReturn("/apply/new").anyTimes();
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("program")).andReturn(new String[]{});
		EasyMock.replay(defaultSavedRequestMock);
		session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);
		ModelMap modelMap = new ModelMap();
		loginController.getLoginPage(request, modelMap);
		assertNull( modelMap.get("program"));
	}
	@Before
	public void setup(){
		loginController = new LoginController();
	}
}
