package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/update")
public class AdditionalInformationController {

	private static final String STUDENTS_FORM_ADDITIONAL_INFORMATION_VIEW = "/private/pgStudents/form/components/additional_information";
	private final ApplicationsService applicationService;

	AdditionalInformationController() {
		this(null);
	}

	@Autowired
	public AdditionalInformationController(ApplicationsService applicationsService) {
		this.applicationService = applicationsService;

	}

	@RequestMapping(value = "/editAdditionalInformation", method = RequestMethod.POST)
	public String editAdditionalInformation(ApplicationForm applicationForm) {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if(applicationForm.isSubmitted()){
			throw new CannotUpdateApplicationException();
		}	
		applicationService.save(applicationForm);
		return "redirect:/update/getAdditionalInformation?applicationId=" + applicationForm.getId();
			
	}

	@RequestMapping(value = "/getAdditionalInformation", method = RequestMethod.GET)
	public String getAdditionalInformationView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENTS_FORM_ADDITIONAL_INFORMATION_VIEW;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {		
		ApplicationForm application = applicationService.getApplicationById(applicationId);
		if(application == null || !getCurrentUser().canSee(application)){
			throw new ResourceNotFoundException();
		}
		return application;
	}


	@ModelAttribute("message")
	public String getMessage(@RequestParam(required=false)String message) {		
		return message;
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
}
