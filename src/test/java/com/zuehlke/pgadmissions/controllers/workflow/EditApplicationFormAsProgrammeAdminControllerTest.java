package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertEquals;

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
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
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
    private CountryService countryServiceMock;
    private CountryPropertyEditor countryPropertyEditorMock;
    private MessageSource messageSourceMock;

    @Before
    public void setUp() {
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        refereesAdminEditDTOValidatorMock = EasyMock.createMock(RefereesAdminEditDTOValidator.class);
        sendToPorticoDataDTOEditorMock = EasyMock.createMock(SendToPorticoDataDTOEditor.class);
        countryServiceMock = EasyMock.createMock(CountryService.class);
        countryPropertyEditorMock = EasyMock.createMock(CountryPropertyEditor.class);
        messageSourceMock = EasyMock.createMock(MessageSource.class);

        controller = new EditApplicationFormAsProgrammeAdminController(userServiceMock, applicationServiceMock, documentPropertyEditorMock, refereeServiceMock,
                refereesAdminEditDTOValidatorMock, sendToPorticoDataDTOEditorMock, encryptionHelperMock, countryServiceMock, countryPropertyEditorMock,
                messageSourceMock);
    }

    @Test
    public void shouldAddNewReferenceWithoutSavingSendToPorticoReferences() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        Referee referee = new RefereeBuilder().application(applicationForm).id(8).toReferee();
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
    public void shouldUpdateReference() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).build();

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expect(refereeServiceMock.editReferenceComment(refereesAdminEditDTO)).andReturn(null);

        EasyMock.replay(refereeServiceMock);
        String ret = controller.updateReference(applicationForm, refereesAdminEditDTO, result, model);
        Map<String, String> retMap = new Gson().fromJson(ret, Map.class);
        assertEquals(1, retMap.size());
        assertEquals("true", retMap.get("success"));

        EasyMock.verify(refereeServiceMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReportUpdateReferenceFormErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).build();

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        result.rejectValue("comment", "text.field.empty");
        Model model = new ExtendedModelMap();

        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);

        EasyMock.expect(messageSourceMock.getMessage("text.field.empty", null, Locale.getDefault())).andReturn("empty field");

        EasyMock.replay(refereeServiceMock, messageSourceMock);
        String ret = controller.updateReference(applicationForm, refereesAdminEditDTO, result, model);
        Map<String, String> retMap = new Gson().fromJson(ret, Map.class);
        assertEquals(2, retMap.size());
        assertEquals("false", retMap.get("success"));
        assertEquals("empty field", retMap.get("comment"));

        EasyMock.verify(refereeServiceMock, messageSourceMock);
    }

    @Test
    public void shouldSaveSendToPorticoReferencesWithoutAddingNewReference() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        Referee referee = new RefereeBuilder().application(applicationForm).id(8).toReferee();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());

        EasyMock.replay(encryptionHelperMock, refereeServiceMock);
        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
        EasyMock.verify(encryptionHelperMock, refereeServiceMock);

        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);
    }

    @Test
    public void shouldSaveSendToPorticoReferencesAndAddNewReference() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).build();
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

        Referee referee = new RefereeBuilder().application(applicationForm).id(8).toReferee();
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
    public void shouldSaveSendToPorticoReferencesAndReportFormErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).build();
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

        Referee referee = new RefereeBuilder().application(applicationForm).id(8).toReferee();

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
    public void shouldGetMainPage() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").status(ApplicationFormStatus.INTERVIEW).build();

        String viewName = controller.view(applicationForm);
        assertEquals("/private/staff/admin/application/main_application_page_programme_administrator", viewName);
    }

    @Test
    public void shouldRegisterPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        
        binderMock.setValidator(refereesAdminEditDTOValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor(Country.class, countryPropertyEditorMock);
        binderMock.registerCustomEditor((Class<?>) EasyMock.isNull(), EasyMock.eq("comment"), EasyMock.isA(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String[].class), EasyMock.anyObject(StringArrayPropertyEditor.class));

        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);

        EasyMock.verify(binderMock);
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfUserCannotSeeApplicationForm() {
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(userMock).anyTimes();
        EasyMock.replay(userServiceMock);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("3")).andReturn(applicationForm);
        EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
        EasyMock.replay(applicationServiceMock, userMock);

        controller.getApplicationForm("3");
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfUserIsNotInterviewer() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(2).firstName("Franciszek").lastName("Pieczka").email("pieczka@test.com").build();

        Program program = new ProgramBuilder().title("some title").build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.INTERVIEW).applicant(applicant).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant).anyTimes();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("3")).andReturn(applicationForm);

        EasyMock.replay(userServiceMock, applicationServiceMock);
        controller.getApplicationForm("3");
    }

}
