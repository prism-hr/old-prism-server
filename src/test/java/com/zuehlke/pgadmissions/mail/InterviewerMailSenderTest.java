package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_REMINDER_FIRST;
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
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class InterviewerMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private InterviewerMailSender interviewerMailSender;
	private EmailTemplateService templateServiceMock;
	private MessageSource msgSourceMock;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).build();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).build();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).build();
		RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();

		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).build()).applicant(applicant).build();
		Interviewer interviewer = new InterviewerBuilder().id(4).user(defaultInterviewer).interview(new InterviewBuilder().id(5).application(form).build()).build();

		Map<String, Object> model = interviewerMailSender.createModel(interviewer);
		assertEquals("bob@test.com;alice@test.com", model.get("adminsEmails"));
		assertEquals(interviewer, model.get("interviewer"));
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}

	@Test
	public void shouldSendInterviewerNotificationForInterviewer() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		interviewerMailSender = new InterviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock,
				msgSourceMock, templateServiceMock) {

			@Override
			Map<String, Object> createModel(Interviewer interviewer) {
				return model;
			}

		};
		RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("fred").program(new ProgramBuilder().title("program abc").build()).build();
		Interviewer interviewer = new InterviewerBuilder().id(4).user(defaultInterviewer).interview(new InterviewBuilder().id(5).application(form).build()).build();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Interviewer notification template").name(INTERVIEWER_NOTIFICATION).build();
		expect(templateServiceMock.getActiveEmailTemplate(INTERVIEWER_NOTIFICATION)).andReturn(template);

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("interview.notification.interviewer"),// 
				EasyMock.aryEq(new Object[] { "fred", "program abc" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");
		EasyMock.expect(//
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject",//
						INTERVIEWER_NOTIFICATION, template.getContent(), model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, templateServiceMock, msgSourceMock);

		interviewerMailSender.sendInterviewerNotification(interviewer);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, templateServiceMock, msgSourceMock);
	}

	@Test
	public void shouldSendFirstInterviewerReminderForInterviewer() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		interviewerMailSender = new InterviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock) {

			@Override
			Map<String, Object> createModel(Interviewer interviewer) {
				return model;
			}
		};

		RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("fred").program(new ProgramBuilder().title("program abc").build()).build();
		Interviewer interviewer = new InterviewerBuilder().id(4).user(defaultInterviewer).interview(new InterviewBuilder().id(5).application(form).build()).build();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Interviewer reminder first template").name(INTERVIEWER_REMINDER_FIRST).build();
		expect(templateServiceMock.getActiveEmailTemplate(INTERVIEWER_REMINDER_FIRST)).andReturn(template);

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("interview.feedback.request"),// 
				EasyMock.aryEq(new Object[] { "fred", "program abc" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");
		EasyMock.expect(//
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject",//
						INTERVIEWER_REMINDER_FIRST, template.getContent(), model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, templateServiceMock, msgSourceMock);

		interviewerMailSender.sendInterviewerReminder(interviewer, true);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, templateServiceMock, msgSourceMock);
	}
	@Test
	public void shouldSendInterviewerReminderForInterviewer() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		interviewerMailSender = new InterviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock,
				msgSourceMock, templateServiceMock) {
			
			@Override
			Map<String, Object> createModel(Interviewer interviewer) {
				return model;
			}
		};
		
		RegisteredUser defaultInterviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("fred").program(new ProgramBuilder().title("program abc").build()).build();
		Interviewer interviewer = new InterviewerBuilder().id(4).user(defaultInterviewer).interview(new InterviewBuilder().id(5).application(form).build()).build();
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Interviewer reminder template").name(INTERVIEWER_REMINDER).build();
		expect(templateServiceMock.getActiveEmailTemplate(INTERVIEWER_REMINDER)).andReturn(template);
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("interview.feedback.request.reminder"),// 
				EasyMock.aryEq(new Object[] { "fred", "program abc" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");
		EasyMock.expect(//
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject",//
						INTERVIEWER_REMINDER, template.getContent(), model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, templateServiceMock, msgSourceMock);
		
		interviewerMailSender.sendInterviewerReminder(interviewer, false);
		
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, templateServiceMock, msgSourceMock);
	}

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		templateServiceMock= createMock(EmailTemplateService.class);
		interviewerMailSender = new InterviewerMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);
	}
}
