package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
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

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public String getDecidedApplicationPage(@RequestParam Integer id, @RequestParam String submit, ModelMap modelMap) {
		ApplicationForm application = applicationsService.getApplicationById(id);
		SecurityContext context = SecurityContextHolder.getContext();
		RegisteredUser approver = (RegisteredUser) context.getAuthentication().getDetails();
		if (application.getApprovalStatus() == null) {
			ApprovalStatus submitAsEnum = getSubmitAsEnum(submit);
			application.setApprovalStatus(submitAsEnum);
			application.setApprover(approver);
			applicationsService.save(application);
			String decision = submitAsEnum.equals(ApprovalStatus.APPROVED)? "rejected" : "accepted";
			modelMap.addAttribute("message","Your have successfully "+ decision + " the application");
		} else {
			modelMap.addAttribute("message","The application has already been decided by user: " + application.getApprover().getUsername());
		}
		return APPROVE_REJECT_VIEW_NAME;
	}

	private ApprovalStatus getSubmitAsEnum(String submit) {
		return submit.equals("Approve")? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;
	}


}
