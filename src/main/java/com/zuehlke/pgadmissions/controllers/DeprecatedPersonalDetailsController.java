package com.zuehlke.pgadmissions.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

@Controller
@RequestMapping("/personalDetails")
public class DeprecatedPersonalDetailsController {

	private final PersonalDetailsService personalDetailsService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final CountryPropertyEditor countryPropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final CountryService countryService;
	private final PersonalDetailsValidator personalDetailValidator;
	private final LanguageService languageService;
	private final LanguagePropertyEditor languagePropertyEditor;

	DeprecatedPersonalDetailsController() {
		this(null, null, null, null, null, null, null, null);
	}

	@Autowired
	public DeprecatedPersonalDetailsController(PersonalDetailsService personalDetailsService, CountryService countryService, LanguageService languageService,
			ApplicationFormPropertyEditor applicationFormPropertyEditor, CountryPropertyEditor countryPropertyEditor,
			LanguagePropertyEditor languagePropertyEditor, DatePropertyEditor datePropertyEditor, PersonalDetailsValidator personalDetailValidator) {
		this.personalDetailsService = personalDetailsService;
		this.countryService = countryService;
		this.languageService = languageService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.countryPropertyEditor = countryPropertyEditor;
		this.languagePropertyEditor = languagePropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.personalDetailValidator = personalDetailValidator;

	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView editPersonalDetails(@ModelAttribute("personalDetails") PersonalDetails personalDetail, BindingResult errors) {

		if (personalDetail.getApplication() != null && personalDetail.getApplication().isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		if (personalDetail.getApplication() != null && personalDetail.getApplication().getApplicant() != null
				&& !getCurrentUser().equals(personalDetail.getApplication().getApplicant())) {
			throw new ResourceNotFoundException();
		}

		personalDetailValidator.validate(personalDetail, errors);
		if (!errors.hasErrors()) {

			personalDetailsService.save(personalDetail);

		}
		if (personalDetail.getApplication() != null) {
			// this is so that the entered values are re-displayed correctly on
			// the form
			personalDetail.getApplication().setPersonalDetails(personalDetail);
		}
		ApplicationPageModel applicationPageModel = new ApplicationPageModel();
		applicationPageModel.setApplicationForm(personalDetail.getApplication());
		applicationPageModel.setUser(getCurrentUser());
		applicationPageModel.setCountries(countryService.getAllCountries());
		applicationPageModel.setLanguages(languageService.getAllLanguages());
		applicationPageModel.setResult(errors);
		applicationPageModel.setGenders(Gender.values());
		applicationPageModel.setPhoneTypes(PhoneType.values());
		ModelAndView modelAndView = new ModelAndView("private/pgStudents/form/components/personal_details", "model", applicationPageModel);
		return modelAndView;

	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	@ModelAttribute("personalDetails")
	public PersonalDetails getPersonalDetails(Integer personalDetailsId) {
		if (personalDetailsId == null) {
			return newPersonalDetail();
		}
		PersonalDetails personalDetails = personalDetailsService.getPersonalDetailsById(personalDetailsId);
		if (personalDetails == null) {
			throw new ResourceNotFoundException();
		}
		return personalDetails;
	}

	PersonalDetails newPersonalDetail() {
		return new PersonalDetails();
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);
		binder.registerCustomEditor(Language.class, languagePropertyEditor);
		binder.registerCustomEditor(Date.class, datePropertyEditor);		
	}

}
