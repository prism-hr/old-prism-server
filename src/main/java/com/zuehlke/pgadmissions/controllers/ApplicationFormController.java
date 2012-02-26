package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationFormModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	
	private static final String APPLICATION_FORM_VIEW_NAME = "application/applicationForm";
	private final ProjectDAO projectDAO;
	private final ApplicationsService applicationService;
	private final UserDAO userDAO;

	ApplicationFormController() {
		this(null, null, null);
	}

	@Autowired
	public ApplicationFormController(ProjectDAO projectDAO, ApplicationsService applicationService, UserDAO userDAO) {
		this.projectDAO = projectDAO;
		this.applicationService = applicationService;
		this.userDAO = userDAO;
	}
	
	@RequestMapping(value="/new", method = RequestMethod.POST)
	@Transactional
	public ModelAndView createNewApplicationForm(@RequestParam Integer project) {	
		
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		Project proj = projectDAO.getProjectById(project);
		
		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setApplicant(user);
		applicationForm.setProject(proj);
		applicationService.save(applicationForm);
		
		return new  ModelAndView("redirect:/pgadmissions/application","id", applicationForm.getId());
		
	}


	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public ModelAndView editApplicationForm(@RequestParam Integer id, @RequestParam String firstName, @RequestParam String lastName) {	
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		user.setLastName(lastName);
		user.setFirstName(firstName);
		userDAO.save(user);
		
		ApplicationForm applicationForm = applicationService.getApplicationById(id);
		ApplicationFormModel model = new ApplicationFormModel();
		model.setApplicationForm(applicationForm);
		model.setUser(user);
		
		return new  ModelAndView(APPLICATION_FORM_VIEW_NAME,"model", model);
	}

	@RequestMapping(value="/submit", method = RequestMethod.POST)
	public ModelAndView submitApplication(@RequestParam Integer applicationForm) {
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();		
		ApplicationForm appForm = applicationService.getApplicationById(applicationForm);
		if(appForm == null || ! user.equals(appForm.getApplicant())){
			throw new ResourceNotFoundException();
		}		
		
		appForm.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		applicationService.save(appForm);
		return new  ModelAndView("redirect:/pgadmissions/applications");
	
	}
	/*
	@RequestMapping(value="/edit", method = RequestMethod.GET)
	@Transactional
	public ModelAndView getNewApplicationFormPage(@RequestParam Integer id) {
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationDAO.get(id);	
		if(applicationForm == null || !currentUser.equals(applicationForm.getApplicant())){
			throw new ResourceNotFoundException();
		}
		ApplicationFormModel model = new ApplicationFormModel();
		model.setApplicationForm(applicationForm);
		model.setUser((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails());
		return new  ModelAndView(APPLICATION_FORM_VIEW_NAME,"model", model);
	}
	*/
	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}
}
