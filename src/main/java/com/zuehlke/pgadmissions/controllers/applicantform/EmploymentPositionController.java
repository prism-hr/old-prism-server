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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;

@RequestMapping("/update")
@Controller
public class EmploymentPositionController {

    @Autowired
    private EmploymentPositionService employmentPositionService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private LanguagePropertyEditor languagePropertyEditor;

    @Autowired
    private DatePropertyEditor datePropertyEditor;

    @Autowired
    private EmploymentPositionValidator employmentPositionValidator;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private DomicileService domicileService;

    @Autowired
    private DomicilePropertyEditor domicilePropertyEditor;

    @Autowired
    private FullTextSearchService searchService;

    @InitBinder("employmentPosition")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(employmentPositionValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(Language.class, languagePropertyEditor);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @RequestMapping(value = "/getEmploymentPosition", method = RequestMethod.GET)
    public String getEmploymentView(@ModelAttribute ApplicationForm applicationForm, @RequestParam(required = false) Integer employmentPositionId,
            ModelMap modelMap) {
        return returnView(modelMap, employmentPositionService.getOrCreate(employmentPositionId));
    }

    @RequestMapping(value = "/editEmploymentPosition", method = RequestMethod.POST)
    public String editEmployment(@Valid EmploymentPosition employmentPosition, BindingResult result,
            @RequestParam(required = false) Integer employmentPositionId, @ModelAttribute ApplicationForm applicationForm, ModelMap modelMap) {
        if (result.hasErrors()) {
            return returnView(modelMap, employmentPosition);
        }
        employmentPositionService.saveOrUpdate(applicationForm, employmentPositionId, employmentPosition);
        return RedirectLocation.UPDATE_APPLICATION_EMPLOYMENT_POSITION + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/deleteEmploymentPosition", method = RequestMethod.POST)
    public String deleteEmployment(@RequestParam("id") Integer employmentPositionId, @ModelAttribute ApplicationForm applicationForm) {
        employmentPositionService.delete(employmentPositionId);
        return RedirectLocation.UPDATE_APPLICATION_EMPLOYMENT_POSITION + applicationForm.getApplicationNumber() + "&message=deleted";
    }

    @RequestMapping(value = "/employment/employer/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForEmploymentPositionEmployerName(@PathVariable final String searchTerm) {
        Gson gson = new Gson();
        return gson.toJson(searchService.getMatchingEmploymentPositionsWithEmployerNamesLike(searchTerm));
    }

    @RequestMapping(value = "/employment/position/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForEmploymentPositionPosition(@PathVariable final String searchTerm) {
        Gson gson = new Gson();
        return gson.toJson(searchService.getMatchingEmploymentPositionsWithPositionsLike(searchTerm));
    }

    @ModelAttribute("languages")
    public List<Language> getAllEnabledLanguages() {
        return languageService.getAllEnabledLanguages();
    }

    @ModelAttribute("domiciles")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileService.getAllEnabledDomiciles();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationFormService.getSecuredApplication(applicationId, ApplicationFormAction.COMPLETE_APPLICATION,
                ApplicationFormAction.CORRECT_APPLICATION);
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

    private String returnView(ModelMap modelMap, EmploymentPosition employmentPosition) {
        modelMap.put("employmentPosition", employmentPosition);
        return TemplateLocation.APPLCIATION_APPLICANT_EMPLOYMENT_POSITION;
    }

}
