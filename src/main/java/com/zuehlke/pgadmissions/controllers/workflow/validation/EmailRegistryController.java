package com.zuehlke.pgadmissions.controllers.workflow.validation;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mail.RegistryMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;

@RequestMapping("/registryHelpRequest")
@Controller
public class EmailRegistryController {

	private final Logger log = Logger.getLogger(EmailRegistryController.class);
	private final RegistryMailSender registryMailSender;
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final CommentService commentService;
	private final CommentFactory commentFactory;

	EmailRegistryController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public EmailRegistryController(RegistryMailSender registryMailSender, ApplicationsService applicationsService, UserService userService,
			CommentService commentService, CommentFactory commentFactory) {
		this.registryMailSender = registryMailSender;
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.commentService = commentService;
		this.commentFactory = commentFactory;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView sendHelpRequestToRegistryContacts(@ModelAttribute("applicationForm") ApplicationForm applicationForm) {
		ModelAndView modelAndView = new ModelAndView("private/common/simpleMessage");
		try {
			registryMailSender.sendApplicationToRegistryContacts(applicationForm);
			NotificationRecord notificationRecord = applicationForm.getNotificationForType(NotificationType.REGISTRY_HELP_REQUEST);
			if (notificationRecord == null) {
				notificationRecord = new NotificationRecord(NotificationType.REGISTRY_HELP_REQUEST);
				applicationForm.getNotificationRecords().add(notificationRecord);
			}
			notificationRecord.setDate(new Date());
			applicationsService.save(applicationForm);
			commentService.save(commentFactory.createComment(applicationForm, getCurrentUser(),
					"Request for assistance in validating application details send to UCL central registry office.", CommentType.GENERIC));
			modelAndView.getModel().put("message", "registry.email.send");
		} catch (Throwable e) {
			log.error("Send email to registry contacts failed:", e);
			modelAndView.getModel().put("message", "registry.email.failed");
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
