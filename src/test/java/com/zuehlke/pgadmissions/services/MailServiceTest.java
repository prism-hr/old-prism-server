package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;

public class MailServiceTest {

	private MailService mailService;
	private ApplicationsService applicationsServiceMock;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;


	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveApplicationFormAndSendEmailsToRefereesAdminsAndApplicantAndCreateNewNotificationRecord() throws UnsupportedEncodingException {

		Role admin = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		RegisteredUser administrator = new RegisteredUserBuilder().role(admin).id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(currentUser).id(2).program(program).toApplicationForm();
		ProgrammeDetails programmeDetails = new ProgrammeDetails();
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		applicationsServiceMock.save(form);

		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Application Validation Reminder"),
						EasyMock.eq("private/staff/admin/mail/application_validation_reminder.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		javaMailSenderMock.send(preparatorMock1);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		mailService.sendValidationReminderMailToAdminsAndChangeLastReminderDate(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);
		assertNotNull(form.getNotificationForType(NotificationType.VALIDATION_REMINDER));
		Assert.assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),
				DateUtils.truncate(form.getNotificationForType(NotificationType.VALIDATION_REMINDER).getNotificationDate(), Calendar.DATE));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveApplicationFormAndSendEmailsToRefereesAdminsAndApplicantAndUpdateExistingNotificationRecord() throws UnsupportedEncodingException,
			ParseException {

		Role admin = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		RegisteredUser administrator = new RegisteredUserBuilder().role(admin).id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		NotificationRecord existingNotificationRecord = new NotificationRecordBuilder().id(1).notificationType(NotificationType.VALIDATION_REMINDER)
				.notificationDate(new SimpleDateFormat("dd MM yyyy").parse("01 06 2011")).toNotificationRecord();
		ApplicationForm form = new ApplicationFormBuilder().applicant(currentUser).id(2).program(program).notificationRecords(existingNotificationRecord)
				.toApplicationForm();
		ProgrammeDetails programmeDetails = new ProgrammeDetails();
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		applicationsServiceMock.save(form);

		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Application Validation Reminder"),
						EasyMock.eq("private/staff/admin/mail/application_validation_reminder.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		javaMailSenderMock.send(preparatorMock1);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		mailService.sendValidationReminderMailToAdminsAndChangeLastReminderDate(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);
		assertSame(existingNotificationRecord, form.getNotificationForType(NotificationType.VALIDATION_REMINDER));
		Assert.assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),
				DateUtils.truncate(form.getNotificationForType(NotificationType.VALIDATION_REMINDER).getNotificationDate(), Calendar.DATE));
	}


	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendEmailToApplicant() throws UnsupportedEncodingException {

		RegisteredUser applicant = new RegisteredUserBuilder().id(1).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).id(2).program(new ProgramBuilder().toProgram()).toApplicationForm();

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);

		InternetAddress toAddress = new InternetAddress("hh@test.com", "harry hen");

		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Application Submitted"),
						EasyMock.eq("private/pgStudents/mail/application_submit_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		mailService.sendSubmissionMailToApplicant(form);
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);

	}
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendSubmissionEmailToAdmins() throws UnsupportedEncodingException {

		RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).toProgram();
	
		ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).toApplicationForm();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
	
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
			
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Application Submitted"),
						EasyMock.eq("private/staff/admin/mail/application_submit_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), EasyMock.eq("Application Submitted"),
						EasyMock.eq("private/staff/admin/mail/application_submit_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		mailService.sendSubmissionMailToAdmins(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);		
		assertNotNull(form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION).getNotificationDate(), Calendar.DATE));
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendUpdatedEmailToAdmins() throws UnsupportedEncodingException, ParseException {

		RegisteredUser administratorOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser administratorTwo = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administratorOne, administratorTwo).toProgram();
	
		NotificationRecord notificationRecord = new NotificationRecordBuilder().id(1).notificationType(NotificationType.UPDATED_NOTIFICATION).notificationDate(new SimpleDateFormat("dd MM yyyy").parse("01 06 2011")).toNotificationRecord();
		ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).notificationRecords(notificationRecord).toApplicationForm();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
	
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
			
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Application Updated"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), EasyMock.eq("Application Updated"),
						EasyMock.eq("private/staff/admin/mail/application_updated_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		mailService.sendApplicationUpdatedMailToAdmins(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);		
		assertSame(notificationRecord, form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION).getNotificationDate(), Calendar.DATE));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToReferees() throws UnsupportedEncodingException, ParseException {
		Program program = new ProgramBuilder().toProgram();
		ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).applicant(new RegisteredUser()).toApplicationForm();
		
		RegisteredUser refereeOne = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser refereeTwo = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").toUser();
		Referee referee1 = new RefereeBuilder().application(form).id(2).user(refereeTwo).toReferee();
		Referee referee2 = new RefereeBuilder().application(form).id(2).id(1).user(refereeOne).toReferee();
		
		
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Application Withdrawn"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), EasyMock.eq("Application Withdrawn"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		
		mailService.sendWithdrawMailToReferees(Arrays.asList(referee1, referee2));
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToAdmins() throws UnsupportedEncodingException, ParseException {
		
		RegisteredUser admin1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser admin2 = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").toUser();
		Program program = new ProgramBuilder().administrators(admin1, admin2).toProgram();
		
		ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).toApplicationForm();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Application Withdrawn"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), EasyMock.eq("Application Withdrawn"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		
		mailService.sendWithdrawToAdmins(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);		
	}
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendWithdrawnNotificationToReviewers() throws UnsupportedEncodingException, ParseException {
		
		RegisteredUser reviewer1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		RegisteredUser reviewer2 = new RegisteredUserBuilder().id(2).firstName("henry").lastName("harck").email("hh@test.com").toUser();
		Program program = new ProgramBuilder().reviewers(reviewer1, reviewer2).toProgram();
		
		ApplicationForm form = new ApplicationFormBuilder().id(2).program(program).toApplicationForm();
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		
		InternetAddress toAddress1 = new InternetAddress("bb@test.com", "benny brack");
		InternetAddress toAddress2 = new InternetAddress("hh@test.com", "harck");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Application Withdrawn"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), EasyMock.eq("Application Withdrawn"),
						EasyMock.eq("private/staff/mail/application_withdrawn_notification.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock2);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		
		mailService.sendWithdrawToReviewers(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);		
	}
	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		
		mailService = new MailService(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationsServiceMock);

	}

}
