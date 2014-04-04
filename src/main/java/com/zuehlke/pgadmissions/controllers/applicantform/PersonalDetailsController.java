package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DisabilityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EthnicityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DisabilityService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.EthnicityService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsUserValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

@Controller
@RequestMapping("/update")
public class PersonalDetailsController {

    @Autowired
    private ApplicationFormService applicationFormService;
    
    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;
    
    @Autowired
    private DatePropertyEditor datePropertyEditor;
    
    @Autowired
    private CountryService countryService;
    
    @Autowired
    private DomicileService domicileService;
    
    @Autowired
    private EthnicityService ethnicityService;
    
    @Autowired
    private DisabilityService disabilityService;
    
    @Autowired
    private LanguageService languageService;
    
    @Autowired
    private LanguagePropertyEditor languagePropertyEditor;
    
    @Autowired
    private CountryPropertyEditor countryPropertyEditor;
    
    @Autowired
    private DisabilityPropertyEditor disabilityPropertyEditor;
    
    @Autowired
    private EthnicityPropertyEditor ethnicityPropertyEditor;
    
    @Autowired
    private PersonalDetailsValidator personalDetailsValidator;
    
    @Autowired
    private DomicilePropertyEditor domicilePropertyEditor;
    
    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;
    
    @Autowired
    private PersonalDetailsUserValidator personalDetailsUserValidator;
    
    @Autowired
    private PersonalDetailsService personalDetailsService;

    @RequestMapping(value = "/getPersonalDetails", method = RequestMethod.GET)
    public String getPersonalDetailsView(@ModelAttribute ApplicationForm applicationForm, ModelMap modelMap) {
        return returnView(modelMap, personalDetailsService.getOrCreate(applicationForm), applicationForm.getApplicant());
    }

    @RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
    public String editPersonalDetails(@Valid PersonalDetails personalDetails, BindingResult personalDetailsResult, @Valid RegisteredUser updatedUser,
            BindingResult userResult, ModelMap modelMap, @ModelAttribute ApplicationForm applicationForm) {
        if (personalDetailsResult.hasErrors() || userResult.hasErrors()) {
            returnView(modelMap, personalDetails, updatedUser);
        }
        personalDetailsService.saveOrUpdate(applicationForm, personalDetails, updatedUser);
        return RedirectLocation.UPDATE_APPLICATION_PERSONAL_DETAIL + applicationForm.getApplicationNumber();
    }

    @InitBinder(value = "personalDetails")
    public void registerPropertyEditorsForPersonalDetails(WebDataBinder binder) {
        binder.setValidator(personalDetailsValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(String.class, "firstNationality", new StringTrimmerEditor(true));
        binder.registerCustomEditor(String.class, "secondNationality", new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(Language.class, languagePropertyEditor);
        binder.registerCustomEditor(Country.class, countryPropertyEditor);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(Ethnicity.class, ethnicityPropertyEditor);
        binder.registerCustomEditor(Disability.class, disabilityPropertyEditor);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
    }

    @InitBinder(value = "updatedUser")
    public void registerUserValidator(WebDataBinder binder) {
        binder.setValidator(personalDetailsUserValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute("languages")
    public List<Language> getAllEnabledLanguages() {
        return languageService.getAllEnabledLanguages();
    }

    @ModelAttribute("countries")
    public List<Country> getAllEnabledCountries() {
        return countryService.getAllEnabledCountries();
    }

    @ModelAttribute("ethnicities")
    public List<Ethnicity> getAllEnabledEthnicities() {
        return ethnicityService.getAllEnabledEthnicities();
    }

    @ModelAttribute("disabilities")
    public List<Disability> getAllEnabledDisabilities() {
        return disabilityService.getAllEnabledDisabilities();
    }

    @ModelAttribute("domiciles")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileService.getAllEnabledDomiciles();
    }

    @ModelAttribute("genders")
    public Gender[] getGenders() {
        return Gender.values();
    }

    @ModelAttribute("titles")
    public Title[] getTitles() {
        return Title.values();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationFormService.getSecuredApplication(applicationId, ApplicationFormAction.COMPLETE_APPLICATION,
                ApplicationFormAction.CORRECT_APPLICATION);
    }

    @ModelAttribute("languageQualificationTypes")
    public LanguageQualificationEnum[] getLanguageQualificationTypes() {
        return LanguageQualificationEnum.values();
    }

    private String returnView(ModelMap modelMap, PersonalDetails personalDetails, RegisteredUser updatedUser) {
        modelMap.put("personalDetails", personalDetails);
        modelMap.put("updatedUser", updatedUser);
        return TemplateLocation.APPLICATION_APPLICANT_PERSONAL_DETAIL;
    }
    
}
