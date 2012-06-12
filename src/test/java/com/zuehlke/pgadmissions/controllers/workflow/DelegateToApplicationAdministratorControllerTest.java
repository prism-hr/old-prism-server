package com.zuehlke.pgadmissions.controllers.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.PlainTextUserPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class DelegateToApplicationAdministratorControllerTest {

	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;
	private DelegateToApplicationAdministratorController controller;
	private UserPropertyEditor userPropertyEditorMock;

	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser currentUser = new RegisteredUserBuilder().id(4).toUser();

		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		assertSame(currentUser, controller.getCurrentUser());
	}

	@Test
	public void shouldGetApplicationFromId() {
		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();
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

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

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
		binderMock.registerCustomEditor(RegisteredUser.class,"applicationAdministrator", userPropertyEditorMock);

		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldSaveApplicationAndRedirectToApplicationsList(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock);
		String view = controller.delegateToApplicationAdministrator(applicationForm);
		assertEquals("redirect:/applications", view);
		EasyMock.verify(applicationServiceMock);
	}
	
	@Test
	public void shouldResetReviewReminder(){		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).notificationRecords(new NotificationRecordBuilder().id(1).notificationType(NotificationType.REVIEW_REMINDER).toNotificationRecord()).toApplicationForm();
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(applicationServiceMock);
		controller.delegateToApplicationAdministrator(applicationForm);
		assertNull(applicationForm.getNotificationForType(NotificationType.REVIEW_REMINDER));
		
	}
	@Before
	public void setup() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);
		controller = new DelegateToApplicationAdministratorController(applicationServiceMock, userServiceMock, userPropertyEditorMock);

	}
}
