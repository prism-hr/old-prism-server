package com.zuehlke.pgadmissions.controllers.workflow.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@RequestMapping("/registryHelpRequest")
@Controller
public class RegistryController {

	private final ApplicationsService applicationsService;
	private final UserService userService;

	RegistryController() {
		this(null, null);
	}

	@Autowired
	public RegistryController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView sendHelpRequestToRegistryContacts(@ModelAttribute("applicationForm") ApplicationForm applicationForm) {
		applicationForm.setAdminRequestedRegistry(getCurrentUser());
		applicationForm.setRegistryUsersDueNotification(true);
		applicationsService.save(applicationForm);
		ModelAndView modelAndView = new ModelAndView("private/common/simpleMessage");
			modelAndView.getModel().put("message", "registry.email.send");
		return modelAndView;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (applicationForm == null || !getCurrentUser().hasAdminRightsOnApplication(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}

	@ModelAttribute("user")
	public RegisteredUser getCurrentUser() {
		return userService.getCurrentUser();
	}

}
