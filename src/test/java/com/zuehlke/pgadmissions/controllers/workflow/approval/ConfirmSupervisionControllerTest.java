package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.ConfirmSupervisionDTOValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ConfirmSupervisionControllerTest {


    @Mock
    @InjectIntoByType
    private ApplicationService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private ApprovalService approvalServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private LocalDatePropertyEditor datePropertyEditorMock;

    @Mock
    @InjectIntoByType
    private ConfirmSupervisionDTOValidator confirmSupervisionDTOValidatorMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @Mock
    @InjectIntoByType
    private ActionService actionsProviderMock;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceServiceMock;

    @TestedObject
    private ConfirmSupervisionController controller;
    
    @Test
    public void testLoadConfirmSupervisionPage() {
        Application applicationForm = new Application();
        User user = new User();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, SystemAction.APPLICATION_CONFIRM_SUPERVISION);

        EasyMock.replay(actionsProviderMock);
        String res = controller.confirmSupervision(modelMap);
        EasyMock.verify(actionsProviderMock);

        assertEquals("/private/staff/supervisors/confirm_supervision_page", res);
    }

    @Test
    public void testApplyConfirmSupervision() {
        User user = new User();
        Application applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();

        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
        confirmSupervisionDTO.setConfirmedSupervision(true);
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        approvalServiceMock.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, SystemAction.APPLICATION_CONFIRM_SUPERVISION);

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
    public void shouldReturnApplicationForm() {
        Application applicationForm = new Application();

        EasyMock.expect(applicationServiceMock.getByApplicationNumber("app1")).andReturn(applicationForm);

        EasyMock.replay(applicationServiceMock);
        assertEquals(applicationForm, controller.getApplicationForm("app1"));
        EasyMock.verify(applicationServiceMock);
    }

}