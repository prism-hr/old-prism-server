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
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EntityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SuggestedSupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ProgramDetailsService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.validators.ProgramDetailsValidator;

@RequestMapping("/update")
@Controller
public class ProgramDetailsController {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private LocalDatePropertyEditor datePropertyEditor;

    @Autowired
    private ProgramDetailsValidator programmeDetailsValidator;

    @Autowired
    private ProgramDetailsService programDetailsService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private SuggestedSupervisorJSONPropertyEditor supervisorJSONPropertyEditor;

    @Autowired
    private EntityPropertyEditor<SourcesOfInterest> sourcesOfInterestPropertyEditor;

    @RequestMapping(value = "/getProgrammeDetails", method = RequestMethod.GET)
    public String getProgrammeDetailsView(@ModelAttribute Application applicationForm, ModelMap modelMap) {
        return returnView(modelMap, programDetailsService.getOrCreate(applicationForm));
    }

    @RequestMapping(value = "/editProgrammeDetails", method = RequestMethod.POST)
    public String editProgrammeDetails(@ModelAttribute Application applicationForm, @Valid ProgramDetails programDetails, BindingResult result,
            ModelMap modelMap) {
        if (result.hasErrors()) {
            return returnView(modelMap, programDetails);
        }
        programDetailsService.saveOrUpdate(applicationForm, programDetails);
        return RedirectLocation.UPDATE_APPLICATION_PROGRAM_DETAIL + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/getDefaultStartDate", method = RequestMethod.GET)
    @ResponseBody
    public Date getDefaultStartDate(@ModelAttribute Application applicationForm) {
        return applicationFormService.getDefaultStartDateForApplication(applicationForm);
    }

    @ModelAttribute("studyOptions")
    public List<StudyOption> getStudyOptions(@RequestParam String applicationId) {
        return programService.getAvailableStudyOptions(getApplicationForm(applicationId).getProgram());
    }

    @ModelAttribute("sourcesOfInterests")
    public List<SourcesOfInterest> getSourcesOfInterests() {
        return importedEntityService.getAllSourcesOfInterest();
    }

    @ModelAttribute("applicationForm")
    public Application getApplicationForm(String applicationId) {
        return applicationFormService.getSecuredApplication(applicationId, ApplicationFormAction.APPLICATION_COMPLETE,
                ApplicationFormAction.APPLICATION_CORRECT);
    }

    @InitBinder(value = "programmeDetails")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(programmeDetailsValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(Application.class, applicationFormPropertyEditor);
        binder.registerCustomEditor(SuggestedSupervisor.class, supervisorJSONPropertyEditor);
        binder.registerCustomEditor(SourcesOfInterest.class, sourcesOfInterestPropertyEditor);
    }

    private String returnView(ModelMap modelMap, ProgramDetails programDetails) {
        modelMap.put("programDetails", programDetails);
        return TemplateLocation.APPLICATION_APPLICANT_PROGRAM_DETAIL;
    }

}
