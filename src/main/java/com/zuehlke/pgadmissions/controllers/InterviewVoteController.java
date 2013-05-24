package com.zuehlke.pgadmissions.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.AcceptedTimeslotsPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewParticipantValidator;

@Controller
@RequestMapping(value = { "/interviewVote" })
public class InterviewVoteController {

    private static final String INTERVIEW_VOTE_PAGE = "private/staff/interviewers/interview_vote";
    private final ApplicationsService applicationsService;
    private final UserService userService;
    private final InterviewService interviewService;
    private final InterviewParticipantValidator interviewParticipantValidator;
    private final AcceptedTimeslotsPropertyEditor acceptedTimeslotsPropertyEditor;
    private final ActionsProvider actionsProvider;

    public InterviewVoteController() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public InterviewVoteController(ApplicationsService applicationsService, UserService userService,
                    InterviewParticipantValidator interviewParticipantValidator, InterviewService interviewService,
                    AcceptedTimeslotsPropertyEditor acceptedTimeslotsPropertyEditor, ActionsProvider actionsProvider) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.interviewService = interviewService;
        this.interviewParticipantValidator = interviewParticipantValidator;
        this.acceptedTimeslotsPropertyEditor = acceptedTimeslotsPropertyEditor;
        this.actionsProvider = actionsProvider;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        RegisteredUser currentUser = userService.getCurrentUser();
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        Interview interview = applicationForm.getLatestInterview();
        if (!interview.isParticipant(currentUser)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        InterviewParticipant participant = interview.getParticipant(currentUser);
        if (!applicationForm.isInState(ApplicationFormStatus.INTERVIEW) || participant.getResponded() ) {
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
        return applicationForm;
    }

    @InitBinder("interviewParticipant")
    public void registerValidatorAndPropertyEditor(WebDataBinder binder) {
        binder.setValidator(this.interviewParticipantValidator);
        binder.registerCustomEditor(null, "acceptedTimeslots", acceptedTimeslotsPropertyEditor);
    }

    @ModelAttribute("interviewParticipant")
    public InterviewParticipant getInterviewParticipant(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        return applicationForm.getLatestInterview().getParticipant(getUser());
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
        return INTERVIEW_VOTE_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitInterviewVotes(@Valid @ModelAttribute InterviewParticipant currentParticipant, BindingResult bindingResult,
                    @ModelAttribute ApplicationForm applicationForm, @RequestParam(required = false) String comment) {
        if (bindingResult.hasErrors()) {
            return INTERVIEW_VOTE_PAGE;
        }

        if (!currentParticipant.getCanMakeIt()) {
            currentParticipant.getAcceptedTimeslots().clear();
        }

        InterviewVoteComment interviewVoteComment = new InterviewVoteComment();
        if (comment == null) {
            interviewVoteComment.setComment("");
        } else {
            interviewVoteComment.setComment(comment);
        }
        interviewVoteComment.setApplication(applicationForm);
        interviewVoteComment.setUser(currentParticipant.getUser());
        interviewVoteComment.setInterviewParticipant(currentParticipant);

        interviewService.postVote(currentParticipant, interviewVoteComment);

        return "redirect:/applications?messageCode=interview.vote.feedback&application=" + applicationForm.getApplicationNumber();
    }

}
