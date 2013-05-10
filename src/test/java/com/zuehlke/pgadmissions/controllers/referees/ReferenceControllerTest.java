package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
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
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

public class ReferenceControllerTest {

    private ApplicationsService applicationsServiceMock;
    private ReferenceController controller;
    private RegisteredUser currentUser;
    private DocumentPropertyEditor documentPropertyEditor;
    private FeedbackCommentValidator referenceValidator;
    private RefereeService refereeServiceMock;
    private CommentService commentServiceMock;
    private UserService userServiceMock;
    private ScoringDefinitionParser scoringDefinitionParserMock;
    private ScoresPropertyEditor scoresPropertyEditorMock;
    private ScoreFactory scoreFactoryMock;

    @Test
    public void shouldReturnApplicationForm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        Referee referee = new RefereeBuilder().build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);

        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);

        assertEquals(applicationForm, returnedApplicationForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("1");
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowResourceNoFoundExceptionIfUserNotRefereeForForm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(false);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);

        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock);
        controller.getApplicationForm("1");
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock);
    }

    @Test
    public void shouldReturnUploadReferencePage() {
        assertEquals("private/referees/upload_references", controller.getUploadReferencesPage());
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldReturnExpiredViewIfApplicationAlreadyDecided() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        EasyMock.replay(currentUser, userServiceMock, applicationsServiceMock);
        assertSame(applicationForm, controller.getApplicationForm("app1"));
        EasyMock.verify(currentUser, userServiceMock, applicationsServiceMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shoulThrowExceptionIfRefereeHasAlreadyProvidedReferecne() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).build();
        Referee referee = new RefereeBuilder().reference(new ReferenceComment()).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);

        EasyMock.replay(currentUser, userServiceMock, applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(currentUser, userServiceMock, applicationsServiceMock);
    }

    @Test
    public void shouldReturnNewReferenceComment() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).build();
        final Referee referee = new RefereeBuilder().id(8).build();

        final Question question1 = new Question();
        question1.setLabel("question1");
        question1.setType(QuestionType.RATING);
        final CustomQuestions customQuestions = new CustomQuestions();
        customQuestions.getQuestion().add(question1);
        ArrayList<Score> generatedScores = Lists.newArrayList(new Score());

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee).anyTimes();
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
        EasyMock.expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);

        EasyMock.replay(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
        ReferenceComment returnedReference = controller.getComment("1");
        EasyMock.verify(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock, scoreFactoryMock);

        assertNull(returnedReference.getId());
        assertEquals(referee, returnedReference.getReferee());
        assertEquals(applicationForm, returnedReference.getApplication());
        assertEquals(currentUser, returnedReference.getUser());
        assertEquals(CommentType.REFERENCE, returnedReference.getType());
        assertEquals(generatedScores, returnedReference.getScores());
    }
    
    @Test
    public void shouldNotApplyScoringConfigurationIfParseException() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).program(program).build();
        final Referee referee = new RefereeBuilder().id(8).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee).anyTimes();
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));

        EasyMock.replay(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock);
        ReferenceComment returnedReference = controller.getComment("1");
        EasyMock.verify(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock);

        assertNull(returnedReference.getId());
        assertEquals(referee, returnedReference.getReferee());
        assertEquals(applicationForm, returnedReference.getApplication());
        assertEquals(currentUser, returnedReference.getUser());
        assertEquals(CommentType.REFERENCE, returnedReference.getType());
        assertEquals(0, returnedReference.getScores().size());
    }

    @Test
    public void shouldBindPRopertyEditorAndValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(referenceValidator);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(Document.class, documentPropertyEditor);
        binderMock.registerCustomEditor(null, "scores", scoresPropertyEditorMock);

        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldReturnToFormViewIfValidationErrors() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("app1").program(program).build();
        final ReferenceComment comment = new ReferenceCommentBuilder().application(applicationForm).build();
        final RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        final Referee referee = new RefereeBuilder().build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(new CustomQuestions());

        BindingResult errors = new DirectFieldBindingResult(comment, "comment");
        errors.reject("error");
        EasyMock.replay(applicationsServiceMock, currentUser, userServiceMock, scoringDefinitionParserMock);
        assertEquals("private/referees/upload_references", controller.handleReferenceSubmission(comment, errors));
        EasyMock.verify(applicationsServiceMock, currentUser, userServiceMock, scoringDefinitionParserMock);
    }

    @Test
    public void shouldSaveReferenceAndRedirectToSaveViewIfNoErrors() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("12").program(program).build();
        final RegisteredUser currentUser = EasyMock.createMock(RegisteredUser.class);
        final Referee referee = new RefereeBuilder().id(1).build();
        final ReferenceComment reference = new ReferenceCommentBuilder().application(application).referee(referee).id(4).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("12")).andReturn(application);
        commentServiceMock.save(reference);
        refereeServiceMock.saveReferenceAndSendMailNotifications(referee);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(application)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(application)).andReturn(referee);
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(new CustomQuestions());

        BindingResult errors = new DirectFieldBindingResult(reference, "comment");

        EasyMock.replay(userServiceMock, currentUser, commentServiceMock, refereeServiceMock, applicationsServiceMock, scoringDefinitionParserMock);
        assertEquals("redirect:/applications?messageCode=reference.uploaded&application=12", controller.handleReferenceSubmission(reference, errors));
        EasyMock.verify(userServiceMock, currentUser, commentServiceMock, refereeServiceMock, applicationsServiceMock, scoringDefinitionParserMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldPreventFromSavingDuplicateReferences() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
        Referee referee = new RefereeBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("12").program(program).build();
        ReferenceComment reference = new ReferenceCommentBuilder().application(application).referee(referee).id(4).build();
        referee.setReference(reference);

        BindingResult errors = new DirectFieldBindingResult(reference, "comment");

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("12")).andReturn(application);
        EasyMock.expect(currentUser.isRefereeOfApplicationForm(application)).andReturn(true);
        EasyMock.expect(currentUser.getRefereeForApplicationForm(application)).andReturn(referee);
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(new CustomQuestions());

        EasyMock.replay(userServiceMock, currentUser, applicationsServiceMock, commentServiceMock, refereeServiceMock);
        controller.handleReferenceSubmission(reference, errors);
    }

    @Before
    public void setUp() {
        currentUser = EasyMock.createMock(RegisteredUser.class);
        commentServiceMock = EasyMock.createMock(CommentService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        documentPropertyEditor = EasyMock.createMock(DocumentPropertyEditor.class);
        referenceValidator = EasyMock.createMock(FeedbackCommentValidator.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        scoringDefinitionParserMock = EasyMock.createMock(ScoringDefinitionParser.class);
        scoresPropertyEditorMock = EasyMock.createMock(ScoresPropertyEditor.class);
        scoreFactoryMock = EasyMock.createMock(ScoreFactory.class);

        controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator,
                commentServiceMock, scoringDefinitionParserMock, scoresPropertyEditorMock, scoreFactoryMock);

    }

}
