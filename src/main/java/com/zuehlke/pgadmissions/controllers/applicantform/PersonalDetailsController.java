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
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EntityPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsUserValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

@Controller
@RequestMapping("/update")
public class PersonalDetailsController {

    @Autowired
    private ApplicationService applicationFormService;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private LocalDatePropertyEditor datePropertyEditor;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private EntityPropertyEditor<Language> languagePropertyEditor;

    @Autowired
    private EntityPropertyEditor<Country> countryPropertyEditor;

    @Autowired
    private EntityPropertyEditor<Disability> disabilityPropertyEditor;

    @Autowired
    private EntityPropertyEditor<Ethnicity> ethnicityPropertyEditor;

    @Autowired
    private EntityPropertyEditor<Domicile> domicilePropertyEditor;

    @Autowired
    private PersonalDetailsValidator personalDetailsValidator;

    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    private PersonalDetailsUserValidator personalDetailsUserValidator;

    @Autowired
    private PersonalDetailsService personalDetailsService;

    @RequestMapping(value = "/getPersonalDetails", method = RequestMethod.GET)
    public String getPersonalDetailsView(@ModelAttribute Application applicationForm, ModelMap modelMap) {
        return returnView(modelMap, personalDetailsService.getOrCreate(applicationForm), applicationForm.getUser());
    }

    @RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
    public String editPersonalDetails(@Valid PersonalDetails personalDetails, BindingResult personalDetailsResult, @Valid User updatedUser,
            BindingResult userResult, ModelMap modelMap, @ModelAttribute Application applicationForm) {
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
        binder.registerCustomEditor(Application.class, applicationFormPropertyEditor);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
    }

    @InitBinder(value = "updatedUser")
    public void registerUserValidator(WebDataBinder binder) {
        binder.setValidator(personalDetailsUserValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute("languages")
    public List<Language> getAllEnabledLanguages() {
        return importedEntityService.getAllLanguages();
    }

    @ModelAttribute("countries")
    public List<Country> getAllEnabledCountries() {
        return importedEntityService.getAllCountries();
    }

    @ModelAttribute("ethnicities")
    public List<Ethnicity> getAllEnabledEthnicities() {
        return importedEntityService.getAllEthnicities();
    }

    @ModelAttribute("disabilities")
    public List<Disability> getAllEnabledDisabilities() {
        return importedEntityService.getAllDisabilities();
    }

    @ModelAttribute("domiciles")
    public List<Domicile> getAllEnabledDomiciles() {
        return importedEntityService.getAllDomiciles();
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
    public Application getApplicationForm(String applicationId) {
        return applicationFormService.getSecuredApplication(applicationId, ApplicationFormAction.APPLICATION_COMPLETE,
                ApplicationFormAction.APPLICATION_CORRECT);
    }

    @ModelAttribute("languageQualificationTypes")
    public LanguageQualificationEnum[] getLanguageQualificationTypes() {
        return LanguageQualificationEnum.values();
    }

    private String returnView(ModelMap modelMap, PersonalDetails personalDetails, User updatedUser) {
        modelMap.put("personalDetails", personalDetails);
        modelMap.put("updatedUser", updatedUser);
        return TemplateLocation.APPLICATION_APPLICANT_PERSONAL_DETAIL;
    }

}
