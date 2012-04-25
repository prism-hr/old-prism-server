package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

public class MailServiceTest {

	private MailService mailService;
	private ApplicationsService applicationsServiceMock;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private RefereeDAO refereeDAOMock;

	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveApplicationFormAndSendEmailsToRefereesAdminsAndApplicant() throws UnsupportedEncodingException{
	
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
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Application Validation Reminder"),EasyMock.eq("private/staff/admin/mail/application_validation_reminder.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		javaMailSenderMock.send(preparatorMock1);
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	
		
		mailService.sendMailToAdminsAndChangeLastReminderDate(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);
		assertNotNull(form.getLastEmailReminderDate());
		Assert.assertEquals(DateUtils.truncate(new Date(), Calendar.DATE), DateUtils.truncate(form.getLastEmailReminderDate(), Calendar.DATE));
	}
	
	
	@Test
	public void shouldDelegateGetRefereesDueReminderToDAO(){
		List<Referee> refList = Arrays.asList(new RefereeBuilder().id(1).toReferee(), new RefereeBuilder().id(2).toReferee());
		EasyMock.expect(refereeDAOMock.getRefereesDueAReminder()).andReturn(refList);		
		EasyMock.replay(refereeDAOMock);
		List<Referee> returnedReferees = mailService.getRefereesDueAReminder();
		assertSame(refList,returnedReferees);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendRefereeReminderUpdateLastNotifiedAndSaveForNewReferee() throws UnsupportedEncodingException{		
		
		Referee referee = new RefereeBuilder().id(4).firstname("john").lastname("boggs").email("ref@test.com").toReferee();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		referee.setApplication(form);
		
		refereeDAOMock.save(referee);
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);		
		InternetAddress toAddress = new InternetAddress("ref@test.com", "john boggs");
				
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Reminder - reference required"),EasyMock.eq("private/referees/mail/referee_reminder_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		
	
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock, refereeDAOMock);
	
		
		mailService.sendReminderAndUpdateLastNotified(referee);
		
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock,refereeDAOMock);
		assertNotNull(referee.getLastNotified());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldNotThrowExceptionAndNotUpdateRefereeIfSendRefereeReminderFailsNewReferee() throws UnsupportedEncodingException{		
		
		Referee referee = new RefereeBuilder().id(4).firstname("john").lastname("boggs").email("ref@test.com").toReferee();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		referee.setApplication(form);		
	
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);		
		InternetAddress toAddress = new InternetAddress("ref@test.com", "john boggs");
				
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Reminder - reference required"),EasyMock.eq("private/referees/mail/referee_reminder_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("Couldn't remind referee!!"));
	
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock, refereeDAOMock);
	
		
		mailService.sendReminderAndUpdateLastNotified(referee);
		
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock,refereeDAOMock);
		assertNull(referee.getLastNotified());
		
	}
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSendRefereeReminderUpdateLastNotifiedAndSaveForExistingUserReferee() throws UnsupportedEncodingException{		
		RegisteredUser user = new RegisteredUserBuilder().id(1).enabled(true).email("jboggs@test.com").firstName("Jonathan").lastName("Boggs").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("john").lastname("boggs").email("ref@test.com").user(user).toReferee();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		referee.setApplication(form);
		
		refereeDAOMock.save(referee);
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);		
		InternetAddress toAddress = new InternetAddress("jboggs@test.com", "Jonathan Boggs");
				
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Reminder - reference required"),EasyMock.eq("private/referees/mail/existing_user_referee_reminder_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		
	
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock, refereeDAOMock);
	
		
		mailService.sendReminderAndUpdateLastNotified(referee);
		
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock,refereeDAOMock);
		assertNotNull(referee.getLastNotified());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldNotThrowExceptionAndNotSaveRefereeIdReminderFailsForExistingUserReferee() throws UnsupportedEncodingException{		
		RegisteredUser user = new RegisteredUserBuilder().id(1).enabled(true).email("jboggs@test.com").firstName("Jonathan").lastName("Boggs").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("john").lastname("boggs").email("ref@test.com").user(user).toReferee();
		ApplicationForm form = new ApplicationFormBuilder().program(new Program()).toApplicationForm();
		referee.setApplication(form);
		
	
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);		
		InternetAddress toAddress = new InternetAddress("jboggs@test.com", "Jonathan Boggs");
				
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Reminder - reference required"),EasyMock.eq("private/referees/mail/existing_user_referee_reminder_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("yikes!"));

		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock, refereeDAOMock);
	
		
		mailService.sendReminderAndUpdateLastNotified(referee);
		
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock,refereeDAOMock);
		assertNull(referee.getLastNotified());
		
	}
	@Before
	public void setUp(){
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		refereeDAOMock = EasyMock.createMock(RefereeDAO.class);
		mailService = new MailService(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationsServiceMock, refereeDAOMock);

	}
	
}
