package com.zuehlke.pgadmissions.controllers.workflow.rejection;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.propertyeditors.RejectReasonPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RejectService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RejectionValidator;

public class RejectApplicationControllerTest {

    private static final String VIEW_RESULT = "private/staff/approver/reject_page";
    private static final String AFTER_REJECT_VIEW = "redirect:/applications";

    private RejectApplicationController controllerUT;

    private ApplicationForm application;
    private ApplicationsService applicationServiceMock;
    private RejectService rejectServiceMock;

    private RegisteredUser admin;
    private RegisteredUser approver;
    private RejectReason reason1;
    private RejectReason reason2;
    private Program program;
    private RejectReasonPropertyEditor rejectReasonPropertyEditorMock;
    private UserService userServiceMock;
    private RejectionValidator rejectionValidatorMock;
    private BindingResult errorsMock;
    private ActionsProvider actionsProviderMock;
    private ApplicationFormAccessService accessServiceMock;

    @Before
    public void setUp() {
        admin = new RegisteredUserBuilder().id(1).username("admin").role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();

        reason1 = new RejectReasonBuilder().id(10).text("idk").build();
        reason2 = new RejectReasonBuilder().id(20).text("idc").build();
        approver = new RegisteredUserBuilder().id(2).username("real approver").role(new RoleBuilder().authorityEnum(Authority.APPROVER).build()).build();
        program = new ProgramBuilder().id(100).administrators(admin).approver(approver).build();
        application = new ApplicationFormBuilder().id(10).status(ApplicationFormStatus.VALIDATION).applicationNumber("abc").program(program)//
                .build();

        rejectServiceMock = EasyMock.createMock(RejectService.class);
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        rejectReasonPropertyEditorMock = EasyMock.createMock(RejectReasonPropertyEditor.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        EasyMock.replay(userServiceMock);
        rejectionValidatorMock = EasyMock.createMock(RejectionValidator.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        controllerUT = new RejectApplicationController(applicationServiceMock, rejectServiceMock, userServiceMock, rejectReasonPropertyEditorMock,
                rejectionValidatorMock, actionsProviderMock, accessServiceMock, null);

        errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        EasyMock.replay(errorsMock);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getRejectionPage() {
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", admin);

        Assert.assertEquals(VIEW_RESULT, controllerUT.getRejectPage(modelMap));
    }

    @Test(expected = ActionNoLongerRequiredException.class)
    public void shouldThrowExceptionIfNoProviledgesForRejectionPage() {
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        modelMap.put("user", userMock);

        EasyMock.expect(userMock.hasAdminRightsOnApplication(application)).andReturn(false);

        EasyMock.replay(userMock);
        Assert.assertEquals(VIEW_RESULT, controllerUT.getRejectPage(modelMap));
        EasyMock.verify(userMock);
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
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("10")).andReturn(application);

        EasyMock.replay(applicationServiceMock);
        ApplicationForm applicationForm = controllerUT.getApplicationForm("10");
        EasyMock.verify(applicationServiceMock);

        Assert.assertEquals(application, applicationForm);
    }

    // -----------------------------------------
    // ------ Retrieve all available reasons:
    @Test
    public void getAvailbalbeReasons() {
        List<RejectReason> values = new ArrayList<RejectReason>();
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
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user).anyTimes();
        EasyMock.replay(userServiceMock);
        assertEquals(user, controllerUT.getUser());

    }

    // -------------------------------------------
    // ------- move application to reject:

    @Test
    public void moveToRejectWithValidRejection() {

        Rejection rejection = new RejectionBuilder().id(3).build();
        rejectServiceMock.moveApplicationToReject(application, admin, rejection);
        EasyMock.expectLastCall();
        rejectServiceMock.sendToPortico(application);

        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", admin);

        EasyMock.replay(rejectServiceMock);
        String nextView = controllerUT.moveApplicationToReject(rejection, errorsMock, modelMap);
        EasyMock.verify(rejectServiceMock);

        Assert.assertEquals(AFTER_REJECT_VIEW + "?messageCode=application.rejected&application=abc", nextView);
    }

    @Test
    public void returnToRejectViewWithInvalidRejection() {
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
