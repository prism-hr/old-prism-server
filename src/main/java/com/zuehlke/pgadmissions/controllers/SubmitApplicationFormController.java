package com.zuehlke.pgadmissions.controllers;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.SubmitApplicationService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = { "/submit" , "application"})
public class SubmitApplicationFormController {

	private final ApplicationsService applicationService;
	private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "/private/pgStudents/form/main_application_page";
	private static final String VIEW_APPLICATION_STAFF_VIEW_NAME = "/private/staff/application/main_application_page";
	private final SubmitApplicationService submitApplicationService;
	private final RefereeService refereeService;
	private final ApplicationFormValidator applicationFormValidator;

	SubmitApplicationFormController() {
		this(null, null, null, null);
	}

	@Autowired
	public SubmitApplicationFormController(ApplicationsService applicationService, SubmitApplicationService submitApplicationService, RefereeService refereeService, ApplicationFormValidator applicationFormValidator) {
		this.applicationService = applicationService;
		this.submitApplicationService = submitApplicationService;
		this.refereeService = refereeService;
		this.applicationFormValidator = applicationFormValidator;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitApplication(@Valid ApplicationForm applicationForm, BindingResult result) {
		if(!getCurrentUser().equals(applicationForm.getApplicant()) || applicationForm.isSubmitted() ){
			throw new ResourceNotFoundException();
		}
			
		if(result.hasErrors()){
			return VIEW_APPLICATION_APPLICANT_VIEW_NAME;			
		}
		applicationForm.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		applicationForm.setSubmittedDate(new Date());
		submitApplicationService.saveApplicationFormAndSendMailNotifications(applicationForm);
//		refereeService.processRefereesRoles(applicationForm.getReferees());
		return "redirect:/applications?submissionSuccess=true";
	}

	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = applicationService.getApplicationById(applicationId);
		if(applicationForm == null || !getCurrentUser().canSee(applicationForm) ){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
		
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	@InitBinder("applicationForm")
	public void registerValidator(WebDataBinder webDataBinder) {
		webDataBinder.setValidator(applicationFormValidator);
		
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getApplicationView() {
		if(getCurrentUser().isInRole(Authority.APPLICANT)){
		return VIEW_APPLICATION_APPLICANT_VIEW_NAME;
		}
		return VIEW_APPLICATION_STAFF_VIEW_NAME;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {		
		return getCurrentUser();
	}

}
