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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@RequestMapping("/delegate")
@Controller
public class DelegateToApplicationAdministratorController {

    private final ApplicationsService applicationsService;
    private final UserService userService;
    private final NewUserByAdminValidator newUserByAdminValidator;
    private final CommentService commentService;

    DelegateToApplicationAdministratorController() {
        this(null, null, null, null);
    }

    @Autowired
    public DelegateToApplicationAdministratorController(ApplicationsService applicationsService, UserService userService,
            NewUserByAdminValidator newUserByAdminValidator, CommentService commentService) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.newUserByAdminValidator = newUserByAdminValidator;
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

    @ModelAttribute("delegatedInterviewer")
    public RegisteredUser getDelegatedInterview() {
        return new RegisteredUser();
    }

    @InitBinder(value = "delegatedInterviewer")
    public void registerPropertyEditors(WebDataBinder dataBinder) {
        dataBinder.setValidator(newUserByAdminValidator);
        dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(method = RequestMethod.POST)
    public String delegateToApplicationAdministrator(@ModelAttribute("applicationForm") ApplicationForm applicationForm,
            @ModelAttribute("delegatedInterviewer") RegisteredUser delegatedInterviewer) {

        RegisteredUser applicationAdmin = userService.getUserByEmailIncludingDisabledAccounts(delegatedInterviewer.getEmail());
        if (applicationAdmin == null) {
            applicationAdmin = userService.createNewUserInRole(delegatedInterviewer.getFirstName(), delegatedInterviewer.getLastName(),
                    delegatedInterviewer.getEmail(), Authority.INTERVIEWER, DirectURLsEnum.VIEW_APPLIATION_PRIOR_TO_INTERVIEW, applicationForm);
        }
        applicationForm.setApplicationAdministrator(applicationAdmin);

        NotificationRecord reviewReminderNotification = applicationForm.getNotificationForType(NotificationType.REVIEW_REMINDER);
        if (reviewReminderNotification != null) {
            applicationForm.removeNotificationRecord(reviewReminderNotification);
        }
        commentService.createDelegateComment(getCurrentUser(), applicationForm);
        return "redirect:/applications?messageCode=delegate.success&application=" + applicationForm.getApplicationNumber();
    }
}
