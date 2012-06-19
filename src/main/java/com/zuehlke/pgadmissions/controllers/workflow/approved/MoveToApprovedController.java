package com.zuehlke.pgadmissions.controllers.workflow.approved;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotApproveApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/approved")
public class MoveToApprovedController {

	private static final String APPROVED_DETAILS_VIEW_NAME = "/private/staff/approver/approve_page";

	private final UserService userService;

	private final ApplicationsService applicationsService;

	private final ApprovalService approvalService;

	MoveToApprovedController() {
		this(null, null, null);
	}

	@Autowired
	public MoveToApprovedController(ApplicationsService applicationsService, UserService userService, ApprovalService approvalService) {

		this.applicationsService = applicationsService;
		this.userService = userService;		
		this.approvalService = approvalService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "moveToApproved")
	public String getApprovedDetailsPage(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		if (!getUser().isInRoleInProgram(Authority.APPROVER, applicationForm.getProgram())) {
			throw new ResourceNotFoundException();
		}
		return APPROVED_DETAILS_VIEW_NAME;
	}

	@RequestMapping(value = "/move", method = RequestMethod.POST)
	public String moveToApproved(@RequestParam String applicationId, @RequestParam(required = false) String comment,
			@RequestParam(required = false) List<String> documents) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		if (!getUser().isInRoleInProgram(Authority.APPROVER, applicationForm.getProgram())) {
			throw new ResourceNotFoundException();
		}
		approvalService.moveToApproved(applicationForm, comment, documents);
		return "redirect:/applications";
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (application == null || !application.isInState("APPROVAL")
				|| (!userService.getCurrentUser().isInRoleInProgram(Authority.APPROVER, application.getProgram()))) {
			throw new ResourceNotFoundException();
		}
		if (!application.isModifiable()) {
			throw new CannotApproveApplicationException();
		}
		return application;
	}

	RegisteredUser getCurrentUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}

}
