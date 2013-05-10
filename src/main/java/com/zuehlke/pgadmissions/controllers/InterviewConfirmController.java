package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/interviewConfirm" })
public class InterviewConfirmController {

    private static final String INTERVIEW_CONFIRM_PAGE = "private/staff/interviewers/interview_confirm";

    private final ApplicationsService applicationsService;

    private final UserService userService;

    public InterviewConfirmController() {
        this(null, null);
    }

    @Autowired
    public InterviewConfirmController(ApplicationsService applicationsService, UserService userService) {
        this.applicationsService = applicationsService;
        this.userService = userService;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        RegisteredUser currentUser = userService.getCurrentUser();
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.hasAdminRightsOnApplication(application) && !currentUser.isInterviewerOfApplicationForm(application)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        Interview interview = application.getLatestInterview();
        if (application.isDecided() || application.isWithdrawn() || !interview.isScheduling()) {
            throw new ActionNoLongerRequiredException(application.getApplicationNumber());
        }
        return application;
    }

    @ModelAttribute("interview")
    public Interview getInterview(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }

        return applicationForm.getLatestInterview();
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("actionsDefinition")
    public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId) {
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.getActionsDefinition(userService.getCurrentUser(), application);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getInterviewVotePage() {
        return INTERVIEW_CONFIRM_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitInterviewConfirmation(@ModelAttribute ApplicationForm applicationForm, Integer timeslotId) {
//        interview.setInterviewDueDate(timeslot.getDueDate());
//        interview.setInterviewTime(timeslot.getStartTime());
        
        return "redirect:/applications?messageCode=interview.confirm&application=" + applicationForm.getApplicationNumber();
    }
    
    @RequestMapping(value = "/restart", method = RequestMethod.POST)
    public String restartInterview(@ModelAttribute ApplicationForm applicationForm) {
//      interviewService.clearScheduling();
        return "redirect:/interview/moveToInterview?applicationId=" + applicationForm.getApplicationNumber();
    }
}
