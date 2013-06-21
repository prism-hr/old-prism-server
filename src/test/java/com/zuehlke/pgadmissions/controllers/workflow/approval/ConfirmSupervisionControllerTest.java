package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_SUPERVISION;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.PrimarySupervisorNotDefinedException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
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

    private ApplicationFormAccessService accessServiceMock;

    private ActionsProvider actionsProviderMock;

    @Test
    public void testLoadConfirmSupervisionPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        RegisteredUser user = new RegisteredUser();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, CONFIRM_SUPERVISION);

        EasyMock.replay(actionsProviderMock);
        String res = controller.confirmSupervision(modelMap);
        EasyMock.verify(actionsProviderMock);

        assertEquals("/private/staff/supervisors/confirm_supervision_page", res);
    }

    @Test
    public void testApplyConfirmSupervision() {
        RegisteredUser user = new RegisteredUser();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();

        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
        confirmSupervisionDTO.setConfirmedSupervision(true);
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        approvalServiceMock.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, CONFIRM_SUPERVISION);

        EasyMock.replay(approvalServiceMock, actionsProviderMock);
        String res = controller.applyConfirmSupervision(confirmSupervisionDTO, result, modelMap);
        EasyMock.verify(approvalServiceMock, actionsProviderMock);

        assertEquals("redirect:/applications?messageCode=supervision.confirmed&application=app1", res);
    }

    @Test
    public void testDontApplyConfirmSupervisionIfFormErrors() {
        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
        confirmSupervisionDTO.setConfirmedSupervision(true);
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        result.reject("error");

        EasyMock.replay(approvalServiceMock);
        String res = controller.applyConfirmSupervision(confirmSupervisionDTO, result, new ModelMap());
        EasyMock.verify(approvalServiceMock);

        assertEquals("/private/staff/supervisors/confirm_supervision_page", res);
    }

    @Test
    public void shouldReturnApplicationFormIfCurrentUserIsPrimarySupervisor() {
        ApplicationForm applicationForm = new ApplicationForm();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("app1")).andReturn(applicationForm);

        EasyMock.replay(applicationServiceMock);
        assertEquals(applicationForm, controller.getApplicationForm("app1"));
        EasyMock.verify(applicationServiceMock);
    }

    @Before
    public void setUp() {
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        approvalServiceMock = EasyMock.createMock(ApprovalService.class);
        datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
        confirmSupervisionDTOValidatorMock = EasyMock.createMock(ConfirmSupervisionDTOValidator.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        controller = new ConfirmSupervisionController(applicationServiceMock, userServiceMock, approvalServiceMock, datePropertyEditorMock,
                confirmSupervisionDTOValidatorMock, accessServiceMock, actionsProviderMock);

    }

}
