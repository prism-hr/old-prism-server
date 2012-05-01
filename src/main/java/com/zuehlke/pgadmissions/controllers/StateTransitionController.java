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
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/progress")
public class StateTransitionController {

	private static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";
	private final ApplicationsService applicationsService;
	private final UserService userService;

	StateTransitionController() {
		this(null, null);

	}

	@Autowired
	public StateTransitionController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;

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

	@ModelAttribute("stati")
	public ApplicationFormStatus[] getAvailableNextStati(@RequestParam Integer application) {
		ApplicationForm applicationForm = getApplicationForm(application);
		return ApplicationFormStatus.getAvailableNextStati(applicationForm.getStatus());
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getStateTransitionView() {
		return STATE_TRANSITION_VIEW;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}

}
