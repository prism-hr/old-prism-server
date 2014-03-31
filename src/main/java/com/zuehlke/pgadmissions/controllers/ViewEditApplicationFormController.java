package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = "/application")
public class ViewEditApplicationFormController {

    @Autowired
    private ApplicationFormValidator applicationFormValidator;

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionService actionService;

    @InitBinder("applicationForm")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(applicationFormValidator);
    }

    @RequestMapping(method = RequestMethod.GET, value = "application")
    public String getApplicationView(HttpServletRequest request, @ModelAttribute ApplicationForm applicationForm) {
        RegisteredUser user = userService.getCurrentUser();
        ApplicationFormAction viewEditAction = actionService.getPrecedentAction(applicationForm, user, ActionType.VIEW_EDIT);

        switch (viewEditAction) {
        case COMPLETE_APPLICATION:
        case CORRECT_APPLICATION:
        case EDIT_AS_APPLICANT:
            applicationFormService.openApplicationForEdit(applicationForm, user);
            return TemplateLocation.APPLICATION_APPLICANT_FORM;
        case EDIT_AS_ADMINISTRATOR:
            return RedirectLocation.UPDATE_APPLICATION_AS_STAFF + applicationForm.getApplicationNumber();
        case VIEW_AS_APPLICANT:
        case VIEW_AS_REFEREE:
        case VIEW_AS_RECRUITER:
            return getApplicationView(applicationForm, user, request);
        default:
            throw new ResourceNotFoundException();
        }

    }

    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        List<ApplicationFormAction> viewActions = actionService.getActionIdByActionType(ActionType.VIEW_EDIT);
        return applicationFormService.getSecuredApplication(applicationId, viewActions.toArray(new ApplicationFormAction[viewActions.size()]));
    }

    private String getApplicationView(ApplicationForm application, RegisteredUser user, HttpServletRequest request) {
        applicationFormService.openApplicationForView(application, user);
        if (request != null && request.getParameter("embeddedApplication") != null && request.getParameter("embeddedApplication").equals("true")) {
            return TemplateLocation.APPLICATION_STAFF_EMBEDDED_FORM;
        }
        return TemplateLocation.APPLICATION_STAFF_FORM;
    }

}
