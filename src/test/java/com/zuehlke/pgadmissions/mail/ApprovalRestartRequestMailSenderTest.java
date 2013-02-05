package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RequestRestartComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RequestRestartCommentBuilder;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApprovalRestartRequestMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private MessageSource msgSourceMock;
	private ApprovalRestartRequestMailSender mailSender;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser approverRequestingRestart = new RegisteredUserBuilder().email("alice@test.com").id(9).build();
		RegisteredUser admin = new RegisteredUserBuilder().email("t@test.com").id(10).build();
		RegisteredUser applicant = new RegisteredUserBuilder().id(10).build();
	
		Date yesterDay = DateUtils.addDays(new Date(), -1);
		Date twoDaysAgo = DateUtils.addDays(new Date(), -2);

		RequestRestartComment commentOne = new RequestRestartCommentBuilder().id(1).date(twoDaysAgo).build();
		RequestRestartComment commentTwo = new RequestRestartCommentBuilder().id(3).date(yesterDay).build();
		
		ApplicationForm form = new ApplicationFormBuilder().id(4).comments(commentOne, commentTwo).program(new ProgramBuilder().administrators(admin).build()).applicant(applicant).approverRequestedRestart(approverRequestingRestart).build();

		Map<String, Object> model = mailSender.createModel(form);
		assertEquals(form, model.get("application"));
		assertEquals(applicant, model.get("applicant"));
		assertEquals(approverRequestingRestart, model.get("requester"));
		assertEquals(commentTwo, model.get("comment"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));

	}
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendRequestRestartApprovalMailToProgramAdmins() throws UnsupportedEncodingException {
		RegisteredUser programAdmin1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		RegisteredUser programAdmin2 = new RegisteredUserBuilder().id(2).firstName("cindy").lastName("cider").email("cc@test.com").build();


		Program program = new ProgramBuilder().administrators(programAdmin1, programAdmin2).title("title").build();
		ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("xyz").program(program).build();
		
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
		
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		 mailSender = new ApprovalRestartRequestMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock){
			@Override
			protected Map<String,Object> createModel(ApplicationForm application) {
			
				return model;
			}
		};
		
		mailSender.sendRequestRestartApproval(form);
		
		EasyMock.verify( javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
			
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendRequestRestartApprovalMailToApplicationAdmin() throws UnsupportedEncodingException {
		RegisteredUser applicationAdmin = new RegisteredUserBuilder().id(54).firstName("arnie").lastName("adams").email("aa@test.com").build();
		RegisteredUser programAdmin1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		RegisteredUser programAdmin2 = new RegisteredUserBuilder().id(2).firstName("cindy").lastName("cider").email("cc@test.com").build();
		
		
		Program program = new ProgramBuilder().administrators(programAdmin1, programAdmin2).title("title").build();
		ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("xyz").program(program).applicationAdministrator(applicationAdmin).build();
		
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
		
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		 mailSender = new ApprovalRestartRequestMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock){
				@Override
				protected Map<String,Object> createModel(ApplicationForm application) {
				
					return model;
				}
			};
		
			mailSender.sendRequestRestartApproval(form);
		
		EasyMock.verify( javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendRequestRestartApprovalreminderMailToProgramAdmins() throws UnsupportedEncodingException {
		RegisteredUser programAdmin1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		RegisteredUser programAdmin2 = new RegisteredUserBuilder().id(2).firstName("cindy").lastName("cider").email("cc@test.com").build();


		Program program = new ProgramBuilder().administrators(programAdmin1, programAdmin2).title("title").build();
		ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("xyz").program(program).build();
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("cc@test.com", "cindy cider");
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.request.restart.approval.reminder"), 
				EasyMock.aryEq(new Object[] { "xyz", "title" }), EasyMock.eq((Locale)null))).andReturn("subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/admin/mail/approval_restart_request_reminder.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/admin/mail/approval_restart_request_reminder.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().times(2);
		
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		 mailSender = new ApprovalRestartRequestMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock){
			@Override
			protected Map<String,Object> createModel(ApplicationForm application) {
			
				return model;
			}
		};
		
		mailSender.sendRequestRestartApprovalReminder(form);
		
		EasyMock.verify( javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
			
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendRequestRestartApprovalMailReminderToApplicationAdmin() throws UnsupportedEncodingException {
		RegisteredUser applicationAdmin = new RegisteredUserBuilder().id(54).firstName("arnie").lastName("adams").email("aa@test.com").build();
		RegisteredUser programAdmin1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		RegisteredUser programAdmin2 = new RegisteredUserBuilder().id(2).firstName("cindy").lastName("cider").email("cc@test.com").build();
		
		
		Program program = new ProgramBuilder().administrators(programAdmin1, programAdmin2).title("title").build();
		ApplicationForm form = new ApplicationFormBuilder().id(2).applicationNumber("xyz").program(program).applicationAdministrator(applicationAdmin).build();
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("aa@test.com", "arnie adams");
		InternetAddress toAddress2 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress3 = new InternetAddress("cc@test.com", "cindy cider");
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.request.restart.approval.reminder"), 
				EasyMock.aryEq(new Object[] { "xyz", "title" }), EasyMock.eq((Locale)null))).andReturn("subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.aryEq(new InternetAddress[] {toAddress2, toAddress3}),// 
						EasyMock.eq("subject"), EasyMock.eq("private/staff/admin/mail/approval_restart_request_reminder.ftl"),// 
						EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		 mailSender = new ApprovalRestartRequestMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock){
				@Override
				protected Map<String,Object> createModel(ApplicationForm application) {
				
					return model;
				}
			};
		
			mailSender.sendRequestRestartApprovalReminder(form);
		
		EasyMock.verify( javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
		
	}
	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		
		 mailSender = new ApprovalRestartRequestMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
	}
}
