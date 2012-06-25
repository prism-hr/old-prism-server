package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceEvent;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.EventFactory;

public class RefereeServiceTest {

	private RefereeService refereeService;
	private UserService userServiceMock;
	private RefereeDAO refereeDAOMock;
	private JavaMailSender javaMailSenderMock;
	private RoleDAO roleDAOMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private MessageSource msgSourceMock;
	private EventFactory eventFactoryMock;
	private ApplicationFormDAO applicationFormDAOMock;
	private EncryptionUtils encryptionUtilsMock;


	@Before
	public void setUp() {
		refereeDAOMock = EasyMock.createMock(RefereeDAO.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		eventFactoryMock = EasyMock.createMock(EventFactory.class);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
		refereeService = new RefereeService(refereeDAOMock, encryptionUtilsMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, userServiceMock, roleDAOMock, msgSourceMock, eventFactoryMock, applicationFormDAOMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveReferenceAndSendEmailsAdminsAndApplicant() throws UnsupportedEncodingException {

		Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		Role applicantRole = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		RegisteredUser admin2 = new RegisteredUserBuilder().id(2).role(adminRole).firstName("anna").lastName("allen").email("email2@test.com").toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(3).role(applicantRole).firstName("fred").lastName("freddy").email("email3@test.com").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").toReferee();
		Program program = new ProgramBuilder().title("program title").administrators(admin1, admin2).toProgram();

		ApplicationForm form = new ApplicationFormBuilder().applicationNumber("xyz").applicant(applicant).referees(referee).id(2).program(program).toApplicationForm();
		referee.setApplication(form);
		ProgrammeDetails programmeDetails = new ProgrammeDetails();
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		ReferenceEvent event = new ReferenceEventBuilder().id(4).toEvent();
		EasyMock.expect(eventFactoryMock.createEvent(referee)).andReturn(event);
		applicationFormDAOMock.save(form);
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock2 = EasyMock.createMock(MimeMessagePreparator.class);
		MimeMessagePreparator preparatorMock3 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("email@test.com", "bob bobson");
		InternetAddress toAddress2 = new InternetAddress("email2@test.com", "anna allen");
		InternetAddress toAddress3 = new InternetAddress("email3@test.com", "fred freddy");

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("reference.provided.admin"),// 
				EasyMock.aryEq(new Object[] { "xyz", "program title", "fred", "freddy" }), EasyMock.eq((Locale) null))).andReturn("admin notification subject");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1),// 
				EasyMock.eq("admin notification subject"),// 
				EasyMock.eq("private/staff/admin/mail/reference_submit_confirmation.ftl"),// 
				EasyMock.isA(Map.class), //
				(InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress2),// 
				EasyMock.eq("admin notification subject"), //
				EasyMock.eq("private/staff/admin/mail/reference_submit_confirmation.ftl"), //
				EasyMock.isA(Map.class), //
				(InternetAddress) EasyMock.isNull())).andReturn(preparatorMock2);

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("reference.provided.applicant"),// 
				EasyMock.aryEq(new Object[] {"xyz", "program title", "fred", "freddy" }), EasyMock.eq((Locale) null))).andReturn("applicant notification subject");
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress3), //
				EasyMock.eq("applicant notification subject"),//
				EasyMock.eq("private/pgStudents/mail/reference_respond_confirmation.ftl"),//
				EasyMock.isA(Map.class), (InternetAddress) //
				EasyMock.isNull())).andReturn(preparatorMock3);
		javaMailSenderMock.send(preparatorMock1);
		javaMailSenderMock.send(preparatorMock2);
		javaMailSenderMock.send(preparatorMock3);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, refereeDAOMock, eventFactoryMock, applicationFormDAOMock);

		refereeService.saveReferenceAndSendMailNotifications(referee);
		assertEquals(1, form.getEvents().size());
		assertEquals(event, form.getEvents().get(0));
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock, refereeDAOMock, eventFactoryMock, applicationFormDAOMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveAndSendEmailToReferee() throws UnsupportedEncodingException {

		Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		Role applicantRole = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		RegisteredUser admin2 = new RegisteredUserBuilder().id(2).role(adminRole).firstName("anna").lastName("allen").email("email2@test.com").toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(3).role(applicantRole).firstName("fred").lastName("freddy").email("email3@test.com").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").toReferee();
		Program program = new ProgramBuilder().title("program title").administrators(admin1, admin2).toProgram();

		ApplicationForm form = new ApplicationFormBuilder().applicationNumber("xyz").applicant(applicant).referees(referee).id(2).program(program).toApplicationForm();
		referee.setApplication(form);
		ProgrammeDetails programmeDetails = new ProgrammeDetails();
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("reference.request"),// 
				EasyMock.aryEq(new Object[] { "xyz", "program title", "fred", "freddy" }), EasyMock.eq((Locale) null))).andReturn("subject");
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("ref@test.com", "ref erre");
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), //
				EasyMock.eq("subject"),//
				EasyMock.eq("private/referees/mail/referee_notification_email.ftl"),//
				EasyMock.isA(Map.class), //
				(InternetAddress) EasyMock.isNull())).andReturn(preparatorMock1);
		javaMailSenderMock.send(preparatorMock1);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		refereeService.sendRefereeMailNotification(referee);
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldNotThrowExceptionIfEmailSendingFails() throws UnsupportedEncodingException {
		Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		Role applicantRole = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		RegisteredUser applicant = new RegisteredUserBuilder().id(3).role(applicantRole).firstName("fred").lastName("freddy").email("email3@test.com").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").toReferee();

		Program program = new ProgramBuilder().title("some title").administrators(admin1).toProgram();

		ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).applicationNumber("xyz").referees(referee).id(2).program(program).toApplicationForm();
		referee.setApplication(form);
		ProgrammeDetails programmeDetails = new ProgrammeDetails();
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email3@test.com", "fred freddy");
		InternetAddress toAdminAddress = new InternetAddress("email@test.com", "bob bobson");

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("reference.provided.applicant"),// 
				EasyMock.aryEq(new Object[] { "xyz", "some title", "fred", "freddy" }), EasyMock.eq((Locale) null))).andReturn("subject");
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("reference.provided.admin"),// 
				EasyMock.aryEq(new Object[] { "xyz", "some title", "fred", "freddy" }), EasyMock.eq((Locale) null))).andReturn("admin subject");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress),// 
				EasyMock.eq("subject"), //
				EasyMock.eq("private/pgStudents/mail/reference_respond_confirmation.ftl"), //
				EasyMock.isA(Map.class),//
				(InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAdminAddress),// 
				EasyMock.eq("admin subject"), //
				EasyMock.eq("private/staff/admin/mail/reference_submit_confirmation.ftl"), //
				EasyMock.isA(Map.class),//
				(InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("AARrrgggg"));
		javaMailSenderMock.send(preparatorMock);
		EasyMock.replay(refereeDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		refereeService.saveReferenceAndSendMailNotifications(referee);
		EasyMock.verify(refereeDAOMock, mimeMessagePreparatorFactoryMock, msgSourceMock);
	}


	@Test
	public void shouldReturnUserIfRefereeAlreadyExists() {
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		userServiceMock.save(reviewer);
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").toReferee();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(reviewer);
		EasyMock.replay(userServiceMock);
		RegisteredUser existedReferee = refereeService.getRefereeIfAlreadyRegistered(referee);
		Assert.assertNotNull(existedReferee);
	}

	@Test
	public void shouldReturnNullIfRefereeNotExists() {
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		userServiceMock.save(reviewer);
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("otherrefemail@test.com").toReferee();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("otherrefemail@test.com")).andReturn(null);
		EasyMock.replay(userServiceMock);
		RegisteredUser existedReferee = refereeService.getRefereeIfAlreadyRegistered(referee);
		Assert.assertNull(existedReferee);
	}

	@Test
	public void shouldAddRefereeRoleIfUserExistsAndIsNotAReferee() {
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser user = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		userServiceMock.save(user);
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").toReferee();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(user);
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		RegisteredUser existedReferee = refereeService.processRefereeAndGetAsUser(referee);
		Assert.assertNotNull(existedReferee);
		Assert.assertEquals(2, existedReferee.getRoles().size());
	}

	@Test
	public void shouldAddRefereeRoleIfUserExistsAndIsApproverReviewerAdmin() {
		Role reviewerRole = new RoleBuilder().id(1).authorityEnum(Authority.REVIEWER).toRole();
		Role adminRole = new RoleBuilder().id(2).authorityEnum(Authority.ADMINISTRATOR).toRole();
		Role approverRole = new RoleBuilder().id(3).authorityEnum(Authority.APPROVER).toRole();
		RegisteredUser user = new RegisteredUserBuilder().id(1).roles(reviewerRole, adminRole, approverRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		userServiceMock.save(user);
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").toReferee();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(user);
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		RegisteredUser existedReferee = refereeService.processRefereeAndGetAsUser(referee);
		Assert.assertNotNull(existedReferee);
		Assert.assertEquals(4, existedReferee.getRoles().size());
	}

	@Test
	public void shouldNotAddRefereeRoleIfUserExistsAndIsAlreadyAReferee() {
		Role refereeRole = new RoleBuilder().authorityEnum(Authority.REFEREE).toRole();
		RegisteredUser user = new RegisteredUserBuilder().id(3).role(refereeRole).firstName("bob").lastName("bobson").email("email@test.com").toUser();
		userServiceMock.save(user);
		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").toReferee();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(user);
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		RegisteredUser existedReferee = refereeService.processRefereeAndGetAsUser(referee);
		Assert.assertNotNull(existedReferee);
		Assert.assertEquals(1, existedReferee.getRoles().size());
	}

	@Test
	public void shouldCreateUserWithRefereeRoleIfRefereeDoesNotExist() {
		final RegisteredUser user = new RegisteredUserBuilder().id(1).accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).toUser();
		Referee referee = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail@test.com").toReferee();
		refereeService = new RefereeService(refereeDAOMock, encryptionUtilsMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, userServiceMock, roleDAOMock, msgSourceMock, eventFactoryMock,applicationFormDAOMock) {
			@Override
			RegisteredUser newRegisteredUser() {
				return user;
			}
		};
		Role role = new RoleBuilder().id(1).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REFEREE)).andReturn(role);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("emailemail@test.com")).andReturn(null);
		userServiceMock.save(user);
		referee.setUser(user);
		refereeDAOMock.save(referee);
		EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("abc");
		EasyMock.replay(userServiceMock, refereeDAOMock, encryptionUtilsMock,roleDAOMock);
		
		RegisteredUser newUser = refereeService.processRefereeAndGetAsUser(referee);
		EasyMock.verify(refereeDAOMock, userServiceMock);
		Assert.assertNotNull(newUser);
		Assert.assertEquals(1, newUser.getRoles().size());
		Assert.assertEquals(role, newUser.getRoles().get(0));
		Assert.assertEquals("ref", newUser.getFirstName());
		Assert.assertEquals("erre", newUser.getLastName());
		Assert.assertEquals("emailemail@test.com", newUser.getEmail());
		Assert.assertEquals("emailemail@test.com", newUser.getUsername());
		assertTrue(newUser.isAccountNonExpired());
		assertTrue(newUser.isAccountNonLocked());
		assertTrue(newUser.isCredentialsNonExpired());
		assertFalse(newUser.isEnabled());
		assertEquals("abc", newUser.getActivationCode());
	}

	@Test
	public void shouldReturnRefereeById() {
		Referee referee = EasyMock.createMock(Referee.class);
		EasyMock.expect(refereeDAOMock.getRefereeById(23)).andReturn(referee);
		EasyMock.replay(referee, refereeDAOMock);

		Assert.assertEquals(referee, refereeService.getRefereeById(23));
	}

	@Test
	public void shouldGetRefereeByUserAndApplication() {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee1 = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail1@test.com").application(form).toReferee();
		Referee referee2 = new RefereeBuilder().id(2).firstname("ref").lastname("erre").email("emailemail2@test.com").toReferee();
		Referee referee3 = new RefereeBuilder().id(3).firstname("ref").lastname("erre").email("emailemail3@test.com").toReferee();

		RegisteredUser user = new RegisteredUserBuilder().referees(referee1, referee2, referee3).id(1).toUser();

		Referee referee = refereeService.getRefereeByUserAndApplication(user, form);

		assertEquals(referee1, referee);
	}

	@Test
	public void shouldDelegateDeleteToDAO() {
		Referee referee = new RefereeBuilder().id(2).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().id(1).referees(referee).toUser();
		referee.setUser(user);
		refereeDAOMock.delete(referee);
		EasyMock.replay(refereeDAOMock);
		refereeService.delete(referee);
		EasyMock.verify(refereeDAOMock);
	}

	@Test
	public void shouldDelegateSaveToDAO() {
		Referee referee = new RefereeBuilder().id(2).toReferee();
		refereeDAOMock.save(referee);
		EasyMock.replay(refereeDAOMock);
		refereeService.save(referee);
		EasyMock.verify(refereeDAOMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSetDeclineAndSendDeclineNotification() throws UnsupportedEncodingException {

		RegisteredUser applicant = new RegisteredUserBuilder().id(3).firstName("fred").lastName("freddy").email("email3@test.com").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").toReferee();
		ApplicationForm form = new ApplicationFormBuilder().id(2342).applicationNumber("xyz").applicant(applicant).program(new ProgramBuilder().title("klala").toProgram()).toApplicationForm();
		referee.setApplication(form);

		refereeDAOMock.save(referee);

		ReferenceEvent event = new ReferenceEventBuilder().id(4).toEvent();
		EasyMock.expect(eventFactoryMock.createEvent(referee)).andReturn(event);
		applicationFormDAOMock.save(form);
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);

		InternetAddress toAddress = new InternetAddress("email3@test.com", "fred freddy");

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("reference.provided.applicant"),// 
				EasyMock.aryEq(new Object[] { "xyz", "klala", "fred", "freddy" }), EasyMock.eq((Locale) null))).andReturn("subject");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), //
				EasyMock.eq("subject"), //
				EasyMock.eq("private/pgStudents/mail/reference_respond_confirmation.ftl"),//
				EasyMock.isA(Map.class), //
				(InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);

		
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, refereeDAOMock, msgSourceMock, eventFactoryMock, applicationFormDAOMock);

		refereeService.declineToActAsRefereeAndNotifiyApplicant(referee);

		assertTrue(referee.isDeclined());
		assertEquals(1, form.getEvents().size());
		assertEquals(event, form.getEvents().get(0));
		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, refereeDAOMock, msgSourceMock,applicationFormDAOMock);

	}

	@Test
	public void shouldNotSendDeclineNotificationIfSaveFails()  {

		RegisteredUser applicant = new RegisteredUserBuilder().id(3).firstName("fred").lastName("freddy").email("email3@test.com").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").toReferee();
		ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).program(new Program()).toApplicationForm();
		referee.setApplication(form);

		refereeDAOMock.save(referee);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaarrrrhh"));

		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, refereeDAOMock);

		try {
			refereeService.declineToActAsRefereeAndNotifiyApplicant(referee);
		} catch (Exception e) {
			// expected..ignore
		}

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, refereeDAOMock);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldTNotFailEmailNotificationFails() throws UnsupportedEncodingException {

		RegisteredUser applicant = new RegisteredUserBuilder().id(3).firstName("fred").lastName("freddy").email("email3@test.com").toUser();
		Referee referee = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").toReferee();
		ApplicationForm form = new ApplicationFormBuilder().id(4).applicationNumber("xyz").applicant(applicant).program(new ProgramBuilder().title("klala").toProgram()).toApplicationForm();
		referee.setApplication(form);

		refereeDAOMock.save(referee);
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);

		InternetAddress toAddress = new InternetAddress("email3@test.com", "fred freddy");
		
		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("reference.provided.applicant"),// 
				EasyMock.aryEq(new Object[] { "xyz", "klala", "fred", "freddy" }), EasyMock.eq((Locale) null))).andReturn("subject");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), //
				EasyMock.eq("subject"), //
				EasyMock.eq("private/pgStudents/mail/reference_respond_confirmation.ftl"), //
				EasyMock.isA(Map.class), //
				(InternetAddress) EasyMock.isNull())).andReturn(preparatorMock);
		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("OH no - email sending's gone wrong!!"));
		EasyMock.replay(mimeMessagePreparatorFactoryMock, javaMailSenderMock, refereeDAOMock, msgSourceMock);

		refereeService.declineToActAsRefereeAndNotifiyApplicant(referee);

		EasyMock.verify(javaMailSenderMock, mimeMessagePreparatorFactoryMock, refereeDAOMock, msgSourceMock);

	}

}
