package com.zuehlke.pgadmissions.controllers.applicantform;

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
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
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
    private ApplicationFormService applicationsService;

    @Autowired
    private DomicilePropertyEditor domicilePropertyEditor;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private RefereeValidator refereeValidator;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private FullTextSearchService searchService;
    
    @RequestMapping(value = "/getReferee", method = RequestMethod.GET)
    public String getRefereeView(@ModelAttribute ApplicationForm applicationForm, @RequestParam(required = false) Integer refereeId, ModelMap modelMap) {
        return returnView(modelMap, refereeService.getOrCreate(refereeId));
    }

    @RequestMapping(value = "/editReferee", method = RequestMethod.POST)
    public String editReferee(Integer refereeId, @Valid Referee newReferee, BindingResult result, ModelMap modelMap,
            @ModelAttribute ApplicationForm applicationForm) {
        Referee referee = null;
        if (refereeId != null) {
            referee = refereeService.getRefereeById(refereeId);
        }

        if (result.hasErrors()) {
            if (referee != null) {
                newReferee.setId(referee.getId());
            }
            modelMap.addAttribute("referee", newReferee);
            return STUDENTS_FORM_REFEREES_VIEW;
        }

        refereeService.saveOrUpdate(applicationForm, refereeId, newReferee);

        return "redirect:/update/getReferee?applicationId=" + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/deleteReferee", method = RequestMethod.POST)
    public String deleteReferee(@RequestParam("id") Integer refereeId) {
        
        Referee referee = refereeService.getRefereeById(refereeId);
        refereeService.delete(referee);
        return "redirect:/update/getReferee?applicationId=" + referee.getApplication().getApplicationNumber() + "&message=deleted";
    }

    @RequestMapping(value = "/referee/employer/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForRefereeJobEmployer(@PathVariable final String searchTerm) {
        Gson gson = new Gson();
        return gson.toJson(searchService.getMatchingRefereesWithJobEmployersLike(searchTerm));
    }

    @RequestMapping(value = "/referee/position/{searchTerm:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String provideSuggestionsForRefereeJobTitle(@PathVariable final String searchTerm) {
        Gson gson = new Gson();
        return gson.toJson(searchService.getMatchingRefereesWithJobTitlesLike(searchTerm));
    }

    @ModelAttribute("domiciles")
    public List<Domicile> getAllDomiciles() {
        return importedEntityService.getAllDomiciles();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        return applicationsService.getSecuredApplication(applicationId, ApplicationFormAction.APPLICATION_EDIT_AS_CREATOR, ApplicationFormAction.APPLICATION_CORRECT);
    }

    @InitBinder(value = "referee")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(refereeValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
    }

    private String returnView(ModelMap modelMap, Referee referee) {
        modelMap.put("referee", referee);
        return TemplateLocation.APPLICATION_APPLICANT_REFEREE;
    }

}
