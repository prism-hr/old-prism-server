package com.zuehlke.pgadmissions.controllers.referees;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ReferenceControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationService applicationsService;

    @Mock
    @InjectIntoByType
    private DocumentPropertyEditor documentPropertyEditor;

    @Mock
    @InjectIntoByType
    private FeedbackCommentValidator referenceValidator;

    @Mock
    @InjectIntoByType
    private RefereeService refereeService;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @Mock
    @InjectIntoByType
    private CommentService commentService;

    @Mock
    @InjectIntoByType
    private ScoringDefinitionParser scoringDefinitionParser;

    @Mock
    @InjectIntoByType
    private ScoresPropertyEditor scoresPropertyEditor;

    @Mock
    @InjectIntoByType
    private ScoreFactory scoreFactory;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleService;

    @Mock
    @InjectIntoByType
    private ActionService actionService;
    
    @TestedObject
    private ReferenceController controller;
    
//    @Test
//    public void shouldReturnApplicationForm() {
//        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
//        expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);
//        replay(applicationsServiceMock, currentUser, userServiceMock);
//        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
//        verify(applicationsServiceMock, currentUser, userServiceMock);
//
//        assertEquals(applicationForm, returnedApplicationForm);
//    }
//
//    @Test(expected = MissingApplicationFormException.class)
//    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
//        expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(null);
//        replay(applicationsServiceMock);
//        controller.getApplicationForm("1");
//    }
//
//    @Test
//    public void shouldReturnUploadReferencePage() {
//        ApplicationForm applicationForm = new ApplicationForm();
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", applicationForm);
//        modelMap.put("user", currentUser);
//
//        actionsProviderMock.validateAction(applicationForm, currentUser, ApplicationFormAction.PROVIDE_REFERENCE);
//
//        replay(actionsProviderMock);
//        assertEquals("private/referees/upload_references", controller.getUploadReferencesPage(modelMap));
//        verify(actionsProviderMock);
//    }
//
//    @Test
//    public void shouldReturnNewReferenceComment() throws Exception {
//        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
//        final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
//        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).build();
//        final Referee referee = new RefereeBuilder().id(8).build();
//
//        final Question question1 = new Question();
//        question1.setLabel("question1");
//        question1.setType(QuestionType.RATING);
//        final CustomQuestions customQuestions = new CustomQuestions();
//        customQuestions.getQuestion().add(question1);
//        ArrayList<Score> generatedScores = Lists.newArrayList(new Score());
//
//        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
//        expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);
//        expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee).anyTimes();
//        expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
//        expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);
//
//        replay(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
//        ReferenceComment returnedReference = controller.getComment("1");
//        verify(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
//
//        assertNull(returnedReference.getId());
//        assertEquals(referee, returnedReference.getReferee());
//        assertEquals(applicationForm, returnedReference.getApplication());
//        assertEquals(currentUser, returnedReference.getUser());
//        assertEquals(CommentType.REFERENCE, returnedReference.getType());
//        assertEquals(generatedScores, returnedReference.getScores());
//    }
//
//    @Test
//    public void shouldNotApplyScoringConfigurationIfParseException() throws Exception {
//        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
//        final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
//        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).build();
//        final Referee referee = new RefereeBuilder().id(8).build();
//
//        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
//        expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);
//        expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee).anyTimes();
//        expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));
//
//        replay(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock);
//        ReferenceComment returnedReference = controller.getComment("1");
//        verify(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock);
//
//        assertNull(returnedReference.getId());
//        assertEquals(referee, returnedReference.getReferee());
//        assertEquals(applicationForm, returnedReference.getApplication());
//        assertEquals(currentUser, returnedReference.getUser());
//        assertEquals(CommentType.REFERENCE, returnedReference.getType());
//        assertEquals(0, returnedReference.getScores().size());
//    }
//
//    @Test
//    public void shouldBindPRopertyEditorAndValidator() {
//        WebDataBinder binderMock = createMock(WebDataBinder.class);
//        binderMock.setValidator(referenceValidator);
//        binderMock.registerCustomEditor(eq(String.class), anyObject(StringTrimmerEditor.class));
//        binderMock.registerCustomEditor(Document.class, documentPropertyEditor);
//        binderMock.registerCustomEditor(null, "scores", scoresPropertyEditorMock);
//
//        replay(binderMock);
//        controller.registerPropertyEditors(binderMock);
//        verify(binderMock);
//    }
//
//    @Test
//    public void shouldReturnToFormViewIfValidationErrors() throws ScoringDefinitionParseException {
//        final Program program = new Program().build();
//        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("app1").program(program).build();
//        final ReferenceComment comment = new ReferenceCommentBuilder().application(applicationForm).build();
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", applicationForm);
//
//        BindingResult errors = new DirectFieldBindingResult(comment, "comment");
//        errors.reject("error");
//        assertEquals("private/referees/upload_references", controller.handleReferenceSubmission(comment, errors, modelMap));
//    }
//
//    @Test
//    public void shouldSaveReferenceAndRedirectToSaveViewIfNoErrors() throws ScoringDefinitionParseException {
//        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
//        final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
//        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("12").program(program).build();
//        final Referee referee = new RefereeBuilder().id(1).build();
//        final ReferenceComment reference = new ReferenceCommentBuilder().application(application).referee(referee).id(4).build();
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", application);
//        modelMap.put("user", currentUser);
//
//        commentServiceMock.save(reference);
//        refereeServiceMock.saveReferenceAndSendMailNotifications(referee);
//        applicantRatingServiceMock.computeAverageRating(application);
//        applicationFormUserRoleServiceMock.referencePosted(referee);
//
//        BindingResult errors = new DirectFieldBindingResult(reference, "comment");
//        actionsProviderMock.validateAction(application, currentUser, ApplicationFormAction.PROVIDE_REFERENCE);
//        applicationFormUserRoleServiceMock.registerApplicationUpdate(application, currentUser, ApplicationUpdateScope.ALL_USERS);
//
//        replay(commentServiceMock, refereeServiceMock, actionsProviderMock, applicantRatingServiceMock, applicationFormUserRoleServiceMock);
//        assertEquals("redirect:/applications?messageCode=reference.uploaded&application=12", controller.handleReferenceSubmission(reference, errors, modelMap));
//        verify(commentServiceMock, refereeServiceMock, actionsProviderMock, applicantRatingServiceMock, applicationFormUserRoleServiceMock);
//
//        Assert.assertThat(application.getApplicationComments(), Matchers.<Comment> contains(reference));
//    }

}