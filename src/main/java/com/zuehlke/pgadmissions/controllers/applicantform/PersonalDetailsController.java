package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.LanguageQualificationDAO;
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
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
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
import com.zuehlke.pgadmissions.services.EthnicityService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.LanguageQualificationValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

@RequestMapping("/update")
@Controller
public class PersonalDetailsController {

    public static final String STUDENTS_FORM_PERSONAL_DETAILS_VIEW = "/private/pgStudents/form/components/personal_details";
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
    private final DomicileDAO domicileDAO;
    private final DomicilePropertyEditor domicilePropertyEditor;
    private final LanguageQualificationValidator languageQualificationValidator;
    private final LanguageQualificationDAO languageQualificationDAO;
    private final DocumentPropertyEditor documentPropertyEditor;
    
    PersonalDetailsController() {
        this(null, null, null, null, null, null, null, null, null, 
                null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public PersonalDetailsController(
            ApplicationsService applicationsService,
            UserService userService,
            ApplicationFormPropertyEditor applicationFormPropertyEditor,
            DatePropertyEditor datePropertyEditor,
            CountryService countryService,
            EthnicityService ethnicityService,
            DisabilityService disabilityService,
            LanguageService languageService,
            LanguagePropertyEditor languagePropertyEditor,
            CountryPropertyEditor countryPropertyEditor,
            DisabilityPropertyEditor disabilityPropertyEditor, EthnicityPropertyEditor ethnicityPropertyEditor,
            PersonalDetailsValidator personalDetailsValidator, PersonalDetailsService personalDetailsService,
            DomicileDAO domicileDAO, DomicilePropertyEditor domicilePropertyEditor,
            LanguageQualificationValidator languageQualificationValidator,
            LanguageQualificationDAO languageQualificationDAO,
            DocumentPropertyEditor documentPropertyEditor) {
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
        this.domicileDAO = domicileDAO;
        this.domicilePropertyEditor = domicilePropertyEditor;
        this.languageQualificationValidator = languageQualificationValidator;
        this.languageQualificationDAO = languageQualificationDAO;
        this.documentPropertyEditor = documentPropertyEditor;
    }

    @RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
    public String editPersonalDetails(@Valid PersonalDetails personalDetails, BindingResult result, Model model) {
        
        
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }
        
        if (personalDetails.getApplication().isDecided()) {
            throw new CannotUpdateApplicationException();
        }

        model.addAttribute("languageQualification", new LanguageQualification());
        
        if (result.hasErrors()) {
            return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
        }

        personalDetailsService.save(personalDetails);
        personalDetails.getApplication().setLastUpdated(new Date());
        applicationsService.save(personalDetails.getApplication());
        
        return "redirect:/update/getPersonalDetails?applicationId="+ personalDetails.getApplication().getApplicationNumber();
    }
    
    @InitBinder(value = "personalDetails")
    public void registerPropertyEditorsForPersonalDetails(WebDataBinder binder) {
        binder.setValidator(personalDetailsValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(Language.class, languagePropertyEditor);
        binder.registerCustomEditor(Country.class, countryPropertyEditor);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(Ethnicity.class, ethnicityPropertyEditor);
        binder.registerCustomEditor(Disability.class, disabilityPropertyEditor);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
    }
    
    @InitBinder(value = "languageQualification")
    public void registerPropertyEditorsForLanguageQualification(WebDataBinder binder) {
        binder.setValidator(languageQualificationValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
    }

    @RequestMapping(value = "/getPersonalDetails", method = RequestMethod.GET)
    public String getPersonalDetailsView(Model model) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }
        model.addAttribute("languageQualification", new LanguageQualification());
        return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
    }
    
    @RequestMapping(value = "/editLanguageQualifications", method = RequestMethod.POST)
    public String editLanguageQualifications(@Valid LanguageQualification languageQualification, BindingResult result, ApplicationForm applicationForm, Model model) {
        
        model.addAttribute("languageQualification", languageQualification);
        
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }
        
        if (applicationForm.isDecided()) {
            throw new CannotUpdateApplicationException();
        }
        
        if (result.hasErrors()) {
            return PersonalDetailsController.STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
        }
        
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        
        personalDetails.addLanguageQualification(languageQualification);
        personalDetailsService.save(personalDetails);
        personalDetails.getApplication().setLastUpdated(new Date());
        
        return "redirect:/update/getPersonalDetails?applicationId="+ personalDetails.getApplication().getApplicationNumber();
    }
    
