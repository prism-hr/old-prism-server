package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationFormModel;

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
	public ModelAndView getNewApplicationForm(@RequestParam Integer project) {	
		
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(!user.isInRole(Authority.APPLICANT)) throw new ResourceNotFoundException();
		
		Project proj = projectDAO.getProjectById(project);
		
		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setUser(user);
		applicationForm.setProject(proj);
		applicationDAO.save(applicationForm);
		
		ApplicationFormModel model = new ApplicationFormModel();
		model.setApplicationForm(applicationForm);
		
		ModelAndView modelAndView = new  ModelAndView("application/applicationForm","model", model);
		
		return modelAndView;
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	@RequestMapping(value="/success", method = RequestMethod.GET)
	@Transactional
	public String submitApplication(@RequestParam Integer id) {
		ApplicationForm applicationForm = applicationDAO.get(id);
		applicationForm.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		applicationDAO.save(applicationForm);
		return "application/applicationFormSubmitted";
	}
}
