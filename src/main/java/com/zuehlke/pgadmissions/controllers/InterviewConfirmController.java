package com.zuehlke.pgadmissions.controllers;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/interviewConfirm" })
public class InterviewConfirmController {

    private static final String INTERVIEW_CONFIRM_PAGE = "private/staff/interviewers/interview_confirm";

    private final ApplicationsService applicationsService;

    private final UserService userService;

    private final InterviewService interviewService;
    
    private final ApplicationFormAccessService accessService;
    
    private final ActionsProvider actionsProvider;

    public InterviewConfirmController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public InterviewConfirmController(ApplicationsService applicationsService, UserService userService,
            InterviewService interviewService, final ApplicationFormAccessService accessService, ActionsProvider actionsProvider) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.interviewService = interviewService;
        this.accessService = accessService;
        this.actionsProvider = actionsProvider;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        RegisteredUser currentUser = userService.getCurrentUser();
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.hasAdminRightsOnApplication(application) && !currentUser.isApplicationAdministrator(application)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        Interview interview = application.getLatestInterview();
        if (!application.isInState(ApplicationFormStatus.INTERVIEW) || !interview.isScheduling()) {
            throw new ActionNoLongerRequiredException(application.getApplicationNumber());
        }
        return application;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("actionsDefinition")
    public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId) {
        ApplicationForm application = getApplicationForm(applicationId);
        return actionsProvider.calculateActions(userService.getCurrentUser(), application);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getInterviewVotePage() {
        return INTERVIEW_CONFIRM_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitInterviewConfirmation(
            @ModelAttribute ApplicationForm applicationForm, 
            @RequestParam(required = false) Integer timeslotId, 
            @RequestParam(required = false) String comments, Model model) {
        
        boolean hasErrors = false;
        
        if (timeslotId == null) {
            model.addAttribute("timeslotIdError", "dropdown.radio.select.none");
            model.addAttribute("comments", comments);
            hasErrors = true;
        }
        
        if (StringUtils.isNotBlank(comments) && !ESAPI.validator().isValidInput("comment", comments, "ExtendedAscii", 2000, true)) {
            model.addAttribute("commentsError", "text.field.nonextendedascii");
            model.addAttribute("comments", comments);
            hasErrors = true;
        }
        
        if (hasErrors) {
            return INTERVIEW_CONFIRM_PAGE;
        }
        
        Interview interview = applicationForm.getLatestInterview();
        interviewService.confirmInterview(interview, timeslotId);
        
        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
        applicationsService.save(applicationForm);
        
        RegisteredUser user = getUser();
        accessService.updateAccessTimestamp(applicationForm, user, new Date());
        
        if (StringUtils.isNotBlank(comments)) {
            InterviewParticipant interviewParticipant = interview.getParticipant(user);
            InterviewVoteComment comment = new InterviewVoteComment();
            comment.setApplication(applicationForm);
            comment.setComment(comments);
            comment.setDate(new Date());
            comment.setInterviewParticipant(interview.getParticipant(user));
            comment.setUser(user);
            interviewService.saveComment(interviewParticipant, comment);
        }
        
        return "redirect:/applications?messageCode=interview.confirm&application=" + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/restart", method = RequestMethod.POST)
    public String restartInterviewScheduling(@ModelAttribute ApplicationForm applicationForm) {
        return "redirect:/interview/moveToInterview?applicationId=" + applicationForm.getApplicationNumber();
    }
}
