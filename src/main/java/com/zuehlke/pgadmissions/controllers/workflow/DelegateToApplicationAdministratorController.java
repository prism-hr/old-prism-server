package com.zuehlke.pgadmissions.controllers.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@RequestMapping("/delegate")
@Controller
public class DelegateToApplicationAdministratorController {

	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final UserPropertyEditor userPropertyEditor;

	DelegateToApplicationAdministratorController() {
		this(null, null, null);
	}

	@Autowired
	public DelegateToApplicationAdministratorController(ApplicationsService applicationsService, UserService userService, UserPropertyEditor userPropertyEditor) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.userPropertyEditor = userPropertyEditor;
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

	@InitBinder(value = "applicationForm")
	public void registerPropertyEditors(WebDataBinder dataBinder) {
		dataBinder.registerCustomEditor(RegisteredUser.class, "applicationAdministrator", userPropertyEditor);

	}

	@RequestMapping(method = RequestMethod.POST)
	public String delegateToApplicationAdministrator(@ModelAttribute("applicationForm") ApplicationForm applicationForm) {
		applicationsService.save(applicationForm);

		return "redirect:/applications";
	}

}
