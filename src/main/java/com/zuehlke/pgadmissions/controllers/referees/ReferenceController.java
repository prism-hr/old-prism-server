package com.zuehlke.pgadmissions.controllers.referees;

import java.util.Date;
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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping("/referee")
public class ReferenceController {

    private static final Logger log = LoggerFactory.getLogger(ReferenceController.class);
    private static final String ADD_REFERENCES_VIEW_NAME = "private/referees/upload_references";
    private final ApplicationsService applicationsService;
    private final DocumentPropertyEditor documentPropertyEditor;
    private final FeedbackCommentValidator referenceValidator;
    private final RefereeService refereeService;
    private final UserService userService;
    private final CommentService commentService;
    private final ScoringDefinitionParser scoringDefinitionParser;
    private final ScoresPropertyEditor scoresPropertyEditor;
    private final ScoreFactory scoreFactory;
    private final ApplicationFormAccessService accessService;
    private final ActionsProvider actionsProvider;

    ReferenceController() {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ReferenceController(ApplicationsService applicationsService, RefereeService refereeService, UserService userService,
            DocumentPropertyEditor documentPropertyEditor, FeedbackCommentValidator referenceValidator, CommentService commentService,
            ScoringDefinitionParser scoringDefinitionParser, ScoresPropertyEditor scoresPropertyEditor, ScoreFactory scoreFactory,
            final ApplicationFormAccessService accessService, ActionsProvider actionsProvider) {
        this.applicationsService = applicationsService;
        this.refereeService = refereeService;
        this.userService = userService;
        this.documentPropertyEditor = documentPropertyEditor;
        this.referenceValidator = referenceValidator;
        this.commentService = commentService;
        this.scoringDefinitionParser = scoringDefinitionParser;
        this.scoresPropertyEditor = scoresPropertyEditor;
        this.scoreFactory = scoreFactory;
        this.accessService = accessService;
        this.actionsProvider = actionsProvider;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        RegisteredUser currentUser = getCurrentUser();

        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.isRefereeOfApplicationForm(applicationForm)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        if (applicationForm.isDecided() || applicationForm.isWithdrawn() || currentUser.getRefereeForApplicationForm(applicationForm).hasResponded()) {
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
        return applicationForm;
    }

    @ModelAttribute("actionsDefinition")
    public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId) {
        ApplicationForm application = getApplicationForm(applicationId);
        return actionsProvider.calculateActions(getCurrentUser(), application);
    }

    @ModelAttribute("user")
    RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("comment")
    public ReferenceComment getComment(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser currentUser = getCurrentUser();
        Referee refereeForApplicationForm = currentUser.getRefereeForApplicationForm(applicationForm);

        ReferenceComment referenceComment = new ReferenceComment();
        referenceComment.setApplication(applicationForm);
        referenceComment.setUser(currentUser);
        referenceComment.setComment("");
        referenceComment.setType(CommentType.REFERENCE);
        referenceComment.setReferee(refereeForApplicationForm);

        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REFERENCE);
        if (scoringDefinition != null) {
            try {
                CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
                List<Score> scores = scoreFactory.createScores(customQuestion.getQuestion());
                referenceComment.getScores().addAll(scores);
                referenceComment.setAlert(customQuestion.getAlert());
            } catch (ScoringDefinitionParseException e) {
                log.error("Incorrect scoring XML configuration for reference stage in program: " + applicationForm.getProgram().getTitle());
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
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.ADD_REFERENCE);
        return ADD_REFERENCES_VIEW_NAME;
    }

    @RequestMapping(value = "/submitReference", method = RequestMethod.POST)
    public String handleReferenceSubmission(@ModelAttribute("comment") ReferenceComment comment,
            BindingResult bindingResult, ModelMap modelMap) throws ScoringDefinitionParseException {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.ADD_REFERENCE);
        
        List<Score> scores = comment.getScores();
        if (!scores.isEmpty()) {
            List<Question> questions = getCustomQuestions(applicationForm);
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                score.setOriginalQuestion(questions.get(i));
            }
        }

        referenceValidator.validate(comment, bindingResult);
        accessService.updateAccessTimestamp(applicationForm, getCurrentUser(), new Date());
        if (bindingResult.hasErrors()) {
            return ADD_REFERENCES_VIEW_NAME;
        }

        if (comment.getReferee().getReference() == null) { // check if the reference isn't already submitted
            commentService.save(comment);
            refereeService.saveReferenceAndSendMailNotifications(comment.getReferee());
        }

        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
        applicationsService.save(applicationForm);
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
