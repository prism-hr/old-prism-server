package com.zuehlke.pgadmissions.controllers.workflow.rejection;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.propertyeditors.RejectReasonPropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
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
    private ApplicationFormService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private RejectService rejectServiceMock;

    @Mock
    @InjectIntoByType
    private RejectReasonPropertyEditor rejectReasonPropertyEditorMock;

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
    public void shouldGetNewRejection() {
        Rejection rejection = controllerUT.getRejection();
        Assert.assertNull(rejection.getId());
    }

    @Test
    public void shouldRegisterRejectReasonProperyEditor() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.registerCustomEditor(RejectReason.class, rejectReasonPropertyEditorMock);
        binderMock.setValidator(rejectionValidatorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        EasyMock.replay(binderMock);
        controllerUT.registerBindersAndValidators(binderMock);
        EasyMock.verify(binderMock);
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

    // -----------------------------------------
    // ------ Retrieve all available reasons:
    @Test
    public void getAvailbalbeReasons() {
        List<RejectReason> values = new ArrayList<RejectReason>();
        RejectReason reason1 = new RejectReasonBuilder().id(10).text("idk").build();
        RejectReason reason2 = new RejectReasonBuilder().id(20).text("idc").build();
        values.add(reason1);
        values.add(reason2);
        EasyMock.expect(rejectServiceMock.getAllRejectionReasons()).andReturn(values);
        EasyMock.replay(rejectServiceMock);

        List<RejectReason> allReasons = controllerUT.getAvailableReasons();

        EasyMock.verify(rejectServiceMock);
        Assert.assertNotNull(allReasons);
        Assert.assertTrue(allReasons.containsAll(values));
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

    @Test
    public void moveToRejectWithValidRejection() {
        Application application = new Application();
        User admin = new User().withId(1);
        Rejection rejection = new RejectionBuilder().id(3).build();
        BindingResult result = new BeanPropertyBindingResult(rejection, "rejection");

        rejectServiceMock.moveApplicationToReject(application, rejection);
        EasyMock.expectLastCall();
        rejectServiceMock.sendToPortico(application);

        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", admin);

        EasyMock.replay(rejectServiceMock);
        String nextView = controllerUT.moveApplicationToReject(rejection, result, modelMap);
        EasyMock.verify(rejectServiceMock);

        Assert.assertEquals(AFTER_REJECT_VIEW + "?messageCode=application.rejected&application=abc", nextView);
    }

    @Test
    public void returnToRejectViewWithInvalidRejection() {
        Application application = new Application();
        User admin = new User().withId(1);
        Rejection rejection = new RejectionBuilder().id(3).build();
        BindingResult result = new DirectFieldBindingResult(rejection, "rejection");
        result.reject("error");

        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", admin);

        EasyMock.replay(rejectServiceMock);
        String nextView = controllerUT.moveApplicationToReject(rejection, result, modelMap);
        EasyMock.verify(rejectServiceMock);

        Assert.assertEquals(VIEW_RESULT, nextView);
    }

}
