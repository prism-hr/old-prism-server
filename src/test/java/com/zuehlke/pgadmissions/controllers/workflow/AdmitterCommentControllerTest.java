package com.zuehlke.pgadmissions.controllers.workflow;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.COMMENT;
import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_ELIGIBILITY;
import static java.util.Arrays.asList;
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
import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientPrivilegesException;
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
    
    private RegisteredUser currentUser;
    
    @Test(expected = InsufficientPrivilegesException.class)
    public void shouldThrowExceptionIfCurrentUserHasNotenoughPrivileges() {
        currentUser = new RegisteredUserBuilder()
        .roles(new RoleBuilder().authorityEnum(Authority.VIEWER).build())
        .build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        
        replay();
        controller.getApplicationForm("id");
        verify();
    }
    
    @Test
    public void shouldGetActionsDefinitions() {
        ApplicationForm form = new ApplicationFormBuilder()
            .applicationNumber("app_id")
            .id(12).build();
        currentUser = new RegisteredUserBuilder()
            .roles(new RoleBuilder().authorityEnum(Authority.ADMITTER).build())
            .build();
        Set<ApplicationFormAction> actionsSet = new HashSet<ApplicationFormAction>();
        actionsSet.addAll(asList(COMMENT, CONFIRM_ELIGIBILITY));
        ActionsDefinitions actions = new ActionsDefinitions(actionsSet, false);
        
        expect(applicationsServiceMock.getApplicationByApplicationNumber("app_id")).andReturn(form);
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        expect(actionsProviderMock.calculateActions(currentUser, form)).andReturn(actions);
        
        replay();
        ActionsDefinitions result = controller.getActionsDefinition("app_id");
        verify();
        
        assertNotNull(result);
        assertNotNull(result.getActions());
        assertEquals(2, result.getActions().size());
        assertTrue(result.getActions().contains(COMMENT));
        assertTrue(result.getActions().contains(CONFIRM_ELIGIBILITY));
    }
    
    @Test
    public void shouldGetComment() {
        ApplicationForm form = new ApplicationFormBuilder()
        .applicationNumber("app_id")
        .id(12).build();
        currentUser = new RegisteredUserBuilder()
        .roles(new RoleBuilder().authorityEnum(Authority.ADMITTER).build())
        .build();
                
        expect(applicationsServiceMock.getApplicationByApplicationNumber("app_id")).andReturn(form);
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        
        replay();
        AdmitterComment result = controller.getComment("app_id");
        verify();
        
        assertNotNull(result);
        assertEquals(form.getId(), result.getApplication().getId());
        assertEquals(currentUser.getId(), result.getUser().getId());
    }
    
    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowExceptionIfApplicationNotFound() {
        currentUser = new RegisteredUserBuilder()
            .roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build())
            .build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        expect(applicationsServiceMock.getApplicationByApplicationNumber("id")).andReturn(null);
        
        replay();
        controller.getApplicationForm("id");
        verify();
    }
    
    @Test
    public void shouldReturnApplication() {
        currentUser = new RegisteredUserBuilder()
        .roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build())
        .build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        
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
        assertEquals("private/staff/admin/comment/genericcomment", controller.getGenericCommentPage());
    }
    
    @Test(expected = InsufficientPrivilegesException.class)
    public void shouldThrowExceptionIfUserHasNoPrivilegeToConfirmEligibility() {
        currentUser = new RegisteredUserBuilder()
        .roles(new RoleBuilder().authorityEnum(Authority.VIEWER).build())
        .build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        
        replay();
        controller.submitAdmitterComment("id", null, null);
        verify();
    }
    
    @Test
    public void shouldReturnDefaultViewIfCommentContainsErrors() {
        currentUser = new RegisteredUserBuilder().id(123)
                .roles(new RoleBuilder().authorityEnum(Authority.ADMITTER).build())
                .build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        
        expect(resultMock.hasErrors()).andReturn(false);
        
        AdmitterComment comment = new AdmitterCommentBuilder().build();
        
        ApplicationForm form = new ApplicationFormBuilder().id(12).applicationNumber("appId").build();
        expect(applicationsServiceMock.getApplicationByApplicationNumber("appId")).andReturn(form);
        
        Event event = new ConfirmEligibilityEvent();
        event.setId(654);
        event.setApplication(form);
        event.setDate(new Date());
        event.setUser(currentUser);
        expect(eventFactoryMock.createEvent(comment)).andReturn(event);
        
        accessServiceMock.updateAccessTimestamp(eq(form), eq(currentUser), EasyMock.isA(Date.class));
        applicationsServiceMock.save(form);
        commentServiceMock.save(comment);
        mailServiceMock.scheduleAdmitterProvidedCommentNotification(form);
        
        replay();
        String result = controller.submitAdmitterComment("appId", comment, resultMock);
        verify();
        
        assertEquals("redirect:/applications?messageCode=validation.comment.success&application=appId", result);
        assertEquals(currentUser, comment.getUser());
        assertNotNull(comment.getDate());
        assertEquals(form, comment.getApplication());
        assertNull(form.getAdminRequestedRegistry());
        assertFalse(form.isRegistryUsersDueNotification());
        
        ApplicationFormUpdate update = form.getApplicationUpdates().get(0);
        assertNotNull(update);
        assertEquals(form, update.getApplicationForm());
        assertEquals(ApplicationUpdateScope.INTERNAL, update.getUpdateVisibility());
        assertNotNull(update.getUpdateTimestamp());
        
        assertNotNull(form.getEvents().get(0));
        assertEquals(event, form.getEvents().get(0));
    }
    
    
}
