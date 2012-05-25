package com.zuehlke.pgadmissions.controllers.workflow.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.UnsupportedEncodingException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
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
	public void shouldSendEmailToRegistryContacts() throws UnsupportedEncodingException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		registryMailSenderMock.sendApplicationToRegistryContacts(applicationForm);
		EasyMock.replay(registryMailSenderMock);
		ModelAndView modelAndView = controller.sendHelpRequestToRegistryContacts(applicationForm);
		assertEquals("private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("Email send", modelAndView.getModel().get("message"));
		EasyMock.verify(registryMailSenderMock);
	}

	@Test
	public void shouldReturnCorrectMessageIfEmailSendFails() throws UnsupportedEncodingException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		registryMailSenderMock.sendApplicationToRegistryContacts(applicationForm);
		EasyMock.expectLastCall().andThrow(new UnsupportedEncodingException("ALL WRONG!"));
		EasyMock.replay(registryMailSenderMock);
		ModelAndView modelAndView = controller.sendHelpRequestToRegistryContacts(applicationForm);
		assertEquals("private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("Email sending failed", modelAndView.getModel().get("message"));
		EasyMock.verify(registryMailSenderMock);
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
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm(5);
		assertEquals(applicationForm, returnedForm);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioNDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(null);
		EasyMock.replay(applicationServiceMock);

		controller.getApplicationForm(5);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserDoesNotHaveAdminRights() {

		Program program = new ProgramBuilder().id(6).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).toApplicationForm();

		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock);
		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationById(5)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, userServiceMock, currentUserMock);

		controller.getApplicationForm(5);
	}

	
	@Before	
	public void setUp(){
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		registryMailSenderMock = EasyMock.createMock(RegistryMailSender.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new EmailRegistryController(registryMailSenderMock,applicationServiceMock , userServiceMock);
	}
}
