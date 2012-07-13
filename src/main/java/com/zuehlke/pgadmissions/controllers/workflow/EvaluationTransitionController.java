package com.zuehlke.pgadmissions.controllers.workflow;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
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

@Controller
@RequestMapping("/progress")
public class EvaluationTransitionController extends StateTransitionController {

	EvaluationTransitionController() {
		this(null, null, null, null, null, null, null, null, null, null);

	}

	@Autowired
	public EvaluationTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
			CommentFactory commentFactory, StateTransitionViewResolver stateTransitionViewResolver, EncryptionHelper encryptionHelper,
			DocumentService documentService, ApprovalService approvalService, StateChangeValidator stateChangeValidator,
			DocumentPropertyEditor documentPropertyEditor) {
		super(applicationsService, userService, commentService, commentFactory, stateTransitionViewResolver, encryptionHelper, documentService,
				approvalService, stateChangeValidator, documentPropertyEditor);

	}

	@ModelAttribute("comment")
	public StateChangeComment getComment(@RequestParam String applicationId) {
		return new StateChangeComment();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/submitEvaluationComment")
	public String addComment(@RequestParam String applicationId, @Valid @ModelAttribute("comment") StateChangeComment stateChangeComment, BindingResult result,
			ModelMap modelMap, @RequestParam(required = false) Boolean delegate) {
		if (result.hasErrors()) {
			return STATE_TRANSITION_VIEW;
		}
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		RegisteredUser user = getCurrentUser();

		Comment newComment = commentFactory.createComment(applicationForm, user, stateChangeComment.getComment(), stateChangeComment.getType(),
				stateChangeComment.getNextStatus());
		if (newComment instanceof ReviewEvaluationComment) {
			((ReviewEvaluationComment) newComment).setReviewRound(applicationForm.getLatestReviewRound());
		}
		if (newComment instanceof InterviewEvaluationComment) {
			((InterviewEvaluationComment) newComment).setInterview(applicationForm.getLatestInterview());
		}
		if (newComment instanceof ApprovalEvaluationComment) {
			((ApprovalEvaluationComment) newComment).setApprovalRound(applicationForm.getLatestApprovalRound());
		}
		commentService.save(newComment);
		if (newComment instanceof ApprovalEvaluationComment) {
			ApprovalEvaluationComment approvalComment = (ApprovalEvaluationComment) newComment;

			if (ApplicationFormStatus.APPROVED == approvalComment.getNextStatus()) {
				approvalService.moveToApproved(applicationForm);
				modelMap.put("messageCode", "move.approved");
				modelMap.put("application", applicationForm.getApplicationNumber());
			}
		}
		if(delegate != null && delegate){
			return "redirect:/applications?messageCode=delegate.success&application=" + applicationForm.getApplicationNumber();
		}
		return stateTransitionViewResolver.resolveView(applicationForm);

	}
}
