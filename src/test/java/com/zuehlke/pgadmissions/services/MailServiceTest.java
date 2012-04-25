package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Assert;
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

public class MailServiceTest {

	private MailService mailService;
	private ApplicationsService applicationsServiceMock;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;

	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveApplicationFormAndSendEmailsToRefereesAdminsAndApplicant() throws UnsupportedEncodingException{
	
		Role admin = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		RegisteredUser administrator = new RegisteredUserBuilder().role(admin).id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();
		Project project = new ProjectBuilder().program(program).toProject();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(currentUser).id(2).project(project).toApplicationForm();
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
		Assert.assertEquals(new Date(), form.getLastEmailReminderDate());
	}
	
	
	@Before
	public void setUp(){
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		
		mailService = new MailService(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationsServiceMock);

	}
	
}
