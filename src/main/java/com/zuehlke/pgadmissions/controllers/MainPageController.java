package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = { "" })
public class MainPageController {


	private static final String MAIN_PAGE_VIEW_NAME = "main";
	private ApplicationsService applicationsService;

	MainPageController(){
		this(null);
	}

	@Autowired
	public MainPageController(ApplicationsService applicationsService) {
		this.applicationsService = applicationsService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getMainPage( ModelMap modelMap) {

		SecurityContext context = SecurityContextHolder.getContext();
		RegisteredUser user = (RegisteredUser) context.getAuthentication().getDetails();
		
		modelMap.addAttribute("user", user);
		modelMap.addAttribute("applications", applicationsService.getVisibleApplications(user));

		return MAIN_PAGE_VIEW_NAME;
	}
}
