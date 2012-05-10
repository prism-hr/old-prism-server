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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.utils.Environment;

public class ReviewerMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private ReviewerMailSender reviewerMailSender;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).toUser();
		RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").toUser();
		
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram()).applicant(applicant).toApplicationForm();
		Reviewer reviewer = new ReviewerBuilder().id(4).user(defaultReviewer).application(form).toReviewer();
		

		Map<String, Object> model = reviewerMailSender.createModel(reviewer);
		assertEquals("bob@test.com, alice@test.com", model.get("adminsEmails"));
		assertEquals(reviewer, model.get("reviewer"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}
	@Test
	public void shouldSendReviewerNotificationForReviewer() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		reviewerMailSender = new ReviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(Reviewer reviewer) {
				return model;
			}

		};
		
		
		RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").toUser();		
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().title("program abc").toProgram()).toApplicationForm();
		Reviewer reviewer = new ReviewerBuilder().id(4).user(defaultReviewer).application(form).toReviewer();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Application 4 for program abc - Reviewer Notification",
						"private/reviewers/mail/reviewer_notification_email.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		reviewerMailSender.sendReviewerNotification(reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}
	
	@Test
	public void shouldSendReviewerReminderForReviewer() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		reviewerMailSender = new ReviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(Reviewer reviewer) {
				return model;
			}

		};
		
		
		RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").toUser();		
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().title("program abc").toProgram()).toApplicationForm();
		Reviewer reviewer = new ReviewerBuilder().id(4).user(defaultReviewer).application(form).toReviewer();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Application 4 for program abc - Review Reminder",
						"private/reviewers/mail/reviewer_reminder_email.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		reviewerMailSender.sendReviewerReminder(reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}
	@Before
	public void setUp() {

		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);

		reviewerMailSender = new ReviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

	}
}
