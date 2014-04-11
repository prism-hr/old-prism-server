package com.zuehlke.pgadmissions.controllers.referees;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping("/referee")
public class ReferenceController {
    // TODO fix tests

    private static final Logger log = LoggerFactory.getLogger(ReferenceController.class);
    private static final String ADD_REFERENCES_VIEW_NAME = "private/referees/upload_references";

    @Autowired
    private ApplicationFormService applicationsService;

    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    private FeedbackCommentValidator referenceValidator;

    @Autowired
    private RefereeService refereeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ScoringDefinitionParser scoringDefinitionParser;

    @Autowired
    private ScoresPropertyEditor scoresPropertyEditor;

    @Autowired
    private ScoreFactory scoreFactory;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private ActionService actionService;

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
        User user = getCurrentUser();
        return applicationsService.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("user")
    User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("comment")
    public ReferenceComment getComment(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        User currentUser = getCurrentUser();

        ReferenceComment referenceComment = new ReferenceComment();
        referenceComment.setApplication(applicationForm);
        referenceComment.setUser(currentUser);
        referenceComment.setContent("");

        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REFERENCE);
        if (scoringDefinition != null) {
            try {
                CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
                List<Score> scores = scoreFactory.createScores(customQuestion.getQuestion());
                referenceComment.getScores().addAll(scores);
                referenceComment.setAlert(customQuestion.getAlert());
            } catch (ScoringDefinitionParseException e) {
                log.error("Incorrect scoring XML configuration for reference stage in program: " + applicationForm.getAdvert().getTitle());
            }
        }

        return referenceComment;
    }

    @InitBinder("comment")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(referenceValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(null, "scores", scoresPropertyEditor);
    }

    @RequestMapping(value = "/addReferences", method = RequestMethod.GET)
    public String getUploadReferencesPage(ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_REFERENCE);
        applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, getCurrentUser());
        return ADD_REFERENCES_VIEW_NAME;
    }

    @RequestMapping(value = "/submitReference", method = RequestMethod.POST)
    public String handleReferenceSubmission(@ModelAttribute("comment") ReferenceComment comment, BindingResult bindingResult, ModelMap modelMap)
            throws ScoringDefinitionParseException {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_REFERENCE);

        List<Score> scores = comment.getScores();
        if (!scores.isEmpty()) {
            List<Question> questions = getCustomQuestions(applicationForm);
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                score.setOriginalQuestion(questions.get(i));
            }
        }

        referenceValidator.validate(comment, bindingResult);

        if (bindingResult.hasErrors()) {
            return ADD_REFERENCES_VIEW_NAME;
        }

        commentService.save(comment);
        applicationForm.getApplicationComments().add(comment);
        applicationFormUserRoleService.referencePosted(comment);

        applicationsService.save(applicationForm);
        applicationFormUserRoleService.applicationUpdated(applicationForm, user);
        return "redirect:/applications?messageCode=reference.uploaded&application=" + comment.getApplication().getApplicationNumber();
    }

    private List<Question> getCustomQuestions(ApplicationForm applicationForm) throws ScoringDefinitionParseException {
        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REFERENCE);
        if (scoringDefinition != null) {
            CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
            return customQuestion.getQuestion();
        }
        return null;
    }

}