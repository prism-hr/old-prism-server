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

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping(value = { "/reviewFeedback" })
public class ReviewCommentController {
    // TODO fix tests

    private static final String REVIEW_FEEDBACK_PAGE = "private/staff/reviewer/feedback/reviewcomment";

    @Autowired
    private ApplicationService applicationsService;

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
    private WorkflowService WorkflowService;

    @Autowired
    private ActionService actionService;

    @ModelAttribute("applicationForm")
    public Application getApplicationForm(@RequestParam String applicationId) {
        Application applicationForm = applicationsService.getByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new ResourceNotFoundException(applicationId);
        }
        return applicationForm;
    }

    @ModelAttribute("applicationDescriptor")
    public Application getApplicationDescriptor(@RequestParam String applicationId) {
        Application applicationForm = getApplicationForm(applicationId);
        User user = getUser();
        return applicationsService.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("comment")
    public ReviewComment getComment(@RequestParam String applicationId) throws ScoringDefinitionParseException {
        Application applicationForm = getApplicationForm(applicationId);
        User user = getUser();
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setApplication(applicationForm);
        reviewComment.setUser(user);
        reviewComment.setContent("");
        reviewComment.setUser(user);

        return reviewComment;
    }

    @InitBinder(value = "comment")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(reviewFeedbackValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getReviewFeedbackPage(ModelMap modelMap) {
        Application applicationForm = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, SystemAction.APPLICATION_PROVIDE_REVIEW);
        WorkflowService.deleteApplicationUpdate(applicationForm, user);
        return REVIEW_FEEDBACK_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addComment(@ModelAttribute("comment") ReviewComment comment, BindingResult result, ModelMap modelMap) throws ScoringDefinitionParseException {
        Application applicationForm = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, SystemAction.APPLICATION_PROVIDE_REVIEW);

        reviewFeedbackValidator.validate(comment, result);

        if (result.hasErrors()) {
            return REVIEW_FEEDBACK_PAGE;
        }

        applicationsService.save(applicationForm);
        commentService.save(comment);
        applicationForm.getApplicationComments().add(comment);
        WorkflowService.reviewPosted(comment);
        WorkflowService.applicationUpdated(applicationForm, user);

        return "redirect:/applications?messageCode=review.feedback&application=" + applicationForm.getCode();
    }

    private List<Question> getCustomQuestions(@RequestParam String applicationId) throws ScoringDefinitionParseException {
        Application applicationForm = getApplicationForm(applicationId);
        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REVIEW);
        if (scoringDefinition != null) {
            CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
            return customQuestion.getQuestion();
        }
        return null;
    }

}