package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.SimpleSessionStatus;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoringDefinitionBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;
import com.zuehlke.pgadmissions.validators.RefereesAdminEditDTOValidator;
import com.zuehlke.pgadmissions.validators.SendToPorticoDataDTOValidator;

public class ApprovalControllerTest {

    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private RegisteredUser currentUserMock;
    private ApprovalController controller;
    private ApprovalRoundValidator approvalRoundValidatorMock;
    private SupervisorPropertyEditor supervisorPropertyEditorMock;
    private ApprovalService approvalServiceMock;
    private BindingResult bindingResultMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private GenericCommentValidator commentValidatorMock;
    private RefereesAdminEditDTOValidator refereesAdminEditDTOValidatorMock;
    private QualificationService qualificationServiceMock;
    private RefereeService refereeServiceMock;
    private EncryptionHelper encryptionHelperMock;
    private SendToPorticoDataDTOEditor sendToPorticoDataDTOEditorMock;
    private SendToPorticoDataDTOValidator sendToPorticoDataDTOValidatorMock;
    private DatePropertyEditor datePropertyEditorMock;
    private DomicileService domicileServiceMock;
    private DomicilePropertyEditor domicilePropertyEditorMock;
    private MessageSource messageSourceMock;
    private ScoringDefinitionParser scoringDefinitionParserMock;
    private ScoresPropertyEditor scoresPropertyEditorMock;
    private ScoreFactory scoreFactoryMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;
    private ActionsProvider actionsProviderMock;

    @Test
    public void shouldGetApprovalPage() {
        Supervisor supervisorOne = new SupervisorBuilder().id(1).build();
        Supervisor suprvisorTwo = new SupervisorBuilder().id(2).build();

        Date nowDate = new Date();
        Date testDate = DateUtils.addMonths(nowDate, 1);
        Date deadlineDate = DateUtils.addMonths(nowDate, 2);

        final Program program = new Program();
        program.setId(1);

        final ProgramInstance programInstance = new ProgramInstance();
        programInstance.setId(1);
        programInstance.setProgram(program);
        programInstance.setApplicationStartDate(nowDate);
        programInstance.setApplicationDeadline(deadlineDate);

        final ApplicationForm application = new ApplicationFormBuilder().id(2).advert(program).applicationNumber("abc")
                .latestApprovalRound(new ApprovalRoundBuilder().recommendedStartDate(testDate).supervisors(supervisorOne, suprvisorTwo).build()).build();

        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("approvalRound", application.getLatestApprovalRound());
        modelMap.put("user", currentUserMock);

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("abc")).andReturn(application);
        ApprovalRound approvalRound = (ApprovalRound) modelMap.get("approvalRound");

        Assert.assertEquals("/private/staff/supervisors/approval_details", controller.getMoveToApprovalPage(modelMap, null));

