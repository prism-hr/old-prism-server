package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoringDefinitionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;
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

    @Test
    public void shouldGetApplicationFormFromId() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();

        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.hasRespondedToProvideReviewForApplicationLatestRound(applicationForm)).andReturn(false);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);

        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
        ApplicationForm returnedApplication = controller.getApplicationForm("5");
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);

        assertEquals(returnedApplication, applicationForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("5");
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfCurrentUserNotReviewerOfForm() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(false);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
        controller.getApplicationForm("5");
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfCurrentUserCannotSeeApplication() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();

        EasyMock.expect(currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
        controller.getApplicationForm("5");
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldThrowExceptionIfReviewerAlreadyResponded() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();

        EasyMock.expect(currentUser.hasRespondedToProvideReviewForApplicationLatestRound(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
        controller.getApplicationForm("5");
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldThrowExceptionIfApplicationIsDecided() {
        Program program = new ProgramBuilder().id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).status(ApplicationFormStatus.APPROVED).build();

        RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();

        EasyMock.expect(currentUser.hasRespondedToProvideReviewForApplicationLatestRound(applicationForm)).andReturn(false);
        EasyMock.expect(currentUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
        controller.getApplicationForm("5");
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);
    }

    @Test
    public void shouldReturnGenericCommentPage() {
        assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.getReviewFeedbackPage());
    }

    @Test
    public void shouldReturnCurrentUser() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(8).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        assertEquals(currentUser, controller.getUser());
    }

    @Test
    public void shouldCreateNewReviewCommentForApplicationForm() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        final RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        final Reviewer reviewer = new ReviewerBuilder().id(5).build();

        final Question question1 = new Question();
        question1.setLabel("question1");
        question1.setType(QuestionType.RATING);
        final CustomQuestions customQuestions = new CustomQuestions();
        customQuestions.getQuestion().add(question1);
        ArrayList<Score> generatedScores = Lists.newArrayList(new Score());

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.getReviewerForCurrentUserFromLatestReviewRound(applicationForm)).andReturn(reviewer);
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
        EasyMock.expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);
        controller = new ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock, scoringDefinitionParserMock, scoresPropertyEditorMock, scoreFactoryMock) {

            @Override
            public ApplicationForm getApplicationForm(String id) {
                return applicationForm;
            }

        };

        EasyMock.replay(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock);
        ReviewComment comment = controller.getComment("5");
        EasyMock.verify(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock);

        assertNull(comment.getId());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUser, comment.getUser());
        assertEquals(CommentType.REVIEW, comment.getType());
        assertEquals(reviewer, comment.getReviewer());
        assertEquals(generatedScores, comment.getScores());
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

        controller = new ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock, scoringDefinitionParserMock, scoresPropertyEditorMock, null) {

            @Override
            public ApplicationForm getApplicationForm(String id) {
                return application;
            }

        };

        assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.addComment(comment, result));
    }

    @Test
    public void shouldSaveCommentAndRedirectApplicationsPageIfNoErrors() throws ScoringDefinitionParseException {
        Program program = new Program();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(6).program(program).build();
        ReviewComment comment = new ReviewCommentBuilder().id(1).application(applicationForm).build();
        BindingResult result = new BeanPropertyBindingResult(comment, "comment");

        controller = new ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock, scoringDefinitionParserMock, scoresPropertyEditorMock, null) {

            @Override
            public ApplicationForm getApplicationForm(String id) {
                return applicationForm;
            }

        };

        commentServiceMock.save(comment);

        EasyMock.replay(commentServiceMock);
        assertEquals("redirect:/applications?messageCode=review.feedback&application=" + applicationForm.getApplicationNumber(),
                controller.addComment(comment, result));
        EasyMock.verify(commentServiceMock);
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
        controller = new ReviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock, scoringDefinitionParserMock, scoresPropertyEditorMock, scoreFactoryMock);

    }
}
