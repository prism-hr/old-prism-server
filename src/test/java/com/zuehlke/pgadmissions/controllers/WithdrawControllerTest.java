package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;
import org.unitils.UnitilsJUnit4TestClassRunner;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WithdrawService;
import com.zuehlke.pgadmissions.services.WorkflowService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class WithdrawControllerTest {

    private ApplicationService applicationsServiceMock;
    private WithdrawService withdrawServiceMock;
    private User student;

    private UserService userServiceMock;
    private WorkflowService applicationFormUserRoleServiceMock;
    private ActionService actionsProviderMock;

    private WithdrawController controller;

    @Test
    public void shouldChangeStatusToWithdrawnAndSaveAndSendEmailsNotifications() {
        Application applicationForm = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_VALIDATION)).applicant(student).id(2)
                .applicationNumber("abc").build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", student);

        actionsProviderMock.validateAction(applicationForm, student, ApplicationFormAction.APPLICATION_WITHDRAW);
        withdrawServiceMock.withdrawApplication(applicationForm);

        withdrawServiceMock.sendToPortico(applicationForm);

        EasyMock.replay(withdrawServiceMock, actionsProviderMock);
        String view = controller.withdrawApplicationAndGetApplicationList(modelMap);
        EasyMock.verify(withdrawServiceMock, actionsProviderMock);

        assertEquals("redirect:/applications?messageCode=application.withdrawn&application=abc", view);
    }

    @Test
    public void shouldGetApplicationForm() {
        String applicationNumber = "abc";
        Application applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("abc")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        EasyMock.reset(userServiceMock);
        User currentUserMock = EasyMock.createMock(User.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        EasyMock.replay(userServiceMock, currentUserMock);
        Application returnedForm = controller.getApplicationForm(applicationNumber);
        assertEquals(applicationForm, returnedForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowExceptionIfApplicationNotFound() {
        String applicationNumber = "abc";
        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("abc")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm(applicationNumber);
    }

}
