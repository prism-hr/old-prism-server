package com.zuehlke.pgadmissions.controllers.workflow.rejection;

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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.RejectReasonPropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
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
    private RejectReasonPropertyEditor rejectReasonPropertyEditor;

    @Autowired
    private UserService userService;
    
    @Autowired
    private RejectionValidator rejectionValidator;

    @Autowired
    private ApplicationFormService applicationsService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private ActionService actionService;


    @RequestMapping(method = RequestMethod.GET)
    public String getRejectPage(ModelMap modelMap) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(application, user, ApplicationFormAction.APPLICATION_CONFIRM_REJECTION);
        applicationFormUserRoleService.deleteApplicationUpdate(application, user);
        return REJECT_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String moveApplicationToReject(@Valid @ModelAttribute("rejection") Rejection rejection, BindingResult errors, ModelMap modelMap) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(application, user, ApplicationFormAction.APPLICATION_CONFIRM_REJECTION);
        
        if (errors.hasErrors()) {
            return REJECT_VIEW_NAME;
        }

        rejectService.moveApplicationToReject(application, rejection);
        rejectService.sendToPortico(application);
        applicationFormUserRoleService.applicationUpdated(application, user);
        return NEXT_VIEW_NAME + "?messageCode=application.rejected&application=" + application.getApplicationNumber();
    }

    @ModelAttribute("availableReasons")
    public List<RejectReason> getAvailableReasons() {
        return rejectService.getAllRejectionReasons();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return application;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        User user = getUser();
        return applicationsService.getApplicationDescriptorForUser(applicationForm, user);
    }

    protected User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("rejection")
    public Rejection getRejection() {
        return new Rejection();
    }

    @InitBinder("rejection")
    public void registerBindersAndValidators(WebDataBinder binder) {
        binder.registerCustomEditor(RejectReason.class, rejectReasonPropertyEditor);
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
