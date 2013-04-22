package com.zuehlke.pgadmissions.controllers;

import java.util.List;

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

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping(value = { "/reviewFeedback" })
public class ReviewCommentController {

    private static final String REVIEW_FEEDBACK_PAGE = "private/staff/reviewer/feedback/reviewcomment";
    private final ApplicationsService applicationsService;
    private final UserService userService;
    private final FeedbackCommentValidator reviewFeedbackValidator;
    private final CommentService commentService;
    private final DocumentPropertyEditor documentPropertyEditor;
    private final ScoringDefinitionParser scoringDefinitionParser;
    private final ScoresPropertyEditor scoresPropertyEditor;

    ReviewCommentController() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public ReviewCommentController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            FeedbackCommentValidator reviewFeedbackValidator, DocumentPropertyEditor documentPropertyEditor, ScoringDefinitionParser scoringDefinitionParser, ScoresPropertyEditor scoresPropertyEditor) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.commentService = commentService;
        this.reviewFeedbackValidator = reviewFeedbackValidator;
        this.documentPropertyEditor = documentPropertyEditor;
        this.scoringDefinitionParser = scoringDefinitionParser;
        this.scoresPropertyEditor = scoresPropertyEditor;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        RegisteredUser currentUser = userService.getCurrentUser();
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm) || !currentUser.canSee(applicationForm)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        if (applicationForm.isDecided() || applicationForm.isWithdrawn() || getUser().hasRespondedToProvideReviewForApplicationLatestRound(applicationForm)) {
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
        return applicationForm;
    }

    @ModelAttribute("actionsDefinition")
    public ApplicationActionsDefinition getActionsDefinition(@RequestParam String applicationId) {
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.getActionsDefinition(getUser(), application);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getReviewFeedbackPage() {
        return REVIEW_FEEDBACK_PAGE;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("comment")
    public ReviewComment getComment(@RequestParam String applicationId) throws ScoringDefinitionParseException {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setApplication(applicationForm);
        reviewComment.setUser(user);
        reviewComment.setComment("");
        reviewComment.setType(CommentType.REVIEW);
        reviewComment.setReviewer(user.getReviewerForCurrentUserFromLatestReviewRound(applicationForm));

        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REVIEW);
        CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
        List<Score> scores = createScores(customQuestion.getQuestion());

        reviewComment.getScores().addAll(scores);
        return reviewComment;
    }

    @ModelAttribute("customQuestions")
    public List<Question> getCustomQuestions(@RequestParam String applicationId) throws ScoringDefinitionParseException {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REVIEW);
        CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
        return customQuestion.getQuestion();
    }

    @InitBinder(value = "comment")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(reviewFeedbackValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(null, "scores", scoresPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addComment(@Valid @ModelAttribute("comment") ReviewComment comment, BindingResult result) {
        ApplicationForm applicationForm = comment.getApplication();
        if (result.hasErrors()) {
            return REVIEW_FEEDBACK_PAGE;
        }
        commentService.save(comment);
        return "redirect:/applications?messageCode=review.feedback&application=" + applicationForm.getApplicationNumber();
    }

    public List<Score> createScores(List<Question> questions) throws ScoringDefinitionParseException {
        List<Score> scores = Lists.newArrayListWithExpectedSize(questions.size());

        for (Question question : questions) {
            Score score = new Score();
            score.setQuestion(question.getLabel());
            score.setQuestionType(question.getType());
            scores.add(score);
        }
        return scores;
    }
}
