package com.zuehlke.pgadmissions.controllers.workflow;

import org.junit.runner.RunWith;
import org.springframework.context.MessageSource;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.RefereesAdminEditDTOValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EditApplicationFormAsProgrammeAdminControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelperMock;

    @Mock
    @InjectIntoByType
    private DocumentPropertyEditor documentPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private RefereeService refereeServiceMock;

    @Mock
    @InjectIntoByType
    private RefereesAdminEditDTOValidator refereesAdminEditDTOValidatorMock;

    @Mock
    @InjectIntoByType
    private SendToPorticoDataDTOEditor sendToPorticoDataDTOEditorMock;

    @Mock
    @InjectIntoByType
    private MessageSource messageSourceMock;

    @Mock
    @InjectIntoByType
    private ScoringDefinitionParser scoringDefinitionParserMock;

    @Mock
    @InjectIntoByType
    private ScoresPropertyEditor scoresPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private ScoreFactory scoreFactoryMock;

    @Mock
    @InjectIntoByType
    private DomicileService domicileServiceMock;

    @Mock
    @InjectIntoByType
    private DomicilePropertyEditor domicilePropertyEditorMock;
    private WorkflowService applicationFormUserRoleServiceMock;

    @Mock
    @InjectIntoByType
    private ActionService actionsProviderMock;
    
    @TestedObject
    private EditApplicationFormAsProgrammeAdminController controller;

