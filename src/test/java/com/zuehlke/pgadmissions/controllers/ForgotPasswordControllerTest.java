package com.zuehlke.pgadmissions.controllers;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.services.UserService;

public class ForgotPasswordControllerTest {

	private static final String FORGOT_PW_PAGE = "public/login/forgot_password";
	private static final String MAIL_SEND_CONFIRMATION_PAGE = "public/login/forgot_password_confirmation";
	private ForgotPasswordController controllerUT;
	private UserService userService;

	@Before
	public void setup() {
		userService = EasyMock.createMock(UserService.class);
		controllerUT = new ForgotPasswordController(userService);
	}

	@Test
	public void shouldReturnForgottenPasswordPageViewName() {
		EasyMock.replay(userService);
		MockHttpSession session = new MockHttpSession();
		String viewName = controllerUT.getForgotPasswordPage(session);
		
		Assert.assertEquals(FORGOT_PW_PAGE, viewName);
		EasyMock.verify(userService);
	}
	
	@Test
	public void shouldClearAlreadyRegisteredAttributeInSessionIfExists() {
		EasyMock.replay(userService);
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED, true);
		Assert.assertTrue((Boolean)session.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
		String viewName = controllerUT.getForgotPasswordPage(session);
		
		Assert.assertEquals(FORGOT_PW_PAGE, viewName);
		Assert.assertNull(session.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
		EasyMock.verify(userService);
	}

	@Test
	public void shouldReturnFPPageAndErrorCodeWhenEmailIncorrect() {
		ModelMap model = new ModelMap();
		EasyMock.replay(userService);

		String viewName = controllerUT.resetPassword("blabla", model);
		
		EasyMock.verify(userService);
		Assert.assertEquals(FORGOT_PW_PAGE, viewName);
		Assert.assertEquals("text.email.notvalid", model.get("errorMessageCode"));
	}

	@Test
	public void shouldCallServiceWhenEmailOK() {
		ModelMap model = new ModelMap();
		String inputEmail = "blabla@lala.com";
		
		userService.resetPassword(inputEmail);
		EasyMock.replay(userService);
		
		String viewName = controllerUT.resetPassword(inputEmail, model);
		
		EasyMock.verify(userService);
		Assert.assertEquals(MAIL_SEND_CONFIRMATION_PAGE, viewName);
		Assert.assertEquals(inputEmail, model.get("email"));
	}
}
