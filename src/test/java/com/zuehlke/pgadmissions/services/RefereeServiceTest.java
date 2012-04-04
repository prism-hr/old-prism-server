package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

public class RefereeServiceTest {
	
	private RefereeService refereeService;
	private UserService userServiceMock;
	private RefereeDAO refereeDAOMock;
	private JavaMailSender javaMailSenderMock;
	private RoleDAO roleDAOMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	
	@Before
	public void setUp(){
		refereeDAOMock = EasyMock.createMock(RefereeDAO.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		refereeService = new RefereeService(refereeDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, userServiceMock, roleDAOMock);
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
	
	@Test
	public void shouldReturnUserIfRefereeAlreadyExists(){
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		userServiceMock.save(reviewer);
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").toReferee();
		EasyMock.expect(userServiceMock.getUserByEmail("email@test.com")).andReturn(reviewer);
		EasyMock.replay(userServiceMock);
		RegisteredUser existedReferee = refereeService.getRefereeIfAlreadyRegistered(referee);
		Assert.assertNotNull(existedReferee);
	}
	
	@Test
	public void shouldReturnNullIfRefereeNotExists(){
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		userServiceMock.save(reviewer);
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("otherrefemail@test.com").toReferee();
		EasyMock.expect(userServiceMock.getUserByEmail("otherrefemail@test.com")).andReturn(null);
		EasyMock.replay(userServiceMock);
		RegisteredUser existedReferee = refereeService.getRefereeIfAlreadyRegistered(referee);
		Assert.assertNull(existedReferee);
	}
	
	@Test
	public void shouldAddRefereeRoleIfUserExistsAndIsNotAReferee(){
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser user = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		userServiceMock.save(user);
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").toReferee();
		EasyMock.expect(userServiceMock.getUserByEmail("email@test.com")).andReturn(user);
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		RegisteredUser existedReferee = refereeService.processRefereeAndGetAsUser(referee);
		Assert.assertNotNull(existedReferee);
		Assert.assertEquals(2, existedReferee.getRoles().size());
	}
	
	@Test
	public void shouldNotAddRefereeRoleIfUserExistsAndIsAlreadyAReferee(){
		Role refereeRole = new RoleBuilder().authorityEnum(Authority.REFEREE).toRole();
		RegisteredUser user = new RegisteredUserBuilder().id(3).role(refereeRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		userServiceMock.save(user);
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").toReferee();
		EasyMock.expect(userServiceMock.getUserByEmail("email@test.com")).andReturn(user);
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		RegisteredUser existedReferee = refereeService.processRefereeAndGetAsUser(referee);
		Assert.assertNotNull(existedReferee);
		Assert.assertEquals(1, existedReferee.getRoles().size());
	}
	
	@Test
	public void shouldCreateUserWithRefereeRoleIfRefereeDoesNotExist(){
		final RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("emailemail@test.com").toReferee();
		refereeService = new RefereeService(refereeDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, userServiceMock, roleDAOMock){
			@Override
			RegisteredUser newRegisteredUser() {
				return user;
			}
		};
		EasyMock.expect(userServiceMock.getUserByEmail("emailemail@test.com")).andReturn(null);
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		RegisteredUser newUser = refereeService.processRefereeAndGetAsUser(referee);
		EasyMock.verify(userServiceMock);
		Assert.assertNotNull(newUser);
		Assert.assertEquals(1, newUser.getRoles().size());
		Assert.assertEquals("ref", newUser.getFirstName());
		Assert.assertEquals("erre", newUser.getLastName());
		Assert.assertEquals("emailemail@test.com", newUser.getEmail());
	}
	
	@Test
	public void shouldReturnRefereeById() {
		Referee referee = EasyMock.createMock(Referee.class);
		EasyMock.expect(refereeDAOMock.getRefereeById(23)).andReturn(referee);
		EasyMock.replay(referee, refereeDAOMock);
		
		Assert.assertEquals(referee, refereeService.getRefereeById(23));
	}

	
}