        assertNull(approvalRound.getId());
        assertEquals(2, approvalRound.getSupervisors().size());
        assertTrue(approvalRound.getSupervisors().containsAll(Arrays.asList(supervisorOne, suprvisorTwo)));
    }
    
    @Test
    public void shouldReturnApprovalRound() {
        ApprovalRound round = new ApprovalRound();
        EasyMock.expect(approvalServiceMock.initiateApprovalRound("test")).andReturn(round);
        EasyMock.replay(approvalServiceMock);
        ApprovalRound testedRound = controller.getApprovalRound("test");
        EasyMock.verify(approvalServiceMock);
        assertSame(round, testedRound);
    }

    @Test
    public void shouldGetApplication() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);

        EasyMock.replay(applicationServiceMock);
        ApplicationForm returnedForm = controller.getApplicationForm("5");
        EasyMock.verify(applicationServiceMock);

        assertSame(applicationForm, returnedForm);

    }

    @Test
    public void shouldGetCurrentUser() {
        assertEquals(currentUserMock, controller.getCurrentUser());
    }

    @Test
    public void shouldAssignSupervisorsAndMoveToApproval() {

        final ApprovalRound approvalRound = new ApprovalRoundBuilder().id(4).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();
        SessionStatus sessionStatus = new SimpleSessionStatus();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", currentUserMock);

        actionsProviderMock.validateAction(application, currentUserMock, ApplicationFormAction.ASSIGN_SUPERVISORS);
        approvalServiceMock.moveApplicationToApproval(application, approvalRound, currentUserMock);

        EasyMock.replay(approvalServiceMock, actionsProviderMock);
        String view = controller.assignSupervisors(modelMap, approvalRound, result, sessionStatus);
        EasyMock.verify(approvalServiceMock, actionsProviderMock);

        assertEquals("/private/common/ajax_OK", view);
        assertTrue(sessionStatus.isComplete());
    }

    @Test
    public void shouldNotSaveApprovalRoundAndReturnToApprovalPageIfHasErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).build();
        SessionStatus sessionStatus = new SimpleSessionStatus();

        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", currentUserMock);

        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        actionsProviderMock.validateAction(applicationForm, currentUserMock, ApplicationFormAction.ASSIGN_SUPERVISORS);

        EasyMock.replay(errorsMock, actionsProviderMock);
        assertEquals("/private/staff/supervisors/propose_offer_recommendation",
                controller.assignSupervisors(modelMap, approvalRound, errorsMock, sessionStatus));
        EasyMock.verify(errorsMock, actionsProviderMock);

        assertFalse(sessionStatus.isComplete());
    }

    @Test
    public void shouldAddApprovalRoundValidatorAndSupervisorPropertyEditor() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(approvalRoundValidatorMock);
        binderMock.registerCustomEditor(Supervisor.class, supervisorPropertyEditorMock);
        binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerValidatorAndPropertyEditorForApprovalRound(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldAddCommentValidatorAndDocumentPropertyEditor() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(commentValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerValidatorAndPropertyEditorForComment(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldRegisterSendToPorticoDataBinder() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(sendToPorticoDataDTOValidatorMock);
        binderMock.registerCustomEditor(List.class, sendToPorticoDataDTOEditorMock);

        EasyMock.replay(binderMock);
        controller.registerSendToPorticoData(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldApplySendToPorticoDataAndMoveToApproval() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(4).build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        sendToPorticoData.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));
        sendToPorticoData.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11, 12 }));

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        EasyMock.expectLastCall().once();

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);

        String returnValue = controller.applySendToPorticoData(applicationForm, approvalRound, sendToPorticoData, result);
        assertEquals("/private/staff/supervisors/propose_offer_recommendation", returnValue);

        EasyMock.verify(qualificationServiceMock, refereeServiceMock);
    }

    @Test
    public void shouldApplySendToPorticoDataWithMissingQualificationsAndExplanation() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(4).build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        sendToPorticoData.setQualificationsSendToPortico(Collections.<Integer> emptyList());
        sendToPorticoData.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11, 12 }));
        sendToPorticoData.setEmptyQualificationsExplanation("explanation");

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        EasyMock.expectLastCall().once();

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);

        String returnValue = controller.applySendToPorticoData(applicationForm, approvalRound, sendToPorticoData, result);
        assertEquals("/private/staff/supervisors/propose_offer_recommendation", returnValue);
        assertEquals("explanation", approvalRound.getMissingQualificationExplanation());

        EasyMock.verify(qualificationServiceMock, refereeServiceMock);
    }

    @Test
    public void shouldApplySendToPorticoDataAndNotToMoveToApprovalIfThereAreErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();
        ApprovalRound interview = new ApprovalRoundBuilder().id(4).build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        sendToPorticoData.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));
        sendToPorticoData.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11, 12 }));

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        result.reject("error"); // does not matter if error

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        EasyMock.expectLastCall().once();

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.replay(qualificationServiceMock, refereeServiceMock, approvalServiceMock);

        String returnValue = controller.applySendToPorticoData(applicationForm, interview, sendToPorticoData, result);
        assertEquals("/private/staff/supervisors/portico_validation_section", returnValue);

        EasyMock.verify(qualificationServiceMock, refereeServiceMock, approvalServiceMock);
    }

    @Test
    public void shouldSubmitQualificationData() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        sendToPorticoData.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        BindingResult porticoResult = new MapBindingResult(Collections.emptyMap(), "");
        porticoResult.reject("error"); // does not matter if error

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.replay(qualificationServiceMock);

        String returnValue = controller.submitQualificationsData(applicationForm, sendToPorticoData, porticoResult);
        assertEquals("/private/staff/supervisors/components/qualification_portico_validation", returnValue);

        EasyMock.verify(qualificationServiceMock);
    }

    @Test
    public void shouldSaveSendToPorticoReferencesAndSaveNewReference() throws ScoringDefinitionParseException {
        Program program = new ProgramBuilder().title("some title").build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").advert(program).build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        List<Integer> refereesSendToPortico = new ArrayList<Integer>();
        refereesSendToPortico.add(11);
        sendToPorticoData.setRefereesSendToPortico(refereesSendToPortico);

        BindingResult porticoResult = new MapBindingResult(Collections.emptyMap(), "");
        porticoResult.reject("error"); // does not matter if error

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        Referee referee = new RefereeBuilder().application(applicationForm).id(12).build();

        BindingResult referenceResult = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).build();

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(12);
        EasyMock.expect(encryptionHelperMock.encrypt(12)).andReturn("refereeId");
        EasyMock.expect(refereeServiceMock.getRefereeById(12)).andReturn(referee);

        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
        refereeServiceMock.refresh(referee);
        EasyMock.expectLastCall();

        sendToPorticoDataDTOValidatorMock.validate(sendToPorticoData, porticoResult);
        EasyMock.expectLastCall();

        EasyMock.replay(refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOValidatorMock);

        String returnValue = controller.submitRefereesData(applicationForm, sendToPorticoData, porticoResult, refereesAdminEditDTO, referenceResult, null,
                model);
        assertEquals("/private/staff/supervisors/components/reference_portico_validation", returnValue);
        assertEquals(Arrays.asList(11, 12), refereesSendToPortico);

        EasyMock.verify(refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOValidatorMock);
    }

    @Test
    public void shouldAddNewReferenceWithoutSavingSendToPorticoReferences() throws ScoringDefinitionParseException {
        Role adminRole = new RoleBuilder().id(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").advert(program).status(ApplicationFormStatus.INTERVIEW)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        BindingResult porticoResult = new MapBindingResult(Collections.emptyMap(), "");

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        Referee referee = new RefereeBuilder().application(applicationForm).id(12).build();
        ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).build();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(12);
        EasyMock.expect(encryptionHelperMock.encrypt(12)).andReturn("refereeId");
        EasyMock.expect(refereeServiceMock.getRefereeById(12)).andReturn(referee);
        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expectLastCall();
        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
        refereeServiceMock.refresh(referee);
        EasyMock.expectLastCall();

        EasyMock.replay(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
        String viewName = controller.submitRefereesData(applicationForm, sendToPorticoDataDTO, porticoResult, refereesAdminEditDTO, result, true, model);
        assertEquals("/private/staff/supervisors/components/reference_portico_validation", viewName);

        EasyMock.verify(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
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

        EasyMock.replay(applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
        RefereesAdminEditDTO dto = controller.getRefereesAdminEditDTO("1");
        EasyMock.verify(applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);

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

        EasyMock.replay(applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);
        RefereesAdminEditDTO dto = controller.getRefereesAdminEditDTO("1");
        EasyMock.verify(applicationServiceMock, scoringDefinitionParserMock, scoreFactoryMock);

        assertNotNull(dto);
        assertEquals(0, dto.getScores().size());
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

    @Before
    public void setUp() {
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        currentUserMock = EasyMock.createMock(RegisteredUser.class);
        approvalRoundValidatorMock = EasyMock.createMock(ApprovalRoundValidator.class);
        supervisorPropertyEditorMock = EasyMock.createMock(SupervisorPropertyEditor.class);
        approvalServiceMock = EasyMock.createMock(ApprovalService.class);
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        commentValidatorMock = EasyMock.createMock(GenericCommentValidator.class);
        refereesAdminEditDTOValidatorMock = EasyMock.createMock(RefereesAdminEditDTOValidator.class);
        qualificationServiceMock = EasyMock.createMock(QualificationService.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        sendToPorticoDataDTOEditorMock = EasyMock.createMock(SendToPorticoDataDTOEditor.class);
        sendToPorticoDataDTOValidatorMock = EasyMock.createMock(SendToPorticoDataDTOValidator.class);
        datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
        domicileServiceMock = EasyMock.createMock(DomicileService.class);
        domicilePropertyEditorMock = EasyMock.createMock(DomicilePropertyEditor.class);
        scoringDefinitionParserMock = EasyMock.createMock(ScoringDefinitionParser.class);
        scoresPropertyEditorMock = EasyMock.createMock(ScoresPropertyEditor.class);
        scoreFactoryMock = EasyMock.createMock(ScoreFactory.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(EasyMock.anyObject(ApplicationForm.class))).andReturn(true);
        EasyMock.replay(userServiceMock, currentUserMock);

        bindingResultMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResultMock);

        controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
                supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock, refereesAdminEditDTOValidatorMock, qualificationServiceMock,
                refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOEditorMock, sendToPorticoDataDTOValidatorMock, datePropertyEditorMock,
                domicileServiceMock, domicilePropertyEditorMock, messageSourceMock, scoringDefinitionParserMock, scoresPropertyEditorMock, scoreFactoryMock,
                applicationFormUserRoleServiceMock, actionsProviderMock);
    }
}