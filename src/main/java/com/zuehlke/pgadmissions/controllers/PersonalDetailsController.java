package com.zuehlke.pgadmissions.controllers;

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
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;
@RequestMapping("/update")
@Controller
public class PersonalDetailsController {

	private static final String STUDENTS_FORM_PERSONAL_DETAILS_VIEW = "/private/pgStudents/form/components/personal_details";
	private final ApplicationsService applicationsService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final CountryService countryService;
	private final LanguageService languageService;
	private final LanguagePropertyEditor languagePropertyEditor;
	private final CountryPropertyEditor countryPropertyEditor;
	private final PersonalDetailsValidator personalDetailsValidator;
	private final PersonalDetailsService personalDetailsService;

	PersonalDetailsController() {
		this(null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public PersonalDetailsController(ApplicationsService applicationsService, ApplicationFormPropertyEditor applicationFormPropertyEditor,
			DatePropertyEditor datePropertyEditor, CountryService countryService, LanguageService languageService,
			LanguagePropertyEditor languagePropertyEditor, CountryPropertyEditor countryPropertyEditor, PersonalDetailsValidator personalDetailsValidator,
			PersonalDetailsService personalDetailsService) {
		this.applicationsService = applicationsService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.countryService = countryService;
		this.languageService = languageService;
		this.languagePropertyEditor = languagePropertyEditor;
		this.countryPropertyEditor = countryPropertyEditor;
		this.personalDetailsValidator = personalDetailsValidator;
		this.personalDetailsService = personalDetailsService;
	}

	@RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
	public String editPersonalDetails(@Valid PersonalDetails personalDetails, BindingResult result) {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if(personalDetails.getApplication().isSubmitted()){
			throw new CannotUpdateApplicationException();
		}
		if(result.hasErrors()){
			return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
		}
		personalDetailsService.save(personalDetails);
		return "redirect:/update/getPersonalDetails?applicationId=" + personalDetails.getApplication().getId();
			
	}

	@RequestMapping(value = "/getPersonalDetails", method = RequestMethod.GET)
	public String getPersonalDetailsView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}


	@ModelAttribute("languages")
	public List<Language> getAllLanguages() {
		return languageService.getAllLanguages();
	}

	@ModelAttribute("countries")
	public List<Country> getAllCountries() {
		return countryService.getAllCountries();
	}
	
	@ModelAttribute("genders")
	public Gender[] getGenders() {
		return Gender.values();
	}

	@InitBinder(value = "personalDetails")
	public void registerPropertyEditors(WebDataBinder binder) {

		binder.setValidator(personalDetailsValidator);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(Language.class, languagePropertyEditor);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		
	}

	@ModelAttribute
	public PersonalDetails getPersonalDetails(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		
		if (applicationForm.getPersonalDetails() == null) {
			return new PersonalDetails();
		}
		return applicationForm.getPersonalDetails();
		
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

	@ModelAttribute("user")
	public RegisteredUser getUser() {		
		return getCurrentUser();
	}

}
