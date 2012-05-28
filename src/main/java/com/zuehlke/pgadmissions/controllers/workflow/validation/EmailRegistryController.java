package com.zuehlke.pgadmissions.controllers.workflow.validation;

import org.apache.log4j.Logger;
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
import com.zuehlke.pgadmissions.mail.RegistryMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@RequestMapping("/registryHelpRequest")
@Controller
public class EmailRegistryController {

	private final Logger log = Logger.getLogger(EmailRegistryController.class);
	private final RegistryMailSender registryMailSender;
	private final ApplicationsService applicationsService;
	private final UserService userService;

	EmailRegistryController(){
		this(null, null, null);
	}
	@Autowired
	public EmailRegistryController(RegistryMailSender registryMailSender, ApplicationsService applicationsService, UserService userService) {
		this.registryMailSender = registryMailSender;
		this.applicationsService = applicationsService;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView sendHelpRequestToRegistryContacts(@ModelAttribute("applicationForm") ApplicationForm applicationForm) {
		ModelAndView modelAndView = new ModelAndView("private/common/simpleMessage");
		try {
			registryMailSender.sendApplicationToRegistryContacts(applicationForm);
			modelAndView.getModel().put("message", "Email send");
		} catch (Throwable e) {
			log.error("Send email to registry contacts failed:", e);
			modelAndView.getModel().put("message", "Email sending failed");			
		}
	
	
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
