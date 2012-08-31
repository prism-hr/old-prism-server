package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DisabilityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EthnicityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DisabilityService;
import com.zuehlke.pgadmissions.services.EthnicityService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

@RequestMapping("/update")
@Controller
public class PersonalDetailsController {

	private static final String STUDENTS_FORM_PERSONAL_DETAILS_VIEW = "/private/pgStudents/form/components/personal_details";
	private final ApplicationsService applicationsService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final CountryService countryService;
	private final EthnicityService ethnicityService;
	private final DisabilityService disabilityService;
	private final LanguageService languageService;
	private final LanguagePropertyEditor languagePropertyEditor;
	private final CountryPropertyEditor countryPropertyEditor;
	private DisabilityPropertyEditor disabilityPropertyEditor;
	private EthnicityPropertyEditor ethnicityPropertyEditor;
	private final PersonalDetailsValidator personalDetailsValidator;
	private final PersonalDetailsService personalDetailsService;
	private final UserService userService;

	PersonalDetailsController() {
		this(null, null , null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public PersonalDetailsController(ApplicationsService applicationsService, UserService userService, ApplicationFormPropertyEditor applicationFormPropertyEditor,// 
			DatePropertyEditor datePropertyEditor, CountryService countryService, EthnicityService ethnicityService,//
			DisabilityService disabilityService, LanguageService languageService,//
			LanguagePropertyEditor languagePropertyEditor, CountryPropertyEditor countryPropertyEditor,// 
			DisabilityPropertyEditor disabilityPropertyEditor, EthnicityPropertyEditor ethnicityPropertyEditor, PersonalDetailsValidator personalDetailsValidator, PersonalDetailsService personalDetailsService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.countryService = countryService;
		this.ethnicityService = ethnicityService;
		this.disabilityService = disabilityService;
		this.languageService = languageService;
		this.languagePropertyEditor = languagePropertyEditor;
		this.countryPropertyEditor = countryPropertyEditor;
		this.ethnicityPropertyEditor = ethnicityPropertyEditor;
		this.disabilityPropertyEditor = disabilityPropertyEditor;
		this.personalDetailsValidator = personalDetailsValidator;
		this.personalDetailsService = personalDetailsService;
	}

	@RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
	public String editPersonalDetails(@Valid PersonalDetails personalDetails, BindingResult result) {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if(personalDetails.getApplication().isDecided()){
			throw new CannotUpdateApplicationException();
		}
		if(result.hasErrors()){
			return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
		}
		personalDetailsService.save(personalDetails);
		personalDetails.getApplication().setLastUpdated(new Date());
		applicationsService.save(personalDetails.getApplication());
		return "redirect:/update/getPersonalDetails?applicationId=" + personalDetails.getApplication().getApplicationNumber();
			
	}

	@RequestMapping(value = "/getPersonalDetails", method = RequestMethod.GET)
	public String getPersonalDetailsView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
	}
	
	private RegisteredUser getCurrentUser() {
		return userService.getCurrentUser();
	}


	@ModelAttribute("languages")
	public List<Language> getAllLanguages() {
		return languageService.getAllLanguages();
	}

	@ModelAttribute("countries")
	public List<Country> getAllCountries() {
		return countryService.getAllCountries();
	}

	@ModelAttribute("ethnicities")
	public List<Ethnicity> getAllEthnicities() {
		return ethnicityService.getAllEthnicities();
	}

	@ModelAttribute("disabilities")
	public List<Disability> getAllDisabilities() {
		return disabilityService.getAllDisabilities();
	}

	@ModelAttribute("genders")
	public Gender[] getGenders() {
		return Gender.values();
	}

	@ModelAttribute("titles")
	public Title[] getTitles() {
	    return Title.values();
	}
	
	@InitBinder(value = "personalDetails")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(personalDetailsValidator);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(Language.class, languagePropertyEditor);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);
		binder.registerCustomEditor(Ethnicity.class, ethnicityPropertyEditor);
		binder.registerCustomEditor(Disability.class, disabilityPropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
	}

	@ModelAttribute
	public PersonalDetails getPersonalDetails(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		
		if (applicationForm.getPersonalDetails() == null) {
			return new PersonalDetails();
		}
		return applicationForm.getPersonalDetails();
		
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {		
		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
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
	
	@ModelAttribute("errorCode")
	public String getErrorCode(String errorCode) {	
		return errorCode;
	}

}
