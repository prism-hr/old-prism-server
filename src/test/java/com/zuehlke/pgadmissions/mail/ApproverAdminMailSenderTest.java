package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPROVAL_NOTIFICATION;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

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
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApproverAdminMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private ApproverAdminMailSender approverMailSender;
	private EmailTemplateService templateServiceMock;
	private MessageSource msgSourceMock;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser approverOne = new RegisteredUserBuilder().email("alice@test.com").id(9).build();
		RegisteredUser admin = new RegisteredUserBuilder().email("t@test.com").id(10).build();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).build();

		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(admin).approver(approverOne).build()).applicant(applicant).build();

		Map<String, Object> model = approverMailSender.createModel(approverOne, form);
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(approverOne, model.get("user"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}

	@Test
	public void sendingApprovalNotificationsToApproverAndAdmin() throws Exception {
		RegisteredUser admin = new RegisteredUserBuilder().id(2).email("admin@test.com").firstName("bob").lastName("the builder").build();
		RegisteredUser approver = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").build();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).build();
		
		ApplicationForm application = new ApplicationFormBuilder().id(4).applicationNumber("bob").applicant(applicant).program(new ProgramBuilder().approver(approver, admin).title("prg").administrators(admin).build()).build();

		InternetAddress expAddr1 = new InternetAddress("bob@test.com", "bob the builder");
		InternetAddress expAddr2 = new InternetAddress("admin@test.com", "bob the builder");
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Approval notification template").name(APPROVAL_NOTIFICATION).build();
		expect(templateServiceMock.getActiveEmailTemplate(APPROVAL_NOTIFICATION)).andReturn(template);



		final Map<String, Object> model = new HashMap<String, Object>();
		approverMailSender = new ApproverAdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock) {
			@Override
			Map<String, Object> createModel(RegisteredUser approver, ApplicationForm application) {
				return model;
			}
		};

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("approval.notification.approverAndAdmin"),// 
				EasyMock.aryEq(new Object[] {"bob", "prg", "Jane", "Smith", "Validation" }), EasyMock.eq((Locale) null))).andReturn("subject").anyTimes();

		MimeMessagePreparator mimePrepMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr1, "subject", APPROVAL_NOTIFICATION, template.getContent(), model, null)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr2, "subject", APPROVAL_NOTIFICATION, template.getContent(), model, null)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);
		
		EasyMock.expectLastCall();
		EasyMock.replay(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, templateServiceMock);

		approverMailSender.sendApprovalNotificationToApproversAndAdmins(application);

		EasyMock.verify(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, templateServiceMock);
	}

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		templateServiceMock = EasyMock.createMock(EmailTemplateService.class);
		approverMailSender = new ApproverAdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, templateServiceMock);
	}
}
