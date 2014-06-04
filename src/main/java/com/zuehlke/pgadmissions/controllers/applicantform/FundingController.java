package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;

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
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.validators.FundingValidator;

@Controller
@RequestMapping("/update")
public class FundingController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private LocalDatePropertyEditor datePropertyEditor;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private FundingValidator fundingValidator;

    @Autowired
    private FundingService fundingService;

    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;

    @InitBinder(value = "funding")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(fundingValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(Application.class, applicationFormPropertyEditor);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
    }

    @RequestMapping(value = "/getFunding", method = RequestMethod.GET)
    public String getFundingView(@RequestParam(value = "fundingId", required = false) Integer fundingId, @ModelAttribute Application applicationForm,
            ModelMap modelMap) {
        return returnView(modelMap, fundingId != null ? fundingService.getById(fundingId) : new Funding());
    }

    @RequestMapping(value = "/editFunding", method = RequestMethod.POST)
    public String editFunding(@RequestParam(value = "fundingId", required = false) Integer fundingId, @Valid Funding funding, BindingResult result,
            ModelMap modelMap, @ModelAttribute Application application) {
        if (result.hasErrors()) {
            return returnView(modelMap, funding);
        }
        fundingService.saveOrUpdate(application.getId(), fundingId, funding);
        return RedirectLocation.UPDATE_APPLICATION_FUNDING + application.getCode();
    }

    @RequestMapping(value = "/deleteFunding", method = RequestMethod.POST)
    public String deleteFunding(@RequestParam("id") Integer fundingId, @ModelAttribute Application applicationForm) {
        fundingService.delete(fundingId);
        return RedirectLocation.UPDATE_APPLICATION_FUNDING + applicationForm.getCode() + "&message=deleted";
    }

    @ModelAttribute("fundingTypes")
    public FundingType[] getFundingTypes() {
        return FundingType.values();
    }

    @ModelAttribute("applicationForm")
    public Application getApplicationForm(String applicationId) {
        // TODO: check actions
        return applicationService.getByApplicationNumber(applicationId);
    }

    private String returnView(ModelMap modelMap, Funding funding) {
        modelMap.put("funding", funding);
        return TemplateLocation.APPLICATION_APPLICANT_FUNDING;
    }

}
