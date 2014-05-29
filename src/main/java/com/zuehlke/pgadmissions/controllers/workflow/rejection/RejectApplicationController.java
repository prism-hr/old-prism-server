package com.zuehlke.pgadmissions.controllers.workflow.rejection;

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

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.RejectionReason;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.RejectService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.RejectionValidator;

@Controller
@RequestMapping(value = { "/rejectApplication" })
public class RejectApplicationController {

    private static final String REJECT_VIEW_NAME = "private/staff/approver/reject_page";
    private static final String NEXT_VIEW_NAME = "redirect:/applications";

    @Autowired
    private RejectService rejectService;

    @Autowired
    private UserService userService;

    @Autowired
    private RejectionValidator rejectionValidator;

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private ActionService actionService;

    @RequestMapping(method = RequestMethod.GET)
    public String getRejectPage(ModelMap modelMap) {
        Application application = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(application, user, PrismAction.APPLICATION_CONFIRM_REJECTION);
        applicationFormUserRoleService.deleteApplicationUpdate(application, user);
        return REJECT_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String moveApplicationToReject(@Valid @ModelAttribute("rejection") Comment rejection, BindingResult errors, ModelMap modelMap) {
        Application application = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(application, user, PrismAction.APPLICATION_CONFIRM_REJECTION);

        if (errors.hasErrors()) {
            return REJECT_VIEW_NAME;
        }

        rejectService.moveApplicationToReject(application, rejection);
        rejectService.sendToPortico(application);
        applicationFormUserRoleService.applicationUpdated(application, user);
        return NEXT_VIEW_NAME + "?messageCode=application.rejected&application=" + application.getCode();
    }

    @ModelAttribute("availableReasons")
    public RejectionReason[] getAvailableReasons() {
        return RejectionReason.values();
    }

    @ModelAttribute("applicationForm")
    public Application getApplicationForm(@RequestParam String applicationId) {
        Application application = applicationsService.getByApplicationNumber(applicationId);
        if (application == null) {
            throw new ResourceNotFoundException(applicationId);
        }
        return application;
    }

    @ModelAttribute("applicationDescriptor")
    public Application getApplicationDescriptor(@RequestParam String applicationId) {
        Application applicationForm = getApplicationForm(applicationId);
        User user = getUser();
        return applicationsService.getApplicationDescriptorForUser(applicationForm, user);
    }

    protected User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @InitBinder("rejection")
    public void registerBindersAndValidators(WebDataBinder binder) {
        binder.setValidator(rejectionValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute("user")
    public User getUser() {
        return getCurrentUser();
    }
}
