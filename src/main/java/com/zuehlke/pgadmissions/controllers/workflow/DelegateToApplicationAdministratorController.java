package com.zuehlke.pgadmissions.controllers.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;

@RequestMapping("/delegate")
@Controller
public class DelegateToApplicationAdministratorController {

	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final UserPropertyEditor userPropertyEditor;
	private final CommentService commentService;

	DelegateToApplicationAdministratorController() {
		this(null, null, null, null);
	}

	@Autowired
	public DelegateToApplicationAdministratorController(ApplicationsService applicationsService, UserService userService, UserPropertyEditor userPropertyEditor
			, CommentService commentService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.userPropertyEditor = userPropertyEditor;
		this.commentService = commentService;
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
		dataBinder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }
        
    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String delegateToApplicationAdministrator(@ModelAttribute("applicationForm") ApplicationForm applicationForm) {
		NotificationRecord reviewReminderNotification = applicationForm.getNotificationForType(NotificationType.REVIEW_REMINDER);
		if(reviewReminderNotification != null){
			applicationForm.removeNotificationRecord(reviewReminderNotification);
		}
		commentService.createDelegateComment(getCurrentUser(), applicationForm);
		applicationsService.save(applicationForm);
		return "redirect:/applications?messageCode=delegate.success&application=" + applicationForm.getApplicationNumber();
	}
}
