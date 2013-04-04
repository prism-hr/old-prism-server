package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.StateTransitionViewResolver;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

public class StateTransitionController {

    protected static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";
    protected final ApplicationsService applicationsService;
    protected final UserService userService;
    protected final CommentService commentService;
    protected final CommentFactory commentFactory;
    protected final StateTransitionViewResolver stateTransitionViewResolver;
    protected final EncryptionHelper encryptionHelper;
    protected final DocumentService documentService;
    protected final ApprovalService approvalService;
    protected final StateChangeValidator stateChangeValidator;
    protected final DocumentPropertyEditor documentPropertyEditor;

    StateTransitionController() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public StateTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            CommentFactory commentFactory, StateTransitionViewResolver stateTransitionViewResolver, EncryptionHelper encryptionHelper,
            DocumentService documentService, ApprovalService approvalService, StateChangeValidator stateChangeValidator,
            DocumentPropertyEditor documentPropertyEditor) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.commentService = commentService;
        this.commentFactory = commentFactory;
        this.stateTransitionViewResolver = stateTransitionViewResolver;
        this.encryptionHelper = encryptionHelper;
        this.documentService = documentService;
        this.approvalService = approvalService;
        this.stateChangeValidator = stateChangeValidator;
        this.documentPropertyEditor = documentPropertyEditor;
    }

    @InitBinder(value = "comment")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(stateChangeValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(null, "comment", new StringTrimmerEditor("\r", true));
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        RegisteredUser currentUser = getCurrentUser();
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.hasAdminRightsOnApplication(applicationForm) && !currentUser.isInRoleInProgram(Authority.APPROVER, applicationForm.getProgram())) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        return applicationForm;
    }
    
    @ModelAttribute("actionsDefinition")
    public ApplicationActionsDefinition getActionsDefinition(@RequestParam String applicationId){
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.getActionsDefinition(getUser(), application);
    }

    RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("stati")
    public ApplicationFormStatus[] getAvailableNextStati(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        return ApplicationFormStatus.getAvailableNextStati(applicationForm.getStatus());
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }

    @ModelAttribute("reviewersWillingToInterview")
    public List<RegisteredUser> getReviewersWillingToInterview(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        if (applicationForm.getStatus() == ApplicationFormStatus.REVIEW) {
            return userService.getReviewersWillingToInterview(applicationForm);
        }
        return null;
    }

}
