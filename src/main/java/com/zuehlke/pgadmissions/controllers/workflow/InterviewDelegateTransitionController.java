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
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

@Controller
@RequestMapping("/progress")
public class InterviewDelegateTransitionController extends StateTransitionController {

    private static final String MY_APPLICATIONS_VIEW = "redirect:/applications";
    
    private InterviewService interviewService;

    public InterviewDelegateTransitionController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public InterviewDelegateTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            CommentFactory commentFactory, EncryptionHelper encryptionHelper, DocumentService documentService, ApprovalService approvalService,
            StateChangeValidator stateChangeValidator, DocumentPropertyEditor documentPropertyEditor, StateTransitionService stateTransitionService,
            ApplicationFormAccessService accessService, ActionsProvider actionsProvider, InterviewService interviewService) {
        super(applicationsService, userService, commentService, commentFactory, encryptionHelper, documentService, approvalService, stateChangeValidator,
                documentPropertyEditor, stateTransitionService, accessService, actionsProvider);
        this.interviewService = interviewService;
    }

    @ModelAttribute("comment")
    public InterviewEvaluationComment getComment(@RequestParam String applicationId) {
        return new InterviewEvaluationComment();
    }

    @ModelAttribute("applicationForm")
    @Override
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        RegisteredUser currentUser = getCurrentUser();
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.isApplicationAdministrator(applicationForm)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        return applicationForm;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/submitInterviewEvaluationComment")
    public String defaultGet() {
        return MY_APPLICATIONS_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/submitInterviewEvaluationComment")
    public String addComment(@RequestParam String applicationId, @Valid @ModelAttribute("comment") StateChangeComment stateChangeComment, BindingResult result,
            @RequestParam(required = false) Boolean fastTrackApplication, ModelMap modelMap) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        
        if (result.hasErrors() || (fastTrackApplication == null && applicationForm.getBatchDeadline() != null)) {
            if (fastTrackApplication == null) {
                modelMap.addAttribute("fastTrackMissing", true);
            }
            return STATE_TRANSITION_VIEW;
        }

        RegisteredUser user = getCurrentUser();
        Interview interview = applicationForm.getLatestInterview();

        Comment comment;
        if (stateChangeComment.getNextStatus() == ApplicationFormStatus.INTERVIEW) {
            // delegate should be able to restart interview 
            comment = commentFactory.createComment(applicationForm, user, stateChangeComment.getComment(), stateChangeComment.getDocuments(),
                    stateChangeComment.getType(), stateChangeComment.getNextStatus());
        } else {
            // moving to other state than interview, simply make suggestion
            interview.setStage(InterviewStage.INACTIVE);
            interviewService.save(interview);
            
            comment = commentFactory.createStateChangeSuggestionComment(user, applicationForm, stateChangeComment.getComment(),
                    stateChangeComment.getNextStatus());
            applicationForm.setApplicationAdministrator(null);
            applicationForm.setDueDate(new Date());
        }
        
        if (BooleanUtils.isTrue(fastTrackApplication)) {
            applicationsService.fastTrackApplication(applicationForm.getApplicationNumber());
        }

        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.INTERNAL, new Date()));
        accessService.updateAccessTimestamp(applicationForm, getCurrentUser(), new Date());
        applicationsService.save(applicationForm);
        commentService.save(comment);

        applicationsService.refresh(applicationForm);
        if (stateChangeComment.getNextStatus() == ApplicationFormStatus.INTERVIEW) {
            return stateTransitionService.resolveView(applicationForm);
        }
        return "redirect:/applications?messageCode=state.change.suggestion&application=" + applicationForm.getApplicationNumber();
    }
}
