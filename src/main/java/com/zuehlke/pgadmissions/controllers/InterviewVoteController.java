package com.zuehlke.pgadmissions.controllers;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping(value = { "/interviewVote" })
public class InterviewVoteController {

    private static final String INTERVIEW_VOTE_PAGE = "private/staff/interviewers/interview_vote";
    private final ApplicationsService applicationsService;
    private final UserService userService;

    public InterviewVoteController() {
        this(null, null, null, null, null, null, null, null);
    }

    @Autowired
    public InterviewVoteController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            FeedbackCommentValidator reviewFeedbackValidator, DocumentPropertyEditor documentPropertyEditor, ScoringDefinitionParser scoringDefinitionParser,
            ScoresPropertyEditor scoresPropertyEditor, ScoreFactory scoreFactory) {
        this.applicationsService = applicationsService;
        this.userService = userService;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        RegisteredUser currentUser = userService.getCurrentUser();
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        Interview interview = applicationForm.getLatestInterview();
        if (!interview.isParticipant(currentUser) || !currentUser.canSee(applicationForm)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        if (applicationForm.isDecided() || applicationForm.isWithdrawn()) {
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
        return applicationForm;
    }
    
    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("actionsDefinition")
    public ApplicationActionsDefinition getActionsDefinition(@RequestParam String applicationId) {
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.getActionsDefinition(userService.getCurrentUser(), application);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getInterviewVotePage() {
        return INTERVIEW_VOTE_PAGE;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String submitInterviewVotes(@ModelAttribute ApplicationForm applicationForm, @RequestParam String timeslots) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Set<Integer>>(){}.getType();
        Set<Integer> fromJson = gson.fromJson(timeslots, collectionType);
//        applicationForm.getLatestInterview().getParticipants()
//        applicationForm.getLatestInterview().getTimeslots()
        return "redirect:/applications?messageCode=interview.feedback&application=" + applicationForm.getApplicationNumber();
    }

}
