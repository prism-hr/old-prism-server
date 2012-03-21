package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.RegistrationDTO;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

public class RegistrationServiceTest {

	private RegistrationService registrationService;
	private EncryptionUtils encryptionUtilsMock;
	private RoleDAO roleDAOMock;
	private UserDAO userDAOMock;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private ProjectDAO projectDAOMock;

	@Test
	public void shouldCreateNewUserFromDTO() throws NoSuchAlgorithmException {
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).id(1).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPLICANT)).andReturn(role);
		EasyMock.replay(roleDAOMock);

		Project project = new ProjectBuilder().id(7).toProject();
		EasyMock.expect(projectDAOMock.getProjectById(7)).andReturn(project);
		EasyMock.replay(projectDAOMock);

		RegistrationDTO recordDTO = new RegistrationDTO();
		recordDTO.setFirstname("Mark");
		recordDTO.setLastname("Euston");
		recordDTO.setEmail("meuston@gmail.com");
		recordDTO.setPassword("1234");
		recordDTO.setConfirmPassword("1234");
		recordDTO.setProjectId(7);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234")).andReturn("5678");
		EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("abc");
		EasyMock.replay(encryptionUtilsMock);

		RegisteredUser newUser = registrationService.createNewUser(recordDTO);

		assertEquals("meuston@gmail.com", newUser.getEmail());
		assertEquals("Mark", newUser.getFirstName());
		assertEquals("Euston", newUser.getLastName());
		assertEquals("5678", newUser.getPassword());
		assertEquals(project, newUser.getProjectOriginallyAppliedTo());
		assertTrue(newUser.isAccountNonExpired());
		assertTrue(newUser.isAccountNonLocked());
		assertTrue(newUser.isCredentialsNonExpired());
		assertFalse(newUser.isEnabled());
		assertEquals("abc", newUser.getActivationCode());
	}

	@Test
	public void shouldCreateNewUserWithProjectNullIfNoIdGiven() throws NoSuchAlgorithmException {
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).id(1).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPLICANT)).andReturn(role);
		EasyMock.replay(roleDAOMock);

		RegistrationDTO recordDTO = new RegistrationDTO();
		recordDTO.setFirstname("Mark");
		recordDTO.setLastname("Euston");
		recordDTO.setEmail("meuston@gmail.com");
		recordDTO.setPassword("1234");
		recordDTO.setConfirmPassword("1234");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234")).andReturn("5678");
		EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("abc");
		EasyMock.replay(encryptionUtilsMock, projectDAOMock);

		RegisteredUser newUser = registrationService.createNewUser(recordDTO);
		EasyMock.verify(projectDAOMock);
		assertNull(newUser.getProjectOriginallyAppliedTo());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveNewUserAndSendEmail() throws UnsupportedEncodingException {
		final RegistrationDTO recordDTO = new RegistrationDTO();

		final RegisteredUser newUser = new RegisteredUserBuilder().id(1).email("email@test.com").firstName("bob").lastName("bobson").toUser();
		registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, projectDAOMock, mimeMessagePreparatorFactoryMock,
				javaMailSenderMock) {

			@Override
			public RegisteredUser createNewUser(RegistrationDTO record) {
				if (record == recordDTO) {
					return newUser;
				}
				return null;
			}

		};

		userDAOMock.save(newUser);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "bob bobson");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Registration confirmation"),
						EasyMock.eq("private/pgStudents/mail/registration_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.replay(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);

		registrationService.generateAndSaveNewUser(recordDTO);

		EasyMock.verify(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);

	}

	@Test
	public void shouldNotSendEmailIdSaveFails() {
		final RegistrationDTO recordDTO = new RegistrationDTO();
		recordDTO.setEmail("email@test.com");
		final RegisteredUser newUser = new RegisteredUserBuilder().id(1).toUser();
		registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, projectDAOMock, mimeMessagePreparatorFactoryMock,
				javaMailSenderMock) {

			@Override
			public RegisteredUser createNewUser(RegistrationDTO record) {
				if (record == recordDTO) {
					return newUser;
				}
				return null;
			}

		};

		userDAOMock.save(newUser);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaaaaaargh"));

		EasyMock.replay(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		try {
			registrationService.generateAndSaveNewUser(recordDTO);
		} catch (RuntimeException e) {
			// expected...ignore
		}

		EasyMock.verify(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);

	}

	@Test
	public void shouldNotThrowExceptionIfEmailSendingFails() throws UnsupportedEncodingException {
		final RegistrationDTO recordDTO = new RegistrationDTO();

		final RegisteredUser newUser = new RegisteredUserBuilder().id(1).email("email@test.com").firstName("bob").lastName("bobson").toUser();

		registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, projectDAOMock, mimeMessagePreparatorFactoryMock,
				javaMailSenderMock) {

			@Override
			public RegisteredUser createNewUser(RegistrationDTO record) {
				if (record == recordDTO) {
					return newUser;
				}
				return null;
			}

		};

		userDAOMock.save(newUser);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "bob bobson");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Registration confirmation"),
						EasyMock.eq("private/pgStudents/mail/registration_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("AARrrgggg"));
		EasyMock.replay(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		registrationService.generateAndSaveNewUser(recordDTO);

		EasyMock.verify(userDAOMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);

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
		projectDAOMock = EasyMock.createMock(ProjectDAO.class);
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, projectDAOMock, mimeMessagePreparatorFactoryMock,
				javaMailSenderMock);
	}
}
