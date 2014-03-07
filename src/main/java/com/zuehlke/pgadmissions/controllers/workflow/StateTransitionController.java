package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CompleteApprovalComment;
import com.zuehlke.pgadmissions.domain.CompleteInterviewComment;
import com.zuehlke.pgadmissions.domain.CompleteReviewComment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.dto.StateChangeDTO;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
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
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

@Controller
@RequestMapping("/progress")
public class StateTransitionController {
    // TODO fix tests
    
    protected static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";

    @Autowired
    protected ApplicationsService applicationsService;

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
    protected ApplicationFormUserRoleService applicationFormUserRoleService;

    @Autowired
    protected ActionsProvider actionsProvider;

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
        RegisteredUser user = getCurrentUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("stateChangeDTO")
    public StateChangeDTO getStateChangeDTO(@RequestParam String applicationId, @RequestParam(required = false) String action) {
        RegisteredUser registeredUser = getCurrentUser();
        ApplicationForm applicationForm = getApplicationForm(applicationId);

        StateChangeDTO stateChangeDTO = new StateChangeDTO();
        stateChangeDTO.setAction(action);
        stateChangeDTO.setRegisteredUser(registeredUser);
        stateChangeDTO.setApplicationForm(applicationForm);

        if (applicationForm.getStatus() == ApplicationFormStatus.VALIDATION) {
            stateChangeDTO.setValidationQuestionOptions(ValidationQuestionOptions.values());
            stateChangeDTO.setHomeOrOverseasOptions(HomeOrOverseas.values());
        }

        stateChangeDTO.setStati(getAvailableNextStati(applicationForm, registeredUser));
        return stateChangeDTO;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPage")
    public String getStateTransitionView(@ModelAttribute("stateChangeDTO") StateChangeDTO stateChangeDTO) {
        RegisteredUser registeredUser = stateChangeDTO.getRegisteredUser();
        ApplicationForm applicationForm = stateChangeDTO.getApplicationForm();
        String action = stateChangeDTO.getAction();

        if (action != null) {
            Comment latestStateChangeComment = null;

            if (applicationForm.getStatus() == ApplicationFormStatus.VALIDATION) {
                ValidationComment validationComment = applicationForm.getValidationComment();
                stateChangeDTO.setQualifiedForPhd(validationComment.getQualifiedForPhd());
                stateChangeDTO.setEnglishCompentencyOk(validationComment.getEnglishCompetencyOk());
                stateChangeDTO.setHomeOrOverseas(validationComment.getHomeOrOverseas());
                latestStateChangeComment = validationComment;
            } else {
                latestStateChangeComment = applicationsService.getLatestStateChangeComment(applicationForm, null);
            }

            if (latestStateChangeComment.getUser() == registeredUser) {
                stateChangeDTO.setComment(latestStateChangeComment.getContent());
                stateChangeDTO.setDocuments(latestStateChangeComment.getDocuments());

                RegisteredUser delegateAdministrator = latestStateChangeComment.getDelegateAdministrator();

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

        applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, registeredUser);
        return stateTransitionService.resolveView(applicationForm, action);
    }

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/submitEvaluationComment")
    public String addComment(@Valid @ModelAttribute("stateChangeDTO") StateChangeDTO stateChangeDTO, BindingResult result) {
        ApplicationForm applicationForm = stateChangeDTO.getApplicationForm();
        ApplicationFormAction invokedAction = null;

        if (stateChangeDTO.getAction() != null) {
            invokedAction = ApplicationFormAction.MOVE_TO_DIFFERENT_STAGE;
        } else {
            switch (applicationForm.getStatus()) {
            case VALIDATION:
                invokedAction = ApplicationFormAction.COMPLETE_VALIDATION_STAGE;
                break;
            case REVIEW:
                invokedAction = ApplicationFormAction.COMPLETE_REVIEW_STAGE;
                break;
            case INTERVIEW:
                invokedAction = ApplicationFormAction.COMPLETE_INTERVIEW_STAGE;
                break;
            case APPROVAL:
                invokedAction = ApplicationFormAction.COMPLETE_APPROVAL_STAGE;
                break;
            default:
            }
        }

        RegisteredUser registeredUser = stateChangeDTO.getRegisteredUser();
        actionsProvider.validateAction(applicationForm, registeredUser, invokedAction);

        if (result.hasErrors()) {
            return STATE_TRANSITION_VIEW;
        }

        if (BooleanUtils.isTrue(stateChangeDTO.getFastTrackApplication())) {
            applicationsService.fastTrackApplication(applicationForm.getApplicationNumber());
        }

        postStateChangeComment(stateChangeDTO);

        if (BooleanUtils.isTrue(stateChangeDTO.hasGlobalAdministrationRights())) {
            if (BooleanUtils.isTrue(stateChangeDTO.getDelegate())) {
                return "redirect:/applications?messageCode=delegate.success&application=" + applicationForm.getApplicationNumber();
            }
        } else if (applicationForm.getStatus() != stateChangeDTO.getNextStatus()) {
            return "redirect:/applications?messageCode=state.change.suggestion&application=" + applicationForm.getApplicationNumber();
        }

        return stateTransitionService.resolveView(applicationForm);
    }

    public RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return applicationForm;
    }

    public List<ApplicationFormStatus> getAvailableNextStati(final ApplicationForm applicationForm, final RegisteredUser registeredUser) {
        List<ApplicationFormStatus> availableNextStatuses = stateTransitionService.getAvailableNextStati(applicationForm.getStatus());
        CollectionUtils.filter(availableNextStatuses, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                ApplicationFormStatus status = (ApplicationFormStatus) object;

                if (status.equals(ApplicationFormStatus.APPROVED)) {
                    if (registeredUser.isNotInRole(Authority.SUPERADMINISTRATOR)
                            && registeredUser.isNotInRoleInProgram(Authority.APPROVER, applicationForm.getProgram())) {
                        return false;
                    }
                }
                if (status.equals(applicationForm.getNextStatus())) {
                    return false;
                }

                return true;
            }
        });
        return availableNextStatuses;
    }

    private void postStateChangeComment(StateChangeDTO stateChangeDTO) {
        ApplicationForm applicationForm = stateChangeDTO.getApplicationForm();
        RegisteredUser registeredUser = stateChangeDTO.getRegisteredUser();
        ApplicationFormStatus status = applicationForm.getStatus();
        Comment stateChangeComment = null;

        switch (status) {
        case VALIDATION:
            ValidationComment validationComment = new ValidationComment();
            validationComment.setQualifiedForPhd(stateChangeDTO.getQualifiedForPhd());
            validationComment.setEnglishCompetencyOk(stateChangeDTO.getEnglishCompentencyOk());
            validationComment.setHomeOrOverseas(stateChangeDTO.getHomeOrOverseas());
            stateChangeComment = validationComment;
            boolean useCustomReferenceQuestions = BooleanUtils.isTrue(stateChangeDTO.getUseCustomReferenceQuestions());
            stateChangeComment.setUseCustomReferenceQuestions(useCustomReferenceQuestions);
            setUseCustomQuestions(stateChangeComment, stateChangeDTO);
            break;
        case REVIEW:
            stateChangeComment = new CompleteReviewComment();
            setUseCustomQuestions(stateChangeComment, stateChangeDTO);
            break;
        case INTERVIEW:
            stateChangeComment = new CompleteInterviewComment();
            setUseCustomQuestions(stateChangeComment, stateChangeDTO);
            break;
        case APPROVAL:
            stateChangeComment = new CompleteApprovalComment();
            setUseCustomQuestions(stateChangeComment, stateChangeDTO);
            break;
        default:
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }

        stateChangeComment.setApplication(applicationForm);
        stateChangeComment.setUser(registeredUser);
        stateChangeComment.setContent(stateChangeDTO.getComment());
        stateChangeComment.getDocuments().addAll(stateChangeDTO.getDocuments());

        ApplicationFormStatus nextStatus = stateChangeDTO.getNextStatus();
        stateChangeComment.setNextStatus(nextStatus);
		stateChangeComment.setDelegateAdministrator(null);

        if (BooleanUtils.isTrue(stateChangeDTO.hasGlobalAdministrationRights())) {
            if (BooleanUtils.isTrue(stateChangeDTO.getDelegate())) {
                String delegateAdministratorEmail = stateChangeDTO.getDelegateEmail();
                RegisteredUser userToSaveAsDelegate = userService.getUserByEmailIncludingDisabledAccounts(delegateAdministratorEmail);

                if (userToSaveAsDelegate == null) {
                    userToSaveAsDelegate = userService.createNewUserInRole(stateChangeDTO.getDelegateFirstName(), stateChangeDTO.getDelegateLastName(),
                            delegateAdministratorEmail, Authority.STATEADMINISTRATOR);
                }

                stateChangeComment.setDelegateAdministrator(userToSaveAsDelegate);
            }
        } else {
            if (status == nextStatus) {
                stateChangeComment.setDelegateAdministrator(registeredUser);
            }
        }

        applicationForm.setNextStatus(nextStatus);
        commentService.save(stateChangeComment);
        applicationsService.save(applicationForm);
        applicationsService.refresh(applicationForm);
        applicationFormUserRoleService.stateChanged(stateChangeComment);
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, registeredUser, ApplicationUpdateScope.ALL_USERS);	
    }

    private void setUseCustomQuestions(Comment comment, StateChangeDTO dto) {
        comment.setUseCustomQuestions(BooleanUtils.isTrue(dto.getUseCustomQuestions()));
    }

}