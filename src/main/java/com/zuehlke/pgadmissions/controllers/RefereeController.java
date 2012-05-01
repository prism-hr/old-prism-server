package com.zuehlke.pgadmissions.controllers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.validators.RefereeValidator;
@RequestMapping("/update")
@Controller
public class RefereeController {

	private static final String STUDENTS_FORM_REFEREES_VIEW = "/private/pgStudents/form/components/references_details";
	private final RefereeService refereeService;
	private final CountryService countryService;
	private final ApplicationsService applicationsService;
	private final CountryPropertyEditor countryPropertyEditor;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final RefereeValidator refereeValidator;
	private final EncryptionUtils encryptionUtils;

	RefereeController() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public RefereeController(RefereeService refereeService, CountryService countryService, ApplicationsService applicationsService,
			CountryPropertyEditor countryPropertyEditor, ApplicationFormPropertyEditor applicationFormPropertyEditor, RefereeValidator refereeValidator, EncryptionUtils encryptionUtils) {
		this.refereeService = refereeService;
		this.countryService = countryService;
		this.applicationsService = applicationsService;
		this.countryPropertyEditor = countryPropertyEditor;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.refereeValidator = refereeValidator;
		this.encryptionUtils = encryptionUtils;
	}

	@RequestMapping(value = "/editReferee", method = RequestMethod.POST)
	public String editReferee(@Valid Referee referee, BindingResult result) {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		ApplicationForm application = referee.getApplication();
		if(application.isDecided()){
			throw new CannotUpdateApplicationException();
		}
		if(result.hasErrors()){
			return STUDENTS_FORM_REFEREES_VIEW;
		}
		referee.setActivationCode(encryptionUtils.generateUUID());
		if(!application.isSubmitted()){
			refereeService.save(referee);
		}
		else if(application.isInState("APPROVAL")){ //later to be checked if it it review as well
			refereeService.processRefereesRoles(Arrays.asList(referee));
			refereeService.sendRefereeMailNotification(referee);
		}
		application.setLastUpdated(new Date());
		applicationsService.save(application);
		return "redirect:/update/getReferee?applicationId=" + application.getId();
			
	}

	@ModelAttribute("countries")
	public List<Country> getAllCountries() {
		return countryService.getAllCountries();
	}
	

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {		
		ApplicationForm application = applicationsService.getApplicationById(applicationId);
		if(application == null || !getCurrentUser().canSee(application)){
			throw new ResourceNotFoundException();
		}
		return application;
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required=false)String message) {		
		return message;
	}

	@InitBinder(value="referee")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(refereeValidator);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);		
	}


	@ModelAttribute
	public Referee getReferee(@RequestParam(required=false) Integer refereeId) {
		if (refereeId == null) {
			return new Referee();
		}
		Referee referee = refereeService.getRefereeById(refereeId);
		if (referee == null) {
			throw new ResourceNotFoundException();
		}
		return referee;
	}

	
	@RequestMapping(value = "/getReferee", method = RequestMethod.GET)
	public String getRefereeView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENTS_FORM_REFEREES_VIEW;
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
	


}
