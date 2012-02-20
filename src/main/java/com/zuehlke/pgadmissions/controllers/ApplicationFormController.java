package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	private final ProjectDAO projectDAO;
	private final ApplicationFormDAO applicationDAO;

	ApplicationFormController() {
		this(null, null);
	}

	@Autowired
	public ApplicationFormController(ProjectDAO projectDAO, ApplicationFormDAO applicationDAO) {
		this.projectDAO = projectDAO;
		this.applicationDAO = applicationDAO;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public String getNewApplicationForm(@RequestParam Integer project, ModelMap modelMap) {
		Project proj = projectDAO.getProjectById(project);
		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setUser((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails());
		applicationForm.setProject(proj);
		applicationDAO.save(applicationForm);
		modelMap.addAttribute("application", applicationForm);
		
		return "applicationForm";
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

}
