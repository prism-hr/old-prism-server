package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.security.ActionsProvider;
import com.zuehlke.pgadmissions.services.ApplicantRatingService;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping(value = { "/interviewFeedback" })
public class InterviewCommentController {

    private static final Logger log = LoggerFactory.getLogger(InterviewCommentController.class);
    private static final String INTERVIEW_FEEDBACK_PAGE = "private/staff/interviewers/feedback/interview_feedback";
    private final ApplicationsService applicationsService;
    private final UserService userService;
    private final FeedbackCommentValidator feedbackCommentValidator;
    private final CommentService commentService;
    private final DocumentPropertyEditor documentPropertyEditor;
    private final ScoringDefinitionParser scoringDefinitionParser;
    private final ScoresPropertyEditor scoresPropertyEditor;
    private final ScoreFactory scoreFactory;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;
    private final ActionsProvider actionsProvider;
    private final ApplicantRatingService applicantRatingService;
    private final InterviewService interviewService;

    public InterviewCommentController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public InterviewCommentController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            FeedbackCommentValidator reviewFeedbackValidator, DocumentPropertyEditor documentPropertyEditor, ScoringDefinitionParser scoringDefinitionParser,
            ScoresPropertyEditor scoresPropertyEditor, ScoreFactory scoreFactory, ActionsProvider actionsProvider,
            ApplicationFormUserRoleService applicationFormUserRoleService, ApplicantRatingService applicantRatingService,
            InterviewService interviewService) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.commentService = commentService;
        this.feedbackCommentValidator = reviewFeedbackValidator;
        this.documentPropertyEditor = documentPropertyEditor;
        this.scoringDefinitionParser = scoringDefinitionParser;
        this.scoresPropertyEditor = scoresPropertyEditor;
        this.scoreFactory = scoreFactory;
        this.actionsProvider = actionsProvider;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.applicantRatingService = applicantRatingService;
        this.interviewService = interviewService;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        actionsProvider.validateAction(applicationForm, getCurrentUser(), ApplicationFormAction.COMMENT);
        return applicationForm;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        return actionsProvider.getApplicationDescriptorForUser(getApplicationForm(applicationId), getCurrentUser());
    }

    @ModelAttribute("user")
    public RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("comment")
    public InterviewComment getComment(@RequestParam String applicationId) throws ScoringDefinitionParseException {
        ApplicationForm application = getApplicationForm(applicationId);
        RegisteredUser user = getCurrentUser();

        InterviewComment interviewComment = new InterviewComment();
        interviewComment.setApplication(application);
        interviewComment.setUser(user);
        interviewComment.setComment("");
        interviewComment.setType(CommentType.INTERVIEW);
        interviewComment.setInterviewer(interviewService.getInterviewerForInterview(user, application.getLatestInterview()));

        ScoringDefinition scoringDefinition = application.getProgram().getScoringDefinitions().get(ScoringStage.INTERVIEW);

        if (scoringDefinition != null) {
            try {
                CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
                List<Score> scores = scoreFactory.createScores(customQuestion.getQuestion());
                interviewComment.getScores().addAll(scores);
                interviewComment.setAlert(customQuestion.getAlert());
            } catch (ScoringDefinitionParseException e) {
                log.error("Incorrect scoring XML configuration for interview stage in program: " + application.getProgram().getTitle());
            }
        }

        return interviewComment;
    }

    @InitBinder(value = "comment")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(feedbackCommentValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(null, "scores", scoresPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getInterviewFeedbackPage(@RequestParam String applicationId) {
        applicationFormUserRoleService.applicationViewed(getApplicationForm(applicationId), getCurrentUser());
        return INTERVIEW_FEEDBACK_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addComment(@ModelAttribute("comment") InterviewComment comment, BindingResult result, ModelMap modelMap,
    		@ModelAttribute ApplicationForm applicationForm)
            throws ScoringDefinitionParseException {
        List<Score> scores = comment.getScores();
        if (!scores.isEmpty()) {
            List<Question> questions = getCustomQuestions(applicationForm.getApplicationNumber());
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                score.setOriginalQuestion(questions.get(i));
            }
        }

        feedbackCommentValidator.validate(comment, result);

        if (result.hasErrors()) {
            return INTERVIEW_FEEDBACK_PAGE;
        }
        commentService.save(comment);
        comment.getInterviewer().setInterviewComment(comment);
        applicationForm.getApplicationComments().add(comment);
        applicantRatingService.computeAverageRating(comment.getInterviewer().getInterview());
        applicantRatingService.computeAverageRating(applicationForm);

        applicationsService.save(applicationForm);
        applicationFormUserRoleService.interviewFeedbackPosted(comment.getInterviewer());
        return "redirect:/applications?messageCode=interview.feedback&application=" + applicationForm.getApplicationNumber();
    }

    private List<Question> getCustomQuestions(String applicationId) throws ScoringDefinitionParseException {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.INTERVIEW);
        if (scoringDefinition != null) {
            CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
            return customQuestion.getQuestion();
        }
        return null;
    }
    
}