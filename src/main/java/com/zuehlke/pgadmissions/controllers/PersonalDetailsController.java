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
import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguageProficiencyJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.MessengerJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.NationalityJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PhoneNumberJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.validators.PersonalDetailValidator;

@Controller
@RequestMapping("/personalDetails")
public class PersonalDetailsController {

	private final PersonalDetailsService personalDetailsService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final CountryPropertyEditor countryPropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final CountryService countryService;
	private final PersonalDetailValidator personalDetailValidator;
	private final PhoneNumberJSONPropertyEditor phoneNumberJSONPropertyEditor;
	private final LanguageService languageService;
	private final LanguagePropertyEditor languagePropertyEditor;
	private final NationalityJSONPropertyEditor nationalityJSONPropertyEditor;
	private final LanguageProficiencyJSONPropertyEditor languageProficiencyJSONPropertyEditor;
	private final MessengerJSONPropertyEditor messengerJSONPropertyEditor;

	PersonalDetailsController() {
		this(null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public PersonalDetailsController(PersonalDetailsService personalDetailsService, CountryService countryService, LanguageService languageService,
			ApplicationFormPropertyEditor applicationFormPropertyEditor, CountryPropertyEditor countryPropertyEditor,
			LanguagePropertyEditor languagePropertyEditor, DatePropertyEditor datePropertyEditor, PersonalDetailValidator personalDetailValidator,
			PhoneNumberJSONPropertyEditor phoneNumberJSONPropertyEditor, NationalityJSONPropertyEditor nationalityJSONPropertyEditor,
			LanguageProficiencyJSONPropertyEditor languageProficiencyJSONPropertyEditor, MessengerJSONPropertyEditor messengerJSONPropertyEditor) {
		this.personalDetailsService = personalDetailsService;
		this.countryService = countryService;
		this.languageService = languageService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.countryPropertyEditor = countryPropertyEditor;
		this.languagePropertyEditor = languagePropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.personalDetailValidator = personalDetailValidator;
		this.phoneNumberJSONPropertyEditor = phoneNumberJSONPropertyEditor;
		this.nationalityJSONPropertyEditor = nationalityJSONPropertyEditor;
		this.languageProficiencyJSONPropertyEditor = languageProficiencyJSONPropertyEditor;
		this.messengerJSONPropertyEditor = messengerJSONPropertyEditor;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView editPersonalDetails(@ModelAttribute("personalDetails") PersonalDetail personalDetail, BindingResult errors) {

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
		applicationPageModel.setResidenceStatuses(ResidenceStatus.values());
		applicationPageModel.setGenders(Gender.values());
		applicationPageModel.setPhoneTypes(PhoneType.values());
		applicationPageModel.setLanguageAptitudes(LanguageAptitude.values());
		ModelAndView modelAndView = new ModelAndView("private/pgStudents/form/components/personal_details", "model", applicationPageModel);	
		return modelAndView;

	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	@ModelAttribute("personalDetails")
	public PersonalDetail getPersonalDetails(Integer personalDetailsId) {
		if (personalDetailsId == null) {
			return newPersonalDetail();
		}
		PersonalDetail personalDetails = personalDetailsService.getPersonalDetailsById(personalDetailsId);
		if (personalDetails == null) {
			throw new ResourceNotFoundException();
		}
		return personalDetails;
	}

	PersonalDetail newPersonalDetail() {
		return new PersonalDetail();
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);
		binder.registerCustomEditor(Language.class, languagePropertyEditor);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(Telephone.class, phoneNumberJSONPropertyEditor);
		binder.registerCustomEditor(Nationality.class, nationalityJSONPropertyEditor);
		binder.registerCustomEditor(LanguageProficiency.class, languageProficiencyJSONPropertyEditor);
		binder.registerCustomEditor(Messenger.class, messengerJSONPropertyEditor);
	}

}
