package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;

public class MailServiceTest {

	private MailService mailService;
	private ApplicationsService applicationsServiceMock;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private MessageSource msgSourceMock;
	
	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		
		mailService = new MailService(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationsServiceMock, msgSourceMock);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendUpdatedEmailToAdmins() throws UnsupportedEncodingException, ParseException {

		RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).title("program title").toProgram();
	
		NotificationRecord notificationRecord = new NotificationRecordBuilder().id(1).notificationType(NotificationType.UPDATED_NOTIFICATION).notificationDate(new SimpleDateFormat("dd MM yyyy").parse("01 06 2011")).toNotificationRecord();
		ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("xyz").program(program).notificationRecords(notificationRecord).toApplicationForm();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
	
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
			
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.update"), 
				EasyMock.aryEq(new Object[] { "xyz", "program title" }), EasyMock.eq((Locale)null))).andReturn("update subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("update subject"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("update subject"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		mailService.sendApplicationUpdatedMailToAdmins(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
		assertSame(notificationRecord, form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION).getDate(), Calendar.DATE));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToReferees() throws UnsupportedEncodingException {
		Program program = new ProgramBuilder().title("title").toProgram();
		ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).applicationNumber("xyz").applicant(new RegisteredUser()).toApplicationForm();
		
		RegisteredUser refereeOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser refereeTwo = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").toUser();
		Referee referee1 = new RefereeBuilder().application(form).id(2).user(refereeTwo).toReferee();
		Referee referee2 = new RefereeBuilder().application(form).id(1).user(refereeOne).toReferee();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.withdrawal"), 
				EasyMock.aryEq(new Object[] { "xyz", "title" }), EasyMock.eq((Locale)null))).andReturn("subject").times(2);
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		mailService.sendWithdrawMailToReferees(Arrays.asList(referee1, referee2));
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToAdmins() throws UnsupportedEncodingException {
		
		RegisteredUser admin1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser admin2 = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").toUser();
		Program program = new ProgramBuilder().administrators(admin1, admin2).title("title").toProgram();
		
		ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("xyz").program(program).toApplicationForm();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.withdrawal"), 
				EasyMock.aryEq(new Object[] { "xyz", "title" }), EasyMock.eq((Locale)null))).andReturn("subject").times(2);
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		mailService.sendWithdrawToAdmins(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToReviewers() throws UnsupportedEncodingException {
		
		RegisteredUser reviewer1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").toUser();
		Program program = new ProgramBuilder().reviewers(reviewer1, reviewer2).title("title").toProgram();
		
		ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("xyz").program(program).toApplicationForm();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.withdrawal"), 
				EasyMock.aryEq(new Object[] { "xyz", "title" }), EasyMock.eq((Locale)null))).andReturn("subject").times(2);
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		mailService.sendWithdrawToReviewers(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendRequestRestartApprovalMailToProgramAdmins() throws UnsupportedEncodingException {
		RegisteredUser programAdmin1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser programAdmin2 = new RegisteredUserBuilder().id(2).firstName("cindy").lastName("cider").email("cc@test.com").toUser();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").email("dd@test.com").toUser();

		Program program = new ProgramBuilder().administrators(programAdmin1, programAdmin2).title("title").toProgram();
		ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("xyz").program(program).toApplicationForm();
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("cc@test.com", "cindy cider");
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.request.restart.approval"), 
				EasyMock.aryEq(new Object[] { "xyz", "title" }), EasyMock.eq((Locale)null))).andReturn("subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/admin/mail/restart_approval_request.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/admin/mail/restart_approval_request.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().times(2);
		
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		mailService = new MailService(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationsServiceMock, msgSourceMock) {
			@Override
			protected Map<String,Object> createModel(ApplicationForm application) {
				Assert.assertNotNull(application);
				return model;
			}
		};
		
		mailService.sendRequestRestartApproval(form, approver);
		
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
		Assert.assertEquals(programAdmin2, model.get("admin")); // after second send mail
		Assert.assertEquals(approver, model.get("approver"));	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendRequestRestartApprovalMailToApplicationAdmin() throws UnsupportedEncodingException {
		RegisteredUser applicationAdmin = new RegisteredUserBuilder().id(54).firstName("arnie").lastName("adams").email("aa@test.com").toUser();
		RegisteredUser programAdmin1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser programAdmin2 = new RegisteredUserBuilder().id(2).firstName("cindy").lastName("cider").email("cc@test.com").toUser();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").email("dd@test.com").toUser();
		
		Program program = new ProgramBuilder().administrators(programAdmin1, programAdmin2).title("title").toProgram();
		ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("xyz").program(program).applicationAdministrator(applicationAdmin).toApplicationForm();
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("aa@test.com", "arnie adams");
		InternetAddress toAddress2 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress3 = new InternetAddress("cc@test.com", "cindy cider");
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.request.restart.approval"), 
				EasyMock.aryEq(new Object[] { "xyz", "title" }), EasyMock.eq((Locale)null))).andReturn("subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.aryEq(new InternetAddress[] {toAddress2, toAddress3}),// 
						EasyMock.eq("subject"), EasyMock.eq("private/staff/admin/mail/restart_approval_request.ftl"),// 
						EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		mailService = new MailService(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationsServiceMock, msgSourceMock) {
			@Override
			protected Map<String,Object> createModel(ApplicationForm application) {
				Assert.assertNotNull(application);
				return model;
			}
		};
		
		mailService.sendRequestRestartApproval(form, approver);
		
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
		Assert.assertEquals(applicationAdmin, model.get("admin"));		
		Assert.assertEquals(approver, model.get("approver"));		
	}
}
