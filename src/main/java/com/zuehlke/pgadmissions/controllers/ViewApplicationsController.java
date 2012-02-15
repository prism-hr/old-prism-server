package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;

@Controller
@RequestMapping(value={"applications"})
public class ViewApplicationsController {

	private static final String APPLICATIONS_VIEW_NAME = "applications/applications";
	private final ApplicationFormDAO applicationFormDAO;
	
	public ViewApplicationsController() {
		this(null);	
	}
	
	@Autowired
	public ViewApplicationsController(ApplicationFormDAO applicationFormDAO) {
		this.applicationFormDAO = applicationFormDAO;
	
	}

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public String createApplicationsView(ModelMap modelMap) {	
		SecurityContext context = SecurityContextHolder.getContext();
		modelMap.addAttribute("user",context.getAuthentication().getDetails());
		modelMap.addAttribute("applications", applicationFormDAO.getAllApplications() );
		return APPLICATIONS_VIEW_NAME;
	}

}
