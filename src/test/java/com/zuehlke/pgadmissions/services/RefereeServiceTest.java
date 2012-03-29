package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
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

public class RefereeServiceTest {
	
	private RefereeService refereeService;
	private RefereeDAO refereeDAOMock;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	
	@Before
	public void setUp(){
		refereeDAOMock = EasyMock.createMock(RefereeDAO.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		refereeService = new RefereeService(refereeDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}

	@Test
	public void shouldSaveReferenceAndSendEmailsAdminsAndApplicant() throws UnsupportedEncodingException{
	
		Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		Role applicantRole = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		RegisteredUser admin2 = new RegisteredUserBuilder().id(2).role(adminRole).firstName("anna").lastName("allen").email("email2@test.com").toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(3).role(applicantRole).firstName("fred").lastName("freddy").email("email3@test.com").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").toReferee();
		Program program = new ProgramBuilder().administrators(admin1, admin2).toProgram();
		Project project = new ProjectBuilder().program(program).toProject();
		ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).referees(referee).id(2).project(project).toApplicationForm();
		referee.setApplication(form);
		ProgrammeDetail programmeDetails = new ProgrammeDetail();	
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		refereeDAOMock.save(referee);
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock3 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("email@test.com", "bob bobson");
		InternetAddress toAddress2 = new InternetAddress("email2@test.com", "anna allen");
		InternetAddress toAddress3 = new InternetAddress("email3@test.com", "fred freddy");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Applicant Reference Submitted"),EasyMock.eq("private/staff/admin/mail/reference_submit_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2), EasyMock.eq("Applicant Reference Submitted"),EasyMock.eq("private/staff/admin/mail/reference_submit_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock2);
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress3), EasyMock.eq("Reference Submitted"),EasyMock.eq("private/pgStudents/mail/reference_submit_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock3);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		javaMailSenderMock.send(preparatorMock3);
	
		EasyMock.replay( mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	
		
		refereeService.saveReferenceAndSendMailNotifications(referee);
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock);
	}
	
	@Test
	public void shouldNotSendEmailIfSaveFails() throws UnsupportedEncodingException {
		refereeDAOMock.save(null);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaaaaaargh"));

		EasyMock.replay(refereeDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		try {
			refereeService.saveReferenceAndSendMailNotifications(null);
		} catch (RuntimeException e) {
			// expected...ignore
		}

		EasyMock.verify(refereeDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}
	
	@Test
	public void shouldNotThrowExceptionIfEmailSendingFails() throws UnsupportedEncodingException {
		Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		Role applicantRole = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		RegisteredUser admin2 = new RegisteredUserBuilder().id(2).role(adminRole).firstName("anna").lastName("allen").email("email2@test.com").toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(3).role(applicantRole).firstName("fred").lastName("freddy").email("email3@test.com").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").toReferee();
		
		Program program = new ProgramBuilder().administrators(admin1, admin2).toProgram();
		Project project = new ProjectBuilder().program(program).toProject();
		ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).referees(referee).id(2).project(project).toApplicationForm();
		referee.setApplication(form);
		ProgrammeDetail programmeDetails = new ProgrammeDetail();	
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		refereeDAOMock.save(referee);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "bob bobson");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Applicant Reference Submitted"),EasyMock.eq("private/staff/admin/mail/reference_submit_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("AARrrgggg"));
		EasyMock.replay(refereeDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		refereeService.saveReferenceAndSendMailNotifications(referee);
		EasyMock.verify(refereeDAOMock, mimeMessagePreparatorFactoryMock);
	}
	
	@Test
	public void shouldReturnRefereeWithActivationCode() {
		Referee referee = EasyMock.createMock(Referee.class);
		referee.setActivationCode("2345");
		EasyMock.expect(refereeDAOMock.getRefereeByActivationCode("2345")).andReturn(referee);
		EasyMock.replay(referee, refereeDAOMock);
		
		Assert.assertEquals(referee, refereeService.getRefereeByActivationCode("2345"));
	}

	
}
