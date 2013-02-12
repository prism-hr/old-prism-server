package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;

public class MailServiceTest {

	private MailService mailService;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private MessageSource msgSourceMock;
	
	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		mailService = new MailService(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendUpdatedEmailToAdminsAndInterviewersIfInInterview() throws UnsupportedEncodingException, ParseException {

		RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").build();
		
		RegisteredUser interviewerUserOne = new RegisteredUserBuilder().id(3).firstName("julia").lastName("jumper").email("jj@test.com").build();
		Interviewer interviewerOne = new InterviewerBuilder().id(2).user(interviewerUserOne).build();
		RegisteredUser interviewerUserTwo = new RegisteredUserBuilder().id(4).firstName("kate").lastName("kook").email("kk@test.com").build();
		Interviewer interviewerTwo = new InterviewerBuilder().id(4).user(interviewerUserTwo).interviewComment(new InterviewCommentBuilder().id(4).build()).build();
		Interview interview = new InterviewBuilder().id(2).interviewers(interviewerOne, interviewerTwo).build();
		
		RegisteredUser approver = new RegisteredUserBuilder().id(78).firstName("aaa").lastName("aaa").email("aa@test.com").build();
		
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).title("program title").approver(approver).build();
	
		NotificationRecord notificationRecord = new NotificationRecordBuilder().id(1).notificationType(NotificationType.UPDATED_NOTIFICATION).notificationDate(new SimpleDateFormat("dd MM yyyy").parse("01 06 2011")).build();
		ApplicationForm form = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).id(2).latestInterview(interview).applicationNumber("xyz").program(program).notificationRecords(notificationRecord).applicant(new RegisteredUserBuilder().firstName("a").lastName("b").build()).build();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock3 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock4 = EasyMock.createMock(MimeMessagePreparator.class);
	