//    @Test
//    public void shouldAddNewReferenceWithoutSavingSendToPorticoReferences() throws ScoringDefinitionParseException {
//        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
//        final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
//
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(new State().withId(ApplicationFormStatus.INTERVIEW)).program(program)
//                .build();
//        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
//
//        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
//        refereesAdminEditDTO.setComment("comment text");
//        refereesAdminEditDTO.setEditedRefereeId("refereeId");
//        refereesAdminEditDTO.setSuitableForProgramme(true);
//        refereesAdminEditDTO.setSuitableForUCL(false);
//
//        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
//        Model model = new ExtendedModelMap();
//
//        Referee referee = new RefereeBuilder().application(applicationForm).id(8).build();
//        ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).build();
//
//        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
//        EasyMock.expect(encryptionHelperMock.encrypt(8)).andReturn("refereeId");
//        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);
//        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
//        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
//        refereeServiceMock.refresh(referee);
//
//        EasyMock.replay(encryptionHelperMock, refereeServiceMock);
//        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, true, model);
//        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);
//
//        EasyMock.verify(encryptionHelperMock, refereeServiceMock);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void shouldUpdateReference() throws ScoringDefinitionParseException {
//        Program program = new Program().build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(new State().withId(ApplicationFormStatus.INTERVIEW)).program(program)
//                .build();
//
//        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
//        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
//        ModelMap model = new ModelMap();
//
//        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
//        EasyMock.expect(refereeServiceMock.editReferenceComment(applicationForm, refereesAdminEditDTO)).andReturn(null);
//
//        EasyMock.replay(refereeServiceMock);
//        String ret = controller.updateReference(applicationForm, refereesAdminEditDTO, result, model);
//        Map<String, String> retMap = new Gson().fromJson(ret, Map.class);
//        assertEquals(1, retMap.size());
//        assertEquals("true", retMap.get("success"));
//
//        EasyMock.verify(refereeServiceMock);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void shouldReportUpdateReferenceFormErrors() throws ScoringDefinitionParseException {
//        Program program = new Program().build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(new State().withId(ApplicationFormStatus.INTERVIEW)).program(program)
//                .build();
//
//        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
//        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
//        FieldError fieldError = new FieldError("refereesAdminEditDTO", "comment", null);
//        result.addError(fieldError);
//        ModelMap model = new ModelMap();
//
//        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
//
//        EasyMock.expect(messageSourceMock.getMessage(fieldError, Locale.getDefault())).andReturn("empty field");
//
//        EasyMock.replay(refereeServiceMock, messageSourceMock);
//        String ret = controller.updateReference(applicationForm, refereesAdminEditDTO, result, model);
//        EasyMock.verify(refereeServiceMock, messageSourceMock);
//
//        Map<String, String> retMap = new Gson().fromJson(ret, Map.class);
//        assertEquals(2, retMap.size());
//        assertEquals("false", retMap.get("success"));
//        assertEquals("empty field", retMap.get("comment"));
//    }
//
//    @Test
//    public void shouldSaveSendToPorticoReferencesWithoutAddingNewReference() throws ScoringDefinitionParseException {
//        Program program = new Program().build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(new State().withId(ApplicationFormStatus.INTERVIEW)).program(program)
//                .build();
//        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
//        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));
//
//        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
//        refereesAdminEditDTO.setEditedRefereeId("refereeId");
//        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
//        Model model = new ExtendedModelMap();
//
//        Referee referee = new RefereeBuilder().application(applicationForm).id(8).build();
//
//        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
//        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);
//
//        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());
//
//        EasyMock.replay(encryptionHelperMock, refereeServiceMock);
//        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
//        EasyMock.verify(encryptionHelperMock, refereeServiceMock);
//
//        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);
//    }
//
//    @Test
//    public void shouldSaveSendToPorticoReferencesAndAddNewReference() throws ScoringDefinitionParseException {
//        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
//        final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(new State().withId(ApplicationFormStatus.INTERVIEW)).program(program)
//                .build();
//        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
//        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));
//
//        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());
//
//        Document document = new Document().build();
//        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
//        refereesAdminEditDTO.setComment("comment text");
//        refereesAdminEditDTO.setEditedRefereeId("refereeId");
//        refereesAdminEditDTO.setReferenceDocument(document);
//        refereesAdminEditDTO.setSuitableForProgramme(true);
//        refereesAdminEditDTO.setSuitableForUCL(false);
//
//        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
//        Model model = new ExtendedModelMap();
//
//        Referee referee = new RefereeBuilder().application(applicationForm).id(8).build();
//        ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).build();
//
//        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
//        EasyMock.expect(encryptionHelperMock.encrypt(8)).andReturn("refereeId");
//        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);
//        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
//        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
//        refereeServiceMock.refresh(referee);
//
//        EasyMock.replay(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
//        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
//        EasyMock.verify(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
//
//        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);
//
//    }
//
//    @Test
//    public void shouldSaveSendToPorticoReferencesAndReportFormErrors() throws ScoringDefinitionParseException {
//        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("xmlContent").build();
//        final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REVIEW, scoringDefinition)).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(new State().withId(ApplicationFormStatus.INTERVIEW)).program(program)
//                .build();
//        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
//        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));
//
//        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());
//        EasyMock.expectLastCall();
//
//        Document document = new Document().build();
//        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
//        refereesAdminEditDTO.setComment("comment text");
//        refereesAdminEditDTO.setEditedRefereeId("refereeId");
//        refereesAdminEditDTO.setReferenceDocument(document);
//        refereesAdminEditDTO.setSuitableForProgramme(true);
//        refereesAdminEditDTO.setSuitableForUCL(false);
//
//        Referee referee = new RefereeBuilder().application(applicationForm).id(8).build();
//
//        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
//        result.reject("error");
//        Model model = new ExtendedModelMap();
//
//        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
//        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);
//        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
//
//        EasyMock.replay(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
//        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
//        EasyMock.verify(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
//
//        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);
//    }
//
//    @Test
//    public void shouldReturnRefereesAdminEditDTO() throws Exception {
//        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
//        final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
//        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(new State().withId(ApplicationFormStatus.REVIEW)).program(program).build();
//
//        final Question question1 = new Question();
//        question1.setLabel("question1");
//        question1.setType(QuestionType.RATING);
//        final CustomQuestions customQuestions = new CustomQuestions();
//        customQuestions.getQuestion().add(question1);
//        ArrayList<Score> generatedScores = Lists.newArrayList(new Score());
//
//        EasyMock.expect(applicationServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);
//        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andReturn(customQuestions);
//        EasyMock.expect(scoreFactoryMock.createScores(customQuestions.getQuestion())).andReturn(generatedScores);
//
//        EasyMock.replay(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
//        RefereesAdminEditDTO dto = controller.getRefereesAdminEditDTO("1");
//        EasyMock.verify(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
//
//        assertNotNull(dto);
//        assertEquals(generatedScores, dto.getScores());
//    }
//
//    @Test
//    public void shouldNotApplyScoringConfigurationIfParseException() throws Exception {
//        final ScoringDefinition scoringDefinition = new ScoringDefinitionBuilder().stage(ScoringStage.REFERENCE).content("xmlContent").build();
//        final Program program = new Program().scoringDefinitions(Collections.singletonMap(ScoringStage.REFERENCE, scoringDefinition)).build();
//        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(new State().withId(ApplicationFormStatus.REVIEW)).program(program).build();
//
//        EasyMock.expect(applicationServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);
//        EasyMock.expect(scoringDefinitionParserMock.parseScoringDefinition("xmlContent")).andThrow(new ScoringDefinitionParseException("error"));
//
//        EasyMock.replay(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
//        RefereesAdminEditDTO dto = controller.getRefereesAdminEditDTO("1");
//        EasyMock.verify(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
//
//        assertNotNull(dto);
//        assertEquals(0, dto.getScores().size());
//    }
//
//    @Test
//    public void shouldGetMainPage() {
//        ApplicationForm applicationForm = new ApplicationForm();
//        EasyMock.replay(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
//        String viewName = controller.view(applicationForm);
//        EasyMock.verify(userServiceMock, applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
//        assertEquals("/private/staff/admin/application/main_application_page_programme_administrator", viewName);
//    }
//
//    @Test
//    public void shouldRegisterPropertyEditors() {
//        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
//
//        binderMock.setValidator(refereesAdminEditDTOValidatorMock);
//        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
//        binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditorMock);
//        binderMock.registerCustomEditor((Class<?>) EasyMock.isNull(), EasyMock.eq("comment"), EasyMock.isA(StringTrimmerEditor.class));
//        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
//        binderMock.registerCustomEditor(EasyMock.eq(String[].class), EasyMock.anyObject(StringArrayPropertyEditor.class));
//        binderMock.registerCustomEditor(null, "scores", scoresPropertyEditorMock);
//        EasyMock.replay(binderMock);
//        controller.registerPropertyEditors(binderMock);
//
//        EasyMock.verify(binderMock);
//    }

}