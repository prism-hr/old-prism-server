package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApplicantMailSenderTest {

	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private ApplicantMailSender applicantMailSender;
	private ApplicationsService applicationServiceMock;
	private MessageSource msgSourceMock;
	private ConfigurationService personServiceMock;

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		personServiceMock = EasyMock.createMock(ConfigurationService.class);
		
		applicantMailSender = new ApplicantMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock,applicationServiceMock, msgSourceMock, personServiceMock);
	}

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).build();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).build();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).build())//
				.applicant(applicant).build();

		List<Person> registryContacts = new ArrayList<Person>();
		registryContacts.add(new PersonBuilder().id(123).build());
		EasyMock.expect(personServiceMock.getAllRegistryUsers()).andReturn(registryContacts);
		EasyMock.replay(applicationServiceMock, personServiceMock);

		Map<String, Object> model = applicantMailSender.createModel(form);
		
		EasyMock.verify(applicationServiceMock, personServiceMock);
		assertEquals("bob@test.com, alice@test.com", model.get("adminsEmails"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(registryContacts, model.get("registryContacts"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
		assertEquals(Environment.getInstance().getAdmissionsOfferServiceLevel(), model.get("admissionOfferServiceLevel"));
		assertNull(model.get("reasons"));
		assertEquals(ApplicationFormStatus.VALIDATION, model.get("previousStage"));
	}

	@Test
	public void shouldReturnCorrectlyPopulatedModelForRejectedApplications() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).build();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).build();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).build())//
				.applicant(applicant).status(ApplicationFormStatus.REJECTED).build();

		RejectReason reason = new RejectReasonBuilder().id(30).text("lalalala").build();
		Rejection rejection = new RejectionBuilder().id(1).rejectionReason(reason).build();
		rejection.setIncludeProspectusLink(true);
		form.setRejection(rejection);

		
		List<Person> registryContacts = new ArrayList<Person>();
		registryContacts.add(new PersonBuilder().id(123).build());
		EasyMock.expect(personServiceMock.getAllRegistryUsers()).andReturn(registryContacts);
		EasyMock.replay(applicationServiceMock, personServiceMock);
		
		Map<String, Object> model = applicantMailSender.createModel(form);
		
		EasyMock.verify(applicationServiceMock, personServiceMock);
		assertEquals("bob@test.com, alice@test.com", model.get("adminsEmails"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(registryContacts, model.get("registryContacts"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
		assertEquals(ApplicationFormStatus.VALIDATION, model.get("previousStage"));
		assertEquals(reason, model.get("reason"));
		assertEquals(Environment.getInstance().getUCLProspectusLink(), model.get("prospectusLink"));
	}

	@Test
	public void shouldSendMovedToReviewNotificationToApplicant() throws UnsupportedEncodingException {
		final Map<String, Object> model = new HashMap<String, Object>();
		applicantMailSender = new ApplicantMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationServiceMock, msgSourceMock, personServiceMock) {

			@Override
			Map<String, Object> createModel(ApplicationForm application) {
				return model;
			}

		};

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("bob").applicant(applicant).program(new ProgramBuilder().title("Some Program").build())
				.build();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("jane.smith@test.com", "Jane Smith");

		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("message.code"),// 
				EasyMock.aryEq(new Object[] { "bob", "Some Program", "Jane", "Smith", "Validation" }//
						), EasyMock.eq((Locale)null))).andReturn("resolved subject");
		
		EasyMock.expect(//
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject",//
						"private/pgStudents/mail/moved_to_review_notification.ftl", model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(applicationServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		applicantMailSender.sendMailsForApplication(form, "message.code", "private/pgStudents/mail/moved_to_review_notification.ftl", null);

		EasyMock.verify(applicationServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
	}
}
