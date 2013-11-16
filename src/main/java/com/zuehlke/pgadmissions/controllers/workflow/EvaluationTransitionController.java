package com.zuehlke.pgadmissions.controllers.workflow;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
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

@Controller
@RequestMapping("/progress")
public class EvaluationTransitionController extends StateTransitionController {

    private static final String MY_APPLICATIONS_VIEW = "redirect:/applications";

    public EvaluationTransitionController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public EvaluationTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            CommentFactory commentFactory, EncryptionHelper encryptionHelper, DocumentService documentService, ApprovalService approvalService,
            StateChangeValidator stateChangeValidator, DocumentPropertyEditor documentPropertyEditor, StateTransitionService stateTransitionService,
            ApplicationFormUserRoleService applicationFormUserRoleService, ActionsProvider actionsProvider) {
        super(applicationsService, userService, commentService, commentFactory, encryptionHelper, documentService, approvalService, stateChangeValidator,
                documentPropertyEditor, stateTransitionService, applicationFormUserRoleService, actionsProvider);
    }

    @ModelAttribute("comment")
    public StateChangeComment getComment(@RequestParam String applicationId) {
        StateChangeComment comment = new StateChangeComment();
        comment.setApplication(getApplicationForm(applicationId));
        comment.setUser(getCurrentUser());
        return comment;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/submitEvaluationComment")
    public String defaultGet(@RequestParam String applicationId) {
        applicationFormUserRoleService.deregisterApplicationUpdate(getApplicationForm(applicationId), getCurrentUser());
        return MY_APPLICATIONS_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/submitEvaluationComment")
    public String addComment(@ModelAttribute("applicationForm") ApplicationForm applicationForm,
            @Valid @ModelAttribute("comment") StateChangeComment stateChangeComment, BindingResult result, ModelMap modelMap,
            @RequestParam(required = false) String action, @RequestParam(required = false) Boolean delegate,
            @ModelAttribute("delegatedAdministrator") RegisteredUser delegateAdministrator) {
    	modelMap.put("delegate", delegate);
    	ApplicationFormAction invokedAction;
        RegisteredUser registeredUser = getCurrentUser();

        if (action != null && action.equals("abort")) {
            invokedAction = ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE;
        } else {
            switch (stateChangeComment.getType()) {
            case APPROVAL_EVALUATION:
                invokedAction = ApplicationFormAction.COMPLETE_APPROVAL_STAGE;
                break;
            case REVIEW_EVALUATION:
                invokedAction = ApplicationFormAction.COMPLETE_REVIEW_STAGE;
                break;
            case INTERVIEW_EVALUATION:
                invokedAction = ApplicationFormAction.COMPLETE_INTERVIEW_STAGE;
                break;
            default:
                throw new IllegalStateException("illegal StateChangeComment type passed to evaluation controller");
            }
        }

        actionsProvider.validateAction(applicationForm, registeredUser, invokedAction);

        if (result.hasErrors()) {
            return STATE_TRANSITION_VIEW;
        }

        if (BooleanUtils.isTrue(stateChangeComment.getFastTrackApplication())) {
            applicationsService.fastTrackApplication(applicationForm.getApplicationNumber());
        }
        
        postStateChangeComment(applicationForm, registeredUser, stateChangeComment, delegateAdministrator);

        if (BooleanUtils.isTrue(delegate)) {
        	return "redirect:/applications?messageCode=delegate.success&application=" + applicationForm.getApplicationNumber();
        }

        applicationsService.refresh(applicationForm);
        return stateTransitionService.resolveView(applicationForm);
    }
}