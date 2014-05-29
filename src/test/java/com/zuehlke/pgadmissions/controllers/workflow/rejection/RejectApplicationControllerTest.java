package com.zuehlke.pgadmissions.controllers.workflow.rejection;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.RejectService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.RejectionValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class RejectApplicationControllerTest {

    private static final String VIEW_RESULT = "private/staff/approver/reject_page";
    private static final String AFTER_REJECT_VIEW = "redirect:/applications";

    @Mock
    @InjectIntoByType
    private ApplicationService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private RejectService rejectServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private RejectionValidator rejectionValidatorMock;

    @Mock
    @InjectIntoByType
    private ActionService actionServiceMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @TestedObject
    private RejectApplicationController controllerUT;

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getRejectionPage() {
        User admin = new User().withId(1);
        Application application = new Application();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", admin);

        Assert.assertEquals(VIEW_RESULT, controllerUT.getRejectPage(modelMap));
    }

    @Test
    public void shouldReturnApplicationForm() {
        Application application = new Application();
        EasyMock.expect(applicationServiceMock.getByApplicationNumber("10")).andReturn(application);

        EasyMock.replay(applicationServiceMock);
        Application applicationForm = controllerUT.getApplicationForm("10");
        EasyMock.verify(applicationServiceMock);

        Assert.assertEquals(application, applicationForm);
    }

    @Test
    public void shouldGetCurrentUserAsUser() {
        EasyMock.reset(userServiceMock);
        User user = new User().withId(1);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();
        EasyMock.replay(userServiceMock);
        assertEquals(user, controllerUT.getUser());

    }

    // -------------------------------------------
    // ------- move application to reject:

}
