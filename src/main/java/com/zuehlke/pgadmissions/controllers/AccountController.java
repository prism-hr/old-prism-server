package com.zuehlke.pgadmissions.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AccountValidator;

@Controller
@RequestMapping("/myAccount")
public class AccountController {

	private final UserService userService;
	private static final String ACCOUNT_PAGE_VIEW_NAME = "/private/my_account";
	private final AccountValidator accountValidator;

	AccountController() {
		this(null, null);
	}

	@Autowired
	public AccountController(UserService userService, AccountValidator accountValidator) {
		this.userService = userService;
		this.accountValidator = accountValidator;
	}
	
	@InitBinder(value="updatedUser")
	public void registerValidator(WebDataBinder binder) {
		binder.setValidator(accountValidator);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getMyAccountPage() {
		return ACCOUNT_PAGE_VIEW_NAME;
	}

	
	@RequestMapping(value="/submit",method = RequestMethod.POST)
	public String saveAccountDetails(@Valid @ModelAttribute("updatedUser") RegisteredUser user, BindingResult bindingResult, ModelMap modelMap) {
		if(bindingResult.hasErrors()){
			return ACCOUNT_PAGE_VIEW_NAME;
		}
		if(userService.isAccountChanged(user)){
			modelMap.put("message", "You have successfully changed your account details. A confirmation mail is sent to your email address.");
			userService.updateCurrentUserAndSendEmailNotification(user);
		}
		return "redirect:/applications";
	}

	@ModelAttribute(value="updatedUser")
	public RegisteredUser getUpdatedUser() {
		return new RegisteredUser();
	}
	
	@ModelAttribute(value="user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

}
