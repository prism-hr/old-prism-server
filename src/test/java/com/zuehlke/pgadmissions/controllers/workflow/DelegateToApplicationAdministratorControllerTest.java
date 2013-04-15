package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class DelegateToApplicationAdministratorControllerTest {

    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private DelegateToApplicationAdministratorController controller;
    private NewUserByAdminValidator newUserByAdminValidator;
    private CommentService commentServiceMock;
    private MessageSource messageSourceMock;

    @Test
    public void shouldReturnCurrentUser() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(4).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        assertSame(currentUser, controller.getCurrentUser());
    }

    @Test
    public void shouldGetApplicationFromId() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfApplicatioNDoesNotExist() {
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationServiceMock);

        controller.getApplicationForm("5");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserDoesNotHaveAdminRights() {

        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

        controller.getApplicationForm("5");
    }

    @Test
    public void shouldRegisterUserPropertyEditor() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(newUserByAdminValidator);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldSaveApplicationAndRedirectToApplicationsListAssumingThatUserDoesNotExist() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("abc").build();
        RegisteredUser currentUser = new RegisteredUserBuilder().build();
        RegisteredUser proposedInterviewerDetails = new RegisteredUserBuilder().firstName("Claudia").lastName("Scanduro").email("cs@zuhlke.com").build();
        RegisteredUser applicationAdmin = new RegisteredUserBuilder().build();
        BindingResult delegatedInterviewerResult = new BeanPropertyBindingResult(proposedInterviewerDetails, "delegatedInterviewer"); 
        
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("cs@zuhlke.com")).andReturn(null);
        EasyMock.expect(
                userServiceMock.createNewUserInRole("Claudia", "Scanduro", "cs@zuhlke.com", Authority.INTERVIEWER,
                        DirectURLsEnum.VIEW_APPLIATION_PRIOR_TO_INTERVIEW, applicationForm)).andReturn(applicationAdmin);
        userServiceMock.sendEmailToDelegateAndRegisterReminder(applicationForm, applicationAdmin);

        EasyMock.replay(userServiceMock);
        controller.delegateToApplicationAdministrator(applicationForm, proposedInterviewerDetails, delegatedInterviewerResult);
        EasyMock.verify(userServiceMock);

        assertSame(applicationAdmin, applicationForm.getApplicationAdministrator());
    }

    @Test
    public void shouldResetReviewReminder() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1)
                .notificationRecords(new NotificationRecordBuilder().id(1).notificationType(NotificationType.REVIEW_REMINDER).build()).build();
        RegisteredUser currentUser = new RegisteredUserBuilder().build();
        RegisteredUser proposedInterviewerDetails = new RegisteredUserBuilder().firstName("Claudia").lastName("Scanduro").email("cs@zuhlke.com").build();
        RegisteredUser applicationAdmin = new RegisteredUserBuilder().build();
        BindingResult delegatedInterviewerResult = new BeanPropertyBindingResult(proposedInterviewerDetails, "delegatedInterviewer");

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("cs@zuhlke.com")).andReturn(applicationAdmin);
        userServiceMock.sendEmailToDelegateAndRegisterReminder(applicationForm, applicationAdmin);

        EasyMock.replay(userServiceMock);
        controller.delegateToApplicationAdministrator(applicationForm, proposedInterviewerDetails, delegatedInterviewerResult);
        EasyMock.verify(userServiceMock);

        assertNull(applicationForm.getNotificationForType(NotificationType.REVIEW_REMINDER));
    }

    @Test
    public void shouldCreateDelegationComment() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1)
                .notificationRecords(new NotificationRecordBuilder().id(1).notificationType(NotificationType.REVIEW_REMINDER).build()).build();
        RegisteredUser currentUser = new RegisteredUserBuilder().build();
        RegisteredUser proposedInterviewerDetails = new RegisteredUserBuilder().firstName("Claudia").lastName("Scanduro").email("cs@zuhlke.com").build();
        RegisteredUser applicationAdmin = new RegisteredUserBuilder().build();
        BindingResult delegatedInterviewerResult = new BeanPropertyBindingResult(proposedInterviewerDetails, "delegatedInterviewer");
        
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("cs@zuhlke.com")).andReturn(applicationAdmin);
        userServiceMock.sendEmailToDelegateAndRegisterReminder(applicationForm, applicationAdmin);
        commentServiceMock.createDelegateComment(currentUser, applicationForm);

        EasyMock.replay(userServiceMock, commentServiceMock);
        controller.delegateToApplicationAdministrator(applicationForm, proposedInterviewerDetails, delegatedInterviewerResult);
        EasyMock.verify(userServiceMock, commentServiceMock);
    }

    @Before
    public void setup() {
        commentServiceMock = EasyMock.createMock(CommentService.class);
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        newUserByAdminValidator = EasyMock.createMock(NewUserByAdminValidator.class);
        messageSourceMock = EasyMock.createMock(MessageSource.class);
        controller = new DelegateToApplicationAdministratorController(applicationServiceMock, userServiceMock, newUserByAdminValidator, commentServiceMock, messageSourceMock);

    }
}
