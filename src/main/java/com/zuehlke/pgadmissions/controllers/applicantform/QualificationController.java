package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.dao.QualificationTypeDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.QualificationTypePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.UserService;
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
	private final DomicilePropertyEditor domicilePropertyEditor;
	private final QualificationValidator qualificationValidator;
	private final DomicileDAO domicileDAO;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final DocumentPropertyEditor documentPropertyEditor;
	private final UserService userService;
	private final EncryptionHelper encryptionHelper;
    private final QualificationTypeDAO qualificationTypeDAO;
    private final QualificationTypePropertyEditor qualificationTypePropertyEditor;
    private final QualificationInstitutionDAO qualificationInstitutionDAO;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;
    private final FullTextSearchService searchService;
    
	public QualificationController() {
		this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

    @Autowired
    public QualificationController(ApplicationsService applicationsService,
            ApplicationFormPropertyEditor applicationFormPropertyEditor, DatePropertyEditor datePropertyEditor,
            DomicileDAO domicileDAO, LanguageService languageService, LanguagePropertyEditor languagePropertyEditor,
            DomicilePropertyEditor domicilePropertyEditor, QualificationValidator qualificationValidator,
            QualificationService qualificationService, DocumentPropertyEditor documentPropertyEditor,
            UserService userService, EncryptionHelper encryptionHelper, QualificationTypeDAO qualificationTypeDAO,
            QualificationTypePropertyEditor qualificationTypePropertyEditor, 
            QualificationInstitutionDAO qualificationInstitutionDAO,
            final ApplicationFormUserRoleService applicationFormUserRoleService,
            final FullTextSearchService searchService) {
		this.applicationService = applicationsService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.domicileDAO = domicileDAO;
		this.languageService = languageService;
		this.languagePropertyEditor = languagePropertyEditor;
		this.domicilePropertyEditor = domicilePropertyEditor;
		this.qualificationValidator = qualificationValidator;
		this.qualificationService = qualificationService;
		this.documentPropertyEditor = documentPropertyEditor;
		this.userService = userService;
		this.encryptionHelper = encryptionHelper;
        this.qualificationTypeDAO = qualificationTypeDAO;
        this.qualificationTypePropertyEditor = qualificationTypePropertyEditor;
        this.qualificationInstitutionDAO = qualificationInstitutionDAO;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.searchService = searchService;
	}
	
	@InitBinder(value="qualification")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(qualificationValidator);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(Language.class, languagePropertyEditor);
		binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(QualificationType.class, qualificationTypePropertyEditor);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);
	}

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }
	
	@RequestMapping(value = "/getQualification", method = RequestMethod.GET)
	public String getQualificationView() {
		if (!userService.getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME;
	}
	
	@RequestMapping(value = "/editQualification", method = RequestMethod.POST)
	public String editQualification(@Valid Qualification qualification, BindingResult result, Model model) {
        if (!userService.getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }

        if (qualification.getApplication().isDecided()) {
            throw new CannotUpdateApplicationException(qualification.getApplication().getApplicationNumber());
        }

        if (result.hasErrors()) {
            if (qualification.getInstitutionCountry() != null) {
                model.addAttribute("institutions", qualificationInstitutionDAO
                        .getEnabledInstitutionsByCountryCode(qualification.getInstitutionCountry().getCode()));
            }
            return APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME;
        }

        ApplicationForm applicationForm = qualification.getApplication();
        
        qualificationService.save(qualification);
        applicationService.save(applicationForm);
        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, userService.getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
		return "redirect:/update/getQualification?applicationId=" + qualification.getApplication().getApplicationNumber();
	}
	
    @RequestMapping(value="/qualification/title/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForQualificationTitle(@PathVariable final String searchTerm) {
    	Gson gson = new Gson();
    	return gson.toJson(searchService.getMatchingQualificationsWithTitlesLike(searchTerm));
    }
    
    @RequestMapping(value="/qualification/subject/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForQualificationSubject(@PathVariable final String searchTerm) {
    	Gson gson = new Gson();
    	return gson.toJson(searchService.getMatchingQualificationsWithSubjectsLike(searchTerm));
    }
    
    @RequestMapping(value="/qualification/grade/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForQualificationGrade(@PathVariable final String searchTerm) {
    	Gson gson = new Gson();
    	return gson.toJson(searchService.getMatchingQualificationsWithGradesLike(searchTerm));
    }

	@ModelAttribute
	public Qualification getQualification(@RequestParam(value="qualificationId", required=false) String encryptedQualificationId, Model model) {
		if (StringUtils.isBlank(encryptedQualificationId)) {
			return new Qualification();
		}
		
		Qualification qualification = qualificationService.getQualificationById(encryptionHelper.decryptToInteger(encryptedQualificationId));
		if (qualification == null) {
			throw new ResourceNotFoundException();
		}
		
		model.addAttribute("institutions", qualificationInstitutionDAO.getEnabledInstitutionsByCountryCode(qualification.getInstitutionCountry().getCode()));
		return qualification;
	}

	@ModelAttribute("institutions")
	public List<QualificationInstitution> getEmptyQualificationInstitution() {
	    return Collections.emptyList();
	}
	
	@ModelAttribute("languages")
	public List<Language> getAllEnabledLanguages() {
		return languageService.getAllEnabledLanguages();
	}

	@ModelAttribute("countries")
	public List<Domicile> getAllEnabledDomiciles() {
		return domicileDAO.getAllEnabledDomicilesExceptAlternateValues();
	}
	
	@ModelAttribute("types")
	public List<QualificationType> getAllEnabledQualificationTypes() {
	    return qualificationTypeDAO.getAllEnabledQualificationTypes();
	}
	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {		
		ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
		if(application == null || !userService.getCurrentUser().canSee(application)){
			throw new ResourceNotFoundException();
		}
		return application;
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required=false)String message) {		
		return message;
	}
	
}