package com.zuehlke.pgadmissions.controllers;

import java.beans.PropertyEditor;
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
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.validators.FundingValidator;

@Controller
@RequestMapping("/update")
public class FundingController {

	private static final String STUDENT_FUNDING_DETAILS_VIEW = "/private/pgStudents/form/components/funding_details";
	private final ApplicationsService applicationService;
	private final PropertyEditor datePropertyEditor;

	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;

	private final FundingValidator fundingValidator;
	private final FundingService fundingService;
	private final DocumentPropertyEditor documentPropertyEditor;

	FundingController() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public FundingController(ApplicationsService applicationsService, ApplicationFormPropertyEditor applicationFormPropertyEditor,
			DatePropertyEditor datePropertyEditor, FundingValidator fundingValidator, FundingService fundingService,
			DocumentPropertyEditor documentPropertyEditor) {
		this.applicationService = applicationsService;

		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.fundingValidator = fundingValidator;
		this.fundingService = fundingService;
		this.documentPropertyEditor = documentPropertyEditor;
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	@InitBinder(value="funding")
	public void registerPropertyEditors(WebDataBinder binder) {

		binder.setValidator(fundingValidator);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);
		
	}

	@RequestMapping(value = "/editFunding", method = RequestMethod.POST)
	public String editFunding(@Valid Funding funding, BindingResult result) {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if(funding.getApplication().isSubmitted()){
			throw new CannotUpdateApplicationException();
		}
		if(result.hasErrors()){
			return STUDENT_FUNDING_DETAILS_VIEW;
		}
		fundingService.save(funding);
		return "redirect:/update/getFunding?applicationId=" + funding.getApplication().getId();
			
	}


	@RequestMapping(value = "/getFunding", method = RequestMethod.GET)
	public String getFundingView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENT_FUNDING_DETAILS_VIEW;
	}

	@ModelAttribute
	public Funding getFunding(@RequestParam(required=false) Integer fundingId) {
		if (fundingId == null) {
			return new Funding();
		}
		Funding funding = fundingService.getFundingById(fundingId);
		if (funding == null) {
			throw new ResourceNotFoundException();
		}
		return funding;
	}

	@ModelAttribute("fundingTypes")
	public FundingType[] getFundingTypes() {		
		return FundingType.values();
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



}
