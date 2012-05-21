package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApplicantMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private ApplicantMailSender applicantMailSender;

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);

		applicantMailSender = new ApplicantMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram())//
				.applicant(applicant).toApplicationForm();

		Map<String, Object> model = applicantMailSender.createModel(form);
		assertEquals("bob@test.com, alice@test.com", model.get("adminsEmails"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
		assertNull(model.get("reasons"));
	}

	@Test
	public void shouldReturnCorrectlyPopulatedModelForRejectedApplications() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram())//
				.applicant(applicant).status(ApplicationFormStatus.REJECTED).toApplicationForm();

		RejectReason reason1 = new RejectReasonBuilder().id(30).text("lalalala").toRejectReason();
		form.getRejectReasons().add(reason1);

		Map<String, Object> model = applicantMailSender.createModel(form);
		assertEquals("bob@test.com, alice@test.com", model.get("adminsEmails"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
		@SuppressWarnings("unchecked")
		Collection<RejectReason> reasons = (Collection<RejectReason>) model.get("reasons");
		Assert.assertTrue(reasons.contains(reason1));
	}

	@Test
	public void shouldSendMovedToReviewNotificationToApplicant() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();
		applicantMailSender = new ApplicantMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm application) {
				return model;
			}

		};

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicant(applicant).program(new ProgramBuilder().title("Some Program").toProgram()).toApplicationForm();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("jane.smith@test.com", "Jane Smith");

		EasyMock.expect(//
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "Application 4 for Some Program now being reviewed",//
						"private/pgStudents/mail/moved_to_review_notification.ftl", model)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		applicantMailSender.sendMailsForApplication(form, "now being reviewed", "private/pgStudents/mail/moved_to_review_notification.ftl");

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);
	}
}
