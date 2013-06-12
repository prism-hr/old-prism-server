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
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
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
            ApplicationFormAccessService accessService, ActionsProvider actionsProvider) {
        super(applicationsService, userService, commentService, commentFactory, encryptionHelper, documentService, approvalService, stateChangeValidator,
                documentPropertyEditor, stateTransitionService, accessService, actionsProvider);
    }

    @ModelAttribute("comment")
    public StateChangeComment getComment(@RequestParam String applicationId) {
        return new StateChangeComment();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/submitEvaluationComment")
    public String defaultGet() {
        return MY_APPLICATIONS_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/submitEvaluationComment")
    public String addComment(@ModelAttribute("applicationForm") ApplicationForm applicationForm, @Valid @ModelAttribute("comment") StateChangeComment stateChangeComment, BindingResult result,
            ModelMap modelMap, @RequestParam(required = false) Boolean delegate, @ModelAttribute("delegatedInterviewer") RegisteredUser delegatedInterviewer,
            @RequestParam(required = false) Boolean fastTrackApplication) {
        modelMap.put("delegate", delegate);
        
        boolean stateChangeRequiresFastTrack = !(ApplicationFormStatus.APPROVED.equals(stateChangeComment.getNextStatus())||ApplicationFormStatus.REJECTED.equals(stateChangeComment.getNextStatus()));
        boolean fastrackValueMissing = fastTrackApplication == null && applicationForm.getBatchDeadline() != null;
		if (result.hasErrors() || (stateChangeRequiresFastTrack && fastrackValueMissing)) {
            if (fastTrackApplication == null) {
                modelMap.addAttribute("fastTrackMissing", true);
            }
            return STATE_TRANSITION_VIEW;
        }

        RegisteredUser user = getCurrentUser();
        
        if (BooleanUtils.isNotTrue(delegate)) {
            applicationForm.setApplicationAdministrator(null);
        }
        
        if (BooleanUtils.isTrue(fastTrackApplication)) {
            applicationsService.fastTrackApplication(applicationForm.getApplicationNumber());
        }
        
        Comment newComment = commentFactory.createComment(applicationForm, user, stateChangeComment.getComment(), stateChangeComment.getDocuments(), stateChangeComment.getType(),
                stateChangeComment.getNextStatus());

        if (newComment instanceof ApprovalEvaluationComment) {

            ApprovalEvaluationComment approvalComment = (ApprovalEvaluationComment) newComment;

            if (ApplicationFormStatus.APPROVED == approvalComment.getNextStatus()) {
                applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
                accessService.updateAccessTimestamp(applicationForm, getCurrentUser(), new Date());
                if (approvalService.moveToApproved(applicationForm)) {
                    approvalService.sendToPortico(applicationForm);
                    modelMap.put("messageCode", "move.approved");
                    modelMap.put("application", applicationForm.getApplicationNumber());
                } else {
                    Comment genericComment = commentFactory.createComment(applicationForm, user, newComment.getComment(), newComment.getDocuments(), CommentType.GENERIC, null);
                    commentService.save(genericComment);
                    return "redirect:/rejectApplication?applicationId=" + applicationForm.getApplicationNumber() + "&rejectionId=7";
                }
            }
        }

        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.INTERNAL, new Date()));
        accessService.updateAccessTimestamp(applicationForm, getCurrentUser(), new Date());
        applicationsService.save(applicationForm);
        commentService.save(newComment);

        if (stateChangeComment.getNextStatus() == ApplicationFormStatus.APPROVAL) {
            applicationsService.makeApplicationNotEditable(applicationForm);
        }

        if (BooleanUtils.isTrue(delegate)) {
            return "redirect:/applications?messageCode=delegate.success&application=" + applicationForm.getApplicationNumber();
        }

        applicationsService.refresh(applicationForm);
        return stateTransitionService.resolveView(applicationForm);
    }
}
