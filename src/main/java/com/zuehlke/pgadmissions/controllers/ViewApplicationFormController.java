package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = { "application" })
public class ViewApplicationFormController {

	private static final String VIEW_APPLICATION_VIEW_NAME = "viewApplication";
	private ApplicationsService applicationService;

	ViewApplicationFormController() {
		this(null);
	}

	@Autowired
	public ViewApplicationFormController(ApplicationsService applicationService) {
		this.applicationService = applicationService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getViewApplicationPage(HttpServletRequest request, ModelMap modelMap) {
		String id = request.getParameter("id");
		SecurityContext context = SecurityContextHolder.getContext();
		ApplicationForm applicationForm = applicationService.getApplicationById(Integer.parseInt(id));
		modelMap.addAttribute("user", context.getAuthentication().getDetails());
		modelMap.addAttribute("application", applicationForm);
		return VIEW_APPLICATION_VIEW_NAME;
	}

}