    @RequestMapping(value = "/deleteLanguageQualifications", method = RequestMethod.POST)
    public String deleteLanguageQualifications(ApplicationForm applicationForm, String languageQualificationId) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }
        
        if (applicationForm.isDecided()) {
            throw new CannotUpdateApplicationException();
        }
        
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        if (StringUtils.isNotBlank(languageQualificationId)) {
            LanguageQualification languageQualificationToDelete = languageQualificationDAO.getLanguageQualificationByEncryptedId(languageQualificationId);
            if (personalDetails.getLanguageQualifications().remove(languageQualificationToDelete)) {
                personalDetailsService.save(personalDetails);
                personalDetails.getApplication().setLastUpdated(new Date());
            }
        }        
        return "redirect:/update/getPersonalDetails?applicationId="+ personalDetails.getApplication().getApplicationNumber();
    }
    
    @RequestMapping(value = "/updateLanguageQualifications", method = RequestMethod.POST)
    public String updateLanguageQualifications(@Valid LanguageQualification languageQualification, BindingResult result, ApplicationForm applicationForm, String languageQualificationId, Model model) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }
        
        if (applicationForm.isDecided()) {
            throw new CannotUpdateApplicationException();
        }
        
        if (result.hasErrors()) {
            model.addAttribute("languageQualificationId", languageQualificationId);
            model.addAttribute("languageQualification", languageQualification);
            return PersonalDetailsController.STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
        }
        
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        
        if (StringUtils.isNotBlank(languageQualificationId)) {
            LanguageQualification languageQualificationToDelete = languageQualificationDAO.getLanguageQualificationByEncryptedId(languageQualificationId);
            if (personalDetails.getLanguageQualifications().remove(languageQualificationToDelete)) {
                personalDetails.addLanguageQualification(languageQualification);
                personalDetailsService.save(personalDetails);
                personalDetails.getApplication().setLastUpdated(new Date());
            }
        }        
        
        return "redirect:/update/getPersonalDetails?applicationId="+ personalDetails.getApplication().getApplicationNumber();
    }
    
    @RequestMapping(value = "/getLanguageQualifications", method = RequestMethod.POST)
    public String getLanguageQualifications(ApplicationForm applicationForm, String languageQualificationId, Model model) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }
        
        if (StringUtils.isNotBlank(languageQualificationId)) {
            LanguageQualification languageQualificationToEdit = languageQualificationDAO.getLanguageQualificationByEncryptedId(languageQualificationId);
            model.addAttribute("languageQualificationId", languageQualificationToEdit.getId());
            model.addAttribute("languageQualification", languageQualificationToEdit);
        }
        return STUDENTS_FORM_PERSONAL_DETAILS_VIEW;
    }
    
    @RequestMapping(value = "/deleteLanguageQualificationsDocument", method = RequestMethod.POST)
    @Transactional
    public String deleteLanguageQualificationsDocument(ApplicationForm applicationForm, String languageQualificationId) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }
        
        if (StringUtils.isNotBlank(languageQualificationId)) {
            LanguageQualification languageQualificationToDelete = languageQualificationDAO.getLanguageQualificationByEncryptedId(languageQualificationId);
            languageQualificationToDelete.setLanguageQualificationDocument(null);
            languageQualificationDAO.save(languageQualificationToDelete);
        }
        
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        
        return "redirect:/update/getPersonalDetails?applicationId="+ personalDetails.getApplication().getApplicationNumber();
    }
    
    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
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
        return domicileDAO.getAllEnabledDomiciles();
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
