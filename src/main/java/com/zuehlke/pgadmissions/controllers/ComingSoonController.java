package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/comingsoon")
public class ComingSoonController {

	private final UserService userService;

	ComingSoonController() {
		this(null);
	}

	@Autowired
	public ComingSoonController(UserService userService) {
		this.userService = userService;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getComingSoonView() {
		return "public/common/coming_soon";
	}

}
