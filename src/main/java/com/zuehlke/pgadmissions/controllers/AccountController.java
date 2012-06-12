package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/myAccount")
public class AccountController {

	private final UserService userService;
	private static final String ACCOUNT_PAGE_VIEW_NAME = "/private/my_account";

	AccountController() {
		this(null);
	}

	@Autowired
	public AccountController(UserService userService) {
		this.userService = userService;
	}


	@RequestMapping(method = RequestMethod.GET)
	public String getAcceptedTermsView() {
		return ACCOUNT_PAGE_VIEW_NAME;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

}
