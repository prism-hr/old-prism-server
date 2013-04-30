package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DisabilityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EthnicityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
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
@SessionAttributes({ "personalDetails" })
public class PersonalDetailsController {

    private static final String STUDENTS_FORM_PERSONAL_DETAILS_VIEW = "/private/pgStudents/form/components/personal_details";
    private static final String STUDENTS_FORM_PERSONAL_DETAILS_LANGUAGE_QUALIFICATION_VIEW = "/private/pgStudents/form/components/personal_details_language_qualifications";

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
    private final PersonalDetailsService personalDetailsService;
    private final UserService userService;
    private final DomicileService domicileService;
    private final DomicilePropertyEditor domicilePropertyEditor;
    private final DocumentPropertyEditor documentPropertyEditor;
    private final DocumentService documentService;
    private final EncryptionHelper encryptionHelper;
    private final PersonalDetailsUserValidator personalDetailsUserValidator;

    public PersonalDetailsController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public PersonalDetailsController(ApplicationsService applicationsService, UserService userService,
            ApplicationFormPropertyEditor applicationFormPropertyEditor, DatePropertyEditor datePropertyEditor, CountryService countryService,
            EthnicityService ethnicityService, DisabilityService disabilityService, LanguageService languageService,
            LanguagePropertyEditor languagePropertyEditor, CountryPropertyEditor countryPropertyEditor, DisabilityPropertyEditor disabilityPropertyEditor,
            EthnicityPropertyEditor ethnicityPropertyEditor, PersonalDetailsValidator personalDetailsValidator, PersonalDetailsService personalDetailsService,
            DomicileService domicileService, DomicilePropertyEditor domicilePropertyEditor, DocumentPropertyEditor documentPropertyEditor,
            DocumentService documentService, EncryptionHelper encryptionHelper, PersonalDetailsUserValidator personalDetailsUserValidator) {
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
        this.domicileService = domicileService;
        this.domicilePropertyEditor = domicilePropertyEditor;
        this.documentPropertyEditor = documentPropertyEditor;
        this.documentService = documentService;
        this.encryptionHelper = encryptionHelper;
        this.personalDetailsUserValidator = personalDetailsUserValidator;
    }

    @InitBinder(value = "personalDetails")
    public void registerPropertyEditorsForPersonalDetails(WebDataBinder binder) {
        binder.setValidator(personalDetailsValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(String.class,"firstNationality", new StringTrimmerEditor(true));
        binder.registerCustomEditor(String.class,"secondNationality", new StringTrimmerEditor(true));
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

    @RequestMapping(value = "/getPersonalDetails", method = RequestMethod.GET)
    public String getPersonalDetailsView(@RequestParam String applicationId, Model model) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }

        ApplicationForm applicationForm = getApplicationForm(applicationId);
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();

        if (personalDetails.getLanguageQualifications().isEmpty()) {
            personalDetails.addLanguageQualification(new LanguageQualification());
        }

        model.addAttribute("personalDetails", personalDetails);
        model.addAttribute("applicationForm", applicationForm);

        return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
    }

    @RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
    public String editPersonalDetails(@Valid PersonalDetails personalDetails, BindingResult personalDetailsResult,
            @ModelAttribute("updatedUser") @Valid RegisteredUser updatedUser, BindingResult userResult, Model model, SessionStatus sessionStatus) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }

        if (personalDetails.getApplication().isDecided()) {
            throw new CannotUpdateApplicationException(personalDetails.getApplication().getApplicationNumber());
        }

        if (personalDetailsResult.hasErrors() || userResult.hasErrors()) {
            return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
        }

        if (BooleanUtils.isNotTrue(personalDetails.getLanguageQualificationAvailable())) {
            personalDetails.getLanguageQualifications().clear();
        }

        userService.updateCurrentUser(updatedUser);
        personalDetailsService.save(personalDetails);
        personalDetails.getApplication().setLastUpdated(new Date());
        applicationsService.save(personalDetails.getApplication());

        sessionStatus.setComplete();

        if (personalDetails.getLanguageQualifications().isEmpty()) {
            personalDetails.addLanguageQualification(new LanguageQualification());
        }

        return "redirect:/update/getPersonalDetails?applicationId=" + personalDetails.getApplication().getApplicationNumber();
    }

    @RequestMapping(value = "/deleteAllLanguageQualifications", method = RequestMethod.POST)
    public String deleteAllLanguageQualifications(@RequestParam(value = "englishFirstLanguage", required = false) Boolean englishFirstLanguage,
            @RequestParam(value = "languageQualificationAvailable", required = false) Boolean languageQualificationAvailable,
            @ModelAttribute("personalDetails") PersonalDetails personalDetails, Model model) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }

        for (LanguageQualification languageQualification : personalDetails.getLanguageQualifications()) {
            languageQualification.setLanguageQualificationDocument(null);
        }

        personalDetails.setEnglishFirstLanguage(englishFirstLanguage);
        personalDetails.setLanguageQualificationAvailable(languageQualificationAvailable);
        personalDetails.getLanguageQualifications().clear();
        personalDetails.addLanguageQualification(new LanguageQualification());
        return STUDENTS_FORM_PERSONAL_DETAILS_LANGUAGE_QUALIFICATION_VIEW;
    }

    @RequestMapping(value = "/deleteLanguageQualificationsDocument", method = RequestMethod.POST)
    public String deleteLanguageQualificationsDocument(@RequestParam String documentId, Model model) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }

        if (StringUtils.isNotBlank(documentId)) {
            documentService.delete(documentService.getDocumentById(encryptionHelper.decryptToInteger(documentId)));
        }

        return STUDENTS_FORM_PERSONAL_DETAILS_LANGUAGE_QUALIFICATION_VIEW;
    }

    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute(value = "updatedUser")
    public RegisteredUser getUpdatedUser() {
        RegisteredUser registeredUser = new RegisteredUser();
        RegisteredUser currentUser = getUser();
        registeredUser.setFirstName(currentUser.getFirstName());
        registeredUser.setFirstName2(currentUser.getFirstName2());
        registeredUser.setFirstName3(currentUser.getFirstName3());
        registeredUser.setLastName(currentUser.getLastName());
        registeredUser.setEmail(currentUser.getEmail());
        registeredUser.setPassword(currentUser.getPassword());
        return registeredUser;
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

    public PersonalDetails getPersonalDetails(String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        if (applicationForm.getPersonalDetails() == null) {
            return new PersonalDetails();
        }
        return applicationForm.getPersonalDetails();
    }

    @ModelAttribute
    public ApplicationForm getApplicationForm(String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null || !getCurrentUser().canSee(application)) {
            throw new ResourceNotFoundException();
        }
        return application;
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
        return getCurrentUser();
    }

    @ModelAttribute("errorCode")
    public String getErrorCode(String errorCode) {
        return errorCode;
    }
}