		InternetAddress toAddress1 = new InternetAddress("kk@test.com", "kate kook");
		InternetAddress toAddress2 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress3 = new InternetAddress("hh@test.com", "henry harck");
		InternetAddress toAddress4 = new InternetAddress("jj@test.com", "julia jumper");
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.update"), 
				EasyMock.aryEq(new Object[] { "xyz", "program title" , "a", "b"}), EasyMock.eq((Locale)null))).andReturn("update subject").anyTimes();
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("update subject"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("update subject"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress3), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("update subject"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock3);
		
		EasyMock.expect(
		        mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress4), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("update subject"),
		                EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock4);
		
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		javaMailSenderMock.send(preparatorMock3);
		javaMailSenderMock.send(preparatorMock4);
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		mailService.sendApplicationUpdatedMailToAdmins(form);
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldNotSendEmailToItnerviewersIfInApproval() throws UnsupportedEncodingException, ParseException{
		RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").build();
		
		RegisteredUser interviewerUserOne = new RegisteredUserBuilder().id(3).firstName("julia").lastName("jumper").email("jj@test.com").build();
		Interviewer interviewerOne = new InterviewerBuilder().id(2).user(interviewerUserOne).build();
		RegisteredUser interviewerUserTwo = new RegisteredUserBuilder().id(4).firstName("kate").lastName("kook").email("kk@test.com").build();
		Interviewer interviewerTwo = new InterviewerBuilder().id(4).user(interviewerUserTwo).interviewComment(new InterviewCommentBuilder().id(4).build()).build();
		Interview interview = new InterviewBuilder().id(2).interviewers(interviewerOne, interviewerTwo).build();
		
		RegisteredUser approver = new RegisteredUserBuilder().id(78).firstName("aaa").lastName("aaa").email("aa@test.com").build();
		
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).title("program title").approver(approver).build();
	
		NotificationRecord notificationRecord = new NotificationRecordBuilder().id(1).notificationType(NotificationType.UPDATED_NOTIFICATION).notificationDate(new SimpleDateFormat("dd MM yyyy").parse("01 06 2011")).build();
		ApplicationForm form = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).id(2).latestInterview(interview).applicationNumber("xyz").program(program).notificationRecords(notificationRecord).applicant(new RegisteredUserBuilder().firstName("a").lastName("b").build()).build();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock3 = EasyMock.createMock(MimeMessagePreparator.class);
	
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "henry harck");
		InternetAddress toAddress3 = new InternetAddress("aa@test.com", "aaa aaa");
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.update"), 
				EasyMock.aryEq(new Object[] { "xyz", "program title" , "a", "b"}), EasyMock.eq((Locale)null))).andReturn("update subject").anyTimes();
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("update subject"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("update subject"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress3), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("update subject"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock3);
		
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		javaMailSenderMock.send(preparatorMock3);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		mailService.sendApplicationUpdatedMailToAdmins(form);
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToReferees() throws UnsupportedEncodingException {
		Program program = new ProgramBuilder().title("title").build();
		ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).applicationNumber("xyz").applicant(new RegisteredUserBuilder().firstName("a").lastName("b").build()).build();
		
		RegisteredUser refereeOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		RegisteredUser refereeTwo = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").build();
		Referee referee1 = new RefereeBuilder().application(form).id(2).user(refereeTwo).toReferee();
		Referee referee2 = new RefereeBuilder().application(form).id(1).user(refereeOne).toReferee();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.withdrawal"), 
				EasyMock.aryEq(new Object[] { "xyz", "title", "a", "b" }), EasyMock.eq((Locale)null))).andReturn("subject").times(2);
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		mailService.sendWithdrawMailToReferees(Arrays.asList(referee1, referee2));
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToAdmins() throws UnsupportedEncodingException {
		
		RegisteredUser admin1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		RegisteredUser admin2 = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").build();
		RegisteredUser formAdmin = new RegisteredUserBuilder().id(3).firstName("Fred").lastName("Forse").email("Forse@test.com").build();
		Program program = new ProgramBuilder().administrators(admin1, admin2).title("title").build();
		
		ApplicationForm form = new ApplicationFormBuilder().applicationAdministrator(formAdmin).id(2).applicationNumber("xyz").program(program).applicant(new RegisteredUserBuilder().firstName("a").lastName("b").build()).build();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock3 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "henry harck");
		InternetAddress toAddress3 = new InternetAddress("Forse@test.com", "Fred Forse");
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.withdrawal"), 
				EasyMock.aryEq(new Object[] { "xyz", "title" ,"a", "b"}), EasyMock.eq((Locale)null))).andReturn("subject").times(3);
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress3), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock3);
						
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		javaMailSenderMock.send(preparatorMock3);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		mailService.sendWithdrawToAdmins(form);
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToReviewers() throws UnsupportedEncodingException {
		
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
		ReviewRound previousReviewRound = new ReviewRoundBuilder().id(1).reviewers(reviewer1).build();
		
		RegisteredUser reviewerUser2 = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").build();
		Reviewer reviewer2 = new ReviewerBuilder().id(2).user(reviewerUser2).review(new ReviewCommentBuilder().id(4).build()).build();
		RegisteredUser reviewerUser3 = new RegisteredUserBuilder().id(3).firstName("Fred").lastName("Forse").email("Forse@test.com").build();
		Reviewer reviewer3 = new ReviewerBuilder().id(3).user(reviewerUser3).build();
		ReviewRound latestReviewRound = new ReviewRoundBuilder().id(1).reviewers(reviewer2, reviewer3).build();
		Program program = new ProgramBuilder().reviewers(reviewerUser1, reviewerUser2).title("title").build();
		
		
		
		ApplicationForm form = new ApplicationFormBuilder().reviewRounds(previousReviewRound, latestReviewRound).latestReviewRound(latestReviewRound).id(2).applicationNumber("xyz").program(program).applicant(new RegisteredUserBuilder().firstName("a").lastName("b").build()).build();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("Forse@test.com", "Fred Forse");
		
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.withdrawal"), 
				EasyMock.aryEq(new Object[] { "xyz", "title", "a", "b" }), EasyMock.eq((Locale)null))).andReturn("subject").anyTimes();
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
	
		javaMailSenderMock.send(preparatorMock1);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		mailService.sendWithdrawToReviewers(form);
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToInterviewers() throws UnsupportedEncodingException {
		
		RegisteredUser inrerviewerUser1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		Interviewer interviewer1 = new InterviewerBuilder().id(1).user(inrerviewerUser1).build();
		Interview previousItnerview = new InterviewBuilder().id(1).interviewers(interviewer1).build();
		
		RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").build();
		Interviewer interviewer2 = new InterviewerBuilder().id(2).user(interviewerUser2).interviewComment(new InterviewCommentBuilder().id(1).build()).build();
		RegisteredUser interviewerUser3 = new RegisteredUserBuilder().id(3).firstName("Fred").lastName("Forse").email("Forse@test.com").build();
		Interviewer itnerviewer3 = new InterviewerBuilder().id(3).user(interviewerUser3).build();
		
		Interview latestInterview = new InterviewBuilder().id(1).interviewers(interviewer2, itnerviewer3).build();
		Program program = new ProgramBuilder().reviewers(inrerviewerUser1, interviewerUser2).title("title").build();
		
		
		
		ApplicationForm form = new ApplicationFormBuilder().interviews(previousItnerview, latestInterview).latestInterview(latestInterview).id(2).applicationNumber("xyz").program(program).applicant(new RegisteredUserBuilder().firstName("a").lastName("b").build()).build();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("Forse@test.com", "Fred Forse");
		
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.withdrawal"), 
				EasyMock.aryEq(new Object[] { "xyz", "title", "a", "b" }), EasyMock.eq((Locale)null))).andReturn("subject").anyTimes();
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
	
		javaMailSenderMock.send(preparatorMock1);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		mailService.sendWithdrawToInterviewers(form);
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToSupervisors() throws UnsupportedEncodingException {
		
		RegisteredUser supervisorUser1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
		Supervisor supervisor1 = new SupervisorBuilder().id(1).user(supervisorUser1).build();
		ApprovalRound previousApprovalRound = new ApprovalRoundBuilder().id(1).supervisors(supervisor1).build();

		RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(3).firstName("Fred").lastName("Forse").email("Forse@test.com").build();
		Supervisor supervisor3 = new SupervisorBuilder().id(3).user(interviewerUser2).build();
		
		ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(1).supervisors( supervisor3).build();
		Program program = new ProgramBuilder().title("title").build();
		
		
		
		ApplicationForm form = new ApplicationFormBuilder().approvalRounds(previousApprovalRound, latestApprovalRound).latestApprovalRound(latestApprovalRound).id(2).applicationNumber("xyz").program(program).applicant(new RegisteredUserBuilder().firstName("a").lastName("b").build()).build();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("Forse@test.com", "Fred Forse");
		
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.withdrawal"), 
				EasyMock.aryEq(new Object[] { "xyz", "title", "a", "b" }), EasyMock.eq((Locale)null))).andReturn("subject").anyTimes();
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
	
		javaMailSenderMock.send(preparatorMock1);
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		
		mailService.sendWithdrawToSupervisors(form);
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);		
	}

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSendWithdrawnNotificationToAllUsers() throws UnsupportedEncodingException {
        RegisteredUser refereeOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").build();
        RegisteredUser refereeTwo = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").build();
        
        Program program = new ProgramBuilder().title("title").administrators(refereeOne).build();

        ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).applicationNumber("xyz").applicant(new RegisteredUserBuilder().firstName("a").lastName("b").build()).build();

        Referee referee1 = new RefereeBuilder().application(form).id(2).user(refereeTwo).toReferee();
        Referee referee2 = new RefereeBuilder().application(form).id(1).user(refereeOne).toReferee();

        MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
        MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
        
        InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
        InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
        
        EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("application.withdrawal"), 
                EasyMock.aryEq(new Object[] { "xyz", "title", "a", "b" }), EasyMock.eq((Locale)null))).andReturn("subject").times(2);
        
        EasyMock.expect(
                mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
                        EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
        EasyMock.expect(
                mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), (InternetAddress[]) EasyMock.isNull(), EasyMock.eq("subject"),
                        EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class), (InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);
        
        javaMailSenderMock.send(preparatorMock1);
        javaMailSenderMock.send(preparatorMock2);
        
        EasyMock.expectLastCall();
        
        EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
        
        mailService.sendWithdrawMailToAdminsReviewersInterviewersSupervisors(Arrays.asList(referee1, referee2), form);
        EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);      
    }	
}
