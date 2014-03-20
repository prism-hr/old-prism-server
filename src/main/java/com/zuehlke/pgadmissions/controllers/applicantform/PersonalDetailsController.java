package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DisabilityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EthnicityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DisabilityService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.EthnicityService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsUserValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

@Controller
@RequestMapping("/update")
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
    private final DisabilityPropertyEditor disabilityPropertyEditor;
    private final EthnicityPropertyEditor ethnicityPropertyEditor;
    private final PersonalDetailsValidator personalDetailsValidator;
    private final UserService userService;
    private final DomicileService domicileService;
    private final DomicilePropertyEditor domicilePropertyEditor;
    private final DocumentPropertyEditor documentPropertyEditor;
    private final DocumentService documentService;
    private final EncryptionHelper encryptionHelper;
    private final PersonalDetailsUserValidator personalDetailsUserValidator;
    private final PersonalDetailsService personalDetailsService;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    public PersonalDetailsController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public PersonalDetailsController(ApplicationsService applicationsService, UserService userService,
            ApplicationFormPropertyEditor applicationFormPropertyEditor, DatePropertyEditor datePropertyEditor, CountryService countryService,
            EthnicityService ethnicityService, DisabilityService disabilityService, LanguageService languageService,
            LanguagePropertyEditor languagePropertyEditor, CountryPropertyEditor countryPropertyEditor, DisabilityPropertyEditor disabilityPropertyEditor,
            EthnicityPropertyEditor ethnicityPropertyEditor, PersonalDetailsValidator personalDetailsValidator, DomicileService domicileService,
            DomicilePropertyEditor domicilePropertyEditor, DocumentPropertyEditor documentPropertyEditor, DocumentService documentService,
            EncryptionHelper encryptionHelper, PersonalDetailsUserValidator personalDetailsUserValidator, PersonalDetailsService personalDetailsService,
            ApplicationFormUserRoleService applicationFormUserRoleService) {
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
        this.domicileService = domicileService;
        this.domicilePropertyEditor = domicilePropertyEditor;
        this.documentPropertyEditor = documentPropertyEditor;
        this.documentService = documentService;
        this.encryptionHelper = encryptionHelper;
        this.personalDetailsUserValidator = personalDetailsUserValidator;
        this.personalDetailsService = personalDetailsService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    @RequestMapping(value = "/getPersonalDetails", method = RequestMethod.GET)
    public String getPersonalDetailsView(@RequestParam String applicationId, ModelMap modelMap) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);

        PersonalDetails personalDetails = Objects.firstNonNull(applicationForm.getPersonalDetails(), new PersonalDetails());
        RegisteredUser updatedUser = applicationForm.getApplicant();

        if (personalDetails.getLanguageQualification() == null) {
            personalDetails.setLanguageQualification(new LanguageQualification());
        }

        modelMap.put("personalDetails", personalDetails);
        modelMap.put("updatedUser", updatedUser);

        return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
    }

    @RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
    public String editPersonalDetails(@Valid PersonalDetails personalDetails, BindingResult personalDetailsResult,
            @ModelAttribute("updatedUser") @Valid RegisteredUser updatedUser, BindingResult userResult, Model model,
            @ModelAttribute ApplicationForm application) {
        if (personalDetailsResult.hasErrors() || userResult.hasErrors()) {
            return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
        }
        
        personalDetailsService.save(application.getId(), personalDetails, updatedUser);
        applicationFormUserRoleService.insertApplicationUpdate(application, getUser(), ApplicationUpdateScope.ALL_USERS);
        return "redirect:/update/getPersonalDetails?applicationId=" + personalDetails.getApplication().getApplicationNumber();
    }

    @InitBinder(value = "personalDetails")
    public void registerPropertyEditorsForPersonalDetails(WebDataBinder binder) {
        binder.setValidator(personalDetailsValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
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

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationsService.getEditableApplicationForm(applicationId);
    }

    @ModelAttribute("languageQualificationTypes")
    public LanguageQualificationEnum[] getLanguageQualificationTypes() {
        return LanguageQualificationEnum.values();
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("errorCode")
    public String getErrorCode(String errorCode) {
        return errorCode;
    }
}