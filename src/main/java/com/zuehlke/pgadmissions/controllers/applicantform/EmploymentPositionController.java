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
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EntityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;

@RequestMapping("/update")
@Controller
public class EmploymentPositionController {

    @Autowired
    private EmploymentPositionService employmentPositionService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private EntityPropertyEditor<Language> languagePropertyEditor;

    @Autowired
    private LocalDatePropertyEditor datePropertyEditor;

    @Autowired
    private EmploymentPositionValidator employmentPositionValidator;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private EntityPropertyEditor<Domicile> domicilePropertyEditor;

    @Autowired
    private FullTextSearchService searchService;

    @InitBinder("employmentPosition")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(employmentPositionValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(Language.class, languagePropertyEditor);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(Application.class, applicationFormPropertyEditor);
    }

    @RequestMapping(value = "/getEmploymentPosition", method = RequestMethod.GET)
    public String getEmploymentView(@ModelAttribute Application applicationForm, @RequestParam(required = false) Integer employmentPositionId,
            ModelMap modelMap) {
        return returnView(modelMap, employmentPositionId != null ? employmentPositionService.getById(employmentPositionId) : new ApplicationEmploymentPosition());
    }

    @RequestMapping(value = "/editEmploymentPosition", method = RequestMethod.POST)
    public String editEmployment(@Valid ApplicationEmploymentPosition employmentPosition, BindingResult result,
            @RequestParam(required = false) Integer employmentPositionId, @ModelAttribute Application applicationForm, ModelMap modelMap) {
        if (result.hasErrors()) {
            return returnView(modelMap, employmentPosition);
        }
        employmentPositionService.saveOrUpdate(applicationForm.getId(), employmentPositionId, employmentPosition);
        return RedirectLocation.UPDATE_APPLICATION_EMPLOYMENT_POSITION + applicationForm.getCode();
    }

    @RequestMapping(value = "/deleteEmploymentPosition", method = RequestMethod.POST)
    public String deleteEmployment(@RequestParam("id") Integer employmentPositionId, @ModelAttribute Application applicationForm) {
        employmentPositionService.delete(employmentPositionId);
        return RedirectLocation.UPDATE_APPLICATION_EMPLOYMENT_POSITION + applicationForm.getCode() + "&message=deleted";
    }

    @ModelAttribute("languages")
    public List<Language> getAllEnabledLanguages() {
        return importedEntityService.getAllLanguages();
    }

    @ModelAttribute("domiciles")
    public List<Domicile> getAllEnabledDomiciles() {
        return importedEntityService.getAllDomiciles();
    }

    @ModelAttribute("applicationForm")
    public Application getApplicationForm(String applicationId) {
        // TODO: check actions
        return applicationService.getByApplicationNumber(applicationId);
    }

    private String returnView(ModelMap modelMap, ApplicationEmploymentPosition employmentPosition) {
        modelMap.put("employmentPosition", employmentPosition);
        return TemplateLocation.APPLCIATION_APPLICANT_EMPLOYMENT_POSITION;
    }

}
