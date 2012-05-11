package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RejectService;

@Controller
@RequestMapping(value = { "/rejectApplication" })
public class RejectApplicationController {

	private static final String REJECT_VIEW_NAME = "private/staff/approver/reject_page";
	private static final String NEXT_VIEW_NAME = "redirect:/applications";
	private final RejectService rejectService;
	private final ApplicationsService applicationService;

	RejectApplicationController() {
		this(null, null);
	}

	@Autowired
	public RejectApplicationController(ApplicationsService applicationsService, RejectService rejectService) {
		this.applicationService = applicationsService;
		this.rejectService = rejectService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getRejectPage() {
		return REJECT_VIEW_NAME;
	}

	@RequestMapping(value = "/moveApplicationToReject", method = RequestMethod.POST)
	public String moveApplicationToReject(//
			@ModelAttribute("applicationForm") ApplicationForm application,// 
			RejectReason[] rejectReasons) {

		checkPermissionForApplication(application);
		checkApplicationStatus(application);
		rejectService.moveApplicationToReject(application, rejectReasons);
		return NEXT_VIEW_NAME;
	}

	@ModelAttribute("availableReasons")
	public List<RejectReason> getAvailableReasons() {
		return rejectService.getAllRejectionReasons();
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm application = applicationService.getApplicationById(applicationId);
		checkPermissionForApplication(application);
		checkApplicationStatus(application);
		return application;
	}

	private void checkApplicationStatus(ApplicationForm application) {
		switch (application.getStatus()) {
		case REVIEW:
		case VALIDATION:
		case APPROVAL:
		case INTERVIEW:
			break;
		default:
			throw new CannotUpdateApplicationException();
		}
	}

	private void checkPermissionForApplication(ApplicationForm application) {
		RegisteredUser currentUser = getCurrentUser();
		if (application == null || // 
				!(currentUser.isApproverOfApplicationForm(application) //
				|| currentUser.isAdminInProgramme(application.getProgram()))) {
			throw new ResourceNotFoundException();
		}
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
}
