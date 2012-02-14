package com.zuehlke.pgadmissions.controllers;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value={"applications"})
public class ViewApplicationsController {

	private static final String APPLICATIONS_VIEW_NAME = "applications/applications";
	
	@RequestMapping(method = RequestMethod.GET)
	public String createApplicationsView(ModelMap modelMap) {	
		SecurityContext context = SecurityContextHolder.getContext();
		modelMap.addAttribute("user",context.getAuthentication().getDetails());
		return APPLICATIONS_VIEW_NAME;
	}

}
