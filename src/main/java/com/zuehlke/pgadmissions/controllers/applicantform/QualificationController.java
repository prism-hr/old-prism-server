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

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.dao.QualificationTypeDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.QualificationTypePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

@RequestMapping("/update")
@Controller
public class QualificationController {

    @Autowired
    private QualificationService qualificationService;

    @Autowired
    private ApplicationFormService applicationsService;

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
    private QualificationTypeDAO qualificationTypeDAO;

    @Autowired
    private QualificationTypePropertyEditor qualificationTypePropertyEditor;

    @Autowired
    private QualificationInstitutionDAO qualificationInstitutionDAO;

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
    public String getQualificationView(@ModelAttribute ApplicationForm applicationForm, @RequestParam(required = false) Integer qualificationId,
            ModelMap modelMap) {
        return returnView(modelMap, qualificationService.getOrCreate(qualificationId));
    }

    @RequestMapping(value = "/editQualification", method = RequestMethod.POST)
    public String editQualification(@RequestParam(required = false) Integer qualificationId, @Valid Qualification qualification, BindingResult result,
            ModelMap modelMap, @ModelAttribute ApplicationForm applicationForm) {
        if (result.hasErrors()) {
            return returnView(modelMap, qualification);
        }
        qualificationService.saveOrUpdate(applicationForm, qualificationId, qualification);
        return RedirectLocation.UPDATE_APPLICATION_QUALIFICATION + qualification.getApplication().getApplicationNumber();
    }

    @RequestMapping(value = "/deleteQualification", method = RequestMethod.POST)
    public String deleteQualification(@RequestParam("id") Integer qualificationId, @ModelAttribute ApplicationForm applicationForm) {
        qualificationService.delete(qualificationId);
        return RedirectLocation.UPDATE_APPLICATION_QUALIFICATION + applicationForm.getApplicationNumber() + "&message=deleted";
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
    public List<Institution> getEmptyQualificationInstitution() {
        return Collections.emptyList();
    }

    @ModelAttribute("languages")
    public List<Language> getAllEnabledLanguages() {
        return languageService.getAllEnabledLanguages();
    }

    @ModelAttribute("countries")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileDAO.getAllEnabledDomiciles();
    }

    @ModelAttribute("types")
    public List<QualificationType> getAllEnabledQualificationTypes() {
        return qualificationTypeDAO.getAllEnabledQualificationTypes();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationsService.getSecuredApplication(applicationId, ApplicationFormAction.APPLICATION_COMPLETE_APPLICATION, ApplicationFormAction.APPLICATION_CORRECT_APPLICATION);
    }

    private String returnView(ModelMap modelMap, Qualification qualification) {
        modelMap.put("qualification", qualification);
        Institution institution = qualification.getInstitution();
        if (institution != null) {
            modelMap.put("institutions", qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode(institution.getDomicileCode()));
        }
        return TemplateLocation.APPLICATION_APPLICANT_ADDITIONAL_INFORMATION;
    }

}
