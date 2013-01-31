package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
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

    @Before
    public void setUp() {
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        qualificationServiceMock = EasyMock.createMock(QualificationService.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        refereesAdminEditDTOValidatorMock = EasyMock.createMock(RefereesAdminEditDTOValidator.class);

        controller = new EditApplicationFormAsProgrammeAdminController(userServiceMock, applicationServiceMock, documentPropertyEditorMock,
                qualificationServiceMock, refereeServiceMock, refereesAdminEditDTOValidatorMock, encryptionHelperMock);
    }

    @Test
    public void shouldPostNewReference() {

        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.INTERVIEW).build();

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        Referee referee = new RefereeBuilder().application(applicationForm).toReferee();

        // no errors
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);
        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(8);
        EasyMock.expect(refereeServiceMock.getRefereeById(8)).andReturn(referee);
        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(null);
        refereeServiceMock.refresh(referee);
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, encryptionHelperMock, refereeServiceMock);
        String viewName = controller.submitReference(refereesAdminEditDTO, result, applicationForm, model);

        assertEquals("/private/staff/admin/application/components/references_details_programme_admin", viewName);
        assertEquals("refereeId", model.asMap().get("editedRefereeId"));

        EasyMock.verify(userServiceMock, encryptionHelperMock, refereeServiceMock);
    }

    @Test
    public void shouldSaveSendToPorticoReferences() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);
        EasyMock.expect(encryptionHelperMock.decryptToInteger("ref-1")).andReturn(1);
        EasyMock.expect(encryptionHelperMock.decryptToInteger("ref-2")).andReturn(2);

        List<Integer> sendToPortico = Arrays.asList(new Integer[] { 1, 2 });
        refereeServiceMock.selectForSendingToPortico("app1", sendToPortico);
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, encryptionHelperMock);
        String viewName = controller.submitRefereesData("{\"referees\":[\"ref-1\",\"ref-2\"]}", applicationForm);
        assertEquals("OK", viewName);

        EasyMock.verify(userServiceMock, encryptionHelperMock);
    }

    @Test
    public void shouldSaveSendToPorticoQualifications() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);
        EasyMock.expect(encryptionHelperMock.decryptToInteger("qual-1")).andReturn(1);
        EasyMock.expect(encryptionHelperMock.decryptToInteger("qual-2")).andReturn(2);

        List<Integer> sendToPortico = Arrays.asList(new Integer[] { 1, 2 });
        qualificationServiceMock.selectForSendingToPortico("app1", sendToPortico);
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, encryptionHelperMock, qualificationServiceMock);
        String viewName = controller.submitQualificationsData("{\"qualifications\":[\"qual-1\",\"qual-2\"]}", applicationForm);
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

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin1);

        qualificationServiceMock.selectForSendingToPortico("app1", Collections.<Integer> emptyList());
        EasyMock.expectLastCall();

        EasyMock.replay(userServiceMock, qualificationServiceMock);
        String viewName = controller.submitQualificationsData("{\"qualifications\":[]}", applicationForm);
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

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        EasyMock.replay(userServiceMock);

        controller.submitQualificationsData("{\"qualifications\":[\"qual-1\",\"qual-2\"]}", applicationForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldInterruptReferenceSubmittingIfUserNotAllowed() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        RegisteredUser applicant = new RegisteredUserBuilder().id(2).firstName("Franciszek").lastName("Pieczka").email("pieczka@test.com").build();

        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.INTERVIEW).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        EasyMock.replay(userServiceMock);

        controller.submitRefereesData("{\"referees\":[\"ref-1\",\"ref-2\"]}", applicationForm);
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

        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        EasyMock.expectLastCall();

        binderMock.registerCustomEditor(EasyMock.eq(String[].class), EasyMock.anyObject(StringArrayPropertyEditor.class));
        EasyMock.expectLastCall();

        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);

        EasyMock.verify(binderMock);
    }

}
