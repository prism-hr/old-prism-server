package com.zuehlke.pgadmissions.controllers.referees;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.ADD_REFERENCE;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
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
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;
import com.zuehlke.pgadmissions.services.ApplicantRatingService;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
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
    private ApplicationFormAccessService accessServiceMock;
    private ActionsProvider actionsProviderMock;
    private ApplicantRatingService applicantRatingServiceMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    @Test
    public void shouldReturnApplicationForm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        Referee referee = new RefereeBuilder().build();

        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);

        expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);
        expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        replay(applicationsServiceMock, currentUser, userServiceMock);
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
        verify(applicationsServiceMock, currentUser, userServiceMock);

        assertEquals(applicationForm, returnedApplicationForm);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
        replay(applicationsServiceMock);
        controller.getApplicationForm("1");
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowResourceNoFoundExceptionIfUserNotRefereeForForm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        currentUser = createMock(RegisteredUser.class);
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(false);
        expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);

        replay(applicationsServiceMock, currentUser, userServiceMock);
        controller.getApplicationForm("1");
        verify(applicationsServiceMock, currentUser, userServiceMock);
    }

    @Test
    public void shouldReturnUploadReferencePage() {
        ApplicationForm applicationForm = new ApplicationForm();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", currentUser);

        actionsProviderMock.validateAction(applicationForm, currentUser, ApplicationFormAction.ADD_REFERENCE);

        replay(actionsProviderMock);
        assertEquals("private/referees/upload_references", controller.getUploadReferencesPage(modelMap));
        verify(actionsProviderMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldReturnExpiredViewIfApplicationAlreadyDecided() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        replay(currentUser, userServiceMock, applicationsServiceMock);
        assertSame(applicationForm, controller.getApplicationForm("app1"));
        verify(currentUser, userServiceMock, applicationsServiceMock);
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shoulThrowExceptionIfRefereeHasAlreadyProvidedReferecne() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).build();
        Referee referee = new RefereeBuilder().reference(new ReferenceComment()).build();

        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee);

        replay(currentUser, userServiceMock, applicationsServiceMock);
        controller.getApplicationForm("app1");
        verify(currentUser, userServiceMock, applicationsServiceMock);
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

        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee).anyTimes();
        expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
        expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);

        replay(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
        ReferenceComment returnedReference = controller.getComment("1");
        verify(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock, scoreFactoryMock);

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

        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        expect(currentUser.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
        expect(currentUser.getRefereeForApplicationForm(applicationForm)).andReturn(referee).anyTimes();
        expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));

        replay(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock);
        ReferenceComment returnedReference = controller.getComment("1");
        verify(currentUser, userServiceMock, applicationsServiceMock, scoringDefinitionParserMock);

        assertNull(returnedReference.getId());
        assertEquals(referee, returnedReference.getReferee());
        assertEquals(applicationForm, returnedReference.getApplication());
        assertEquals(currentUser, returnedReference.getUser());
        assertEquals(CommentType.REFERENCE, returnedReference.getType());
        assertEquals(0, returnedReference.getScores().size());
    }

    @Test
    public void shouldBindPRopertyEditorAndValidator() {
        WebDataBinder binderMock = createMock(WebDataBinder.class);
        binderMock.setValidator(referenceValidator);
        binderMock.registerCustomEditor(eq(String.class), anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(Document.class, documentPropertyEditor);
        binderMock.registerCustomEditor(null, "scores", scoresPropertyEditorMock);

        replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        verify(binderMock);
    }

    @Test
    public void shouldReturnToFormViewIfValidationErrors() throws ScoringDefinitionParseException {
        final Program program = new ProgramBuilder().build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("app1").program(program).build();
        final ReferenceComment comment = new ReferenceCommentBuilder().application(applicationForm).build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);

        BindingResult errors = new DirectFieldBindingResult(comment, "comment");
        errors.reject("error");
        assertEquals("private/referees/upload_references", controller.handleReferenceSubmission(comment, errors, modelMap));
    }

    @Test
    public void shouldSaveReferenceAndRedirectToSaveViewIfNoErrors() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("12").program(program).build();
        final Referee referee = new RefereeBuilder().id(1).build();
        final ReferenceComment reference = new ReferenceCommentBuilder().application(application).referee(referee).id(4).build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", currentUser);

        commentServiceMock.save(reference);
        refereeServiceMock.saveReferenceAndSendMailNotifications(referee);
        applicantRatingServiceMock.computeAverageRating(application);
        applicationFormUserRoleServiceMock.referencePosted(referee);

        BindingResult errors = new DirectFieldBindingResult(reference, "comment");
        actionsProviderMock.validateAction(application, currentUser, ADD_REFERENCE);

        replay(commentServiceMock, refereeServiceMock, actionsProviderMock, applicantRatingServiceMock, applicationFormUserRoleServiceMock);
        assertEquals("redirect:/applications?messageCode=reference.uploaded&application=12", controller.handleReferenceSubmission(reference, errors, modelMap));
        verify(commentServiceMock, refereeServiceMock, actionsProviderMock, applicantRatingServiceMock, applicationFormUserRoleServiceMock);

        Assert.assertThat(application.getApplicationComments(), Matchers.<Comment> contains(reference));
    }

    @Before
    public void setUp() {
        currentUser = createMock(RegisteredUser.class);
        commentServiceMock = createMock(CommentService.class);
        applicationsServiceMock = createMock(ApplicationsService.class);
        documentPropertyEditor = createMock(DocumentPropertyEditor.class);
        referenceValidator = createMock(FeedbackCommentValidator.class);
        refereeServiceMock = createMock(RefereeService.class);
        userServiceMock = createMock(UserService.class);
        scoringDefinitionParserMock = createMock(ScoringDefinitionParser.class);
        scoresPropertyEditorMock = createMock(ScoresPropertyEditor.class);
        accessServiceMock = createMock(ApplicationFormAccessService.class);
        scoreFactoryMock = createMock(ScoreFactory.class);
        actionsProviderMock = createMock(ActionsProvider.class);
        applicantRatingServiceMock = createMock(ApplicantRatingService.class);
        applicationFormUserRoleServiceMock = createMock(ApplicationFormUserRoleService.class);

        controller = new ReferenceController(applicationsServiceMock, refereeServiceMock, userServiceMock, documentPropertyEditor, referenceValidator,
                commentServiceMock, scoringDefinitionParserMock, scoresPropertyEditorMock, scoreFactoryMock, accessServiceMock, actionsProviderMock, null,
                applicantRatingServiceMock, applicationFormUserRoleServiceMock);

    }

}
