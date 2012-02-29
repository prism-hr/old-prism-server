package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	
	private final ProjectDAO projectDAO;
	private final ApplicationsService applicationService;
	private final UserService userService;

	ApplicationFormController() {
		this(null, null, null);
	}

	@Autowired
	public ApplicationFormController(ProjectDAO projectDAO, ApplicationsService applicationService, UserService userService) {
		this.projectDAO = projectDAO;
		this.applicationService = applicationService;
		this.userService = userService;
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
		
		return new  ModelAndView("redirect:/application","id", applicationForm.getId());
		
	}

	@RequestMapping(value="/edit", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editApplicationForm(@ModelAttribute PersonalDetails personalDetails, 
											@RequestParam Integer appId, @RequestParam Integer id,
											BindingResult result) {	
		
		PersonalDetailsValidator personalDetailsValidator = new PersonalDetailsValidator();
		personalDetailsValidator.validate(personalDetails, result);
		if (result.hasErrors()) {
			PageModel model = new PageModel();
			model.setErrorObjs(result.getAllErrors());
			return new  ModelAndView("error/errors","model", model);
		}
		RegisteredUser user = userService.getUser(id);
		user.setLastName(personalDetails.getLastName());
		user.setFirstName(personalDetails.getFirstName());
		user.setEmail(personalDetails.getEmail());
		userService.save(user);
		
		return new  ModelAndView("redirect:/application","id", appId);
	}

	@RequestMapping(value="/submit", method = RequestMethod.POST)
	public ModelAndView submitApplication(@RequestParam Integer applicationForm) {
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm appForm = applicationService.getApplicationById(applicationForm);
		if(appForm == null || ! user.equals(appForm.getApplicant()) || appForm.isSubmitted()){
			throw new ResourceNotFoundException();
		}		
		appForm.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		applicationService.save(appForm);
		return new  ModelAndView("redirect:/applications?submissionSuccess=true");
	
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}
	
}
