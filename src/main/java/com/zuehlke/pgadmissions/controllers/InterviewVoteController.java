package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Strings;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.AcceptedTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.CommentAssignedUserValidator;

@Controller
@RequestMapping(value = { "/interviewVote" })
public class InterviewVoteController {
    // TODO change InterviewParticipant to CommentAssignedUser, fix tests

    private static final String INTERVIEW_VOTE_PAGE = "private/staff/interviewers/interview_vote";
    
    @Autowired
    private ApplicationsService applicationsService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private InterviewService interviewService;
    
    @Autowired
    private CommentAssignedUserValidator assignedUserValidator;
    
    @Autowired
    private AcceptedTimeslotsPropertyEditor acceptedTimeslotsPropertyEditor;
    
    @Autowired
    private ActionsProvider actionsProvider;
    
    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;


    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return applicationForm;
    }

    @InitBinder("interviewParticipant")
    public void registerValidatorAndPropertyEditor(WebDataBinder binder) {
        binder.setValidator(assignedUserValidator);
        binder.registerCustomEditor(null, "acceptedTimeslots", acceptedTimeslotsPropertyEditor);
    }
    
    @ModelAttribute("assignedUser")
    public CommentAssignedUser getAssignedUser(@RequestParam String applicationId) {
//        ApplicationForm applicationForm = getApplicationForm(applicationId);
//        InterviewParticipant participant = applicationForm.getLatestInterview().getParticipant(getUser());
//        return participant;
        return null;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getInterviewVotePage(ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY);
        applicationFormUserRoleService.deregisterApplicationUpdate(applicationForm, user);
        return INTERVIEW_VOTE_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitInterviewVotes(BindingResult bindingResult,
            @RequestParam(required = false) String comment, ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_INTERVIEW_AVAILABILITY);

        if (bindingResult.hasErrors()) {
            return INTERVIEW_VOTE_PAGE;
        }

        // FIXME pass can(t)MakeIt flag as request param or transient field
//        if (!currentParticipant.getCanMakeIt()) {
//            currentParticipant.getAcceptedTimeslots().clear();
//        }

        InterviewVoteComment interviewVoteComment = new InterviewVoteComment();
        interviewVoteComment.setContent(Strings.emptyToNull(comment));
        interviewVoteComment.setApplication(applicationForm);
        interviewVoteComment.setUser(user);
        interviewService.postVote(interviewVoteComment, user);

        return "redirect:/applications?messageCode=interview.vote.feedback&application=" + applicationForm.getApplicationNumber();
    }

}
