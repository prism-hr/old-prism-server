package com.zuehlke.pgadmissions.controllers.workflow;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_ELIGIBILITY;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.AdmitterComment;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.ConfirmEligibilityEvent;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdmitterCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AdmitterCommentValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class AdmitterCommentControllerTest {

    @TestedObject
    private AdmitterCommentController controller;

    @Mock
    @InjectIntoByType
    private ApplicationsService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private AdmitterCommentValidator admitterCommentValidatorMock;

    @Mock
    @InjectIntoByType
    private CommentService commentServiceMock;

    @Mock
    @InjectIntoByType
    private DocumentPropertyEditor documentPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private ActionsProvider actionsProviderMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormAccessService accessServiceMock;

    @Mock
    @InjectIntoByType
    private MailSendingService mailServiceMock;

    @Mock
    @InjectIntoByType
    private EventFactory eventFactoryMock;

    @Mock
    BindingResult resultMock;

    @Test
    public void shouldGetComment() {
        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("app_id").id(12).build();
        RegisteredUser currentUser = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.ADMITTER).build()).build();

        expect(applicationsServiceMock.getApplicationByApplicationNumber("app_id")).andReturn(form);
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();

        replay();
        AdmitterComment result = controller.getComment("app_id");
        verify();

        assertNotNull(result);
        assertEquals(form.getId(), result.getApplication().getId());
        assertEquals(currentUser.getId(), result.getUser().getId());
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionIfApplicationNotFound() {
        expect(applicationsServiceMock.getApplicationByApplicationNumber("id")).andReturn(null);

        replay();
        controller.getApplicationForm("id");
        verify();
    }

    @Test
    public void shouldReturnApplication() {
        ApplicationForm form = new ApplicationFormBuilder().id(12).build();
        expect(applicationsServiceMock.getApplicationByApplicationNumber("id")).andReturn(form);

        replay();
        ApplicationForm result = controller.getApplicationForm("id");
        verify();

        assertEquals(form.getId(), result.getId());
    }

    @Test
    public void isConfirmEligibilityCommentFlagShouldAlwaysBeTrue() {
        assertTrue(controller.isConfirmElegibilityComment());
    }

    @Test
    public void defaultGetMappingShouldAlwaysReturnGenericCommentPage() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(666).build();
        ApplicationForm applicationForm = new ApplicationForm();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", currentUser);

        actionsProviderMock.validateAction(applicationForm, currentUser, CONFIRM_ELIGIBILITY);

        replay();
        assertEquals("private/staff/admin/comment/genericcomment", controller.getConfirmEligibilityPage(modelMap));
        verify();
    }

    @Test
    public void shouldReturnDefaultViewIfCommentContainsErrors() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(123).roles(new RoleBuilder().authorityEnum(Authority.ADMITTER).build()).build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();

        expect(resultMock.hasErrors()).andReturn(false);

        AdmitterComment comment = new AdmitterCommentBuilder().build();

        ApplicationForm application = new ApplicationFormBuilder().id(12).applicationNumber("appId").build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);
        modelMap.put("user", currentUser);

        Event event = new ConfirmEligibilityEvent();
        event.setId(654);
        event.setApplication(application);
        event.setDate(new Date());
        event.setUser(currentUser);
        expect(eventFactoryMock.createEvent(comment)).andReturn(event);

        actionsProviderMock.validateAction(application, currentUser, CONFIRM_ELIGIBILITY);
        accessServiceMock.updateAccessTimestamp(eq(application), eq(currentUser), EasyMock.isA(Date.class));
        applicationsServiceMock.save(application);
        commentServiceMock.save(comment);
        mailServiceMock.scheduleAdmitterProvidedCommentNotification(application);

        replay();
        String result = controller.confirmEligibility(modelMap, comment, resultMock);
        verify();

        assertEquals("redirect:/applications?messageCode=validation.comment.success&application=appId", result);
        assertEquals(currentUser, comment.getUser());
        assertNotNull(comment.getDate());
        assertEquals(application, comment.getApplication());
        assertNull(application.getAdminRequestedRegistry());
        assertFalse(application.isRegistryUsersDueNotification());

        ApplicationFormUpdate update = application.getApplicationUpdates().get(0);
        assertNotNull(update);
        assertEquals(application, update.getApplicationForm());
        assertEquals(ApplicationUpdateScope.INTERNAL, update.getUpdateVisibility());
        assertNotNull(update.getUpdateTimestamp());

        assertNotNull(application.getEvents().get(0));
        assertEquals(event, application.getEvents().get(0));
    }

}
