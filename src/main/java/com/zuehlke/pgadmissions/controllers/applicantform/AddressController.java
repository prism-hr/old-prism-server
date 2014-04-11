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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAddressService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.validators.ApplicationFormAddressValidator;

@RequestMapping("/update")
@Controller
public class AddressController {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private ApplicationFormAddressService applicationFormAddressService;

    @Autowired
    private ApplicationFormAddressValidator applicationFormAddressValidator;

    @Autowired
    private DomicileService domicileService;

    @Autowired
    private DomicilePropertyEditor domicilePropertyEditor;

    @RequestMapping(value = "/getAddress", method = RequestMethod.GET)
    public String getAddressView(@ModelAttribute ApplicationForm applicationForm, ModelMap modelMap) {
        return returnView(modelMap, applicationFormAddressService.getOrCreate(applicationForm));
    }

    @RequestMapping(value = "/editAddress", method = RequestMethod.POST)
    public String editAddresses(@Valid ApplicationAddress applicationFormAddress, BindingResult result, @ModelAttribute ApplicationForm applicationForm,
            ModelMap modelMap) {
        if (result.hasErrors()) {
            return returnView(modelMap, applicationFormAddress);
        }
        applicationFormAddressService.saveOrUpdate(applicationForm, applicationFormAddress);
        return RedirectLocation.UPDATE_APPLICATION_ADDRESS + applicationForm.getApplicationNumber();
    }

    @InitBinder(value = "addressSectionDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(applicationFormAddressValidator);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
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

    @ModelAttribute("domiciles")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileService.getAllEnabledDomiciles();
    }

    private String returnView(ModelMap modelMap, ApplicationAddress applicationFormAddress) {
        modelMap.put("applicationFormAddress", applicationFormAddress);
        return TemplateLocation.APPLICATION_APPLICANT_ADDRESS;
    }

}
