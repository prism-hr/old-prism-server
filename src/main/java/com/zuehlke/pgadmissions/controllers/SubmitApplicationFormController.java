package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.SubmitApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = "/submit")
public class SubmitApplicationFormController {

    private final Logger log = LoggerFactory.getLogger(SubmitApplicationFormController.class);


    @Autowired
    private ApplicationFormValidator applicationFormValidator;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;
    
    @Autowired
    private ProgramService programsService;

    @Autowired
    private SubmitApplicationFormService submitApplicationFormService;

    @RequestMapping(method = RequestMethod.POST)
    public String submitApplication(@Valid Application application, BindingResult result, HttpServletRequest request) {
        User user = userService.getCurrentUser();
        actionService.validateAction(application, user, PrismAction.APPLICATION_COMPLETE);

        if (result.hasErrors()) {
            if (result.getFieldError("program") != null) {
                throw new CannotApplyException();
            }
            return TemplateLocation.APPLICATION_APPLICANT_FORM;
        }
     // TODO: remove class and integrate with workflow engine
//        applicationService.submitApplication(application);
        return "redirect:/applications?messageCode=application.submitted&application=" + application.getCode();
    }

    @InitBinder("applicationForm")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(applicationFormValidator);
    }
    
    @ModelAttribute
    public Application getApplicationForm(@RequestParam String applicationId) {
        return applicationService.getSecuredApplication(applicationId, PrismAction.APPLICATION_COMPLETE);
    }
    
}
