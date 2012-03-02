package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/submit")
public class SubmitApplicationFormController {
	
	private final ApplicationsService applicationService;
	private final UserPropertyEditor userPropertyEditor;

	
	SubmitApplicationFormController() {
		this(null, null);
	}

	@Autowired
	public SubmitApplicationFormController(ApplicationsService applicationService,
			UserPropertyEditor userPropertyEditor) {
		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
	}
	

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView submitApplication(@ModelAttribute ApplicationForm applicationForm) {
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if (applicationForm == null || !user.equals(applicationForm.getApplicant()) || applicationForm.isSubmitted()) {
			throw new ResourceNotFoundException();
		}
		applicationForm.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		applicationService.save(applicationForm);
		return new ModelAndView("redirect:/applications?submissionSuccess=true");

	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}
	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(Integer applicationFormId) {
		RegisteredUser approver = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationService.getApplicationById(applicationFormId);
		Assert.notNull(applicationForm);
		if(applicationForm == null || !approver.canSee(applicationForm)){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}
}
