package com.zuehlke.pgadmissions.controllers.workflow;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
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

@Controller
@RequestMapping("/progress")
public class DelegateTransitionController extends StateTransitionController {

    private static final String MY_APPLICATIONS_VIEW = "redirect:/applications";

    public DelegateTransitionController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public DelegateTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            CommentFactory commentFactory, EncryptionHelper encryptionHelper, DocumentService documentService, ApprovalService approvalService,
            StateChangeValidator stateChangeValidator, DocumentPropertyEditor documentPropertyEditor, StateTransitionService stateTransitionService,
            ApplicationFormUserRoleService applicationFormUserRoleService, ActionsProvider actionsProvider) {
        super(applicationsService, userService, commentService, commentFactory, encryptionHelper, documentService, approvalService, stateChangeValidator,
                documentPropertyEditor, stateTransitionService, applicationFormUserRoleService, actionsProvider);
    }

    @ModelAttribute("comment")
    public InterviewEvaluationComment getComment(@RequestParam String applicationId) {
        InterviewEvaluationComment comment = new InterviewEvaluationComment();
        comment.setApplication(getApplicationForm(applicationId));
        comment.setUser(getCurrentUser());
        return comment;
    }

    @ModelAttribute("applicationForm")
    @Override
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return applicationForm;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/submitDelegateStateChangeComment")
    public String defaultGet(@RequestParam String applicationId) {
        applicationFormUserRoleService.deregisterApplicationUpdate(getApplicationForm(applicationId), getCurrentUser());
        return MY_APPLICATIONS_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/submitDelegateStateChangeComment")
    public String addComment(@RequestParam(required = false) String action,
    		@ModelAttribute("applicationForm") ApplicationForm applicationForm,
            @Valid @ModelAttribute("comment") StateChangeComment stateChangeComment, BindingResult result) {     
        ApplicationFormAction invokedAction = null;
        ApplicationFormStatus status = applicationForm.getStatus();
        
        if (action != null && action.length() > 0 && action.equals("abort")) {
            invokedAction = ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE;
        } else if (status == ApplicationFormStatus.REVIEW) {
        	invokedAction = ApplicationFormAction.COMPLETE_REVIEW_STAGE;
        } else if (status == ApplicationFormStatus.INTERVIEW) {
        	invokedAction = ApplicationFormAction.COMPLETE_INTERVIEW_STAGE;
    	} else if (status == ApplicationFormStatus.APPROVAL) {
			invokedAction = ApplicationFormAction.COMPLETE_APPROVAL_STAGE;        	
        }
        
        actionsProvider.validateAction(applicationForm, getCurrentUser(), invokedAction);
        
        if (result.hasErrors()) {
            return STATE_TRANSITION_VIEW;
        }
        
        RegisteredUser registeredUser = getCurrentUser();
        ApplicationFormStatus nextStatus = applicationForm.getNextStatus();
        applicationForm.setNextStatus(nextStatus);
        if (status == nextStatus) {
        	stateChangeComment.setDelegateAdministrator(registeredUser);
        }

        if (BooleanUtils.isTrue(stateChangeComment.getFastTrackApplication())) {
            applicationsService.fastTrackApplication(applicationForm.getApplicationNumber());
        }

        commentService.save(stateChangeComment);
        applicationsService.save(applicationForm);
        applicationsService.refresh(applicationForm);
        applicationFormUserRoleService.stateChanged(stateChangeComment);
        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, registeredUser, ApplicationUpdateScope.INTERNAL);
        
        if (status == nextStatus) {
            return stateTransitionService.resolveView(applicationForm);
        }
        
        return "redirect:/applications?messageCode=state.change.suggestion&application=" + applicationForm.getApplicationNumber();
    }
}