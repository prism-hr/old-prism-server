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
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

@RequestMapping("/update")
@Controller
public class QualificationController {
	public static final String APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/components/qualification_details";
	private final QualificationService qualificationService;
	private final ApplicationsService applicationService;
	private final DatePropertyEditor datePropertyEditor;
	private final LanguageService languageService;
	private final LanguagePropertyEditor languagePropertyEditor;
	private final CountryPropertyEditor countryPropertyEditor;
	private final QualificationValidator qualificationValidator;
	private final CountryService countryService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final DocumentPropertyEditor documentPropertyEditor;

	QualificationController() {
		this(null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public QualificationController(ApplicationsService applicationsService, ApplicationFormPropertyEditor applicationFormPropertyEditor,
			DatePropertyEditor datePropertyEditor, CountryService countryService, LanguageService languageService,
			LanguagePropertyEditor languagePropertyEditor, CountryPropertyEditor countryPropertyEditor, QualificationValidator qualificationValidator,
			QualificationService qualificationService, DocumentPropertyEditor documentPropertyEditor) {
		this.applicationService = applicationsService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.countryService = countryService;
		this.languageService = languageService;
		this.languagePropertyEditor = languagePropertyEditor;
		this.countryPropertyEditor = countryPropertyEditor;
		this.qualificationValidator = qualificationValidator;
		this.qualificationService = qualificationService;
		this.documentPropertyEditor = documentPropertyEditor;

	}
	
	@InitBinder(value="qualification")
	public void registerPropertyEditors(WebDataBinder binder) {

		binder.setValidator(qualificationValidator);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(Language.class, languagePropertyEditor);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);
		
	}
	
	@RequestMapping(value = "/getQualification", method = RequestMethod.GET)
	public String getQualificationView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME;
	}
	
	@RequestMapping(value = "/editQualification", method = RequestMethod.POST)
	public String editQualification(@Valid Qualification qualification, BindingResult result) {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if(qualification.getApplication().isDecided()){
			throw new CannotUpdateApplicationException();
		}
		if(result.hasErrors()){
			return APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME;
		}
		qualificationService.save(qualification);
		return "redirect:/update/getQualification?applicationId=" + qualification.getApplication().getId();
			
	}

	@ModelAttribute
	public Qualification getQualification(@RequestParam(required=false) Integer qualificationId) {
		if (qualificationId == null) {
			return new Qualification();
		}
		Qualification qualification = qualificationService.getQualificationById(qualificationId);
		if (qualification == null) {
			throw new ResourceNotFoundException();
		}
		return qualification;
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	/* Reference data section */
	
	@ModelAttribute("languages")
	public List<Language> getAllLanguages() {
		return languageService.getAllLanguages();
	}

	@ModelAttribute("countries")
	public List<Country> getAllCountries() {
		return countryService.getAllCountries();
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
