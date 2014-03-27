package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = "/submit")
public class SubmitApplicationFormController {
    
    @Autowired
    private ApplicationFormValidator applicationFormValidator;
    
    @Autowired
    private ApplicationFormService applicationFormService;
    
    @Autowired
    private ActionsProvider actionsProvider;

    @RequestMapping(method = RequestMethod.POST)
    public String submitApplication(@Valid ApplicationForm application, BindingResult result, HttpServletRequest request) {
        actionsProvider.validateAction(application, application.getApplicant(), ApplicationFormAction.COMPLETE_APPLICATION);
        if (result.hasErrors()) {
            if (result.getFieldError("program") != null) {
                throw new CannotApplyException();
            }
            return TemplateLocation.APPLICATION_APPLICANT_FORM;
        }
        applicationFormService.submitApplication(application, request);
        return "redirect:/applications?messageCode=application.submitted&application=" + application.getApplicationNumber();
    }

    @InitBinder("applicationForm")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(applicationFormValidator);
    }
    
    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        return applicationFormService.getSecuredApplication(applicationId, ApplicationFormAction.COMPLETE_APPLICATION);
    }
    
}
