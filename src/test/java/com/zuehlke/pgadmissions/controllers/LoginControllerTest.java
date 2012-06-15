package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

public class LoginControllerTest {

	private LoginController loginController;

	@Test
	public void shouldReturnLoginPageViewName(){
		assertEquals("public/login/login_page", loginController.getLoginPage(new MockHttpServletRequest()));
	}
	
	@Test
	public void shouldGetBadgeParametersFromRequestIfNewApplyRequest(){
		MockHttpSession session = new MockHttpSession();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		DefaultSavedRequest defaultSavedRequestMock = EasyMock.createMock(DefaultSavedRequest.class);
		EasyMock.expect(defaultSavedRequestMock.getRequestURL()).andReturn("/apply/new").anyTimes();
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("program")).andReturn(new String[]{"code"});
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("programhome")).andReturn(new String[]{"programhome"});
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("programDeadline")).andReturn(new String[]{"programDeadline"});
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("projectTitle")).andReturn(new String[]{"projectTitle"});
		EasyMock.replay(defaultSavedRequestMock);
		session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);		
		loginController.getLoginPage(request);
		assertEquals("program:code||programhome:programhome||bacthdeadline:programDeadline||projectTitle:projectTitle", session.getAttribute("applyRequest"));
	}
	
	@Test
	public void shouldNotFailIfNoParametersProvided(){
		MockHttpSession session = new MockHttpSession();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		DefaultSavedRequest defaultSavedRequestMock = EasyMock.createMock(DefaultSavedRequest.class);
		EasyMock.expect(defaultSavedRequestMock.getRequestURL()).andReturn("/apply/new").anyTimes();
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("program")).andReturn(new String[]{});
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("programhome")).andReturn(new String[]{});
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("programDeadline")).andReturn(new String[]{});
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("projectTitle")).andReturn(new String[]{});
		EasyMock.replay(defaultSavedRequestMock);
		session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);	
		loginController.getLoginPage(request );
		assertEquals("", session.getAttribute("applyRequest"));
	}
	

	@Test
	public void shouldSetSessionParameterToNullIfNotNewAPplicatioNRequest(){
		MockHttpSession session = new MockHttpSession();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		DefaultSavedRequest defaultSavedRequestMock = EasyMock.createMock(DefaultSavedRequest.class);
		EasyMock.expect(defaultSavedRequestMock.getRequestURL()).andReturn("/bob/new").anyTimes();

		EasyMock.replay(defaultSavedRequestMock);
		session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);	
		loginController.getLoginPage(request );
		assertNull(session.getAttribute("applyRequest"));
	}
	@Before
	public void setup(){
		loginController = new LoginController();
	}
}
