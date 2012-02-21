package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = { "/decision" })
public class ApproveRejectController {

	private static final String APPROVE_REJECT_VIEW_NAME = "approveRejectSuccess";
	private ApplicationsService applicationsService;

	ApproveRejectController() {
		this(null);
	}

	@Autowired
	public ApproveRejectController(ApplicationsService applicationsService) {
		this.applicationsService = applicationsService;
	}

	@RequestMapping(value = { "/approve" }, method = RequestMethod.GET)
	@Transactional
	public String getApprovedApplicationPage(int id, ModelMap modelMap) {
		ApplicationForm application = applicationsService
				.getApplicationById(id);
		SecurityContext context = SecurityContextHolder.getContext();
		RegisteredUser approver = (RegisteredUser) context.getAuthentication()
				.getDetails();
		if (application.getApproved() == null) {
			application.setApproved("1");
			application.setApprover(approver);
			applicationsService.save(application);
			modelMap.addAttribute("message",
					"Your have successfully approved the application");
		} else {
			modelMap.addAttribute("message",
					"The application has already been decided by user: " + application.getApprover().getUsername());
		}
		return APPROVE_REJECT_VIEW_NAME;
	}

	@RequestMapping(value = { "/reject" }, method = RequestMethod.GET)
	@Transactional
	public String getRejectedApplicationPage(int id, ModelMap modelMap) {
		ApplicationForm application = applicationsService
				.getApplicationById(id);
		SecurityContext context = SecurityContextHolder.getContext();
		RegisteredUser approver = (RegisteredUser) context.getAuthentication()
				.getDetails();
		if (application.getApproved() == null) {
			application.setApproved("0");
			application.setApprover(approver);
			applicationsService.save(application);
			modelMap.addAttribute("message",
					"Your have successfully rejected the application");
		} else {
			modelMap.addAttribute("message",
					"The application has already been decided by user: " + application.getApprover().getUsername());
		}
		return APPROVE_REJECT_VIEW_NAME;
	}
}
