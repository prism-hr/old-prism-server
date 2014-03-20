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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping(value = { "/application" })
public class ViewEditApplicationFormController {

    private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "/private/pgStudents/form/main_application_page";
    private static final String VIEW_APPLICATION_STAFF_VIEW_NAME = "/private/staff/application/main_application_page";
    private static final String VIEW_APPLICATION_INTERNAL_PLAIN_VIEW_NAME = "/private/staff/application/main_application_page_without_headers";

    @Autowired
    private ApplicationFormValidator applicationFormValidator;

    @Autowired
    private StateService stateService;

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

    @Autowired
    private ActionService actionService;

    @InitBinder("applicationForm")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(applicationFormValidator);
    }

    @RequestMapping(method = RequestMethod.GET, value = "application")
    public String getApplicationView(HttpServletRequest request, @ModelAttribute ApplicationForm applicationForm) {
        RegisteredUser user = getUser();
        ApplicationFormAction viewEditAction = actionsProvider.getPrecedentAction(applicationForm, user, ActionType.VIEW_EDIT);
        applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, user);

        switch (viewEditAction) {
        case COMPLETE_APPLICATION:
        case CORRECT_APPLICATION:
        case EDIT_AS_APPLICANT:
            programsService.getValidProgramProjectAdvert(applicationForm.getProgram().getCode(), applicationForm.getAdvert().getId());
            return VIEW_APPLICATION_APPLICANT_VIEW_NAME;
        case EDIT_AS_ADMINISTRATOR:
            return "redirect:/editApplicationFormAsProgrammeAdmin?applicationId=" + applicationForm.getApplicationNumber();
        case VIEW:
            return getApplicationView(request);
        default:
            throw new ResourceNotFoundException();
        }

    }

    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        List<ApplicationFormAction> viewActions = actionService.getActionIdByActionType(ActionType.VIEW_EDIT);
        return applicationsService.getSecuredApplicationForm(applicationId, viewActions.toArray(new ApplicationFormAction[viewActions.size()]));
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    private String getApplicationView(HttpServletRequest request) {
        if (request != null && request.getParameter("embeddedApplication") != null && request.getParameter("embeddedApplication").equals("true")) {
            return VIEW_APPLICATION_INTERNAL_PLAIN_VIEW_NAME;
        }
        return VIEW_APPLICATION_STAFF_VIEW_NAME;
    }

}
