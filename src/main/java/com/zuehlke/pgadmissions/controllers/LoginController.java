package com.zuehlke.pgadmissions.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.temporary.User;

@Controller
@RequestMapping("/login")
public class LoginController {

	@RequestMapping(method = RequestMethod.GET)
	public String getLoginForm(ModelMap model) {
		model.addAttribute("user", new User());
		return "login";
	}

	@RequestMapping(value = "/submit")
	public String getLoginSubmit(@ModelAttribute("user") User user,
			BindingResult result, ModelMap model) {
		if (user.getEmail().isEmpty() && user.getPassword().isEmpty()) {
			return "failedLogin";
		}
		model.addAttribute("user", user);
		return "successLogin";
	}

}
