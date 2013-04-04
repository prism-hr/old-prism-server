package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REVIEWER_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REVIEWER_REMINDER;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

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
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ReviewerMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private ReviewerMailSender reviewerMailSender;
	private MessageSource msgSourceMock;
	private EmailTemplateService templateServiceMock;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).build();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).build();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).build();
		RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();
		
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).build()).applicant(applicant).build();
		Reviewer reviewer = new ReviewerBuilder().id(4).user(defaultReviewer).reviewRound(new ReviewRoundBuilder().application(form).build()).build();
		

		Map<String, Object> model = reviewerMailSender.createModel(reviewer);
		assertEquals("bob@test.com;alice@test.com", model.get("adminsEmails"));
		assertEquals(reviewer, model.get("reviewer"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}
	@Test
	public void shouldSendReviewerNotificationForReviewer() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		reviewerMailSender = new ReviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock) {

			@Override
			Map<String, Object> createModel(Reviewer reviewer) {
				return model;
			}

		};
		
		
		RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();		
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("fred").program(new ProgramBuilder().title("program abc").build()).build();
		Reviewer reviewer = new ReviewerBuilder().id(4).user(defaultReviewer).reviewRound(new ReviewRoundBuilder().application(form).build()).build();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Reviewer notification template").name(REVIEWER_NOTIFICATION).build();
		expect(templateServiceMock.getActiveEmailTemplate(REVIEWER_NOTIFICATION)).andReturn(template);
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("review.request"),// 
				EasyMock.aryEq(new Object[] { "fred", "program abc" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject",
						REVIEWER_NOTIFICATION, template.getContent(), model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);

		reviewerMailSender.sendReviewerNotification(reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, templateServiceMock);

	}
	
	@Test
	public void shouldSendReviewerReminderForReviewer() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		reviewerMailSender = new ReviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock) {

			@Override
			Map<String, Object> createModel(Reviewer reviewer) {
				return model;
			}

		};
		
		
		RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();		
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("fred").program(new ProgramBuilder().title("program abc").build()).build();
		Reviewer reviewer = new ReviewerBuilder().id(4).user(defaultReviewer).reviewRound(new ReviewRoundBuilder().application(form).build()).build();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Reviewer rmeinder template").name(REVIEWER_REMINDER).build();
		expect(templateServiceMock.getActiveEmailTemplate(REVIEWER_REMINDER)).andReturn(template);

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("review.request.reminder"),// 
				EasyMock.aryEq(new Object[] { "fred", "program abc" }), EasyMock.eq((Locale) null))).andReturn("resolved reminder subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved reminder subject",
						REVIEWER_REMINDER, template.getContent(), model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);

		reviewerMailSender.sendReviewerReminder(reviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, templateServiceMock);

	}
	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = createMock(MessageSource.class);
		templateServiceMock = createMock(EmailTemplateService.class);
		reviewerMailSender = new ReviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);

	}
}
