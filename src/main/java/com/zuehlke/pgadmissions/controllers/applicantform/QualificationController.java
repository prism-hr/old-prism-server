package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Collections;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Preconditions;
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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
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

    @Autowired
    private QualificationService qualificationService;

    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private DatePropertyEditor datePropertyEditor;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private LanguagePropertyEditor languagePropertyEditor;

    @Autowired
    private DomicilePropertyEditor domicilePropertyEditor;

    @Autowired
    private QualificationValidator qualificationValidator;

    @Autowired
    private DomicileDAO domicileDAO;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    private UserService userService;

    @Autowired
    private QualificationTypeDAO qualificationTypeDAO;

    @Autowired
    private QualificationTypePropertyEditor qualificationTypePropertyEditor;

    @Autowired
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @Autowired
    private FullTextSearchService searchService;

    @InitBinder(value = "qualification")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(qualificationValidator);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(Language.class, languagePropertyEditor);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(QualificationType.class, qualificationTypePropertyEditor);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(true);
    }

    @RequestMapping(value = "/getQualification", method = RequestMethod.GET)
    public String getQualificationView(@ModelAttribute ApplicationForm applicationForm, @RequestParam(required = false) Integer qualificationId, ModelMap modelMap) {
        RegisteredUser currentUser = userService.getCurrentUser();
        Qualification qualification;
        if (qualificationId != null) {
            qualification = getQualification(applicationForm, qualificationId, currentUser);
        } else {
            qualification = new Qualification();
        }
        modelMap.put("qualification", qualification);
        if (qualification.getInstitutionCountry() != null) {
            modelMap.put("institutions", qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode(qualification.getInstitutionCountry().getCode()));
        }
        return APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME;
    }

    @RequestMapping(value = "/editQualification", method = RequestMethod.POST)
    public String editQualification(@RequestParam(required = false) Integer qualificationId, @Valid Qualification qualification, BindingResult result,
            ModelMap modelMap, @ModelAttribute ApplicationForm applicationForm) {
        RegisteredUser currentUser = userService.getCurrentUser();
        if (qualificationId != null) {
            getQualification(applicationForm, qualificationId, currentUser);
        }

        if (result.hasErrors()) {
            qualification.setId(qualificationId);
            modelMap.put("qualification", qualification);
            if (qualification.getInstitutionCountry() != null) {
                modelMap.put("institutions", qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode(qualification.getInstitutionCountry().getCode()));
            }
            return APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME;
        }

        qualificationService.save(applicationForm, qualificationId, qualification);
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, currentUser, ApplicationUpdateScope.ALL_USERS);
        return "redirect:/update/getQualification?applicationId=" + qualification.getApplication().getApplicationNumber();
    }

    private Qualification getQualification(ApplicationForm application, Integer qualificationId, RegisteredUser currentUser) {
        Qualification qualification = qualificationService.getQualificationById(qualificationId);
        // check if given qualification belongs to given application
        Preconditions.checkNotNull(qualification);
        Preconditions.checkArgument(application.getId().equals(qualification.getApplication().getId()));
        return qualification;
    }

    @RequestMapping(value = "/qualification/title/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForQualificationTitle(@PathVariable final String searchTerm) {
        Gson gson = new Gson();
        return gson.toJson(searchService.getMatchingQualificationsWithTitlesLike(searchTerm));
    }

    @RequestMapping(value = "/qualification/subject/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForQualificationSubject(@PathVariable final String searchTerm) {
        Gson gson = new Gson();
        return gson.toJson(searchService.getMatchingQualificationsWithSubjectsLike(searchTerm));
    }

    @RequestMapping(value = "/qualification/grade/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForQualificationGrade(@PathVariable final String searchTerm) {
        Gson gson = new Gson();
        return gson.toJson(searchService.getMatchingQualificationsWithGradesLike(searchTerm));
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
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationsService.getEditableApplicationForm(applicationId);
    }

}
