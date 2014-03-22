package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;
import com.zuehlke.pgadmissions.services.ApplicantRatingService;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

public class InterviewCommentControllerTest {

    private ApplicationFormService applicationsServiceMock;
    private UserService userServiceMock;
    private InterviewCommentController controller;
    private FeedbackCommentValidator reviewFeedbackValidatorMock;
    private CommentService commentServiceMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private ScoringDefinitionParser scoringDefinitionParserMock;
    private ScoresPropertyEditor scoresPropertyEditorMock;
    private ScoreFactory scoreFactoryMock;
    private ActionsProvider actionsProviderMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;
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
        verify(applicationsServiceMock);
    }

    @Test
    public void shouldReturnGenericCommentPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        RegisteredUser user = new RegisteredUser();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_INTERVIEW_FEEDBACK);

        replay(actionsProviderMock);
        assertEquals("private/staff/interviewers/feedback/interview_feedback", controller.getInterviewFeedbackPage(modelMap));
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
    public void shouldCreateNewInterviewCommentForApplicationForm() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.INTERVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.INTERVIEW, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().advert(program).build();
        final RegisteredUser currentUser = createMock(RegisteredUser.class);
        final Interviewer interviewer = new InterviewerBuilder().id(5).build();

        final Question question1 = new Question();
        question1.setLabel("question1");
        question1.setType(QuestionType.RATING);
        final CustomQuestions customQuestions = new CustomQuestions();
        customQuestions.getQuestion().add(question1);
        ArrayList<Score> generatedScores = Lists.newArrayList(new Score());

        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        expect(applicationsServiceMock.getApplicationByApplicationNumber("app")).andReturn(applicationForm);
        expect(currentUser.getInterviewersForApplicationForm(applicationForm)).andReturn(Arrays.asList(interviewer));
        expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
        expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);

        replay(userServiceMock, currentUser, scoringDefinitionParserMock, scoreFactoryMock, applicationsServiceMock);
        InterviewComment comment = controller.getComment("app");
        verify(userServiceMock, currentUser, scoringDefinitionParserMock, scoreFactoryMock, applicationsServiceMock);

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
        final ApplicationForm applicationForm = new ApplicationFormBuilder().advert(program).build();
        final RegisteredUser currentUser = createMock(RegisteredUser.class);
        final Interviewer interviewer = new InterviewerBuilder().id(5).build();

        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        expect(applicationsServiceMock.getApplicationByApplicationNumber("app")).andReturn(applicationForm);
        expect(currentUser.getInterviewersForApplicationForm(applicationForm)).andReturn(Arrays.asList(interviewer));
        expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));

        replay(userServiceMock, currentUser, scoringDefinitionParserMock, applicationsServiceMock);
        InterviewComment comment = controller.getComment("app");
        verify(userServiceMock, currentUser, scoringDefinitionParserMock, applicationsServiceMock);

        assertNull(comment.getId());
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUser, comment.getUser());
        assertEquals(CommentType.INTERVIEW, comment.getType());
        assertEquals(interviewer, comment.getInterviewer());
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
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().advert(program).build();
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
                .advert(program).build();
        Interview interview = new Interview();
        Interviewer interviewer = new InterviewerBuilder().interview(interview).build();
        InterviewComment comment = new InterviewCommentBuilder().id(1).application(application).interviewer(interviewer).build();
        BindingResult errorsMock = new BeanPropertyBindingResult(comment, "comment");
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);

        applicationsServiceMock.save(application);
        commentServiceMock.save(comment);
        applicantRatingServiceMock.computeAverageRating(interview);
        applicantRatingServiceMock.computeAverageRating(application);
        applicationFormUserRoleServiceMock.interviewFeedbackPosted(interviewer);
        applicationFormUserRoleServiceMock.insertApplicationUpdate(application, userServiceMock.getCurrentUser(), ApplicationUpdateScope.INTERNAL);

        replay(commentServiceMock, applicationsServiceMock, applicantRatingServiceMock, applicationFormUserRoleServiceMock);
        assertEquals("redirect:/applications?messageCode=interview.feedback&application=abc", controller.addComment(comment, errorsMock, modelMap));
        verify(commentServiceMock, applicationsServiceMock, applicantRatingServiceMock, applicationFormUserRoleServiceMock);
        Assert.assertSame(comment, interviewer.getInterviewComment());
    }

    @Before
    public void setUp() {
        applicationsServiceMock = createMock(ApplicationFormService.class);
        userServiceMock = createMock(UserService.class);
        reviewFeedbackValidatorMock = createMock(FeedbackCommentValidator.class);
        commentServiceMock = createMock(CommentService.class);
        documentPropertyEditorMock = createMock(DocumentPropertyEditor.class);
        scoringDefinitionParserMock = createMock(ScoringDefinitionParser.class);
        scoresPropertyEditorMock = createMock(ScoresPropertyEditor.class);
        scoreFactoryMock = createMock(ScoreFactory.class);
        actionsProviderMock = createMock(ActionsProvider.class);
        applicationFormUserRoleServiceMock = createMock(ApplicationFormUserRoleService.class);
        applicantRatingServiceMock = createMock(ApplicantRatingService.class);
        controller = new InterviewCommentController(applicationsServiceMock, userServiceMock, commentServiceMock, reviewFeedbackValidatorMock,
                documentPropertyEditorMock, scoringDefinitionParserMock, scoresPropertyEditorMock, scoreFactoryMock, actionsProviderMock, applicationFormUserRoleServiceMock,
                applicantRatingServiceMock);

    }
}
