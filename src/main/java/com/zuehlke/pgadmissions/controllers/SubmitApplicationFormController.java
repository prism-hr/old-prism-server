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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = "/submit")
public class SubmitApplicationFormController {
    
    private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "/private/pgStudents/form/main_application_page";
    
    @Autowired
    private ApplicationFormValidator applicationFormValidator;
    
    @Autowired
    private StateService stageService;
    
    @Autowired
    private ApplicationsService applicationsService;
    
    @Autowired
    private ApplicationFormTransferService applicationFormTransferService;
    
    @Autowired
    private EventFactory eventFactory;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ActionsProvider actionsProvider;
    
    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;
    
    @Autowired
    private ProgramsService programsService;

    @RequestMapping(method = RequestMethod.POST)
    public String submitApplication(@Valid ApplicationForm application, BindingResult result, HttpServletRequest request) {
        actionsProvider.validateAction(application, application.getApplicant(), ApplicationFormAction.COMPLETE_APPLICATION);

        if (result.hasErrors()) {
            if (result.getFieldError("program") != null) {
                throw new CannotApplyException();
            }
            return VIEW_APPLICATION_APPLICANT_VIEW_NAME;
        }

        applicationsService.submitApplication(application, request);
        return "redirect:/applications?messageCode=application.submitted&application=" + application.getApplicationNumber();
    }

    @InitBinder("applicationForm")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(applicationFormValidator);
    }
    
    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        return applicationsService.getSecuredApplicationForm(applicationId, ApplicationFormAction.COMPLETE_APPLICATION);
    }
    
    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }
    
}
