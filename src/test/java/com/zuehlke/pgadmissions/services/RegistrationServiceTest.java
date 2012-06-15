package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.Environment;

public class RegistrationServiceTest {

	private RegistrationService registrationService;
	private EncryptionUtils encryptionUtilsMock;
	private RoleDAO roleDAOMock;
	private UserDAO userDAOMock;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private ProgramDAO programDAOMock;
	private MessageSource msgSourceMock;

	@Test
	public void shouldCreateNewUserFromDTOAndQueryString() {
		String queryString ="queryString";
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).id(1).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPLICANT)).andReturn(role);
		EasyMock.replay(roleDAOMock);

		RegisteredUser record = new RegisteredUser();
		record.setFirstName("Mark");
		record.setLastName("Euston");
		record.setEmail("meuston@gmail.com");
		record.setPassword("1234");
		record.setConfirmPassword("1234");
		
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234")).andReturn("5678");
		EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("abc");
		EasyMock.replay(encryptionUtilsMock);

		RegisteredUser newUser = registrationService.createNewUser(record, queryString);

		assertEquals("meuston@gmail.com", newUser.getEmail());
		assertEquals("Mark", newUser.getFirstName());
		assertEquals("Euston", newUser.getLastName());
		assertEquals("5678", newUser.getPassword());

		assertTrue(newUser.isAccountNonExpired());
		assertTrue(newUser.isAccountNonLocked());
		assertTrue(newUser.isCredentialsNonExpired());
		assertFalse(newUser.isEnabled());
		assertEquals("abc", newUser.getActivationCode());
		assertEquals(queryString, newUser.getOriginalApplicationQueryString());
	}
	
	@Test
	public void shouldUpdateUser() {
		RegisteredUser record = new RegisteredUser();
		record.setFirstName("Mark");
		record.setLastName("Euston");
		record.setEmail("meuston@gmail.com");
		record.setPassword("1234");
		record.setConfirmPassword("1234");
		EasyMock.expect(userDAOMock.get(2)).andReturn(new RegisteredUser());
		EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("121");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234")).andReturn("1234");
		EasyMock.replay(userDAOMock, encryptionUtilsMock);
		RegisteredUser updateUser = registrationService.updateUser(record, 2);
		Assert.assertEquals("Mark", updateUser.getFirstName());
		Assert.assertEquals("Euston", updateUser.getLastName());
		Assert.assertEquals("meuston@gmail.com", updateUser.getEmail());
		Assert.assertEquals("meuston@gmail.com", updateUser.getUsername());
		Assert.assertEquals("1234", updateUser.getPassword());
		Assert.assertEquals("121", updateUser.getActivationCode());
	}

	

	@Test
	public void shouldSaveNewUserAndSendEmail() throws UnsupportedEncodingException {
		final RegisteredUser expectedRecord = new RegisteredUser();
			final Map<String, Object> modelMap = new HashMap<String, Object>();

		Program program = new ProgramBuilder()
				.id(1)
				.administrators(new RegisteredUserBuilder().id(1).email("email1@test.com").toUser(),
						new RegisteredUserBuilder().id(1).email("email2@test.com").toUser()).toProgram();
		final RegisteredUser newUser = new RegisteredUserBuilder().id(1).email("email@test.com").firstName("bob").lastName("bobson")
				.programOriginallyAppliedTo(program).toUser();
		registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, programDAOMock, mimeMessagePreparatorFactoryMock,
				javaMailSenderMock, msgSourceMock) {

			@Override
			public RegisteredUser createNewUser(RegisteredUser record, String queryString) {
				if (expectedRecord == record && "queryString" == queryString ) {
					return newUser;
				}
				return null;
			}

			@Override
			public Map<String, Object> modelMap() {
				return modelMap;
			}

		};

		userDAOMock.save(newUser);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "bob bobson");
		
		EasyMock.expect(msgSourceMock.getMessage("registration.confirmation", null, null)).andReturn("registration subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(toAddress, "registration subject",
						"private/pgStudents/mail/registration_confirmation.ftl", modelMap, null)).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.replay(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

		registrationService.generateAndSaveNewUser(expectedRecord, null,"queryString");

		EasyMock.verify(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		assertEquals(newUser, modelMap.get("user"));
		assertEquals(Environment.getInstance().getApplicationHostName(), modelMap.get("host"));
		assertEquals("email1@test.com, email2@test.com", modelMap.get("adminsEmails"));
	}

	@Test
	public void shouldNotSendEmailIdSaveFails() {
		final RegisteredUser expectedRecord = new RegisteredUser();
		expectedRecord.setEmail("email@test.com");
		final RegisteredUser newUser = new RegisteredUserBuilder().id(1).toUser();
		registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, programDAOMock, mimeMessagePreparatorFactoryMock,
				javaMailSenderMock, msgSourceMock) {

			@Override
			public RegisteredUser createNewUser(RegisteredUser record, String queryString) {
				if (expectedRecord == record && "queryString" == queryString ) {
					return newUser;
				}
				return null;
			}

		};

		userDAOMock.save(newUser);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaaaaaargh"));

		EasyMock.replay(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		try {
			registrationService.generateAndSaveNewUser(expectedRecord, null, "queryString");
		} catch (RuntimeException e) {
			// expected...ignore
		}

		EasyMock.verify(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldNotThrowExceptionIfEmailSendingFails() throws UnsupportedEncodingException {
		final RegisteredUser expectedRecord = new RegisteredUser();

		final RegisteredUser newUser = new RegisteredUserBuilder().id(1).email("email@test.com").firstName("bob").lastName("bobson").toUser();

		registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, programDAOMock, mimeMessagePreparatorFactoryMock,
				javaMailSenderMock, msgSourceMock) {

			@Override
			public RegisteredUser createNewUser(RegisteredUser record, String queryString) {
				if (expectedRecord == record && "queryString" == queryString ) {
					return newUser;
				}
				return null;
			}

		};

		userDAOMock.save(newUser);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "bob bobson");
		EasyMock.expect(msgSourceMock.getMessage("registration.confirmation", null, null)).andReturn("reg subject");
		
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("reg subject"),
						EasyMock.eq("private/pgStudents/mail/registration_confirmation.ftl"), EasyMock.isA(Map.class), (InternetAddress)EasyMock.isNull())).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("AARrrgggg"));
		EasyMock.replay(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);
		registrationService.generateAndSaveNewUser(expectedRecord, null,"queryString");

		EasyMock.verify(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock);

	}

	@Test
	public void shouldDelegateFindUserToDAO() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userDAOMock.getUserByActivationCode("code")).andReturn(user);
		EasyMock.replay(userDAOMock);
		RegisteredUser foundUser = registrationService.findUserForActivationCode("code");
		assertEquals(user, foundUser);

	}

	@Before
	public void setup() {
		userDAOMock = EasyMock.createMock(UserDAO.class);
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		programDAOMock = EasyMock.createMock(ProgramDAO.class);
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		
		registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, programDAOMock, mimeMessagePreparatorFactoryMock,
				javaMailSenderMock, msgSourceMock);
	}
}
