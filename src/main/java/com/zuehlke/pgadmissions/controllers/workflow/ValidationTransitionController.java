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
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
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
public class ValidationTransitionController extends StateTransitionController {

    public ValidationTransitionController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ValidationTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            CommentFactory commentFactory, EncryptionHelper encryptionHelper, DocumentService documentService, ApprovalService approvalService,
            StateChangeValidator stateChangeValidator, DocumentPropertyEditor documentPropertyEditor, StateTransitionService stateTransitionService,
            ApplicationFormUserRoleService applicationFormUserRoleService, ActionsProvider actionsProvider) {
        super(applicationsService, userService, commentService, commentFactory, encryptionHelper, documentService, approvalService, stateChangeValidator,
                documentPropertyEditor, stateTransitionService, applicationFormUserRoleService, actionsProvider);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPage")
    public String getStateTransitionView(@ModelAttribute ApplicationForm applicationForm, @RequestParam(required = false) String action, ModelMap model) {
        RegisteredUser user = getCurrentUser();

        if (action != null && action.equals("abort")) {
        	StateChangeComment latestStateChangeComment = applicationForm.getLatestStateChangeComment();
        	RegisteredUser delegateAdministrator = latestStateChangeComment.getDelegateAdministrator();
            if (user.hasAdminRightsOnApplication(applicationForm) ||
            		(applicationForm.getStatus() == ApplicationFormStatus.APPROVAL &&
            		user.isApproverInProgram(applicationForm.getProgram())) ||
            		user == delegateAdministrator) {
                model.put("comment", latestStateChangeComment);
                if (delegateAdministrator != null) {
                    model.put("delegate", true);
                    model.put("delegatedAdministrator", delegateAdministrator);
                } else {
                    model.put("delegate", false);
                }
            }
        }
        
        applicationFormUserRoleService.deregisterApplicationUpdate(applicationForm, user);
        return stateTransitionService.resolveView(applicationForm, action);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/submitValidationComment")
    public String defaultGet() {
        return "redirect:/applications";
    }

    @ModelAttribute("comment")
    public ValidationComment getComment(@RequestParam String applicationId) {
        ValidationComment validationComment = new ValidationComment();
        validationComment.setApplication(getApplicationForm(applicationId));
        validationComment.setUser(getCurrentUser());
        return validationComment;
    }

    @RequestMapping(value = "/submitValidationComment", method = RequestMethod.POST)
    public String addComment(@ModelAttribute("applicationForm") ApplicationForm applicationForm, 
    		@Valid @ModelAttribute("comment") ValidationComment validationComment, BindingResult result, ModelMap model, 
    		@RequestParam(required = false) String action, @RequestParam(required = false) Boolean delegate, 
    		@ModelAttribute("delegatedAdministrator") RegisteredUser delegateAdministrator) {
        model.put("delegate", delegate);
        
        if (result.hasErrors()) {
            return STATE_TRANSITION_VIEW;
        }
        
        ApplicationFormAction invokedAction;
        
        if (action != null && action.equals("abort")) {
            invokedAction = ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE;
        } else {
            invokedAction = ApplicationFormAction.COMPLETE_VALIDATION_STAGE;
        }
        
        RegisteredUser registeredUser = getCurrentUser();
        actionsProvider.validateAction(applicationForm, registeredUser, invokedAction);
        
        if (BooleanUtils.isTrue(validationComment.getFastTrackApplication())) {
            applicationsService.fastTrackApplication(applicationForm.getApplicationNumber());
        }
        
        postStateChangeComment(applicationForm, registeredUser, validationComment, delegateAdministrator);

        if (BooleanUtils.isTrue(delegate)) {
        	return "redirect:/applications?messageCode=delegate.success&application=" + applicationForm.getApplicationNumber();
        }

        return stateTransitionService.resolveView(applicationForm);
    }

    @ModelAttribute("validationQuestionOptions")
    public ValidationQuestionOptions[] getValidationQuestionOptions() {
        return ValidationQuestionOptions.values();
    }

    @ModelAttribute("homeOrOverseasOptions")
    public HomeOrOverseas[] getHomeOrOverseasOptions() {
        return HomeOrOverseas.values();
    }

}