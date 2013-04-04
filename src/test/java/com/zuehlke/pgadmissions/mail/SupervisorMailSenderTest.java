package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.SUPERVISOR_NOTIFICATION;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class SupervisorMailSenderTest {

	private JavaMailSender javaMailSenderMock;

	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	
	private SupervisorMailSender supervisorMailSender;
	
	private MessageSource msgSourceMock;
	private EmailTemplateService templateServiceMock;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {
		RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).build();
		RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).build();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).build();
		RegisteredUser defaultSupervisor = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();
		
		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(adminOne, adminTwo).build()).applicant(applicant).build();
		Supervisor supervisor = new SupervisorBuilder().id(4).user(defaultSupervisor).approvalRound(new ApprovalRoundBuilder().application(form).build()).build();

		Map<String, Object> model = supervisorMailSender.createModel(supervisor);		
		assertEquals(supervisor, model.get("supervisor"));
		assertEquals(form, model.get("application"));
		Assert.assertEquals("bob@test.com;alice@test.com", model.get("adminsEmails"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
	}
	
	@Test
	public void shouldSendSupervisorNotificationForSupervisor() throws UnsupportedEncodingException {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		supervisorMailSender = new SupervisorMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock) {
			@Override
			Map<String, Object> createModel(Supervisor supervisor) {
				return model;
			}
		};
		
		RegisteredUser defaultSupervisor = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();		
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("fred").program(new ProgramBuilder().title("program abc").build()).build();
		Supervisor supervisor = new SupervisorBuilder().id(4).user(defaultSupervisor).approvalRound(new ApprovalRoundBuilder().application(form).build()).build();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Supervisor  notification template").name(SUPERVISOR_NOTIFICATION).build();
		expect(templateServiceMock.getActiveEmailTemplate(SUPERVISOR_NOTIFICATION)).andReturn(template);
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("supervisor.notification"),// 
				EasyMock.aryEq(new Object[] { "fred", "program abc" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject",
						SUPERVISOR_NOTIFICATION, template.getContent(), model, null)).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);

		supervisorMailSender.sendSupervisorNotification(supervisor);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, templateServiceMock);
	}
	
    @Test
    public void shouldSendSupervisorNotificationForPrimarySupervisor() throws UnsupportedEncodingException {
        final HashMap<String, Object> model = new HashMap<String, Object>();
        supervisorMailSender = new SupervisorMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock) {
            @Override
            Map<String, Object> createModel(Supervisor supervisor) {
                return model;
            }
        };
        
        RegisteredUser defaultSupervisor = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();     
        ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("fred").program(new ProgramBuilder().title("program abc").build()).build();
        Supervisor supervisor = new SupervisorBuilder().id(4).user(defaultSupervisor).approvalRound(new ApprovalRoundBuilder().application(form).build()).build();

        MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
        InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");
        
        EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Supervisor confirm supervision notification template").name(SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION).build();
		expect(templateServiceMock.getActiveEmailTemplate(SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION)).andReturn(template);
        
        EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("supervisor.primary.notification"),// 
                EasyMock.aryEq(new Object[] { "fred", "program abc" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");
        
        EasyMock.expect(
                mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject",
                		SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION, template.getContent(), model, null)).andReturn(preparatorMock);
        javaMailSenderMock.send(preparatorMock);

        EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);

        supervisorMailSender.sendPrimarySupervisorConfirmationNotification(supervisor);

        EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, templateServiceMock);
    }
    
    @Test
    public void shouldSendSupervisorNotificationReminderForPrimarySupervisor() throws UnsupportedEncodingException {
        final HashMap<String, Object> model = new HashMap<String, Object>();
        supervisorMailSender = new SupervisorMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock) {
            @Override
            Map<String, Object> createModel(Supervisor supervisor) {
                return model;
            }
        };
        
        RegisteredUser defaultSupervisor = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").build();     
        ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("fred").program(new ProgramBuilder().title("program abc").build()).build();
        Supervisor supervisor = new SupervisorBuilder().id(4).user(defaultSupervisor).approvalRound(new ApprovalRoundBuilder().application(form).build()).build();

        MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
        InternetAddress toAddress = new InternetAddress("hanna.hoopla@test.com", "Hanna Hoopla");
        
        EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Supervisor confirm supervision notification reminder template").name(SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER).build();
		expect(templateServiceMock.getActiveEmailTemplate(SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER)).andReturn(template);
        
        EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("supervisor.primary.notification.reminder"),// 
                EasyMock.aryEq(new Object[] { "fred", "program abc" }), EasyMock.eq((Locale) null))).andReturn("resolved subject");
        
        EasyMock.expect(
                mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "resolved subject",
                		SUPERVISOR_CONFIRM_SUPERVISION_NOTIFICATION_REMINDER, template.getContent(), model, null)).andReturn(preparatorMock);
        javaMailSenderMock.send(preparatorMock);

        EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);

        supervisorMailSender.sendPrimarySupervisorConfirmationNotificationReminder(supervisor);

        EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, templateServiceMock);
    }
	
	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = createMock(MessageSource.class);
		templateServiceMock = createMock(EmailTemplateService.class); 
		supervisorMailSender = new SupervisorMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);
	}
}
