package com.zuehlke.pgadmissions.controllers.applicantform;

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

import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EntityPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

@RequestMapping("/update")
@Controller
public class RefereeController {

    private static final String STUDENTS_FORM_REFEREES_VIEW = "/private/pgStudents/form/components/references_details";

    @Autowired
    private RefereeService refereeService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private EntityPropertyEditor<Domicile> domicilePropertyEditor;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private RefereeValidator refereeValidator;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private FullTextSearchService searchService;
    
    @RequestMapping(value = "/getReferee", method = RequestMethod.GET)
    public String getRefereeView(@ModelAttribute Application applicationForm, @RequestParam(required = false) Integer refereeId, ModelMap modelMap) {
        return returnView(modelMap, refereeId != null ? refereeService.getById(refereeId) : new ApplicationReferee());
    }

    @RequestMapping(value = "/editReferee", method = RequestMethod.POST)
    public String editReferee(Integer refereeId, @Valid ApplicationReferee newReferee, BindingResult result, ModelMap modelMap,
            @ModelAttribute Application applicationForm) {
        ApplicationReferee referee = null;
        if (refereeId != null) {
            referee = refereeService.getById(refereeId);
        }

        if (result.hasErrors()) {
            if (referee != null) {
                newReferee.setId(referee.getId());
            }
            modelMap.addAttribute("referee", newReferee);
            return STUDENTS_FORM_REFEREES_VIEW;
        }

        refereeService.saveOrUpdate(applicationForm.getId(), refereeId, newReferee);

        return "redirect:/update/getReferee?applicationId=" + applicationForm.getCode();
    }

    @RequestMapping(value = "/deleteReferee", method = RequestMethod.POST)
    public String deleteReferee(@RequestParam("id") Integer refereeId) {
        ApplicationReferee referee = refereeService.getById(refereeId);
        refereeService.delete(refereeId);
        return "redirect:/update/getReferee?applicationId=" + referee.getApplication().getCode() + "&message=deleted";
    }

    @ModelAttribute("applicationForm")
    public Application getApplicationForm(String applicationId) {
        // TODO: check actions
        return applicationService.getByApplicationNumber(applicationId);
    }

    @InitBinder(value = "referee")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(refereeValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(Application.class, applicationFormPropertyEditor);
    }

    private String returnView(ModelMap modelMap, ApplicationReferee referee) {
        modelMap.put("referee", referee);
        return TemplateLocation.APPLICATION_APPLICANT_REFEREE;
    }

}
