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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping(value = { "/reviewFeedback" })
public class ReviewCommentController {
    // TODO fix tests

    private static final Logger log = LoggerFactory.getLogger(ReviewCommentController.class);
    private static final String REVIEW_FEEDBACK_PAGE = "private/staff/reviewer/feedback/reviewcomment";
    
    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private FeedbackCommentValidator reviewFeedbackValidator;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;
    
    @Autowired
    private ScoringDefinitionParser scoringDefinitionParser;
    
    @Autowired
    private ScoresPropertyEditor scoresPropertyEditor;
    
    @Autowired
    private ScoreFactory scoreFactory;
    
    @Autowired
    private ApplicationFormUserRoleService ApplicationFormUserRoleService;
    
    @Autowired
    private ActionsProvider actionsProvider;

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return applicationForm;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
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
        reviewComment.setContent("");
        reviewComment.setUser(user);

        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REVIEW);
        if (scoringDefinition != null) {
            try {
                CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
                List<Score> scores = scoreFactory.createScores(customQuestion.getQuestion());
                reviewComment.getScores().addAll(scores);
                reviewComment.setAlert(customQuestion.getAlert());
            } catch (ScoringDefinitionParseException e) {
                log.error("Incorrect scoring XML configuration for review stage in program: " + applicationForm.getAdvert().getTitle());
            }
        }

        return reviewComment;
    }

    @InitBinder(value = "comment")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(reviewFeedbackValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(null, "scores", scoresPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getReviewFeedbackPage(ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_REVIEW);
        ApplicationFormUserRoleService.deleteApplicationUpdate(applicationForm, user);
        return REVIEW_FEEDBACK_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addComment(@ModelAttribute("comment") ReviewComment comment, BindingResult result, ModelMap modelMap) throws ScoringDefinitionParseException {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_REVIEW);

        List<Score> scores = comment.getScores();
        if (!scores.isEmpty()) {
            List<Question> questions = getCustomQuestions(applicationForm.getApplicationNumber());
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                score.setOriginalQuestion(questions.get(i));
            }
        }

        reviewFeedbackValidator.validate(comment, result);

        if (result.hasErrors()) {
            return REVIEW_FEEDBACK_PAGE;
        }
        
        applicationsService.save(applicationForm);
        commentService.save(comment);
        applicationForm.getApplicationComments().add(comment);
        ApplicationFormUserRoleService.reviewPosted(comment);
        ApplicationFormUserRoleService.insertApplicationUpdate(applicationForm, user, ApplicationUpdateScope.INTERNAL);

        return "redirect:/applications?messageCode=review.feedback&application=" + applicationForm.getApplicationNumber();
    }

    private List<Question> getCustomQuestions(@RequestParam String applicationId) throws ScoringDefinitionParseException {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REVIEW);
        if (scoringDefinition != null) {
            CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
            return customQuestion.getQuestion();
        }
        return null;
    }

}