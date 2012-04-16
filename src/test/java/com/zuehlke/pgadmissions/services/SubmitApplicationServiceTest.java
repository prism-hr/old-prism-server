package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;


public class SubmitApplicationServiceTest {
	
	private ApplicationsService applicationsServiceMock;
	private SubmitApplicationService submitApplicationService;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;

	@Before
	public void setUp(){
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		
		submitApplicationService = new SubmitApplicationService(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationsServiceMock);

	}

	@Test
	public void shouldSaveApplicationFormAndSendEmailsToRefereesAdminsAndApplicant() throws UnsupportedEncodingException{
	
		Role reviewer = new RoleBuilder().authorityEnum(Authority.REFEREE).toRole();
		RegisteredUser refereeUserDisabled= new RegisteredUserBuilder().id(1).role(reviewer).enabled(false).toUser();
		Referee referee1 = new RefereeBuilder().id(1).firstname("bob").user(refereeUserDisabled).lastname("bobson").email("email@test.com").toReferee();
		Referee referee2 = new RefereeBuilder().id(2).firstname("anna").user(refereeUserDisabled).lastname("allen").email("email2@test.com").toReferee();
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();
		Project project = new ProjectBuilder().program(program).toProject();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(currentUser).referees(referee1, referee2).id(2).project(project).toApplicationForm();
		ProgrammeDetails programmeDetails = new ProgrammeDetails();	
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		applicationsServiceMock.save(form);
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock3 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock4 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("email@test.com", "bob bobson");
		InternetAddress toAddress2 = new InternetAddress("email2@test.com", "anna allen");
		InternetAddress toAddress3 = new InternetAddress("hh@test.com", "harry hen");
		InternetAddress toAddress4 = new InternetAddress("bb@test.com", "benny brack");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Referee Notification"),EasyMock.eq("private/referees/mail/referee_notification_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), EasyMock.eq("Referee Notification"),EasyMock.eq("private/referees/mail/referee_notification_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock2);
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress3), EasyMock.eq("Application Submitted"),EasyMock.eq("private/pgStudents/mail/application_submit_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock3);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress4), EasyMock.eq("Application Submitted"),EasyMock.eq("private/staff/admin/mail/application_submit_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock4);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		javaMailSenderMock.send(preparatorMock3);
		javaMailSenderMock.send(preparatorMock4);
	
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	
		
		submitApplicationService.saveApplicationFormAndSendMailNotifications(form);
		EasyMock.verify(applicationsServiceMock, javaMailSenderMock, mimeMessagePreparatorFactoryMock);
	}
	@Test
	public void shouldNotSendEmailWithRegistrationFormToAlreadyExistingReferees() throws UnsupportedEncodingException{
		Role reviewer = new RoleBuilder().authorityEnum(Authority.REFEREE).toRole();
		RegisteredUser refereeUserEnabled = new RegisteredUserBuilder().id(1).role(reviewer).enabled(true).toUser();
		RegisteredUser refereeUserDisabled = new RegisteredUserBuilder().id(1).enabled(false).role(reviewer).toUser();
		Referee referee1 = new RefereeBuilder().id(1).firstname("bob").lastname("bobson").user(refereeUserEnabled).email("email@test.com").toReferee();
		Referee referee2 = new RefereeBuilder().id(2).firstname("anna").lastname("allen").user(refereeUserDisabled).email("email2@test.com").toReferee();
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();
		Project project = new ProjectBuilder().program(program).toProject();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(currentUser).referees(referee1, referee2).id(2).project(project).toApplicationForm();
		ProgrammeDetails programmeDetails = new ProgrammeDetails();	
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("email@test.com", "bob bobson");
		InternetAddress toAddress2 = new InternetAddress("email2@test.com", "anna allen");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Referee Notification"),EasyMock.eq("private/referees/mail/existing_user_referee_notification_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), EasyMock.eq("Referee Notification"),EasyMock.eq("private/referees/mail/referee_notification_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock2);
		
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		
		
		submitApplicationService.sendMailToReferees(form);
		EasyMock.verify( javaMailSenderMock, mimeMessagePreparatorFactoryMock);
	}
	
	@Test
	public void shouldNotSendEmailIfSaveFails() throws UnsupportedEncodingException {
		applicationsServiceMock.save(null);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaaaaaargh"));

		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		try {
			submitApplicationService.saveApplicationFormAndSendMailNotifications(null);
		} catch (RuntimeException e) {
			// expected...ignore
		}

		EasyMock.verify(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}
	
	@Test
	public void shouldNotThrowExceptionIfEmailSendingFails() throws UnsupportedEncodingException {
		Role reviewer = new RoleBuilder().authorityEnum(Authority.REFEREE).toRole();
		RegisteredUser refereeUserEnabled = new RegisteredUserBuilder().id(1).role(reviewer).enabled(true).toUser();
		Referee referee1 = new RefereeBuilder().id(1).firstname("bob").lastname("bobson").user(refereeUserEnabled).email("email@test.com").toReferee();
		Referee referee2 = new RefereeBuilder().id(2).firstname("anna").lastname("allen").user(refereeUserEnabled).email("email2@test.com").toReferee();
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();
		Project project = new ProjectBuilder().program(program).toProject();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(currentUser).referees(referee1, referee2).id(2).project(project).toApplicationForm();
		ProgrammeDetails programmeDetails = new ProgrammeDetails();	
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		applicationsServiceMock.save(form);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "bob bobson");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Referee Notification"),EasyMock.eq("private/referees/mail/existing_user_referee_notification_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("AARrrgggg"));
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		submitApplicationService.saveApplicationFormAndSendMailNotifications(form);

		EasyMock.verify(applicationsServiceMock, mimeMessagePreparatorFactoryMock);

	}

	
	

}

