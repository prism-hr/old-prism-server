package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class StateTransitionController {

    protected static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";

    protected final ApplicationsService applicationsService;

    protected final UserService userService;

    protected final CommentService commentService;

    protected final CommentFactory commentFactory;

    protected final EncryptionHelper encryptionHelper;

    protected final DocumentService documentService;

    protected final ApprovalService approvalService;

    protected final StateChangeValidator stateChangeValidator;

    protected final DocumentPropertyEditor documentPropertyEditor;

    protected final StateTransitionService stateTransitionService;

    protected final ApplicationFormUserRoleService applicationFormUserRoleService;

    protected final ActionsProvider actionsProvider;

    public StateTransitionController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public StateTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            CommentFactory commentFactory, EncryptionHelper encryptionHelper, DocumentService documentService, ApprovalService approvalService,
            StateChangeValidator stateChangeValidator, DocumentPropertyEditor documentPropertyEditor, StateTransitionService stateTransitionService,
            ApplicationFormUserRoleService applicationFormUserRoleService, ActionsProvider actionsProvider) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.commentService = commentService;
        this.commentFactory = commentFactory;
        this.encryptionHelper = encryptionHelper;
        this.documentService = documentService;
        this.approvalService = approvalService;
        this.stateChangeValidator = stateChangeValidator;
        this.documentPropertyEditor = documentPropertyEditor;
        this.stateTransitionService = stateTransitionService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.actionsProvider = actionsProvider;
    }

    @InitBinder(value = "comment")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(stateChangeValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(null, "comment", new StringTrimmerEditor("\r", true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        RegisteredUser currentUser = getCurrentUser();
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.hasAdminRightsOnApplication(applicationForm) && !currentUser.isApplicationAdministrator(applicationForm)
                && !currentUser.isInRoleInProgram(Authority.APPROVER, applicationForm.getProgram())) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        return applicationForm;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("stati")
    public List<ApplicationFormStatus> getAvailableNextStati(@RequestParam String applicationId) {
        final ApplicationForm form = getApplicationForm(applicationId);
        final RegisteredUser currentUser = getCurrentUser();
        List<ApplicationFormStatus> availableNextStatuses = stateTransitionService.getAvailableNextStati(getApplicationForm(applicationId).getStatus());
        CollectionUtils.filter(availableNextStatuses, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                ApplicationFormStatus status = (ApplicationFormStatus) object;
                if (status.equals(ApplicationFormStatus.APPROVED)) {
                    if (currentUser.isNotInRole("SUPERADMINISTRATOR") && currentUser.isNotInRoleInProgram(Authority.APPROVER, form.getProgram())) {
                        return false;
                    }
                }
                if (form.getNextStatus() != null && status.equals(form.getNextStatus())) {
                    if (!status.equals(ApplicationFormStatus.INTERVIEW)) {
                        return false;
                    }
                    if (!currentUser.hasAdminRightsOnApplication(form)) {
                        return false;
                    }
                }
                return true;
            }
        });
        return availableNextStatuses;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }

    @ModelAttribute("delegatedAdministrator")
    public RegisteredUser getDelegatedAdministrator() {
        return new RegisteredUser();
    }

}