package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;

@RequestMapping("/update")
@Controller
public class EmploymentController {

	static final String STUDENTS_EMPLOYMENT_DETAILS_VIEW = "/private/pgStudents/form/components/employment_position_details";
	private final EmploymentPositionService employmentPositionService;
	private final LanguageService languageService;
	private final CountryService countryService;
	private final ApplicationsService applicationService;
	private final LanguagePropertyEditor languagePropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final CountryPropertyEditor countryPropertyEditor;
	private final EmploymentPositionValidator employmentPositionValidator;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final UserService userService;
	private final EncryptionHelper encryptionHelper;
	private final ApplicationFormAccessService accessService;

	EmploymentController() {
		this(null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public EmploymentController(EmploymentPositionService employmentPositionService, LanguageService languageService, CountryService countryService,
			ApplicationsService applicationsService, LanguagePropertyEditor languagePropertyEditor, DatePropertyEditor datePropertyEditor,
			CountryPropertyEditor countryPropertyEditor, ApplicationFormPropertyEditor applicationFormPropertyEditor,
			EmploymentPositionValidator employmentPositionValidator, UserService userService, EncryptionHelper encryptionHelper, final ApplicationFormAccessService accessService) {
		this.employmentPositionService = employmentPositionService;
		this.languageService = languageService;
		this.countryService = countryService;
		this.applicationService = applicationsService;
		this.languagePropertyEditor = languagePropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.countryPropertyEditor = countryPropertyEditor;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.employmentPositionValidator = employmentPositionValidator;
		this.userService = userService;
		this.encryptionHelper = encryptionHelper;
        this.accessService = accessService;
	}

	@InitBinder("employmentPosition")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(employmentPositionValidator);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(Language.class, languagePropertyEditor);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);		
	}
	
	public StringTrimmerEditor newStringTrimmerEditor() {
	     return new StringTrimmerEditor(false);
	 }
	
	@RequestMapping(value = "/getEmploymentPosition", method = RequestMethod.GET)
	public String getEmploymentView() {
		if (!userService.getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENTS_EMPLOYMENT_DETAILS_VIEW;
	}
	
	@RequestMapping(value = "/editEmploymentPosition", method = RequestMethod.POST)
	public String editEmployment(@Valid EmploymentPosition employment, BindingResult result) {
		if (!userService.getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if(employment.getApplication().isDecided()){
			throw new CannotUpdateApplicationException(employment.getApplication().getApplicationNumber());
		}
		if(result.hasErrors()){
			return STUDENTS_EMPLOYMENT_DETAILS_VIEW;
		}
		ApplicationForm applicationForm = employment.getApplication();
		applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
		accessService.updateAccessTimestamp(applicationForm, userService.getCurrentUser(), new Date());
		employmentPositionService.save(employment);
		applicationForm.setLastUpdated(new Date());
		applicationService.save(employment.getApplication());
		return "redirect:/update/getEmploymentPosition?applicationId=" + employment.getApplication().getApplicationNumber();
	}

	@ModelAttribute("languages")
	public List<Language> getAllEnabledLanguages() {
		return languageService.getAllEnabledLanguages();
	}

	@ModelAttribute("countries")
	public List<Country> getAllEnabledCountries() {
		return countryService.getAllEnabledCountries();
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
		if (application == null || !userService.getCurrentUser().canSee(application)) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	@ModelAttribute
	public EmploymentPosition getEmploymentPosition(@RequestParam(value="employmentId", required=false) String encryptedEmploymentId) {

		if (StringUtils.isBlank(encryptedEmploymentId)) {
			return new EmploymentPosition();
		}
		EmploymentPosition employment = employmentPositionService.getEmploymentPositionById(encryptionHelper.decryptToInteger(encryptedEmploymentId));
		if (employment == null) {
			throw new ResourceNotFoundException();
		}
		return employment;
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required=false)String message) {		
		return message;
	}	
}
