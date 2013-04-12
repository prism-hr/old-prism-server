package com.zuehlke.pgadmissions.mail.refactor;

import static com.zuehlke.pgadmissions.utils.Environment.getInstance;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.PrismMailMessageException;
import com.zuehlke.pgadmissions.utils.Environment;

public class MailSendingServiceTest {
	
	private MailSendingService service;
	
	private TemplateAwareMailSender mockMailSender;
	
	@Before
	public void setup() {
		mockMailSender = createMock(TemplateAwareMailSender.class);
		service = new MailSendingService(mockMailSender);
	}
	
	@Test
	public void sendExportErrorMessageShouldSuccessfullySendMessage() throws Exception {
		RegisteredUser user1 = new RegisteredUserBuilder().id(1).build();
		RegisteredUser user2 = new RegisteredUserBuilder().id(2).build();
		String messageCode = "message_code";
		Date timestamp = new Date();
		Map<String, Object> model1 = new HashMap<String, Object>();
		model1.put("user", user1);
		model1.put("message", messageCode);
		model1.put("time", timestamp);
		model1.put("host", getInstance().getApplicationHostName());
		Map<String, Object> model2 =  new HashMap<String, Object>();
		model1.putAll(model1);
		model2.put("user", user2);
		
		expect(mockMailSender.resolveMessage("reference.data.export.error", (Object[])null))
		.andReturn("UCL Prism to Portico Export Error");
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		expectLastCall().times(2);
		
		replay(mockMailSender);
		service.sendExportErrorMessage(asList(user1, user2), messageCode, timestamp);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValues().get(0);
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals("UCL Prism to Portico Export Error", message.getSubjectCode());
		assertModelEquals(model1, message.getModel());
		
		message = messageCaptor.getValues().get(1);
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)2, message.getTo().get(0).getId());
		assertEquals("UCL Prism to Portico Export Error", message.getSubjectCode());
		assertModelEquals(model2, message.getModel());
	}
	
	@Test
	public void sendImportErrorMessageShouldSuccessfullySendMessage() throws Exception {
		RegisteredUser user1 = new RegisteredUserBuilder().id(1).build();
		RegisteredUser user2 = new RegisteredUserBuilder().id(2).build();
		String messageCode = "message_code";
		Date timestamp = new Date();
		Map<String, Object> model1 = new HashMap<String, Object>();
		model1.put("user", user1);
		model1.put("message", messageCode);
		model1.put("time", timestamp);
		model1.put("host", getInstance().getApplicationHostName());
		Map<String, Object> model2 =  new HashMap<String, Object>();
		model1.putAll(model1);
		model2.put("user", user2);
		
		expect(mockMailSender.resolveMessage("reference.data.import.error", (Object[])null))
		.andReturn("UCL Prism to Portico Import Error");
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		expectLastCall().times(2);
		
		replay(mockMailSender);
		service.sendImportErrorMessage(asList(user1, user2), messageCode, timestamp);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValues().get(0);
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals("UCL Prism to Portico Import Error", message.getSubjectCode());
		assertModelEquals(model1, message.getModel());
		
		message = messageCaptor.getValues().get(1);
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)2, message.getTo().get(0).getId());
		assertEquals("UCL Prism to Portico Import Error", message.getSubjectCode());
		assertModelEquals(model2, message.getModel());
	}
	
	@Test
	public void sendInterviewAdministrationReminderShouldSuccessfullySendMessage() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		RegisteredUser admin1 = new RegisteredUserBuilder().id(2).build();
		RegisteredUser admin2  = new RegisteredUserBuilder().id(3).build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).build();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", user);
		model.put("applicationForm", form);
		model.put("host", Environment.getInstance().getApplicationHostName());
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		expect(mockMailSender.resolveMessage("application.interview.delegation", (Object[])null)).andReturn("Application interview administration delegation");
		
		replay(mockMailSender);
		service.sendInterviewAdministrationReminder(user, Arrays.asList(admin1, admin2), form);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertNotNull(message.getCc());
		assertEquals(2, message.getCc().size());
		assertEquals((Integer)2, message.getCc().get(0).getId());
		assertEquals((Integer)3, message.getCc().get(1).getId());
		
		assertEquals("Application interview administration delegation", message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void sendRefereeMailNotificationShouldSuccessfullySendMessage() throws Exception {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).firstName("Maccio").
				lastName("Capatonda").build();
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		Referee referee = new RefereeBuilder().id(0).user(user).build();
		String adminMails = "admin_mail1, admin_mail2";
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(12).build();
		Program programme = new ProgramBuilder().id(75).title("programme_title").build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(programme).
				applicationNumber("form_number").programmeDetails(programmeDetails).applicant(applicant).build();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("referee", referee);
		model.put("adminEmails", adminMails);
		model.put("applicant", applicant);
		model.put("programme", programmeDetails);
		model.put("host", Environment.getInstance().getApplicationHostName());
		
		expect(mockMailSender.resolveMessage("reference.request", "form_number", "programme_title", "Maccio", "Capatonda"))
				.andReturn("Maccio Capatonda Application form_number for UCL programme_title - Reference Request");
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		
		replay(mockMailSender);
		service.sendRefereeMailNotification(referee, form, adminMails);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		
		assertEquals("Maccio Capatonda Application form_number for UCL programme_title - Reference Request", message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void sendReferenceSubmitConfirmationToAdministratorsShouldSuccessfullySendMessage() throws Exception {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).firstName("Maccio").
				lastName("Capatonda").build();
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		RegisteredUser admin1 = new RegisteredUserBuilder().id(3).build();
		RegisteredUser admin2 = new RegisteredUserBuilder().id(4).build();
		Referee referee = new RefereeBuilder().id(0).user(user).build();
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(12).build();
		Program programme = new ProgramBuilder().id(75).title("programme_title").build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(programme).
				applicationNumber("form_number").programmeDetails(programmeDetails).applicant(applicant).build();
		Map<String, Object> model1 = new HashMap<String, Object>();
		model1.put("admin", admin1);
		model1.put("application", form);
		model1.put("referee", referee);
		model1.put("host", Environment.getInstance().getApplicationHostName());
	
		Map<String, Object> model2 = new HashMap<String, Object>();
		model2.putAll(model1);
		model2.put("admin", admin2);
		
		expect(mockMailSender.resolveMessage("reference.provided.admin", "form_number", "programme_title", "Maccio", "Capatonda"))
		.andReturn("Maccio Capatonda Application form_number for UCL programme_title - Reference Request");
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		expectLastCall().times(2);
		
		
		replay(mockMailSender);
		service.sendReferenceSubmitConfirmationToAdministrators(referee, asList(admin1, admin2), form);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValues().get(0);
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)3, message.getTo().get(0).getId());
		assertEquals("Maccio Capatonda Application form_number for UCL programme_title - Reference Request", message.getSubjectCode());
		assertModelEquals(model1, message.getModel());
		
		message = messageCaptor.getValues().get(1);
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)4, message.getTo().get(0).getId());
		assertEquals("Maccio Capatonda Application form_number for UCL programme_title - Reference Request", message.getSubjectCode());
		assertModelEquals(model2, message.getModel());
	}
	
	@Test
	public void sendRefereeMailNotificationShouldSuccessfullySendMessageWithNoApplicant() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		Referee referee = new RefereeBuilder().id(0).user(user).build();
		String adminMails = "admin_mail1, admin_mail2";
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(12).build();
		Program programme = new ProgramBuilder().id(75).title("programme_title").build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(programme).
				applicationNumber("form_number").programmeDetails(programmeDetails).build();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("referee", referee);
		model.put("adminEmails", adminMails);
		model.put("applicant", null);
		model.put("programme", programmeDetails);
		model.put("host", Environment.getInstance().getApplicationHostName());
		
		expect(mockMailSender.resolveMessage("reference.request", "form_number", "programme_title"))
		.andReturn("[2] [3] Application form_number for UCL programme_title - Reference Request");
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		
		replay(mockMailSender);
		service.sendRefereeMailNotification(referee, form, adminMails);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		
		assertEquals("[2] [3] Application form_number for UCL programme_title - Reference Request", message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void sendResetPasswordShouldSuccessfullySendMessage() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		String newPassword = "password";
		Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("newPassword", newPassword);
        model.put("host", Environment.getInstance().getApplicationHostName());
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		expect(mockMailSender.resolveMessage("user.password.reset", (Object[])null)).andReturn("New Password for UCL Prism");
		
		replay(mockMailSender);
		service.sendResetPasswordMessage(user, newPassword);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals("New Password for UCL Prism", message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void sendConfirmationEmailToRegisteringUserShouldSuccessfullySendMessage() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		String action = "action";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", user);
		model.put("action", action);
		model.put("host", getInstance().getApplicationHostName());
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		expect(mockMailSender.resolveMessage("registration.confirmation", (Object[])null)).andReturn("Your Registration for UCL Prism");
		
		replay(mockMailSender);
		service.sendConfirmationEmailToRegisteringUser(user, action);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals("Your Registration for UCL Prism", message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test(expected = PrismMailMessageException.class)
	public void sendConfirmationEmailToRegisteringUserShouldThrowExceptionIfActionIsNull() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		
		mockMailSender.sendEmail(isA(PrismEmailMessage.class));
		
		expect(mockMailSender.resolveMessage("registration.confirmation", (Object[])null)).andReturn("Your Registration for UCL Prism");
		
		replay(mockMailSender);
		service.sendConfirmationEmailToRegisteringUser(user, null);
		verify(mockMailSender);
	}
	
	@Test(expected = PrismMailMessageException.class)
	public void sendResetPasswordShouldThrowExceptionIfUserIsNull() throws Exception {
		String newPassword = "password";
		
		mockMailSender.sendEmail(isA(PrismEmailMessage.class));
		
		expect(mockMailSender.resolveMessage("user.password.reset", (Object[])null)).andReturn("New Password for UCL Prism");
		
		replay(mockMailSender);
		service.sendResetPasswordMessage(null, newPassword);
		verify(mockMailSender);
	}
	
	@Test(expected = PrismMailMessageException.class)
	public void sendResetPasswordShouldThrowExceptionIfSenderFails() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		String newPassword = "password";
		
		expect(mockMailSender.resolveMessage("user.password.reset", (Object[])null)).andReturn("New Password for UCL Prism");

		mockMailSender.sendEmail(isA(PrismEmailMessage.class));
		expectLastCall().andThrow(new RuntimeException());
		
		replay(mockMailSender);
		service.sendResetPasswordMessage(user, newPassword);
		verify(mockMailSender);
	}
	
	private void assertModelEquals(Map<String, Object> expected, Map<String, Object> actual) {
		for (Map.Entry<String, Object> entry: expected.entrySet()) {
			assertTrue(actual.containsKey(entry.getKey()));
			assertEquals(entry.getValue(), actual.get(entry.getKey()));
		}
	}
	
}
