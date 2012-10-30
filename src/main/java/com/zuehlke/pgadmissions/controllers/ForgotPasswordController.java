package com.zuehlke.pgadmissions.controllers;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

	private static final String FORGOT_PW_VIEW_NAME = "public/login/forgot_password";
	private static final String FORGOT_PW_CONFIRMATION_VIEW_NAME = "public/login/forgot_password_confirmation";
	private final UserService userService;

	public ForgotPasswordController() {
	    this(null);
	}
	
	@Autowired
	public ForgotPasswordController(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getForgotPasswordPage() {
		return FORGOT_PW_VIEW_NAME;
	}

	@RequestMapping(value = "resetPassword", method = RequestMethod.POST)
	public String resetPassword(@RequestParam String email, ModelMap model) {
		if (!EmailValidator.getInstance().isValid(email)) {
			model.put("errorMessageCode", "text.email.notvalid");
			return FORGOT_PW_VIEW_NAME;
		}
		model.put("email", email);
		userService.resetPassword(email);
		return FORGOT_PW_CONFIRMATION_VIEW_NAME;
	}
}
