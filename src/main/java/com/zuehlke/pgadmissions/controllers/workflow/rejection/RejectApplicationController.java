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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.exceptions.application.CannotTerminateApplicationException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.RejectReasonPropertyEditor;
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

    public RejectApplicationController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public RejectApplicationController(ApplicationsService applicationsService, RejectService rejectService, UserService userService,
            RejectReasonPropertyEditor rejectReasonPropertyEditor, RejectionValidator rejectionValidator) {
        this.applicationsService = applicationsService;
        this.rejectService = rejectService;
        this.userService = userService;
        this.rejectReasonPropertyEditor = rejectReasonPropertyEditor;
        this.rejectionValidator = rejectionValidator;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getRejectPage() {
        return REJECT_VIEW_NAME;
    }
    
    @RequestMapping(value = "/moveApplicationToReject", method = RequestMethod.GET)
    public void defaultGet() {
        return;
    }

    @RequestMapping(value = "/moveApplicationToReject", method = RequestMethod.POST)
    public String moveApplicationToReject(@Valid @ModelAttribute("rejection") Rejection rejection, BindingResult errors,
            @ModelAttribute("applicationForm") ApplicationForm application, ModelMap modelMap) {
        checkPermissionForApplication(application);
        checkApplicationStatus(application);
        if (errors.hasErrors()) {
            return REJECT_VIEW_NAME;
        }
        rejectService.moveApplicationToReject(application, getCurrentUser(), rejection);
        rejectService.sendToPortico(application);
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
        checkPermissionForApplication(application);
        checkApplicationStatus(application);
        return application;
    }
    
    @ModelAttribute("actionsDefinition")
    public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId){
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.getActionsDefinition(getUser(), application);
    }

    private void checkApplicationStatus(ApplicationForm application) {
        if (!application.isSubmitted() || application.isDecided() || application.isWithdrawn()) {
            throw new CannotTerminateApplicationException(application.getApplicationNumber());
        }
    }

    private void checkPermissionForApplication(ApplicationForm application) {
        RegisteredUser currentUser = getCurrentUser();
        if (!(application.getProgram().isApprover(currentUser) || currentUser.hasAdminRightsOnApplication(application))) {
            throw new InsufficientApplicationFormPrivilegesException(application.getApplicationNumber());
        }
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
