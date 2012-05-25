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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.utils.Environment;

public class RefereeMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private RefereeMailSender refereeMailSender;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {
		Referee referee = new RefereeBuilder().id(4).firstname("john").lastname("boggs").email("ref@test.com").toReferee();
		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram())
				.applicant(applicant).toApplicationForm();
		referee.setApplication(form);

		Map<String, Object> model = refereeMailSender.createModel(referee);
		assertEquals("bob@test.com, alice@test.com", model.get("adminsEmails"));
		assertEquals(referee, model.get("referee"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}

	@Test
	public void shouldSendRefereeRemindeForNewReferee() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		refereeMailSender = new RefereeMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(Referee referee) {
				return model;
			}

		};
		Referee referee = new RefereeBuilder().id(4).firstname("john").lastname("boggs").email("ref@test.com").toReferee();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		referee.setApplication(form);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("ref@test.com", "john boggs");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Reminder - reference required",
						"private/referees/mail/referee_reminder_email.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		refereeMailSender.sendRefereeReminder(referee);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}

	@Test
	public void shouldSendRefereeReminderUExistingUserReferee() throws UnsupportedEncodingException {

		final HashMap<String, Object> model = new HashMap<String, Object>();
		refereeMailSender = new RefereeMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			Map<String, Object> createModel(Referee referee) {

				return model;
			}

		};
		RegisteredUser user = new RegisteredUserBuilder().id(1).enabled(true).email("jboggs@test.com").firstName("Jonathan").lastName("Boggs").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("john").lastname("boggs").email("ref@test.com").user(user).toReferee();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		referee.setApplication(form);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("jboggs@test.com", "Jonathan Boggs");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Reminder - reference required",
						"private/referees/mail/existing_user_referee_reminder_email.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		refereeMailSender.sendRefereeReminder(referee);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}
	
	@Test
	public void shouldSendRefereeNotificationForNewReferee() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		refereeMailSender = new RefereeMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(Referee referee) {
				return model;
			}

		};
		Referee referee = new RefereeBuilder().id(4).firstname("john").lastname("boggs").email("ref@test.com").toReferee();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		referee.setApplication(form);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("ref@test.com", "john boggs");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Referee Notification",
						"private/referees/mail/referee_notification_email.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		refereeMailSender.sendRefereeNotification(referee);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}

	@Test
	public void shouldSendRefereeNotificationExistingUserReferee() throws UnsupportedEncodingException {

		final HashMap<String, Object> model = new HashMap<String, Object>();
		refereeMailSender = new RefereeMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {
			@Override
			Map<String, Object> createModel(Referee referee) {

				return model;
			}

		};
		RegisteredUser user = new RegisteredUserBuilder().id(1).enabled(true).email("jboggs@test.com").firstName("Jonathan").lastName("Boggs").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("john").lastname("boggs").email("ref@test.com").user(user).toReferee();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		referee.setApplication(form);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("jboggs@test.com", "Jonathan Boggs");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Referee Notification",
						"private/referees/mail/existing_user_referee_notification_email.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		refereeMailSender.sendRefereeNotification(referee);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}


	@Before
	public void setUp() {

		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);

		refereeMailSender = new RefereeMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

	}
}
