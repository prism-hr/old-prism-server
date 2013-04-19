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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.PrismMailMessageException;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.Environment;

public class MailSendingServiceTest {
	
	protected static final String SAMPLE_REJECTION_REASON = "You ain't goog enough";

	protected static final String SAMPLE_PROGRAM_TITLE = "MRes Security Science";

	protected static final String SAMPLE_APPLICATION_NUMBER = "TMRSECSING01-2013-000004";

	protected static final String SAMPLE_ADMIN2_EMAIL_ADDRESS = "admin2@mail.com";

	protected static final String SAMPLE_ADMIN1_EMAIL_ADDRESS = "admin1@mail.com";

	protected static final String SAMPLE_APPLICANT_SURNAME = "Capatonda";

	protected static final String SAMPLE_APPLICANT_NAME = "Maccio";

	protected static final String SAMPLE_APPLICANT_EMAIL_ADDRESS = "capatonda@mail.com";

	protected MailSendingService service;
	
	protected MailSender mockMailSender;

	protected ConfigurationService configurationServiceMock;
	
	protected ApplicationFormDAO applicationFormDAOMock;

	@Before
	public void setup() {
		mockMailSender = createMock(MailSender.class);
		configurationServiceMock = createMock(ConfigurationService.class);
		applicationFormDAOMock = createMock(ApplicationFormDAO.class);
		service = new MailSendingService(mockMailSender, configurationServiceMock, applicationFormDAOMock);
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
		model2.putAll(model1);
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
		model2.putAll(model1);
		model2.put("user", user2);
		
		expect(mockMailSender.resolveMessage("reference.data.import.error", (Object[])null))
		.andReturn("UCL Prism to Portico Import Error");
		
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		expectLastCall().times(2);
		
		replay(mockMailSender);
		service.sendImportErrorMessage(asList(user1, user2),messageCode, timestamp);
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
	public void shouldScheduleInterviewAdministrationRequest() {
		ApplicationForm form = getSampleApplicationForm();
		RegisteredUser delegate = new RegisteredUserBuilder().id(1).build();
		
		service.scheduleInterviewAdministrationRequest(delegate, form);
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(delegate.getDigestNotificationType(), DigestNotificationType.TASK_NOTIFICATION);
		assertEquals(admins.get(0).getDigestNotificationType(), DigestNotificationType.TASK_NOTIFICATION);
		assertEquals(admins.get(1).getDigestNotificationType(), DigestNotificationType.TASK_NOTIFICATION);
	}
	
	@Test
	public void shouldScheduleReferenceSubmitConfirmation() {
		ApplicationForm form = getSampleApplicationForm();
		
		service.scheduleReferenceSubmitConfirmation(form);
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(form.getApplicant().getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(admins.get(0).getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(admins.get(1).getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
	}
	
	@Test
	public void shouldScheduleSupervisorConfirmedSupervision() {
		ApplicationForm form = getSampleApplicationForm();
		
		service.scheduleSupervisionConfirmedNotification(form);
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(admins.get(0).getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(admins.get(1).getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
	}
	
	@Test
	public void shouldScheduleWithdrawalConfirmation() {
		ApplicationForm form = getSampleApplicationForm();

		RegisteredUser refereeUser1 = new RegisteredUserBuilder().id(10).build();
		RegisteredUser refereeUser2 = new RegisteredUserBuilder().id(11).build();
		Referee referee1 = new RefereeBuilder().user(refereeUser1).build();
		Referee referee2 = new RefereeBuilder().user(refereeUser2).build();
		
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(12).build();
		RegisteredUser reviewerUser2 = new RegisteredUserBuilder().id(13).build();
		Reviewer reviewer1 = new ReviewerBuilder().user(reviewerUser1).build();
		Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).build();
		form.setLatestReviewRound(new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build());
		
		RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(14).build();
		RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(15).build();
		Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).build();
		Interviewer interviewer2 = new InterviewerBuilder().user(interviewerUser2).build();
		form.setLatestInterview(new InterviewBuilder().interviewers(interviewer1, interviewer2).build());
		
		RegisteredUser supervisorUser1 = new RegisteredUserBuilder().id(16).build();
		RegisteredUser supervisorUser2 = new RegisteredUserBuilder().id(17).build();
		Supervisor supervisor1 = new SupervisorBuilder().user(supervisorUser1).build();
		Supervisor supervisor2 = new SupervisorBuilder().user(supervisorUser2).build();
		form.setLatestApprovalRound(new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build());
	
		service.scheduleWithdrawalConfirmation(asList(referee1, referee2), form);
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(admins.get(0).getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(admins.get(1).getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		
		assertEquals(refereeUser1.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(refereeUser2.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		
		assertEquals(reviewerUser1.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(reviewerUser2.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		
		assertEquals(interviewerUser1.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(interviewerUser2.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		
		assertEquals(supervisorUser1.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(supervisorUser2.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
	}
	
	@Test
	public void sendRefereeRequestShouldSuccessfullySendMessage() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		Referee referee = new RefereeBuilder().id(0).user(user).build();
		String adminMails = SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS;
		ApplicationForm form = getSampleApplicationForm();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("referee", referee);
		model.put("adminsEmails", adminMails);
		model.put("applicant", form.getApplicant());
		model.put("application", form);
		model.put("programme", form.getProgrammeDetails());
		model.put("host", Environment.getInstance().getApplicationHostName());
		
		String subjectToReturn=SAMPLE_APPLICANT_NAME+" "+SAMPLE_APPLICANT_SURNAME+" "
		+"Application "+SAMPLE_APPLICATION_NUMBER+" for UCL "+SAMPLE_PROGRAM_TITLE+" - Reference Request";
		
		expect(mockMailSender.resolveMessage("reference.request", SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME))
				.andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		
		replay(mockMailSender);
		service.sendReferenceRequest(referee, form);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void sendRefereeRequestShouldSuccessfullySendMessageWithNoApplicant() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		Referee referee = new RefereeBuilder().id(0).user(user).build();
		String adminMails = SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS;
		ApplicationForm form = getSampleApplicationForm();
		form.setApplicant(null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("referee", referee);
		model.put("adminsEmails", adminMails);
		model.put("applicant", form.getApplicant());
		model.put("application", form);
		model.put("programme", form.getProgrammeDetails());
		model.put("host", Environment.getInstance().getApplicationHostName());
		
		expect(mockMailSender.resolveMessage("reference.request", SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE))
		.andReturn("[2] [3] Application "+SAMPLE_APPLICATION_NUMBER+" for UCL "+SAMPLE_PROGRAM_TITLE+" - Reference Request");
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		
		replay(mockMailSender);
		service.sendReferenceRequest(referee, form);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		
		assertEquals("[2] [3] Application "+SAMPLE_APPLICATION_NUMBER+" for UCL "+SAMPLE_PROGRAM_TITLE+" - Reference Request", message.getSubjectCode());
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
		service.sendRegistrationConfirmation(user, action);
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
		service.sendRegistrationConfirmation(user, null);
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
	
	@Test
	public void shouldSendSubmissionConfirmationToApplicant() {
		Person person1 = new PersonBuilder()
			.email("person1@mail.com")
			.firstname("Ivo")
			.lastname("avido")
			.build();
		Person person2 = new PersonBuilder()
			.email("person2@mail.com")
			.firstname("Ektor")
			.lastname("Baboden")
			.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());

		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn="Application "+SAMPLE_APPLICATION_NUMBER+" for UCL "+SAMPLE_PROGRAM_TITLE;
		expect(mockMailSender.resolveMessage("validation.submission.applicant", SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE))
						.andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		replay(mockMailSender, configurationServiceMock);
		service.sendSubmissionConfirmationToApplicant(form);
		verify(mockMailSender, configurationServiceMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendSubmissionConfirmationToApplicantAndAddReasonToModel() {
		Person person1 = new PersonBuilder()
		.email("person1@mail.com")
		.firstname("Ivo")
		.lastname("avido")
		.build();
		Person person2 = new PersonBuilder()
		.email("person2@mail.com")
		.firstname("Ektor")
		.lastname("Baboden")
		.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(ApplicationFormStatus.REJECTED);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());
		model.put("reason", form.getRejection().getRejectionReason());
		
		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn="Application "+SAMPLE_APPLICATION_NUMBER+" for UCL "+SAMPLE_PROGRAM_TITLE;
		expect(mockMailSender.resolveMessage("validation.submission.applicant", SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE))
						.andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		replay(mockMailSender, configurationServiceMock);
		service.sendSubmissionConfirmationToApplicant(form);
		verify(mockMailSender, configurationServiceMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendSubmissionConfirmationToApplicantAndAddProspectusLinkToModel() {
		Person person1 = new PersonBuilder()
		.email("person1@mail.com")
		.firstname("Ivo")
		.lastname("avido")
		.build();
		Person person2 = new PersonBuilder()
		.email("person2@mail.com")
		.firstname("Ektor")
		.lastname("Baboden")
		.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(ApplicationFormStatus.REJECTED);
		form.getRejection().setIncludeProspectusLink(true);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());
		model.put("reason", form.getRejection().getRejectionReason());
		model.put("prospectusLink",  Environment.getInstance().getUCLProspectusLink());
		
		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn="Application "+SAMPLE_APPLICATION_NUMBER+" for UCL "+SAMPLE_PROGRAM_TITLE;
		expect(mockMailSender.resolveMessage("validation.submission.applicant", SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE))
						.andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		replay(mockMailSender, configurationServiceMock);
		service.sendSubmissionConfirmationToApplicant(form);
		verify(mockMailSender, configurationServiceMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendRejectionConfirmationToUser() {
		Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
		Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());

		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - " + form.getOutcomeOfStage().displayValue() + " Outcome";
		expect(
				mockMailSender.resolveMessage("rejection.notification", SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage()
								.displayValue())).andReturn(subjectToReturn);

		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

		replay(mockMailSender, configurationServiceMock);
		service.sendRejectionConfirmationToApplicant(form);
		verify(mockMailSender, configurationServiceMock);

		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer) 1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendRejectionConfirmationToUserAndAddReasonToModel() {
		Person person1 = new PersonBuilder()
		.email("person1@mail.com")
		.firstname("Ivo")
		.lastname("avido")
		.build();
		Person person2 = new PersonBuilder()
		.email("person2@mail.com")
		.firstname("Ektor")
		.lastname("Baboden")
		.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(ApplicationFormStatus.REJECTED);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());
		model.put("reason", form.getRejection().getRejectionReason());
		
		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - " + form.getOutcomeOfStage().displayValue() + " Outcome";
		expect(
				mockMailSender.resolveMessage("rejection.notification", SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage()
								.displayValue())).andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		replay(mockMailSender, configurationServiceMock);
		service.sendRejectionConfirmationToApplicant(form);
		verify(mockMailSender, configurationServiceMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendRejectionConfirmationToUserAndAddProspectusLinkToModel() {
		Person person1 = new PersonBuilder()
		.email("person1@mail.com")
		.firstname("Ivo")
		.lastname("avido")
		.build();
		Person person2 = new PersonBuilder()
		.email("person2@mail.com")
		.firstname("Ektor")
		.lastname("Baboden")
		.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(ApplicationFormStatus.REJECTED);
		form.getRejection().setIncludeProspectusLink(true);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());
		model.put("reason", form.getRejection().getRejectionReason());
		model.put("prospectusLink",  Environment.getInstance().getUCLProspectusLink());
		
		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - " + form.getOutcomeOfStage().displayValue() + " Outcome";
		expect(
				mockMailSender.resolveMessage("rejection.notification", SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage()
								.displayValue())).andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		replay(mockMailSender, configurationServiceMock);
		service.sendRejectionConfirmationToApplicant(form);
		verify(mockMailSender, configurationServiceMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendApprovedNotification() {
		Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
		Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());

		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - Approval Outcome";
		expect(
				mockMailSender.resolveMessage("approved.notification.applicant", SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage()
								.displayValue())).andReturn(subjectToReturn);

		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

		replay(mockMailSender, configurationServiceMock);
		service.sendApprovedNotification(form);
		verify(mockMailSender, configurationServiceMock);

		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer) 1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendApprovedNotificationAndAddReasonToModel() {
		Person person1 = new PersonBuilder()
		.email("person1@mail.com")
		.firstname("Ivo")
		.lastname("avido")
		.build();
		Person person2 = new PersonBuilder()
		.email("person2@mail.com")
		.firstname("Ektor")
		.lastname("Baboden")
		.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(ApplicationFormStatus.REJECTED);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());
		model.put("reason", form.getRejection().getRejectionReason());
		
		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - Approval Outcome";
		expect(
				mockMailSender.resolveMessage("approved.notification.applicant", SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage()
								.displayValue())).andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		replay(mockMailSender, configurationServiceMock);
		service.sendApprovedNotification(form);
		verify(mockMailSender, configurationServiceMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendApprovedNotificationAndAddProspectusLinkToModel() {
		Person person1 = new PersonBuilder()
		.email("person1@mail.com")
		.firstname("Ivo")
		.lastname("avido")
		.build();
		Person person2 = new PersonBuilder()
		.email("person2@mail.com")
		.firstname("Ektor")
		.lastname("Baboden")
		.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(ApplicationFormStatus.REJECTED);
		form.getRejection().setIncludeProspectusLink(true);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());
		model.put("reason", form.getRejection().getRejectionReason());
		model.put("prospectusLink",  Environment.getInstance().getUCLProspectusLink());
		
		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - Approval Outcome";
		expect(
				mockMailSender.resolveMessage("approved.notification.applicant", SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage()
								.displayValue())).andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		replay(mockMailSender, configurationServiceMock);
		service.sendApprovedNotification(form);
		verify(mockMailSender, configurationServiceMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendInterviewConfirmationToInterviewers() {
		ApplicationForm form = getSampleApplicationForm();
		Interview interview = new InterviewBuilder().application(form).build();
		RegisteredUser user1 = new RegisteredUserBuilder().id(1).build();
		RegisteredUser user2 = new RegisteredUserBuilder().id(2).build();
		Interviewer interviewer1 = new InterviewerBuilder().user(user1).interview(interview).build();
		Interviewer interviewer2 = new InterviewerBuilder().user(user2).interview(interview).build();
		
		Map<String, Object> model1 = new HashMap<String, Object>();
		model1.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model1.put("interviewer", interviewer1);
		model1.put("application", form);
		model1.put("applicant", form.getApplicant());
		model1.put("host", getInstance().getApplicationHostName());
		Map<String, Object> model2 =  new HashMap<String, Object>();
		model2.putAll(model1);
		model2.put("interviewer", interviewer2);
		
		String subjectToReturn = SAMPLE_APPLICANT_NAME+" " +SAMPLE_APPLICANT_SURNAME+
				" Application "+SAMPLE_APPLICATION_NUMBER+" for UCL "+SAMPLE_PROGRAM_TITLE+" - Interview Confirmation";
		expect(mockMailSender.resolveMessage("interview.notification.interviewer", SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME))
		.andReturn(subjectToReturn).times(2);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		expectLastCall().times(2);
		
		replay(mockMailSender);
		service.sendInterviewConfirmationToInterviewers(asList(interviewer1, interviewer2));
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValues().get(0);
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model1, message.getModel());
		
		message = messageCaptor.getValues().get(1);
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)2, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model2, message.getModel());
	}
	
	@Test
	public void shouldSendInterviewConfirmationToApplicant() {
		Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
		Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(null);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());

		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - Interview Confirmation";
		expect(
				mockMailSender.resolveMessage("interview.notification.applicant", SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage()
								.displayValue())).andReturn(subjectToReturn);

		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

		replay(mockMailSender, configurationServiceMock);
		service.sendInterviewConfirmationToApplicant(form);
		verify(mockMailSender, configurationServiceMock);

		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer) 1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendInterviewConfirmationToApplicantAndAddReasonToModel() {
		Person person1 = new PersonBuilder()
		.email("person1@mail.com")
		.firstname("Ivo")
		.lastname("avido")
		.build();
		Person person2 = new PersonBuilder()
		.email("person2@mail.com")
		.firstname("Ektor")
		.lastname("Baboden")
		.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(ApplicationFormStatus.REJECTED);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());
		model.put("reason", form.getRejection().getRejectionReason());
		
		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - Interview Confirmation";
		expect(
				mockMailSender.resolveMessage("interview.notification.applicant", SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage()
								.displayValue())).andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		replay(mockMailSender, configurationServiceMock);
		service.sendInterviewConfirmationToApplicant(form);
		verify(mockMailSender, configurationServiceMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test
	public void shouldSendInterviewConfirmationToApplicantAndAddProspectusLinkToModel() {
		Person person1 = new PersonBuilder()
		.email("person1@mail.com")
		.firstname("Ivo")
		.lastname("avido")
		.build();
		Person person2 = new PersonBuilder()
		.email("person2@mail.com")
		.firstname("Ektor")
		.lastname("Baboden")
		.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		form.setStatus(ApplicationFormStatus.REJECTED);
		form.getRejection().setIncludeProspectusLink(true);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("registryContacts", registryUsers);
		model.put("host", getInstance().getApplicationHostName());
		model.put("admissionOfferServiceLevel", getInstance().getAdmissionsOfferServiceLevel());
		model.put("previousStage", form.getOutcomeOfStage());
		model.put("reason", form.getRejection().getRejectionReason());
		model.put("prospectusLink",  Environment.getInstance().getUCLProspectusLink());
		
		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - Interview Confirmation";
		expect(
				mockMailSender.resolveMessage("interview.notification.applicant", SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage()
								.displayValue())).andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		replay(mockMailSender, configurationServiceMock);
		service.sendInterviewConfirmationToApplicant(form);
		verify(mockMailSender, configurationServiceMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	protected void assertModelEquals(Map<String, Object> expected, Map<String, Object> actual) {
		assertEquals("The size of the expected and actual models don't match",
				expected.size(), actual.size());
		for (Map.Entry<String, Object> entry: expected.entrySet()) {
			assertTrue("Model doesn't contain key: "+entry.getKey(), actual.containsKey(entry.getKey()));
			assertEquals("Expected: "+entry.getValue()+" but was: "+actual.get(entry.getKey()),
					entry.getValue(), actual.get(entry.getKey()));
		}
	}
	
	protected ApplicationForm getSampleApplicationForm() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1)
				.email(SAMPLE_APPLICANT_EMAIL_ADDRESS)
				.firstName(SAMPLE_APPLICANT_NAME)
				.lastName(SAMPLE_APPLICANT_SURNAME)
				.build();
		RegisteredUser applicant1 = new RegisteredUserBuilder().id(2)
				.email(SAMPLE_ADMIN1_EMAIL_ADDRESS)
				.build();
		RegisteredUser applicant2 = new RegisteredUserBuilder().id(3)
				.email(SAMPLE_ADMIN2_EMAIL_ADDRESS)
				.build();
		Program program = new ProgramBuilder().id(4)
				.title(SAMPLE_PROGRAM_TITLE)
				.administrators(applicant1, applicant2)
				.build();
		ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(5)
				.build();
		RejectReason reason = new RejectReasonBuilder()
				.text(SAMPLE_REJECTION_REASON)
				.build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(6)
				.status(ApplicationFormStatus.APPROVED)
				.rejection(new RejectionBuilder()
						.rejectionReason(reason)
						.includeProspectusLink(false)
						.build())
				.applicant(applicant)
				.programmeDetails(programDetails)
				.applicationNumber(SAMPLE_APPLICATION_NUMBER)
				.program(program)
				.build();
		return applicationForm;
		
	}
	
}
