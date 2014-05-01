package com.zuehlke.pgadmissions.controllers.workflow;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.dto.StateChangeDTO;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

@Controller
@RequestMapping("/progress")
public class StateTransitionController {
    // TODO fix tests
    
    protected static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";

    @Autowired
    protected ApplicationFormService applicationFormService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected CommentService commentService;

    @Autowired
    protected EncryptionHelper encryptionHelper;

    @Autowired
    protected DocumentService documentService;

    @Autowired
    protected ApprovalService approvalService;

    @Autowired
    protected StateChangeValidator stateChangeValidator;

    @Autowired
    protected DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    protected StateTransitionService stateTransitionService;

    @Autowired
    protected WorkflowService applicationFormUserRoleService;

    @Autowired
    protected ActionService actionService;

    @InitBinder(value = "stateChangeDTO")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(stateChangeValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(null, "comment", new StringTrimmerEditor("\r", true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        User user = getCurrentUser();
        return applicationFormService.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("stateChangeDTO")
    public StateChangeDTO getStateChangeDTO(@RequestParam String applicationId, @RequestParam(required = false) String action) {
    	User user = getCurrentUser();
    	ApplicationForm applicationForm = getApplicationForm(applicationId);
    	
    	StateChangeDTO stateChangeDTO = new StateChangeDTO();
    	stateChangeDTO.setAction(action);
    	stateChangeDTO.setUser(user);
    	stateChangeDTO.setApplicationForm(applicationForm);
    	
    	if (applicationForm.getState().getId() == ApplicationFormStatus.APPLICATION_VALIDATION) {
	    	stateChangeDTO.setValidationQuestionOptions(ValidationQuestionOptions.values());
	    	stateChangeDTO.setHomeOrOverseasOptions(HomeOrOverseas.values());
    	}

    	stateChangeDTO.setStati(stateTransitionService.getAssignableNextStati(applicationForm, user));
    	return stateChangeDTO;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPage")
    public String getStateTransitionView(@ModelAttribute StateChangeDTO stateChangeDTO) {
        User user = stateChangeDTO.getUser();
        ApplicationForm applicationForm = stateChangeDTO.getApplicationForm();
        String action = stateChangeDTO.getAction();

        if (action != null) {
            Comment latestStateChangeComment = null;

            if (applicationForm.getState().getId() == ApplicationFormStatus.APPLICATION_VALIDATION) {
                ValidationComment validationComment = commentService.getLastCommentOfType(applicationForm, ValidationComment.class);
                stateChangeDTO.setQualifiedForPhd(validationComment.getQualifiedForPhd());
                stateChangeDTO.setEnglishCompentencyOk(validationComment.getEnglishCompetencyOk());
                stateChangeDTO.setHomeOrOverseas(validationComment.getHomeOrOverseas());
                latestStateChangeComment = validationComment;
            } else {
                latestStateChangeComment = applicationFormService.getLatestStateChangeComment(applicationForm, null);
            }

            if (latestStateChangeComment.getUser() == user) {
                stateChangeDTO.setComment(latestStateChangeComment.getContent());
                stateChangeDTO.setDocuments(latestStateChangeComment.getDocuments());

                User delegateAdministrator = latestStateChangeComment.getDelegateAdministrator();

                if (delegateAdministrator != null) {
                    stateChangeDTO.setDelegate(true);
                    stateChangeDTO.setDelegateFirstName(delegateAdministrator.getFirstName());
                    stateChangeDTO.setDelegateLastName(delegateAdministrator.getLastName());
                    stateChangeDTO.setDelegateEmail(delegateAdministrator.getEmail());
                } else {
                    stateChangeDTO.setDelegate(false);
                }
            } else {
                stateChangeDTO.setQualifiedForPhd(null);
                stateChangeDTO.setEnglishCompentencyOk(null);
                stateChangeDTO.setHomeOrOverseas(null);
            }

        } else {
            stateChangeDTO.setDelegate(false);
        }

        applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, user);
        return stateTransitionService.resolveView(applicationForm, action);
    }

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/submitEvaluationComment")
    public String addComment(@Valid @ModelAttribute StateChangeDTO stateChangeDTO, BindingResult result) {
        ApplicationForm applicationForm = stateChangeDTO.getApplicationForm();
        ApplicationFormAction invokedAction = null;

        // TODO reimplement
//        if (stateChangeDTO.getAction() != null) {
//            invokedAction = ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE;
//        } else {
//        	switch (applicationForm.getState().getId()) {
//		    	case VALIDATION:
//		    		invokedAction = ApplicationFormAction.COMPLETE_VALIDATION_STAGE;
//		    		break;
//		    	case REVIEW:
//		    		invokedAction = ApplicationFormAction.COMPLETE_REVIEW_STAGE;
//		    		break;
//		    	case INTERVIEW:
//		    		invokedAction = ApplicationFormAction.COMPLETE_INTERVIEW_STAGE;
//		    		break;
//		    	case APPROVAL:
//		    		invokedAction = ApplicationFormAction.COMPLETE_APPROVAL_STAGE;
//		    		break;
//		    	default:
//        	}
//        }

        User user = stateChangeDTO.getUser();
        actionService.validateAction(applicationForm, user, invokedAction);

        if (result.hasErrors()) {
            return STATE_TRANSITION_VIEW;
        }

        if (BooleanUtils.isTrue(stateChangeDTO.getFastTrackApplication())) {
            // TODO set into comment
        }

        commentService.postStateChangeComment(stateChangeDTO);

        // FIXME check if has global administration rights (administrator or approver), use PermissionsService, split it into 2 actions
        if (true) {
            if (BooleanUtils.isTrue(stateChangeDTO.getDelegate())) {
                return "redirect:/applications?messageCode=delegate.success&application=" + applicationForm.getApplicationNumber();
            }
        } else if (applicationForm.getState().getId() != stateChangeDTO.getNextStatus()) {
            return "redirect:/applications?messageCode=state.change.suggestion&application=" + applicationForm.getApplicationNumber();
        }

        return stateTransitionService.resolveView(applicationForm);
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationFormService.getByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return applicationForm;
    }
    
}
