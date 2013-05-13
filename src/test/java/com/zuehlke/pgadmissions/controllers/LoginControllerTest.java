package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.UserService;

public class LoginControllerTest {

	private LoginController loginController;
	
	private static final String LOGIN_PAGE = "public/login/login_page";
	
	private static final String REGISTER_USER_REDIRECT = "redirect:/register";

	@Test
	public void shouldReturnLoginPageViewNameAndSetNotAuthorizedResponseCode(){
	    MockHttpSession session = new MockHttpSession();
	    MockHttpServletResponse response = new MockHttpServletResponse();
		assertEquals(LOGIN_PAGE, loginController.getLoginPage(new MockHttpServletRequest(), response, session));
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
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
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(null).anyTimes();
		EasyMock.replay(defaultSavedRequestMock);
		session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);		
		assertEquals(REGISTER_USER_REDIRECT, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
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
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(null).anyTimes();
		EasyMock.replay(defaultSavedRequestMock);
		session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);	
		
		assertEquals(REGISTER_USER_REDIRECT, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
		assertEquals("", session.getAttribute("applyRequest"));
	}
	
	@Test
	public void shouldSetSessionParameterToNullIfNotNewApplicationRequest(){
		MockHttpSession session = new MockHttpSession();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
		DefaultSavedRequest defaultSavedRequestMock = EasyMock.createMock(DefaultSavedRequest.class);
		EasyMock.expect(defaultSavedRequestMock.getRequestURL()).andReturn("/bob/new").anyTimes();
		EasyMock.expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(null);
		
		EasyMock.replay(defaultSavedRequestMock);
		session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);	

		assertEquals(LOGIN_PAGE, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
		assertNull(session.getAttribute("applyRequest"));
	}
	
	@Test
    public void shouldSaveUserEmailInSessionIfRequestContainsActivationCode() {
        RegisteredUser userWithActivationCode = new RegisteredUserBuilder()
            .firstName("Kevin")
            .lastName("Denver")
            .email("ked@zuhlke.com")
            .enabled(true)
            .activationCode("13ca4700-1393-11e2-892e-0800200c9a66")
            .build();
	    
        UserService userServiceMock = EasyMock.createMock(UserService.class);
        
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        
        DefaultSavedRequest defaultSavedRequestMock = EasyMock.createMock(DefaultSavedRequest.class);
        
        // Assumptions
        EasyMock.expect(defaultSavedRequestMock.getRequestURL()).andReturn("/applications&activationCode=" + userWithActivationCode.getActivationCode()).anyTimes();
        EasyMock.expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(new String[] {userWithActivationCode.getActivationCode()});
        EasyMock.expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(new String[] {userWithActivationCode.getActivationCode()});
        EasyMock.expect(userServiceMock.getUserByActivationCode(userWithActivationCode.getActivationCode())).andReturn(userWithActivationCode);
        
        // Verify
        loginController = new LoginController(userServiceMock);
        EasyMock.replay(defaultSavedRequestMock, userServiceMock);
        
        session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock); 
        assertEquals(LOGIN_PAGE, loginController.getLoginPage(request, new MockHttpServletResponse(), session));

        EasyMock.verify(defaultSavedRequestMock, userServiceMock);
        
        assertEquals("ked@zuhlke.com", session.getAttribute("loginUserEmail"));
        assertNull(session.getAttribute("applyRequest"));
    }
	
	@Test
    public void shouldSetEmailSessionParameterToNullIfNotNewApplicationRequest(){
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        DefaultSavedRequest defaultSavedRequestMock = EasyMock.createMock(DefaultSavedRequest.class);
        EasyMock.expect(defaultSavedRequestMock.getRequestURL()).andReturn("/bob/new").anyTimes();
        EasyMock.expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(null);
        
        EasyMock.replay(defaultSavedRequestMock);
        session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);
        session.putValue("loginUserEmail", "ked@zuhlke.com");
        
        assertEquals(LOGIN_PAGE, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
        assertNull(session.getAttribute("applyRequest"));
        assertNull(session.getAttribute("loginUserEmail"));
    }
	
	@Test
	public void shouldRedirectToLoginPageFromRegistrationForm() {
	    MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        request.setSession(session);
        request.setRequestURI("/login");
        request.addHeader("referer", "/register");
        
        assertEquals(LOGIN_PAGE, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
        assertNull(session.getAttribute("applyRequest"));
        assertNull(session.getAttribute("loginUserEmail"));
        assertTrue((Boolean) session.getAttribute("CLICKED_ON_ALREADY_REGISTERED"));
	}
	
	@Test
	public void shouldRedirectToLoginPageFromClickingAlreadyRegisteredAndProvidingWrongCredentials() {
	    MockHttpSession session = new MockHttpSession();
	    session.setAttribute("CLICKED_ON_ALREADY_REGISTERED", true);
	    MockHttpServletRequest request = new MockHttpServletRequest();
        
        request.setSession(session);
        request.setRequestURI("/login");
        request.addHeader("referer", "/login");
        
        assertEquals(LOGIN_PAGE, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
        assertNull(session.getAttribute("applyRequest"));
        assertNull(session.getAttribute("loginUserEmail"));
        assertTrue((Boolean) session.getAttribute("CLICKED_ON_ALREADY_REGISTERED"));
	}
	
	@Before
	public void setup(){
		loginController = new LoginController();
	}
}
