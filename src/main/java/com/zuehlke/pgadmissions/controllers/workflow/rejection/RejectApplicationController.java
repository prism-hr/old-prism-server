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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.RejectReasonPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RejectService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RejectionValidator;

@Controller
@RequestMapping(value = { "/rejectApplication" })
public class RejectApplicationController {

    private static final String REJECT_VIEW_NAME = "private/staff/approver/reject_page";
    private static final String NEXT_VIEW_NAME = "redirect:/applications";

    private final RejectService rejectService;

    private final RejectReasonPropertyEditor rejectReasonPropertyEditor;

    private final UserService userService;

    private final RejectionValidator rejectionValidator;

    private final ApplicationsService applicationsService;

    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    private final ActionsProvider actionsProvider;

    public RejectApplicationController() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public RejectApplicationController(ApplicationsService applicationsService, RejectService rejectService, UserService userService,
            RejectReasonPropertyEditor rejectReasonPropertyEditor, RejectionValidator rejectionValidator, ApplicationFormUserRoleService applicationFormUserRoleService,
            ActionsProvider actionsProvider) {
        this.applicationsService = applicationsService;
        this.rejectService = rejectService;
        this.userService = userService;
        this.rejectReasonPropertyEditor = rejectReasonPropertyEditor;
        this.rejectionValidator = rejectionValidator;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.actionsProvider = actionsProvider;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getRejectPage(ModelMap modelMap) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(application, user, ApplicationFormAction.CONFIRM_REJECTION);
        applicationFormUserRoleService.deleteApplicationUpdate(application, user);
        return REJECT_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String moveApplicationToReject(@Valid @ModelAttribute("rejection") Rejection rejection, BindingResult errors, ModelMap modelMap) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(application, user, ApplicationFormAction.CONFIRM_REJECTION);
        
        if (errors.hasErrors()) {
            return REJECT_VIEW_NAME;
        }

        rejectService.moveApplicationToReject(application, rejection);
        rejectService.sendToPortico(application);
        applicationFormUserRoleService.deleteApplicationActions(application);
        applicationFormUserRoleService.insertApplicationUpdate(application, user, ApplicationUpdateScope.ALL_USERS);
        return NEXT_VIEW_NAME + "?messageCode=application.rejected&application=" + application.getApplicationNumber();
    }

    @ModelAttribute("availableReasons")
    public List<RejectReason> getAvailableReasons() {
        return rejectService.getAllRejectionReasons();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return application;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    protected RegisteredUser getCurrentUser() {
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
    public RegisteredUser getUser() {
        return getCurrentUser();
    }
}
