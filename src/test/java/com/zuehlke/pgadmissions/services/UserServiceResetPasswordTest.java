package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;

import javax.mail.internet.AddressException;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationsFilterDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class UserServiceResetPasswordTest {

	private UserService serviceUT;

	private UserDAO userDAOMock;
	private ApplicationsFilterDAO applicationsFilterDAOmock;
	private EncryptionUtils encryptionUtilsMock;
	private MailSendingService mailServiceMock;

	@Before
	public void setUp() {
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);

		userDAOMock = EasyMock.createMock(UserDAO.class);
		RoleDAO roleDAOMock = EasyMock.createMock(RoleDAO.class);
		applicationsFilterDAOmock = EasyMock.createMock(ApplicationsFilterDAO.class);
		UserFactory userFactoryMock = createMock(UserFactory.class);
		mailServiceMock = createMock(MailSendingService.class);
		
		serviceUT = new UserService(userDAOMock, roleDAOMock, userFactoryMock, encryptionUtilsMock, applicationsFilterDAOmock, mailServiceMock);
	}

	private void replayAllMocks() {
		EasyMock.replay(userDAOMock, encryptionUtilsMock, mailServiceMock);
	}

	private void verifyAllMocks() {
		EasyMock.verify(userDAOMock, encryptionUtilsMock, mailServiceMock);
	}

	@Test
	public void ignoreInvalidEmails() {
		EasyMock.expect(userDAOMock.getUserByEmail("aaaa")).andReturn(null);
		replayAllMocks();

		serviceUT.resetPassword("aaaa");

		verifyAllMocks();
	}

	@Test
	public void generateNewPasswordAndSendMail() throws AddressException {
		String oldPassword = "i forget this every time";
		RegisteredUser storedUser = new RegisteredUserBuilder().id(23).firstName("first").lastName("last").email("first@last.com").password(oldPassword).build();
		EasyMock.expect(userDAOMock.getUserByEmail("aaaa")).andReturn(storedUser);

		String newPassword = "this is better";
		String hashedNewPassword = "some ol' celtic bollocks";
		EasyMock.expect(encryptionUtilsMock.generateUserPassword()).andReturn(newPassword);

		mailServiceMock.sendResetPasswordMessage(storedUser, newPassword);
		

		EasyMock.expect(encryptionUtilsMock.getMD5Hash(newPassword)).andReturn(hashedNewPassword);
		userDAOMock.save(storedUser);
		EasyMock.expectLastCall();
		replayAllMocks();

		serviceUT.resetPassword("aaaa");

		verifyAllMocks();
		Assert.assertEquals(hashedNewPassword, storedUser.getPassword());
	}

	@Test
	public void keepOldPasswordIfMailSendFails() throws AddressException {
		String oldPassword = "i forget this every time";
		RegisteredUser storedUser = new RegisteredUserBuilder().id(23).firstName("first").lastName("last")//
				.username("firstlast").email("first@last.com").password(oldPassword).build();
		EasyMock.expect(userDAOMock.getUserByEmail("aaaa")).andReturn(storedUser);

		String newPassword = "this is better";
		EasyMock.expect(encryptionUtilsMock.generateUserPassword()).andReturn(newPassword);

		mailServiceMock.sendResetPasswordMessage(storedUser, newPassword);
		EasyMock.expectLastCall().andThrow(new RuntimeException("intentional exception"));
		replayAllMocks();

		serviceUT.resetPassword("aaaa");

		verifyAllMocks();
		Assert.assertEquals(oldPassword, storedUser.getPassword());
	}
}