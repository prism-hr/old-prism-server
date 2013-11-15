package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.Date;

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
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
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
            if (user.hasAdminRightsOnApplication(applicationForm)) {
                model.put("comment", applicationForm.getLatestStateChangeComment());
                if (applicationForm.getNextStatus() == ApplicationFormStatus.INTERVIEW) {
                    if (applicationForm.getLatestStateChangeComment().getDelegateAdministrator() != null) {
                        model.put("delegate", true);
                        model.put("delegatedAdministrator", applicationForm.getLatestStateChangeComment().getDelegateAdministrator());
                    }

                    else {
                        model.put("delegate", false);
                    }
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
    public String addComment(@RequestParam String applicationId, @RequestParam(required = false) String action,
            @Valid @ModelAttribute("comment") ValidationComment comment, BindingResult result, ModelMap model,
            @RequestParam(required = false) Boolean delegate, @ModelAttribute("delegatedAdministrator") RegisteredUser delegatedAdministrator) {

        model.put("delegate", delegate);
        ApplicationForm form = getApplicationForm(applicationId);
        RegisteredUser user = getCurrentUser();

        // validate action is still available

        ApplicationFormAction invokedAction;

        if (action != null && action.equals("abort")) {
            invokedAction = ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE;
        }

        else {
            invokedAction = ApplicationFormAction.COMPLETE_VALIDATION_STAGE;
        }

        actionsProvider.validateAction(form, user, invokedAction);

        if (result.hasErrors()) {
            return STATE_TRANSITION_VIEW;
        }

        if (BooleanUtils.isTrue(comment.getFastTrackApplication())) {
            applicationsService.fastTrackApplication(form.getApplicationNumber());
        }

        if (delegatedAdministrator.getEmail() != null) {
            RegisteredUser loadedAdministrator = userService.getUserByEmailIncludingDisabledAccounts(delegatedAdministrator.getEmail());
            comment.setDelegateAdministrator(loadedAdministrator);
        }

        comment.setDate(new Date());
        commentService.save(comment);

        if (comment.getNextStatus() == ApplicationFormStatus.APPROVAL) {
            applicationsService.makeApplicationNotEditable(form);
        }

        applicationsService.save(form);
        applicationFormUserRoleService.stateChanged(comment);
        applicationFormUserRoleService.registerApplicationUpdate(form, user, ApplicationUpdateScope.ALL_USERS);

        if (delegatedAdministrator.getEmail() != null) {
            return "redirect:/applications?messageCode=delegate.success&application=" + form.getApplicationNumber();
        }

        applicationsService.refresh(form);
        return stateTransitionService.resolveView(form);
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