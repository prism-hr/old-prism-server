package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class LoginControllerTest {

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @TestedObject
    private LoginController loginController;

    private static final String LOGIN_PAGE = "public/login/login_page";

    private static final String REGISTER_USER_REDIRECT = "redirect:/register";

    @Test
    public void shouldReturnLoginPageViewNameAndSetNotAuthorizedResponseCode() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertEquals(LOGIN_PAGE, loginController.getLoginPage(new MockHttpServletRequest(), response, session));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void shouldGetBadgeParametersFromRequestIfNewApplyRequest() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        DefaultSavedRequest defaultSavedRequestMock = EasyMockUnitils.createMock(DefaultSavedRequest.class);
        expect(defaultSavedRequestMock.getRequestURL()).andReturn("/apply/new").anyTimes();
        expect(defaultSavedRequestMock.getParameterValues("program")).andReturn(new String[] { "code" });
        expect(defaultSavedRequestMock.getParameterValues("advert")).andReturn(new String[] { "1" });
        expect(defaultSavedRequestMock.getParameterValues("project")).andReturn(new String[] { "1" });
        expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(null).anyTimes();
        replay();
        session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);
        assertEquals(REGISTER_USER_REDIRECT, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
        assertEquals(1, session.getAttribute("requestAdvertId"));
    }

    @Test
    public void shouldNotFailIfNoParametersProvided() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        DefaultSavedRequest defaultSavedRequestMock = EasyMockUnitils.createMock(DefaultSavedRequest.class);
        expect(defaultSavedRequestMock.getRequestURL()).andReturn("/apply/new").anyTimes();
        expect(defaultSavedRequestMock.getParameterValues("program")).andReturn(null);
        expect(defaultSavedRequestMock.getParameterValues("advert")).andReturn(null);
        expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(null);

        replay();
        session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);

        assertEquals(REGISTER_USER_REDIRECT, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
        assertNull(session.getAttribute("requestAdvertId"));
    }

    @Test
    public void shouldSetSessionParameterToNullIfNotNewApplicationRequest() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        DefaultSavedRequest defaultSavedRequestMock = EasyMockUnitils.createMock(DefaultSavedRequest.class);
        expect(defaultSavedRequestMock.getRequestURL()).andReturn("/bob/new").anyTimes();
        expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(null);

        replay();
        session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);

        assertEquals(LOGIN_PAGE, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
        assertNull(session.getAttribute("requestAdvertId"));
    }

    @Test
    public void shouldSaveUserEmailInSessionIfRequestContainsActivationCode() {
        User userWithActivationCode = new UserBuilder().firstName("Kevin").lastName("Denver").email("ked@zuhlke.com").userAccount(new UserAccount().withEnabled(true))
                .activationCode("13ca4700-1393-11e2-892e-0800200c9a66").build();

        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);

        DefaultSavedRequest defaultSavedRequestMock = EasyMockUnitils.createMock(DefaultSavedRequest.class);

        // Assumptions
        expect(defaultSavedRequestMock.getRequestURL()).andReturn("/applications&activationCode=" + userWithActivationCode.getActivationCode()).anyTimes();
        expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(new String[] { userWithActivationCode.getActivationCode() });
        expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(new String[] { userWithActivationCode.getActivationCode() });
        expect(userServiceMock.getUserByActivationCode(userWithActivationCode.getActivationCode())).andReturn(userWithActivationCode);

        // Verify
        replay();

        session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);
        assertEquals(LOGIN_PAGE, loginController.getLoginPage(request, new MockHttpServletResponse(), session));

        verify();

        assertEquals("ked@zuhlke.com", session.getAttribute("loginUserEmail"));
        assertNull(session.getAttribute("requestAdvertId"));
    }

    @Test
    public void shouldSetEmailSessionParameterToNullIfNotNewApplicationRequest() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        DefaultSavedRequest defaultSavedRequestMock = EasyMockUnitils.createMock(DefaultSavedRequest.class);
        expect(defaultSavedRequestMock.getRequestURL()).andReturn("/bob/new").anyTimes();
        expect(defaultSavedRequestMock.getParameterValues("activationCode")).andReturn(null);

        replay();
        session.putValue("SPRING_SECURITY_SAVED_REQUEST", defaultSavedRequestMock);
        session.putValue("loginUserEmail", "ked@zuhlke.com");

        assertEquals(LOGIN_PAGE, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
        assertNull(session.getAttribute("requestAdvertId"));
        assertNull(session.getAttribute("loginUserEmail"));
    }

    @Test
    public void shouldRedirectToLoginPageFromRegistrationForm() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setSession(session);
        request.setRequestURI("/alreadyRegistered");
        request.addHeader("referer", "/register");

        assertEquals(LOGIN_PAGE, loginController.getLoginPage(request, new MockHttpServletResponse(), session));
        assertNull(session.getAttribute("requestAdvertId"));
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
        assertNull(session.getAttribute("requestAdvertId"));
        assertNull(session.getAttribute("loginUserEmail"));
        assertTrue((Boolean) session.getAttribute("CLICKED_ON_ALREADY_REGISTERED"));
    }

}
