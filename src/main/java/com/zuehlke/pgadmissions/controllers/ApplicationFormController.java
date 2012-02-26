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
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationFormModel;

import cucumber.annotation.lu.a;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	private static final String APPLICATION_FORM_SUBMITTED_VIEW_NAME = "application/applicationFormSubmitted";
	private static final String APPLICATION_FORM_VIEW_NAME = "application/applicationForm";
	private final ProjectDAO projectDAO;
	private final ApplicationFormDAO applicationDAO;
	private final UserDAO userDAO;

	ApplicationFormController() {
		this(null, null, null);
	}

	@Autowired
	public ApplicationFormController(ProjectDAO projectDAO, ApplicationFormDAO applicationDAO, UserDAO userDAO) {
		this.projectDAO = projectDAO;
		this.applicationDAO = applicationDAO;
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
		applicationDAO.save(applicationForm);
		
		return new   ModelAndView("redirect:edit","id", applicationForm.getId());
		
	}


	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public ModelAndView editApplicationForm(@RequestParam Integer id, @RequestParam String firstName, @RequestParam String lastName) {	
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		user.setLastName(lastName);
		user.setFirstName(firstName);
		userDAO.save(user);
		
		ApplicationForm applicationForm = applicationDAO.get(id);
		ApplicationFormModel model = new ApplicationFormModel();
		model.setApplicationForm(applicationForm);
		model.setUser(user);
		
		return new  ModelAndView(APPLICATION_FORM_VIEW_NAME,"model", model);
	}

	@RequestMapping(value="/submit", method = RequestMethod.POST)
	@Transactional
	public ModelAndView submitApplication(@RequestParam Integer id) {
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();		
		ApplicationForm applicationForm = applicationDAO.get(id);
		if(applicationForm == null || ! user.equals(applicationForm.getApplicant())){
			throw new ResourceNotFoundException();
		}		
		
		applicationForm.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		applicationDAO.save(applicationForm);
		return new   ModelAndView("redirect:/pgadmissions/applications");
	
	}
	
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
	
	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}
}
