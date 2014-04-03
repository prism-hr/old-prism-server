package com.zuehlke.pgadmissions.controllers;

import java.net.UnknownHostException;

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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.SubmitApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = "/submit")
public class SubmitApplicationFormController {

    private final Logger log = LoggerFactory.getLogger(SubmitApplicationFormController.class);


    @Autowired
    private ApplicationFormValidator applicationFormValidator;

    @Autowired
    private ApplicationsService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;
    @Autowired
    private ProgramsService programsService;

    @Autowired
    private SubmitApplicationFormService submitApplicationFormService;

    @RequestMapping(method = RequestMethod.POST)
    public String submitApplication(@Valid ApplicationForm applicationForm, BindingResult result, HttpServletRequest request) {
        RegisteredUser user = userService.getCurrentUser();
        actionService.validateAction(applicationForm, user, ApplicationFormAction.COMPLETE_APPLICATION);

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
