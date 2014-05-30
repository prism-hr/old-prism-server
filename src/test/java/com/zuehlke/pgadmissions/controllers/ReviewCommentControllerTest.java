package com.zuehlke.pgadmissions.controllers;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ReviewCommentControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationService applicationsService;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @Mock
    @InjectIntoByType
    private FeedbackCommentValidator reviewFeedbackValidator;

    @Mock
    @InjectIntoByType
    private CommentService commentService;

    @Mock
    @InjectIntoByType
    private DocumentPropertyEditor documentPropertyEditor;

    @Mock
    @InjectIntoByType
    private ScoringDefinitionParser scoringDefinitionParser;

    @Mock
    @InjectIntoByType
    private WorkflowService WorkflowService;

    @Mock
    @InjectIntoByType
    private ActionService actionService;

    @TestedObject
    private ReviewCommentController controller;

    // @Test
    // public void shouldGetApplicationFormFromId() {
    // ApplicationForm applicationForm = new ApplicationForm();
    //
    // expect(applicationsServiceMock.getByApplicationNumber("5")).andReturn(applicationForm);
    //
    // replay(applicationsServiceMock);
    // ApplicationForm returnedApplication = controller.getApplicationForm("5");
    // verify(applicationsServiceMock);
    //
    // assertEquals(returnedApplication, applicationForm);
    // }
    //
    // @Test(expected = MissingApplicationFormException.class)
    // public void shouldThrowExceptionIfApplicationFormDoesNotExist() {
    // expect(applicationsServiceMock.getByApplicationNumber("5")).andReturn(null);
    // replay(applicationsServiceMock);
    // controller.getApplicationForm("5");
    // }
    //
    // @Test
    // public void shouldReturnGenericCommentPage() {
    // ApplicationForm applicationForm = new ApplicationForm();
    // RegisteredUser user = new RegisteredUser();
    // ModelMap modelMap = new ModelMap();
    // modelMap.put("applicationForm", applicationForm);
    // modelMap.put("user", user);
    //
    // actionsProviderMock.validateAction(applicationForm, user, ApplicationFormAction.PROVIDE_REVIEW);
    //
    // replay(actionsProviderMock);
    // assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.getReviewFeedbackPage(modelMap));
    // verify(actionsProviderMock);
    // }
    //
    // @Test
    // public void shouldReturnCurrentUser() {
    // RegisteredUser currentUser = new RegisteredUserBuilder().id(8).build();
    // expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
    // replay(userServiceMock);
    // assertEquals(currentUser, controller.getUser());
    // }
    //
    // @Test
    // public void shouldCreateNewReviewCommentForApplicationForm() throws Exception {
    // final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
    // final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
    // final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
    // final RegisteredUser currentUser = createMock(RegisteredUser.class);
    // final Reviewer reviewer = new ReviewerBuilder().id(5).build();
    //
    // final Question question1 = new Question();
    // question1.setLabel("question1");
    // question1.setType(QuestionType.RATING);
    // final CustomQuestions customQuestions = new CustomQuestions();
    // customQuestions.getQuestion().add(question1);
    // ArrayList<Score> generatedScores = Lists.newArrayList(new Score());
    //
    // expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
    // expect(applicationsServiceMock.getByApplicationNumber("app")).andReturn(applicationForm);
    // expect(currentUser.getReviewerForCurrentUserFromLatestReviewRound(applicationForm)).andReturn(reviewer);
    // expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
    // expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);
    //
    // replay(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock, applicationsServiceMock);
    // ReviewComment comment = controller.getComment("app");
    // verify(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock, applicationsServiceMock);
    //
    // assertNull(comment.getId());
    // assertEquals(applicationForm, comment.getApplication());
    // assertEquals(currentUser, comment.getUser());
    // assertEquals(CommentType.REVIEW, comment.getType());
    // assertEquals(reviewer, comment.getReviewer());
    // assertEquals(generatedScores, comment.getScores());
    // }
    //
    // @Test
    // public void shouldNotApplyScoringConfigurationIfParseException() throws Exception {
    // final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
    // final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
    // final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
    // final RegisteredUser currentUser = createMock(RegisteredUser.class);
    // final Reviewer reviewer = new ReviewerBuilder().id(5).build();
    //
    // expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
    // expect(applicationsServiceMock.getByApplicationNumber("app")).andReturn(applicationForm);
    // expect(currentUser.getReviewerForCurrentUserFromLatestReviewRound(applicationForm)).andReturn(reviewer);
    // expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));
    //
    // replay(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock, applicationsServiceMock);
    // ReviewComment comment = controller.getComment("app");
    // verify(userServiceMock, scoringDefinitionParserMock, currentUser, scoreFactoryMock, applicationsServiceMock);
    //
    // assertNull(comment.getId());
    // assertEquals(applicationForm, comment.getApplication());
    // assertEquals(currentUser, comment.getUser());
    // assertEquals(CommentType.REVIEW, comment.getType());
    // assertEquals(reviewer, comment.getReviewer());
    // assertEquals(0, comment.getScores().size());
    // }
    //
    // @Test
    // public void shouldRegisterValidator() {
    // WebDataBinder binderMock = createMock(WebDataBinder.class);
    // binderMock.setValidator(reviewFeedbackValidatorMock);
    // binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
    // binderMock.registerCustomEditor(null, "scores", scoresPropertyEditorMock);
    // replay(binderMock);
    // controller.registerBinders(binderMock);
    // verify(binderMock);
    // }
    //
    // @Test
    // public void shouldReturnToCommentsPageIfErrors() throws ScoringDefinitionParseException {
    // Program program = new Program();
    // final ApplicationForm application = new ApplicationFormBuilder().program(program).applicationNumber("5").build();
    // ReviewComment comment = new ReviewCommentBuilder().application(application).build();
    // BindingResult result = new BeanPropertyBindingResult(comment, "comment");
    // result.reject("error");
    //
    // assertEquals("private/staff/reviewer/feedback/reviewcomment", controller.addComment(comment, result, new ModelMap()));
    // }
    //
    // @Test
    // public void shouldSaveCommentAndRedirectApplicationsPageIfNoErrors() throws ScoringDefinitionParseException {
    // Program program = new Program();
    // final ApplicationForm applicationForm = new ApplicationFormBuilder().id(6).program(program).build();
    // ReviewRound reviewRound = new ReviewRound();
    // Reviewer reviewer = new ReviewerBuilder().reviewRound(reviewRound).build();
    // ReviewComment comment = new ReviewCommentBuilder().id(1).application(applicationForm).reviewer(reviewer).build();
    // BindingResult result = new BeanPropertyBindingResult(comment, "comment");
    // ModelMap modelMap = new ModelMap();
    // modelMap.put("applicationForm", applicationForm);
    //
    // commentServiceMock.save(comment);
    // applicantRatingServiceMock.computeAverageRating(reviewRound);
    // applicantRatingServiceMock.computeAverageRating(applicationForm);
    // applicationFormUserRoleServiceMock.reviewPosted(reviewer);
    // applicationFormUserRoleServiceMock.registerApplicationUpdate(applicationForm, userServiceMock.getCurrentUser(), ApplicationUpdateScope.INTERNAL);
    //
    // replay(commentServiceMock, applicantRatingServiceMock, applicationFormUserRoleServiceMock);
    // assertEquals("redirect:/applications?messageCode=review.feedback&application=" + applicationForm.getApplicationNumber(),
    // controller.addComment(comment, result, modelMap));
    // verify(commentServiceMock, applicantRatingServiceMock, applicationFormUserRoleServiceMock);
    //
    // Assert.assertSame(comment, reviewer.getReview());
    // Assert.assertThat(applicationForm.getApplicationComments(), Matchers.<Comment> contains(comment));
    // }

}
