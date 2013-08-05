package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoringDefinitionBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;
import com.zuehlke.pgadmissions.services.ApplicantRatingService;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

public class InterviewCommentControllerTest {

    private ApplicationsService applicationsServiceMock;
    private UserService userServiceMock;
    private InterviewCommentController controller;
    private FeedbackCommentValidator reviewFeedbackValidatorMock;
    private CommentService commentServiceMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private ScoringDefinitionParser scoringDefinitionParserMock;
    private ScoresPropertyEditor scoresPropertyEditorMock;
    private ScoreFactory scoreFactoryMock;
    private ActionsProvider actionsProviderMock;
    private ApplicationFormAccessService accessServiceMock;
    private ApplicantRatingService applicantRatingService;

    @Test
    public void shouldGetApplicationFormFromId() {
        ApplicationForm applicationForm = new ApplicationForm();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);

        EasyMock.replay(applicationsServiceMock);
        ApplicationForm returnedApplication = controller.getApplicationForm("5");
        EasyMock.verify(applicationsServiceMock);

        assertEquals(returnedApplication, applicationForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);

        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");
        EasyMock.verify(applicationsServiceMock);
    }

    @Test
    public void shouldReturnGenericCommentPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        RegisteredUser user = new RegisteredUser();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, ApplicationFormAction.ADD_INTERVIEW_FEEDBACK);

        EasyMock.replay(actionsProviderMock);
        assertEquals("private/staff/interviewers/feedback/interview_feedback", controller.getInterviewFeedbackPage(modelMap));
        EasyMock.verify(actionsProviderMock);
    }

    @Test
    public void shouldReturnCurrentUser() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(8).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        assertEquals(currentUser, controller.getUser());
    }

    @Test
    public void shouldCreateNewInterviewCommentForApplicationForm() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.INTERVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.INTERVIEW, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).build();
        final RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        final ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", currentUser);
        final Interviewer interviewer = new InterviewerBuilder().id(5).build();

        final Question question1 = new Question();
        question1.setLabel("question1");
        question1.setType(QuestionType.RATING);
        final CustomQuestions customQuestions = new CustomQuestions();
        customQuestions.getQuestion().add(question1);
        ArrayList<Score> generatedScores = Lists.newArrayList(new Score());

        EasyMock.expect(currentUser.getInterviewersForApplicationForm(applicationForm)).andReturn(Arrays.asList(interviewer));
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
        EasyMock.expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);

        EasyMock.replay(currentUser, scoringDefinitionParserMock, scoreFactoryMock);
        InterviewComment comment = controller.getComment(modelMap);
        EasyMock.verify(currentUser, scoringDefinitionParserMock, scoreFactoryMock);

        assertNull(comment.getId());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUser, comment.getUser());
        assertEquals(CommentType.INTERVIEW, comment.getType());
        assertEquals(interviewer, comment.getInterviewer());
        assertEquals(generatedScores, comment.getScores());
    }

    @Test
    public void shouldNotApplyScoringConfigurationIfParseException() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.INTERVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.INTERVIEW, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).build();
        final RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        final ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", currentUser);
        final Interviewer interviewer = new InterviewerBuilder().id(5).build();

        EasyMock.expect(currentUser.getInterviewersForApplicationForm(applicationForm)).andReturn(Arrays.asList(interviewer));
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));

        EasyMock.replay(currentUser, scoringDefinitionParserMock);
        InterviewComment comment = controller.getComment(modelMap);
        EasyMock.verify(currentUser, scoringDefinitionParserMock);

        assertNull(comment.getId());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUser, comment.getUser());
        assertEquals(CommentType.INTERVIEW, comment.getType());
        assertEquals(interviewer, comment.getInterviewer());
    }

    @Test
    public void shouldRegisterValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(reviewFeedbackValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor(null, "scores", scoresPropertyEditorMock);
        EasyMock.replay(binderMock);
        controller.registerBinders(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldReturnToCommentsPageIfErrors() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).build();
        InterviewComment comment = new InterviewCommentBuilder().application(applicationForm).build();
        BindingResult errorsMock = new BeanPropertyBindingResult(comment, "comment");
        errorsMock.reject("error");

        assertEquals("private/staff/interviewers/feedback/interview_feedback", controller.addComment(comment, errorsMock, new ModelMap()));
    }

    @Test
    public void shouldSaveCommentAndResetAppAdminAndToApplicationListIfNoErrors() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
        ApplicationForm application = new ApplicationFormBuilder().id(6).applicationNumber("abc").applicationAdministrator(new RegisteredUser())
                .program(program).build();
        Interview interview = new Interview();
        Interviewer interviewer = new InterviewerBuilder().interview(interview).build();
        InterviewComment comment = new InterviewCommentBuilder().id(1).application(application).interviewer(interviewer).build();
        BindingResult errorsMock = new BeanPropertyBindingResult(comment, "comment");
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);

        applicationsServiceMock.save(application);
        commentServiceMock.save(comment);
        applicantRatingService.computeAverageRating(interview);
        applicantRatingService.computeAverageRating(application);

        EasyMock.replay(commentServiceMock, applicationsServiceMock, applicantRatingService);
        assertEquals("redirect:/applications?messageCode=interview.feedback&application=abc", controller.addComment(comment, errorsMock, modelMap));
        EasyMock.verify(commentServiceMock, applicationsServiceMock, applicantRatingService);
        Assert.assertSame(comment, interviewer.getInterviewComment());
    }

    @Before
    public void setUp() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        reviewFeedbackValidatorMock = EasyMock.createMock(FeedbackCommentValidator.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        scoringDefinitionParserMock = EasyMock.createMock(ScoringDefinitionParser.class);
        scoresPropertyEditorMock = EasyMock.createMock(ScoresPropertyEditor.class);
        scoreFactoryMock = EasyMock.createMock(ScoreFactory.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        applicantRatingService = EasyMock.createMock(ApplicantRatingService.class);
        controller = new InterviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock, scoringDefinitionParserMock, scoresPropertyEditorMock, scoreFactoryMock, actionsProviderMock, accessServiceMock,
                null, applicantRatingService);

    }
}
