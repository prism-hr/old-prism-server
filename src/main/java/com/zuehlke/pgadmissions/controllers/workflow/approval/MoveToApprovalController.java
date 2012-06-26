package com.zuehlke.pgadmissions.controllers.workflow.approval;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/approval")
public class MoveToApprovalController extends ApprovalController {

	protected final String REQUEST_APROVAL_DETAILS_VIEW_NAME = "/private/staff/approver/request_restart_approve_page";
	private final DocumentPropertyEditor documentPropertyEditor;
	private final GenericCommentValidator genericCommentValidator;
	

	
	MoveToApprovalController() {
		this(null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public MoveToApprovalController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator supervisorValidator,
			ApprovalRoundValidator approvalroundValidator, ApprovalService approvalService, MessageSource messageSource,
			SupervisorPropertyEditor supervisorPropertyEditor, EncryptionHelper encryptionHelper, GenericCommentValidator genericCommentValidator, DocumentPropertyEditor documentPropertyEditor) {
		super(applicationsService, userService, supervisorValidator, approvalroundValidator, approvalService, messageSource, supervisorPropertyEditor, encryptionHelper);
		this.genericCommentValidator = genericCommentValidator;
		this.documentPropertyEditor = documentPropertyEditor;
	}

	@RequestMapping(method = RequestMethod.GET, value = "moveToApproval")
	public String getApprovalRoundDetailsPage(ModelMap modelMap) {
		modelMap.put("assignOnly", false);
		return APROVAL_DETAILS_VIEW_NAME;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "requestRestart")
	public String getRequestRestartPage() {
		return REQUEST_APROVAL_DETAILS_VIEW_NAME;
	}

	@RequestMapping(value = "/move", method = RequestMethod.POST)
	public String moveToApproval(@RequestParam String applicationId, @Valid @ModelAttribute("approvalRound") ApprovalRound approvalRound,
			BindingResult bindingResult) {

		ApplicationForm applicationForm = getApplicationForm(applicationId);
		if (bindingResult.hasErrors()) {
			return APROVAL_DETAILS_VIEW_NAME;
		}
		approvalService.moveApplicationToApproval(applicationForm, approvalRound);

		return "redirect:/applications";
	}
	
	@ModelAttribute("comment")
	public Comment getComment(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		Comment comment = new Comment();
		comment.setApplication(applicationForm);
		comment.setUser(userService.getCurrentUser());
		return comment;
	}

	@InitBinder(value = "comment")
	public void registerBinders(WebDataBinder binder) {
		binder.setValidator(genericCommentValidator);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);

	}

	@RequestMapping(value = "submitRequestRestart", method = RequestMethod.POST)
	public String requestRestart(@ModelAttribute("applicationForm") ApplicationForm applicationForm, @Valid @ModelAttribute("comment") Comment comment, BindingResult result, ModelMap modelMap) {
		if (result.hasErrors()) {
			return REQUEST_APROVAL_DETAILS_VIEW_NAME;
		}
		
		approvalService.requestApprovalRestart(applicationForm, getUser(), comment);

		modelMap.put("message", String.format("An e-mail requesting the restart of the approval phase " + //
				"for application %s was sent to the administrator!", applicationForm.getApplicationNumber()));
		return "redirect:/applications";
	}

	@Override
	@ModelAttribute("approvalRound")
	public ApprovalRound getApprovalRound(@RequestParam String applicationId) {
		return new ApprovalRound();
	}
}
