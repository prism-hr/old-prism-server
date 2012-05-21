package com.zuehlke.pgadmissions.controllers.workflow.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/approval" })
public class ThrowAwayApprovalController {

	private final ApplicationsService applicationsService;
	private final UserService userService;

	ThrowAwayApprovalController() {
		this(null, null);

	}


	@Autowired
	public ThrowAwayApprovalController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;

	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getApprovalPage(){
		return "private/staff/approver/approve_page";
	}
	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {

		ApplicationForm application = applicationsService.getApplicationById(applicationId);
		if (application == null
				|| (!userService.getCurrentUser().isInRoleInProgram(Authority.APPROVER, application.getProgram()))) {
			throw new ResourceNotFoundException();
		}
		return application;
	}
	
	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

}
