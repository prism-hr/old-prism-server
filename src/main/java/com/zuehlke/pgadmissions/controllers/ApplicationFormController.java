package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;

public class ApplicationFormController {

	private final ProjectDAO projectDAO;
	private final ApplicationFormDAO applicationDAO;

	@Autowired
	public ApplicationFormController(ProjectDAO projectDAO, ApplicationFormDAO applicationDAO) {
		this.projectDAO = projectDAO;
		this.applicationDAO = applicationDAO;
	}

	public String getNewApplicationForm(Integer id, ModelMap modelMap) {
		Project project = projectDAO.getProjectById(id);
		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setProject(project);
		applicationDAO.save(applicationForm);
		modelMap.addAttribute("application", applicationForm);
		return "applicationForm";
	}

	 ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}


}
