package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoringDefinitionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RefereesAdminEditDTOValidator;

public class EditApplicationFormAsProgrammeAdminControllerTest {

    private EditApplicationFormAsProgrammeAdminController controller;
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private EncryptionHelper encryptionHelperMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private RefereeService refereeServiceMock;
    private RefereesAdminEditDTOValidator refereesAdminEditDTOValidatorMock;
    private SendToPorticoDataDTOEditor sendToPorticoDataDTOEditorMock;
    private MessageSource messageSourceMock;
    private ScoringDefinitionParser scoringDefinitionParserMock;
    private ScoresPropertyEditor scoresPropertyEditorMock;
    private ScoreFactory scoreFactoryMock;
    private DomicileService domicileServiceMock;
    private DomicilePropertyEditor domicilePropertyEditorMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;
    private ActionsProvider actionsProviderMock;

    @Before
    public void setUp() {
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        refereesAdminEditDTOValidatorMock = EasyMock.createMock(RefereesAdminEditDTOValidator.class);
        sendToPorticoDataDTOEditorMock = EasyMock.createMock(SendToPorticoDataDTOEditor.class);
        messageSourceMock = EasyMock.createMock(MessageSource.class);
        scoringDefinitionParserMock = EasyMock.createMock(ScoringDefinitionParser.class);
        scoresPropertyEditorMock = EasyMock.createMock(ScoresPropertyEditor.class);
        scoreFactoryMock = EasyMock.createMock(ScoreFactory.class);
        domicileServiceMock = EasyMock.createMock(DomicileService.class);
        domicilePropertyEditorMock = EasyMock.createMock(DomicilePropertyEditor.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);

        controller = new EditApplicationFormAsProgrammeAdminController(userServiceMock, applicationServiceMock, documentPropertyEditorMock, refereeServiceMock,
                refereesAdminEditDTOValidatorMock, sendToPorticoDataDTOEditorMock, encryptionHelperMock, messageSourceMock, scoringDefinitionParserMock,
                scoresPropertyEditorMock, scoreFactoryMock, domicileServiceMock, domicilePropertyEditorMock, applicationFormUserRoleServiceMock,
                actionsProviderMock);
        
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(null).anyTimes();
    }

    @Test
    public void shouldAddNewReferenceWithoutSavingSendToPorticoReferences() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).advert(program)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        Referee referee = new RefereeBuilder().application(applicationForm).id(8).build();
        ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).build();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
        EasyMock.expect(encryptionHelperMock.encrypt(8)).andReturn("refereeId");
        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);
        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
        refereeServiceMock.refresh(referee);

        EasyMock.replay(encryptionHelperMock, refereeServiceMock);
        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, true, model);
        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);

        EasyMock.verify(encryptionHelperMock, refereeServiceMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUpdateReference() throws ScoringDefinitionParseException {
        Program program = new ProgramBuilder().build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).advert(program)
                .build();

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        ModelMap model = new ModelMap();

        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expect(refereeServiceMock.editReferenceComment(applicationForm, refereesAdminEditDTO)).andReturn(null);

        EasyMock.replay(refereeServiceMock);
        String ret = controller.updateReference(applicationForm, refereesAdminEditDTO, result, model);
        Map<String, String> retMap = new Gson().fromJson(ret, Map.class);
        assertEquals(1, retMap.size());
        assertEquals("true", retMap.get("success"));

        EasyMock.verify(refereeServiceMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReportUpdateReferenceFormErrors() throws ScoringDefinitionParseException {
        Program program = new ProgramBuilder().build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).advert(program)
                .build();

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        FieldError fieldError = new FieldError("refereesAdminEditDTO", "comment", null);
        result.addError(fieldError);
        ModelMap model = new ModelMap();

        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);

        EasyMock.expect(messageSourceMock.getMessage(fieldError, Locale.getDefault())).andReturn("empty field");

        EasyMock.replay(refereeServiceMock, messageSourceMock);
        String ret = controller.updateReference(applicationForm, refereesAdminEditDTO, result, model);
        EasyMock.verify(refereeServiceMock, messageSourceMock);

        Map<String, String> retMap = new Gson().fromJson(ret, Map.class);
        assertEquals(2, retMap.size());
        assertEquals("false", retMap.get("success"));
        assertEquals("empty field", retMap.get("comment"));
    }

    @Test
    public void shouldSaveSendToPorticoReferencesWithoutAddingNewReference() throws ScoringDefinitionParseException {
        Program program = new ProgramBuilder().build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).advert(program)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        Referee referee = new RefereeBuilder().application(applicationForm).id(8).build();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());

        EasyMock.replay(encryptionHelperMock, refereeServiceMock);
        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
        EasyMock.verify(encryptionHelperMock, refereeServiceMock);

        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);
    }

    @Test
    public void shouldSaveSendToPorticoReferencesAndAddNewReference() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).advert(program)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        Referee referee = new RefereeBuilder().application(applicationForm).id(8).build();
        ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).build();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
        EasyMock.expect(encryptionHelperMock.encrypt(8)).andReturn("refereeId");
        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);
        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
        refereeServiceMock.refresh(referee);

        EasyMock.replay(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
        EasyMock.verify(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);

        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);

    }

    @Test
    public void shouldSaveSendToPorticoReferencesAndReportFormErrors() throws ScoringDefinitionParseException {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).advert(program)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());
        EasyMock.expectLastCall();

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        Referee referee = new RefereeBuilder().application(applicationForm).id(8).build();

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        result.reject("error");
        Model model = new ExtendedModelMap();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);
        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);

        EasyMock.replay(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
        EasyMock.verify(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);

        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);
    }

    @Test
    public void shouldReturnRefereesAdminEditDTO() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).advert(program).build();

        final Question question1 = new Question();
        question1.setLabel("question1");
        question1.setType(QuestionType.RATING);
        final CustomQuestions customQuestions = new CustomQuestions();
        customQuestions.getQuestion().add(question1);
        ArrayList<Score> generatedScores = Lists.newArrayList(new Score());

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
        EasyMock.expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);

        EasyMock.replay(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
        RefereesAdminEditDTO dto = controller.getRefereesAdminEditDTO("1");
        EasyMock.verify(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);

        assertNotNull(dto);
        assertEquals(generatedScores, dto.getScores());
    }

    @Test
    public void shouldNotApplyScoringConfigurationIfParseException() throws Exception {
        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
        final Program program = new ProgramBuilder().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).advert(program).build();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));

        EasyMock.replay(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
        RefereesAdminEditDTO dto = controller.getRefereesAdminEditDTO("1");
        EasyMock.verify(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);

        assertNotNull(dto);
        assertEquals(0, dto.getScores().size());
    }

    @Test
    public void shouldGetMainPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        EasyMock.replay(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
        String viewName = controller.view(applicationForm);
        EasyMock.verify(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
        assertEquals("/private/staff/admin/application/main_application_page_programme_administrator", viewName);
    }

    @Test
    public void shouldRegisterPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);

        binderMock.setValidator(refereesAdminEditDTOValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditorMock);
        binderMock.registerCustomEditor((Class<?>) EasyMock.isNull(), EasyMock.eq("comment"), EasyMock.isA(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String[].class), EasyMock.anyObject(StringArrayPropertyEditor.class));
        binderMock.registerCustomEditor(null, "scores", scoresPropertyEditorMock);
        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);

        EasyMock.verify(binderMock);
    }

}