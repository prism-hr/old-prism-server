package com.zuehlke.pgadmissions.controllers.workflow.validation;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.DocumentException;
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
import com.zuehlke.pgadmissions.mail.RegistryMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class EmailRegistryControllerTest {

	private RegistryMailSender registryMailSenderMock;
	private EmailRegistryController controller;
	private ApplicationsService applicationServiceMock;
	private UserService userServiceMock;

	@Test
	public void shouldSendEmailToRegistryContacts() throws MalformedURLException, DocumentException, IOException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		registryMailSenderMock.sendApplicationToRegistryContacts(applicationForm);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(registryMailSenderMock, applicationServiceMock);
		ModelAndView modelAndView = controller.sendHelpRequestToRegistryContacts(applicationForm);
		assertEquals("private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("registry.email.send", modelAndView.getModel().get("message"));
		EasyMock.verify(registryMailSenderMock, applicationServiceMock);
		NotificationRecord notification = applicationForm.getNotificationForType(NotificationType.REGISTRY_HELP_REQUEST);
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(notification.getDate(), Calendar.DATE));
	}

	@Test
	public void shouldUPdateNotificationRecordIfExists() throws MalformedURLException, DocumentException, IOException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationForm.getNotificationRecords().add(
				new NotificationRecordBuilder().notificationType(NotificationType.REGISTRY_HELP_REQUEST).notificationDate(DateUtils.addDays(new Date(), -10))
						.toNotificationRecord());
		registryMailSenderMock.sendApplicationToRegistryContacts(applicationForm);
		applicationServiceMock.save(applicationForm);
		EasyMock.replay(registryMailSenderMock, applicationServiceMock);
		ModelAndView modelAndView = controller.sendHelpRequestToRegistryContacts(applicationForm);
		assertEquals("private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("registry.email.send", modelAndView.getModel().get("message"));
		EasyMock.verify(registryMailSenderMock, applicationServiceMock);
		NotificationRecord notification = applicationForm.getNotificationForType(NotificationType.REGISTRY_HELP_REQUEST);
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(notification.getDate(), Calendar.DATE));
	}

	@Test
	public void shouldReturnCorrectMessageIfEmailSendFails() throws MalformedURLException, DocumentException, IOException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		registryMailSenderMock.sendApplicationToRegistryContacts(applicationForm);
		EasyMock.expectLastCall().andThrow(new UnsupportedEncodingException("ALL WRONG!"));
		EasyMock.replay(registryMailSenderMock, applicationServiceMock);
		ModelAndView modelAndView = controller.sendHelpRequestToRegistryContacts(applicationForm);
		assertEquals("private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("registry.email.failed", modelAndView.getModel().get("message"));
		assertNull(applicationForm.getNotificationForType(NotificationType.REGISTRY_HELP_REQUEST));
		EasyMock.verify(registryMailSenderMock, applicationServiceMock);
	}

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

	@Before
	public void setUp() {
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		registryMailSenderMock = EasyMock.createMock(RegistryMailSender.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new EmailRegistryController(registryMailSenderMock, applicationServiceMock, userServiceMock);
	}
}
