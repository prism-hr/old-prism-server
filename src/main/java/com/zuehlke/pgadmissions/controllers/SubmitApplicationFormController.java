package com.zuehlke.pgadmissions.controllers;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.time.DateUtils;
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
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = { "/submit" , "application"})
public class SubmitApplicationFormController {

	private final ApplicationsService applicationService;
	private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "/private/pgStudents/form/main_application_page";
	private static final String VIEW_APPLICATION_STAFF_VIEW_NAME = "/private/staff/application/main_application_page";
	private final ApplicationFormValidator applicationFormValidator;
	private final StageDurationDAO stageDurationDAO;
	private static final String VIEW_APPLICATION_INTERNAL_PLAIN_VIEW_NAME = "/private/staff/application/main_application_page_without_headers";

	SubmitApplicationFormController() {
		this(null, null,  null);
	}

	@Autowired
	public SubmitApplicationFormController(ApplicationsService applicationService, ApplicationFormValidator applicationFormValidator,  StageDurationDAO stageDurationDAO) {
		this.applicationService = applicationService;
		this.applicationFormValidator = applicationFormValidator;
		this.stageDurationDAO = stageDurationDAO;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitApplication(@Valid ApplicationForm applicationForm, BindingResult result) {
		if(!getCurrentUser().equals(applicationForm.getApplicant()) || applicationForm.isDecided()){
			throw new ResourceNotFoundException();
		}
			
		if(result.hasErrors()){
			return VIEW_APPLICATION_APPLICANT_VIEW_NAME;			
		}
		applicationForm.setStatus(ApplicationFormStatus.VALIDATION);		
		applicationForm.setDueDate(calculateAndGetValidationDueDate());
		applicationForm.setSubmittedDate(new Date());
		applicationForm.setLastUpdated(applicationForm.getSubmittedDate());
				
		applicationService.save(applicationForm);
		return "redirect:/applications?submissionSuccess=true";
	}

	public Date calculateAndGetValidationDueDate() {
		 Calendar dueDate = Calendar.getInstance();
		 dueDate.add(Calendar.MINUTE, stageDurationDAO.getByStatus(ApplicationFormStatus.VALIDATION).getDurationInMinutes());
		 return dueDate.getTime();
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
	public String getApplicationView(HttpServletRequest request) {
		if(getCurrentUser().isInRole(Authority.APPLICANT)){
			return VIEW_APPLICATION_APPLICANT_VIEW_NAME;
		}
		if (request != null && request.getParameter("embeddedApplication") != null && request.getParameter("embeddedApplication").equals("true")) {
			return VIEW_APPLICATION_INTERNAL_PLAIN_VIEW_NAME;
		}
		return VIEW_APPLICATION_STAFF_VIEW_NAME;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {		
		return getCurrentUser();
	}

}
