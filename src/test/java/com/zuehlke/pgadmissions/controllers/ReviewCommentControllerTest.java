package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;

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
    private ApplicantRatingService applicantRatingServiceMock;

    @Test
    public void shouldGetApplicationFormFromId() {
        ApplicationForm applicationForm = new ApplicationForm();

        expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);

        replay(applicationsServiceMock);
        ApplicationForm returnedApplication = controller.getApplicationForm("5");
        verify(applicationsServiceMock);

        assertEquals(returnedApplication, applicationForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionIfApplicationFormDoesNotExist() {
        expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
        replay(applicationsServiceMock);
        controller.getApplicationForm("5");
    }

    @Test
    public void shouldReturnGenericCommentPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        RegisteredUser user = new RegisteredUser();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_REVIEW);

        replay(actionsProviderMock);
        assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.getReviewFeedbackPage(modelMap));
        verify(actionsProviderMock);
    }

    @Test
    public void shouldReturnCurrentUser() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(8).build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        replay(userServiceMock);
        assertEquals(currentUser, controller.getUser());
    }

    @Test
    public void shouldCreateNewReviewCommentForApplicationForm() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        final RegisteredUser currentUser = createMock(RegisteredUser.class);
        final Reviewer reviewer = new ReviewerBuilder().id(5).build();

        final Question question1 = new Question();
        question1.setLabel("question1");
        question1.setType(QuestionType.RATING);
        final CustomQuestions customQuestions = new CustomQuestions();
        customQuestions.getQuestion().add(question1);
        ArrayList<Score> generatedScores = Lists.newArrayList(new Score());

        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        expect(applicationsServiceMock.getApplicationByApplicationNumber("app")).andReturn(applicationForm);
        expect(currentUser.getReviewerForCurrentUserFromLatestReviewRound(applicationForm)).andReturn(reviewer);
        expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
        expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);

        replay(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock, applicationsServiceMock);
        ReviewComment comment = controller.getComment("app");
        verify(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock, applicationsServiceMock);

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
        final RegisteredUser currentUser = createMock(RegisteredUser.class);
        final Reviewer reviewer = new ReviewerBuilder().id(5).build();

        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        expect(applicationsServiceMock.getApplicationByApplicationNumber("app")).andReturn(applicationForm);
        expect(currentUser.getReviewerForCurrentUserFromLatestReviewRound(applicationForm)).andReturn(reviewer);
        expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));

        replay(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock, applicationsServiceMock);
        ReviewComment comment = controller.getComment("app");
        verify(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock, applicationsServiceMock);

        assertNull(comment.getId());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUser, comment.getUser());
        assertEquals(CommentType.REVIEW, comment.getType());
        assertEquals(reviewer, comment.getReviewer());
        assertEquals(0, comment.getScores().size());
    }

    @Test
    public void shouldRegisterValidator() {
        WebDataBinder binderMock = createMock(WebDataBinder.class);
        binderMock.setValidator(reviewFeedbackValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor(null, "scores", scoresPropertyEditorMock);
        replay(binderMock);
        controller.registerBinders(binderMock);
        verify(binderMock);
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
        applicantRatingServiceMock.computeAverageRating(reviewRound);
        applicantRatingServiceMock.computeAverageRating(applicationForm);
        accessServiceMock.reviewPosted(reviewer);

        replay(commentServiceMock, applicantRatingServiceMock, accessServiceMock);
        assertEquals("redirect:/applications?messageCode=review.feedback&application=" + applicationForm.getApplicationNumber(),
                controller.addComment(comment, result, modelMap));
        verify(commentServiceMock, applicantRatingServiceMock, accessServiceMock);

        Assert.assertSame(comment, reviewer.getReview());
        Assert.assertThat(applicationForm.getApplicationComments(), Matchers.<Comment> contains(comment));
    }

    @Before
    public void setUp() {
        applicationsServiceMock = createMock(ApplicationsService.class);
        userServiceMock = createMock(UserService.class);
        reviewFeedbackValidatorMock = createMock(FeedbackCommentValidator.class);
        commentServiceMock = createMock(CommentService.class);
        documentPropertyEditorMock = createMock(DocumentPropertyEditor.class);
        scoringDefinitionParserMock = createMock(ScoringDefinitionParser.class);
        scoresPropertyEditorMock = createMock(ScoresPropertyEditor.class);
        scoreFactoryMock = createMock(ScoreFactory.class);
        accessServiceMock = createMock(ApplicationFormAccessService.class);
        actionsProviderMock = createMock(ActionsProvider.class);
        applicantRatingServiceMock = createMock(ApplicantRatingService.class);

        controller = new ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock, scoringDefinitionParserMock, scoresPropertyEditorMock, scoreFactoryMock, accessServiceMock, actionsProviderMock,
                applicantRatingServiceMock);

    }
}
