package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	private final ProjectDAO projectDAO;
	private final ApplicationsService applicationService;
	private final UserPropertyEditor userPropertyEditor;

	ApplicationFormController() {
		this(null, null, null);
	}
	
	@Autowired
	public ApplicationFormController(ProjectDAO projectDAO, ApplicationsService applicationService,
			UserPropertyEditor userPropertyEditor) {
		this.projectDAO = projectDAO;
		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ModelAndView createNewApplicationForm(@RequestParam Integer project) {

		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

		Project proj = projectDAO.getProjectById(project);

		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setApplicant(user);
		applicationForm.setProject(proj);
		applicationService.save(applicationForm);

		return new ModelAndView("redirect:/application", "id", applicationForm.getId());

	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}
}
