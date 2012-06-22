package com.zuehlke.pgadmissions.mail;

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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApproverAdminMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private ApproverAdminMailSender approverMailSender;
	private MessageSource msgSourceMock;
	private ApplicationsService applicationsServiceMock;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser approverOne = new RegisteredUserBuilder().email("alice@test.com").id(9).toUser();
		RegisteredUser admin = new RegisteredUserBuilder().email("t@test.com").id(10).toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).toUser();

		ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().administrators(admin).approver(approverOne).toProgram()).applicant(applicant).toApplicationForm();

		Map<String, Object> model = approverMailSender.createModel(approverOne, form);
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(approverOne, model.get("user"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}

	@Test
	public void sendingApprovalNotificationsToApproverAndAdmin() throws Exception {
		RegisteredUser admin = new RegisteredUserBuilder().id(2).email("admin@test.com").firstName("bob").lastName("the builder").toUser();
		RegisteredUser approver = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").id(10).toUser();
		
		ApplicationForm application = new ApplicationFormBuilder().id(4).applicationNumber("bob").applicant(applicant).program(new ProgramBuilder().approver(approver).title("prg").administrators(admin).toProgram()).toApplicationForm();

		InternetAddress expAddr1 = new InternetAddress("bob@test.com", "bob the builder");
		InternetAddress expAddr2 = new InternetAddress("admin@test.com", "bob the builder");

		String expTemplate = "private/approvers/mail/approval_notification_email.ftl";

		final Map<String, Object> model = new HashMap<String, Object>();
		approverMailSender = new ApproverAdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, applicationsServiceMock) {
			@Override
			Map<String, Object> createModel(RegisteredUser approver, ApplicationForm application) {
				return model;
			}
		};

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("approval.notification.approverAndAdmin"),// 
				EasyMock.aryEq(new Object[] {"bob", "prg", "Jane", "Smith" }), EasyMock.eq((Locale) null))).andReturn("subject").anyTimes();

		MimeMessagePreparator mimePrepMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr1, "subject", expTemplate, model, null)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr2, "subject", expTemplate, model, null)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);
		
		EasyMock.expectLastCall();
		EasyMock.replay(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);

		approverMailSender.sendApprovalNotificationToApproversAndAdmins(application);

		EasyMock.verify(mimePrepMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
	}

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		approverMailSender = new ApproverAdminMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, applicationsServiceMock);
	}
}
