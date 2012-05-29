package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApplicantMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private ApplicantMailSender applicantMailSender;
	private ApplicationsService applicationServiceMock;
	private MessageSource msgSourceMock;

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		
		applicantMailSender = new ApplicantMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock,applicationServiceMock, msgSourceMock);
	}

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram())//
				.applicant(applicant).toApplicationForm();

		EasyMock.expect(applicationServiceMock.getStageComingFrom(form)).andReturn(ApplicationFormStatus.INTERVIEW);
		EasyMock.replay(applicationServiceMock);

		Map<String, Object> model = applicantMailSender.createModel(form);
		
		EasyMock.verify(applicationServiceMock);
		assertEquals("bob@test.com, alice@test.com", model.get("adminsEmails"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
		assertNull(model.get("reasons"));
		assertEquals(ApplicationFormStatus.INTERVIEW, model.get("previousStage"));
	}

	@Test
	public void shouldReturnCorrectlyPopulatedModelForRejectedApplications() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).toUser();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).toProgram())//
				.applicant(applicant).status(ApplicationFormStatus.REJECTED).toApplicationForm();

		RejectReason reason = new RejectReasonBuilder().id(30).text("lalalala").toRejectReason();
		Rejection rejection = new RejectionBuilder().id(1).rejectionReason(reason).toRejection();
		rejection.setIncludeProspectusLink(true);
		form.setRejection(rejection);

		EasyMock.expect(applicationServiceMock.getStageComingFrom(form)).andReturn(ApplicationFormStatus.INTERVIEW);
		EasyMock.replay(applicationServiceMock);
		
		Map<String, Object> model = applicantMailSender.createModel(form);
		
		EasyMock.verify(applicationServiceMock);
		assertEquals("bob@test.com, alice@test.com", model.get("adminsEmails"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
		assertEquals(ApplicationFormStatus.INTERVIEW, model.get("previousStage"));
		assertEquals(reason, model.get("reason"));
		assertEquals(Environment.getInstance().getUCLProspectusLink(), model.get("prospectusLink"));
	}

	@Test
	public void shouldSendMovedToReviewNotificationToApplicant() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();
		applicantMailSender = new ApplicantMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm application) {
				return model;
			}

		};

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicant(applicant).program(new ProgramBuilder().title("Some Program").toProgram())
				.toApplicationForm();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("jane.smith@test.com", "Jane Smith");

		EasyMock.expect(applicationServiceMock.getStageComingFrom(form)).andReturn(ApplicationFormStatus.REVIEW);
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("message.code"),// 
				EasyMock.aryEq(new Object[] { 4, "Some Program", "Jane", "Smith", "Review" }//
						), EasyMock.eq((Locale)null))).andReturn("resolved subject");
		
		EasyMock.expect(//
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject",//
						"private/pgStudents/mail/moved_to_review_notification.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(applicationServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		applicantMailSender.sendMailsForApplication(form, "message.code", "private/pgStudents/mail/moved_to_review_notification.ftl");

		EasyMock.verify(applicationServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
	}
}
