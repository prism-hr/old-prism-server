package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RefereesAdminEditDTOValidator;

public class EditApplicationFormAsProgrammeAdminControllerTest {

    private EditApplicationFormAsProgrammeAdminController controller;
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private EncryptionHelper encryptionHelperMock;
    private DocumentPropertyEditor documentPropertyEditorMock;

    private QualificationService qualificationServiceMock;
    private RefereeService refereeServiceMock;
    private RefereesAdminEditDTOValidator refereesAdminEditDTOValidatorMock;
    private SendToPorticoDataDTOEditor sendToPorticoDataDTOEditorMock;
    private CountryService countryServiceMock;
    private CountryPropertyEditor countryPropertyEditorMock;

    @Before
    public void setUp() {
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        qualificationServiceMock = EasyMock.createMock(QualificationService.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        refereesAdminEditDTOValidatorMock = EasyMock.createMock(RefereesAdminEditDTOValidator.class);
        sendToPorticoDataDTOEditorMock = EasyMock.createMock(SendToPorticoDataDTOEditor.class);
        countryServiceMock = EasyMock.createMock(CountryService.class);
        countryPropertyEditorMock = EasyMock.createMock(CountryPropertyEditor.class);

        controller = new EditApplicationFormAsProgrammeAdminController(userServiceMock, applicationServiceMock, documentPropertyEditorMock,
                qualificationServiceMock, refereeServiceMock, refereesAdminEditDTOValidatorMock, sendToPorticoDataDTOEditorMock, encryptionHelperMock,
                countryServiceMock, countryPropertyEditorMock);
    }

    @Test
    public void shouldAddNewReferenceWithoutSavingSendToPorticoReferences() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        Referee referee = new RefereeBuilder().application(applicationForm).id(8).toReferee();
        ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).build();

        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expectLastCall();
        EasyMock.expect(encryptionHelperMock.encrypt(8)).andReturn("refereeId");
        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
        refereeServiceMock.refresh(referee);
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, encryptionHelperMock, refereeServiceMock);
        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, true, model);
        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);

        EasyMock.verify(userServiceMock, encryptionHelperMock, refereeServiceMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUpdateReference() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expectLastCall();
        EasyMock.expect(refereeServiceMock.editReferenceComment(refereesAdminEditDTO)).andReturn(null).once();

        EasyMock.replay(userServiceMock, refereeServiceMock);
        String ret = controller.updateReference(applicationForm, refereesAdminEditDTO, result, model);
        Map<String, String> retMap = new Gson().fromJson(ret, Map.class);
        assertEquals(1, retMap.size());
        assertEquals("true", retMap.get("success"));

        EasyMock.verify(userServiceMock, refereeServiceMock);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldReportUpdateReferenceFormErrors() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        result.rejectValue("comment", "text.field.empty");
        Model model = new ExtendedModelMap();

        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, refereeServiceMock);
        String ret = controller.updateReference(applicationForm, refereesAdminEditDTO, result, model);
        Map<String, String> retMap = new Gson().fromJson(ret, Map.class);
        assertEquals(2, retMap.size());
        assertEquals("false", retMap.get("success"));
        assertEquals("text.field.empty", retMap.get("comment"));

        EasyMock.verify(userServiceMock, refereeServiceMock);
    }

    @Test
    public void shouldSaveSendToPorticoReferencesWithoutAddingNewReference() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, encryptionHelperMock, refereeServiceMock);
        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);

        EasyMock.verify(userServiceMock, encryptionHelperMock, refereeServiceMock);
    }

    @Test
    public void shouldSaveSendToPorticoReferencesAndAddNewReference() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());
        EasyMock.expectLastCall();

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

        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expectLastCall();
        EasyMock.expect(encryptionHelperMock.encrypt(8)).andReturn("refereeId");
        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
        refereeServiceMock.refresh(referee);
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);

        EasyMock.verify(userServiceMock, encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
    }

    @Test
    public void shouldSaveSendToPorticoReferencesAndReportFormErrors() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getRefereesSendToPortico());
        EasyMock.expectLastCall();

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        result.reject("error");
        Model model = new ExtendedModelMap();

        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
        String viewName = controller.submitRefereesData(applicationForm, refereesAdminEditDTO, result, sendToPorticoDataDTO, null, model);
        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);

        EasyMock.verify(userServiceMock, encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
    }

    @Test
    public void shouldSaveSendToPorticoQualifications() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getQualificationsSendToPortico());
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, encryptionHelperMock, qualificationServiceMock);
        String viewName = controller.submitQualificationsData(sendToPorticoDataDTO, applicationForm);
        assertEquals("OK", viewName);
        EasyMock.verify(userServiceMock, encryptionHelperMock, qualificationServiceMock);
    }

    @Test
    public void shouldSaveEmptySendToPorticoQualifications() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setQualificationsSendToPortico(Arrays.asList(new Integer[] {}));

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoDataDTO.getQualificationsSendToPortico());
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, qualificationServiceMock);
        String viewName = controller.submitQualificationsData(sendToPorticoDataDTO, applicationForm);
        assertEquals("OK", viewName);
        EasyMock.verify(userServiceMock, qualificationServiceMock);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldInterruptQualificationSubmittingIfUserNotAllowed() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        RegisteredUser applicant = new RegisteredUserBuilder().id(2).firstName("Franciszek").lastName("Pieczka").email("pieczka@test.com").build();

        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.INTERVIEW).build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setQualificationsSendToPortico(Arrays.asList(new Integer[] {}));

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        EasyMock.replay(userServiceMock);

        controller.submitQualificationsData(sendToPorticoDataDTO, applicationForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldInterruptReferenceSubmittingIfUserNotAllowed() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        RegisteredUser applicant = new RegisteredUserBuilder().id(2).firstName("Franciszek").lastName("Pieczka").email("pieczka@test.com").build();

        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.INTERVIEW).build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] {}));

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        EasyMock.replay(userServiceMock);

        controller.submitRefereesData(applicationForm, null, null, sendToPorticoDataDTO, null, null);
    }

    @Test
    public void shouldGetMainPage() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        EasyMock.replay(userServiceMock, qualificationServiceMock);
        String viewName = controller.view(applicationForm);

        assertEquals("/private/staff/admin/application/main_application_page_programme_administrator", viewName);
        EasyMock.verify(userServiceMock, qualificationServiceMock);
    }

    @Test
    public void shouldRegisterPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(refereesAdminEditDTOValidatorMock);
        EasyMock.expectLastCall();

        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        EasyMock.expectLastCall();

        binderMock.registerCustomEditor(Country.class, countryPropertyEditorMock);
        EasyMock.expectLastCall();

        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        EasyMock.expectLastCall();

        binderMock.registerCustomEditor(EasyMock.eq(String[].class), EasyMock.anyObject(StringArrayPropertyEditor.class));
        EasyMock.expectLastCall();

        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);

        EasyMock.verify(binderMock);
    }

}
