package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;

import org.easymock.EasyMock;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.components.ApplicationDescriptorProvider;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
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

public class ReviewCommentControllerTest {

    private ApplicationsService applicationsServiceMock;
    private UserService userServiceMock;
    private ReviewCommentController controller;
    private FeedbackCommentValidator reviewFeedbackValidatorMock;
    private CommentService commentServiceMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private ScoringDefinitionParser scoringDefinitionParserMock;
    private ScoresPropertyEditor scoresPropertyEditorMock;
    private ScoreFactory scoreFactoryMock;
    private ApplicationFormAccessService accessServiceMock;
    private ActionsProvider actionsProviderMock;
    private ApplicationDescriptorProvider applicationDescriptorProviderMock;
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
    }

    @Test
    public void shouldReturnGenericCommentPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        RegisteredUser user = new RegisteredUser();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, ApplicationFormAction.ADD_REVIEW);

        EasyMock.replay(actionsProviderMock);
        assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.getReviewFeedbackPage(modelMap));
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
    public void shouldCreateNewReviewCommentForApplicationForm() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        final RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        final ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", currentUser);
        final Reviewer reviewer = new ReviewerBuilder().id(5).build();

        final Question question1 = new Question();
        question1.setLabel("question1");
        question1.setType(QuestionType.RATING);
        final CustomQuestions customQuestions = new CustomQuestions();
        customQuestions.getQuestion().add(question1);
        ArrayList<Score> generatedScores = Lists.newArrayList(new Score());

        EasyMock.expect(currentUser.getReviewerForCurrentUserFromLatestReviewRound(applicationForm)).andReturn(reviewer);
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
        EasyMock.expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);

        EasyMock.replay(scoringDefinitionParserMock, currentUser, scoreFactoryMock);
        ReviewComment comment = controller.getComment(modelMap);
        EasyMock.verify(scoringDefinitionParserMock, currentUser, scoreFactoryMock);

        assertNull(comment.getId());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUser, comment.getUser());
        assertEquals(CommentType.REVIEW, comment.getType());
        assertEquals(reviewer, comment.getReviewer());
        assertEquals(generatedScores, comment.getScores());
    }

    @Test
    public void shouldNotApplyScoringConfigurationIfParseException() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        final RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        final ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", currentUser);
        final Reviewer reviewer = new ReviewerBuilder().id(5).build();

        EasyMock.expect(currentUser.getReviewerForCurrentUserFromLatestReviewRound(applicationForm)).andReturn(reviewer);
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));

        EasyMock.replay(scoringDefinitionParserMock, currentUser, scoreFactoryMock);
        ReviewComment comment = controller.getComment(modelMap);
        EasyMock.verify(scoringDefinitionParserMock, currentUser, scoreFactoryMock);

        assertNull(comment.getId());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUser, comment.getUser());
        assertEquals(CommentType.REVIEW, comment.getType());
        assertEquals(reviewer, comment.getReviewer());
        assertEquals(0, comment.getScores().size());
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
        Program program = new Program();
        final ApplicationForm application = new ApplicationFormBuilder().program(program).applicationNumber("5").build();
        ReviewComment comment = new ReviewCommentBuilder().application(application).build();
        BindingResult result = new BeanPropertyBindingResult(comment, "comment");
        result.reject("error");

        assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.addComment(comment, result, new ModelMap()));
    }

    @Test
    public void shouldSaveCommentAndRedirectApplicationsPageIfNoErrors() throws ScoringDefinitionParseException {
        Program program = new Program();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(6).program(program).build();
        ReviewRound reviewRound = new ReviewRound();
        Reviewer reviewer = new ReviewerBuilder().reviewRound(reviewRound).build();
        ReviewComment comment = new ReviewCommentBuilder().id(1).application(applicationForm).reviewer(reviewer).build();
        BindingResult result = new BeanPropertyBindingResult(comment, "comment");
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);

        commentServiceMock.save(comment);
        applicantRatingService.computeAverageRating(reviewRound);
        applicantRatingService.computeAverageRating(applicationForm);

        EasyMock.replay(commentServiceMock, applicantRatingService);
        assertEquals("redirect:/applications?messageCode=review.feedback&application=" + applicationForm.getApplicationNumber(),
                controller.addComment(comment, result, modelMap));
        EasyMock.verify(commentServiceMock, applicantRatingService);
        
        Assert.assertSame(comment, reviewer.getReview());
        Assert.assertThat(applicationForm.getApplicationComments(), Matchers.<Comment> contains(comment));
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
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        applicationDescriptorProviderMock = EasyMock.createMock(ApplicationDescriptorProvider.class);
        applicantRatingService = EasyMock.createMock(ApplicantRatingService.class);

        controller = new ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock, scoringDefinitionParserMock, scoresPropertyEditorMock, scoreFactoryMock, accessServiceMock, actionsProviderMock,
                applicationDescriptorProviderMock, applicantRatingService);

    }
}
