package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

	@Test
	public void shouldReturnLoginPageViewNameAndSetNotAuthorizedResponseCode(){
		MockHttpServletResponse response = new MockHttpServletResponse();
		assertEquals("public/login/login_page", loginController.getLoginPage(new MockHttpServletRequest(), response));
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
		loginController.getLoginPage(request, new MockHttpServletResponse());
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
		loginController.getLoginPage(request, new MockHttpServletResponse() );
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
		loginController.getLoginPage(request, new MockHttpServletResponse());
		
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
        loginController.getLoginPage(request, new MockHttpServletResponse());

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
        loginController.getLoginPage(request, new MockHttpServletResponse());
        
        assertNull(session.getAttribute("applyRequest"));
        assertNull(session.getAttribute("loginUserEmail"));
    }
	
	@Before
	public void setup(){
		loginController = new LoginController();
	}
}
