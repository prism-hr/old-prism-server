package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.PrimarySupervisorNotDefinedException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ConfirmSupervisionDTOValidator;

public class ConfirmSupervisionControllerTest {

    private ConfirmSupervisionController controller;

    private ApplicationsService applicationServiceMock;

    private ApprovalService approvalServiceMock;

    private UserService userServiceMock;

    private DatePropertyEditor datePropertyEditorMock;

    private ConfirmSupervisionDTOValidator confirmSupervisionDTOValidatorMock;

    @Test
    public void testLoadConfirmSupervisionPage() {
        String res = controller.confirmSupervision(null);
        assertEquals("/private/staff/supervisors/confirm_supervision_page", res);
    }

    @Test
    public void testApplyConfirmSupervision() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();

        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
        confirmSupervisionDTO.setConfirmedSupervision(true);
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        approvalServiceMock.confirmSupervision(applicationForm, confirmSupervisionDTO);
        EasyMock.expectLastCall().once();

        EasyMock.replay(approvalServiceMock);

        String res = controller.applyConfirmSupervision(applicationForm, confirmSupervisionDTO, result);
        assertEquals("redirect:/applications?messageCode=supervision.confirmed&application=app1", res);

        EasyMock.verify(approvalServiceMock);
    }

    @Test
    public void testDontApplyConfirmSupervisionIfFormErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();

        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
        confirmSupervisionDTO.setConfirmedSupervision(true);
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        result.reject("error");

        EasyMock.replay(approvalServiceMock);

        String res = controller.applyConfirmSupervision(applicationForm, confirmSupervisionDTO, result);
        assertEquals("/private/staff/supervisors/confirm_supervision_page", res);

        EasyMock.verify(approvalServiceMock);
    }

    @Test(expected = PrimarySupervisorNotDefinedException.class)
    public void shouldNotReturnApplicationFormIfPrimarySupervisorIsNotDefined() {
        Supervisor supervisor = new SupervisorBuilder().id(13).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).latestApprovalRound(approvalRound).build();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);

        EasyMock.replay(applicationServiceMock);

        controller.getApplicationForm("app1");
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldNotReturnApplicationFormIfPrimarySupervisorAlreadyConfirmed() {
        RegisteredUser primarySupervisorUser = new RegisteredUserBuilder().id(88).build();

        Supervisor primarySupervisor = new SupervisorBuilder().id(14).user(primarySupervisorUser).isPrimary(true).confirmedSupervision(true).build();

        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(primarySupervisor).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).latestApprovalRound(approvalRound).build();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(primarySupervisorUser);

        EasyMock.replay(applicationServiceMock, userServiceMock);

        controller.getApplicationForm("app1");
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldNotReturnApplicationFormIfCurrentUserIsNotPrimarySupervisor() {
        RegisteredUser primarySupervisorUser = new RegisteredUserBuilder().id(88).build();
        RegisteredUser secondarySupervisorUser = new RegisteredUserBuilder().id(89).build();

        Supervisor secondarySupervisor = new SupervisorBuilder().id(13).user(secondarySupervisorUser).build();
        Supervisor primarySupervisor = new SupervisorBuilder().id(14).user(primarySupervisorUser).isPrimary(true).build();

        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(secondarySupervisor, primarySupervisor).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).latestApprovalRound(approvalRound).build();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(secondarySupervisorUser);

        EasyMock.replay(applicationServiceMock, userServiceMock);

        controller.getApplicationForm("app1");
    }

    @Test
    public void shouldReturnApplicationFormIfCurrentUserIsPrimarySupervisor() {
        RegisteredUser primarySupervisorUser = new RegisteredUserBuilder().id(88).build();
        RegisteredUser secondarySupervisorUser = new RegisteredUserBuilder().id(89).build();

        Supervisor secondarySupervisor = new SupervisorBuilder().id(13).user(secondarySupervisorUser).build();
        Supervisor primarySupervisor = new SupervisorBuilder().id(14).user(primarySupervisorUser).isPrimary(true).build();

        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(secondarySupervisor, primarySupervisor).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(8).latestApprovalRound(approvalRound).build();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(primarySupervisorUser);

        EasyMock.replay(applicationServiceMock, userServiceMock);

        assertEquals(applicationForm, controller.getApplicationForm("app1"));

        EasyMock.verify(applicationServiceMock, userServiceMock);
    }

    @Before
    public void setUp() {
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        approvalServiceMock = EasyMock.createMock(ApprovalService.class);
        datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);

        controller = new ConfirmSupervisionController(applicationServiceMock, userServiceMock, approvalServiceMock, datePropertyEditorMock,
                confirmSupervisionDTOValidatorMock);

    }

}
