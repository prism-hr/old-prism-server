package com.zuehlke.pgadmissions.controllers.workflow;

import java.text.SimpleDateFormat;

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
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
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
public class ValidationTransitionController extends StateTransitionController {

	ValidationTransitionController() {
		 this(null, null, null, null, null, null, null, null, null, null);
	
	}

	@Autowired
	public ValidationTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
			CommentFactory commentFactory, StateTransitionViewResolver stateTransitionViewResolver, EncryptionHelper encryptionHelper,
			DocumentService documentService, ApprovalService approvalService, StateChangeValidator stateChangeValidator, DocumentPropertyEditor documentPropertyEditor) {
		super(applicationsService, userService, commentService, commentFactory, stateTransitionViewResolver, encryptionHelper,documentService, approvalService, stateChangeValidator, documentPropertyEditor);

	}
	
	@RequestMapping(method = RequestMethod.GET, value="/getPage")
	public String getStateTransitionView(@ModelAttribute ApplicationForm applicationForm) {
		return stateTransitionViewResolver.resolveView(applicationForm);
	}
	
	@ModelAttribute("comment")
	public ValidationComment getComment(@RequestParam String applicationId) {
		ValidationComment validationComment = new ValidationComment();
		validationComment.setApplication(getApplicationForm(applicationId));
		validationComment.setUser(getCurrentUser());
		return validationComment;
	}
	
	@RequestMapping(value="/submitValidationComment", method = RequestMethod.POST)
	public String addComment(@RequestParam String applicationId, @RequestParam String closingDate,
	        @RequestParam String projectTitle, @Valid @ModelAttribute("comment") ValidationComment validationComment, BindingResult result, ModelMap modelMap) {
		if (result.hasErrors()) {
		    return STATE_TRANSITION_VIEW;
		}
		
		try {
		    ApplicationForm form = getApplicationForm(applicationId);
		    form.setBatchDeadline(new SimpleDateFormat("dd MMM yyyy").parse(closingDate));
		    form.setProjectTitle(projectTitle);
		    applicationsService.save(form);
		    commentService.save(validationComment);
		} catch (Exception e) {
		    return STATE_TRANSITION_VIEW;
		}
		return stateTransitionViewResolver.resolveView(getApplicationForm(applicationId));
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
