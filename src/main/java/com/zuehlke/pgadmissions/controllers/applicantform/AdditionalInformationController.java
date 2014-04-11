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

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.BooleanPropertyEditor;
import com.zuehlke.pgadmissions.services.AdditionalInformationService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.validators.AdditionalInformationValidator;

@Controller
@RequestMapping("/update")
public class AdditionalInformationController {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private AdditionalInformationService additionalInformationService;

    @Autowired
    private AdditionalInformationValidator additionalInformationValidator;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private BooleanPropertyEditor booleanPropertyEditor;
    
    @RequestMapping(value = "/getAdditionalInformation", method = RequestMethod.GET)
    public String getAdditionalInformationView(@ModelAttribute ApplicationForm applicationForm, ModelMap modelMap) {
        return returnView(modelMap, additionalInformationService.getOrCreate(applicationForm));
    }

    @RequestMapping(value = "/editAdditionalInformation", method = RequestMethod.POST)
    public String editAdditionalInformation(@Valid AdditionalInformation additionalInformation, BindingResult result,
            @ModelAttribute ApplicationForm applicationForm, ModelMap modelMap) {
        if (result.hasErrors()) {
            return returnView(modelMap, additionalInformation);
        }
        additionalInformationService.saveOrUpdate(applicationForm, additionalInformation);
        return RedirectLocation.UPDATE_APPLICATION_ADDITIONAL_INFORMATION + applicationForm.getApplicationNumber();
    }

    @InitBinder(value = "additionalInformation")
    public void registerValidatorsEditors(WebDataBinder binder) {
        binder.setValidator(additionalInformationValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binder.registerCustomEditor(Boolean.class, booleanPropertyEditor);
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationFormService.getSecuredApplication(applicationId, ApplicationFormAction.APPLICATION_COMPLETE_APPLICATION,
                ApplicationFormAction.APPLICATION_CORRECT_APPLICATION);
    }

    private String returnView(ModelMap modelMap, AdditionalInformation additionalInformation) {
        modelMap.put("additionalInformation", additionalInformation);
        return TemplateLocation.APPLICATION_APPLICANT_ADDITIONAL_INFORMATION;
    }
    
}
