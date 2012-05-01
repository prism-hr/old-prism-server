package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApproveApplicationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/approval")
public class ApprovalController {
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final ApproveApplicationService approveApplicationService;
	private final RefereeService refereeService;

	ApprovalController(){
		this(null, null, null, null);
	}
	@Autowired
	public ApprovalController(ApplicationsService applicationsService, UserService userService, ApproveApplicationService approveApplicationService,
			RefereeService refereeService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.approveApplicationService = approveApplicationService;
		this.refereeService = refereeService;
	
	}
	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer application) {
		ApplicationForm applicationForm = applicationsService.getApplicationById(application);
		if (applicationForm == null || !getCurrentUser().isInRoleInProgram(Authority.ADMINISTRATOR, applicationForm.getProgram())) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;

	}

	RegisteredUser getCurrentUser() {
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		return userService.getUser(currentUser.getId());
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String moveToApproval(@ModelAttribute ApplicationForm applicationForm) {
		applicationForm.setStatus(ApplicationFormStatus.APPROVAL);
		refereeService.processRefereesRoles(applicationForm.getReferees());
		approveApplicationService.saveApplicationFormAndSendMailNotifications(applicationForm);
		return "redirect:/applications";
		
	}

}
