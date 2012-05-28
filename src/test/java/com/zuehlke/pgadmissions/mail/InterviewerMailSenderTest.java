package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.utils.Environment;

public class InterviewerMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private InterviewerMailSender interviewerMailSender;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).toUser();
		RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").toUser();
		
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram()).applicant(applicant).toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().id(4).user(defaultInterviewer).interview(new InterviewBuilder().id(5).application(form).toInterview()).toInterviewer();

		Map<String, Object> model = interviewerMailSender.createModel(interviewer);
		assertEquals("bob@test.com, alice@test.com", model.get("adminsEmails"));
		assertEquals(interviewer, model.get("interviewer"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}
	@Test
	public void shouldSendInterviewerNotificationForInterviewer() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		interviewerMailSender = new InterviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(Interviewer interviewer) {
				return model;
			}

		};
		
		
		RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").toUser();		
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().title("program abc").toProgram()).toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().id(4).user(defaultInterviewer).interview(new InterviewBuilder().id(5).application(form).toInterview()).toInterviewer();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Application 4 for program abc - Interviewer Notification",
						"private/interviewers/mail/interviewer_notification_email.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		interviewerMailSender.sendInterviewerNotification(interviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}
	
	@Test
	public void shouldSendInterviewerReminderForInterviewer() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		interviewerMailSender = new InterviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(Interviewer interviewer) {
				return model;
			}

		};
		
		
		RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").toUser();		
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().title("program abc").toProgram()).toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().id(4).user(defaultInterviewer).interview(new InterviewBuilder().id(5).application(form).toInterview()).toInterviewer();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Application 4 for program abc - Interview Feedback Reminder",
						"private/interviewers/mail/interviewer_reminder_email.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		interviewerMailSender.sendInterviewerReminder(interviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}
	
	
	
	
	@Before
	public void setUp() {

		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);

		interviewerMailSender = new InterviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

	}
}
