package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.List;

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
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Controller
@RequestMapping(value = { "" })
public class MainPageController {


	private static final String MAIN_PAGE_VIEW_NAME = "main";
	private final ApplicationFormDAO applicationFormDAO;

	MainPageController(){
		this(null);
	}

	@Autowired
	public MainPageController(ApplicationFormDAO applicationFormDAO) {
		this.applicationFormDAO = applicationFormDAO;
	}

	@Transactional
	@RequestMapping(method = RequestMethod.GET)
	public String getMainPage( ModelMap modelMap) {

		SecurityContext context = SecurityContextHolder.getContext();
		RegisteredUser user = (RegisteredUser) context.getAuthentication().getDetails();
		List<ApplicationForm> applications = applicationFormDAO.getAllApplications();
		List<ApplicationForm> visibleApplications = new ArrayList<ApplicationForm>();

		if (applications != null) {
			for (ApplicationForm application : applications) {
				if (user.canSee(application)) {
					visibleApplications.add(application);
				}
			}
		}
		modelMap.addAttribute("user", user);
		modelMap.addAttribute("applications", visibleApplications);

		return MAIN_PAGE_VIEW_NAME;
	}
}
