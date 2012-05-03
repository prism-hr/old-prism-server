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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.utils.Environment;

public class AdminMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private AdminMailSender adminMailSender;
	
	@Test
	public void shouldReturnCorrectlyPopulatedModel() {
		
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).toUser();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(11).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(admin).toProgram())
				.applicant(applicant).toApplicationForm();

		Map<String, Object> model = adminMailSender.createModel(admin, form, reviewer);
		assertEquals(admin, model.get("admin"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(reviewer, model.get("reviewer"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}
	
	@Test
	public void shouldSendAdminNotificationForNewReviewComment() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			public
			Map<String, Object> createModel(RegisteredUser admin, ApplicationForm form, RegisteredUser reviewer) {
				return model;
			}

		};
		RegisteredUser admin = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(11).toUser();
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("bob@bobson.com", "Bob Bobson");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Notification - review added",
						"private/staff/admin/mail/review_submission_notification.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		adminMailSender.sendAdminReviewNotification(admin, form , reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}

	
	@Before
	public void setUp() {

		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);

		adminMailSender = new AdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

	}
}
