package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.UserFactory;

public class UserServiceResetPasswordTest {

	private UserService serviceUT;

	private UserDAO userDAOMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private JavaMailSender mailsenderMock;
	private MessageSource msgSourceMock;
	private EncryptionUtils encryptionUtilsMock;

	@Before
	public void setUp() {
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		mailsenderMock = EasyMock.createMock(JavaMailSender.class);

		userDAOMock = EasyMock.createMock(UserDAO.class);
		RoleDAO roleDAOMock = EasyMock.createMock(RoleDAO.class);
		UserFactory userFactoryMock = EasyMock.createMock(UserFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);

		serviceUT = new UserService(userDAOMock, roleDAOMock, userFactoryMock, mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock);
	}

	private void replayAllMocks() {
		EasyMock.replay(userDAOMock, mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock);
	}

	private void verifyAllMocks() {
		EasyMock.verify(userDAOMock, mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock);
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
		RegisteredUser storedUser = new RegisteredUserBuilder().id(23).firstName("first").lastName("last").email("first@last.com").password(oldPassword).toUser();
		EasyMock.expect(userDAOMock.getUserByEmail("aaaa")).andReturn(storedUser);

		String newPassword = "this is better";
		String hashedNewPassword = "some ol' celtic bollocks";
		EasyMock.expect(encryptionUtilsMock.generateUserPassword()).andReturn(newPassword);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(msgSourceMock.getMessage("user.password.reset", null, null)).andReturn("subject");
		InternetAddress toAddress = new InternetAddress("first@last.com");

		Map<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("user", storedUser);
		expectedMap.put("newPassword", newPassword);
		expectedMap.put("host", "http://localhost:8080");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(//
				EasyMock.eq(toAddress), //
				EasyMock.eq("subject"), //
				EasyMock.eq("private/pgStudents/mail/new_password_confirmation.ftl"),//
				EasyMock.eq(expectedMap), //
				EasyMock.isNull(InternetAddress.class))).andReturn(preparatorMock);
		mailsenderMock.send(preparatorMock);

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
				.username("firstlast").email("first@last.com").password(oldPassword).toUser();
		EasyMock.expect(userDAOMock.getUserByEmail("aaaa")).andReturn(storedUser);

		String newPassword = "this is better";
		EasyMock.expect(encryptionUtilsMock.generateUserPassword()).andReturn(newPassword);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(msgSourceMock.getMessage("user.password.reset", null, null)).andReturn("subject");
		InternetAddress toAddress = new InternetAddress("first@last.com");

		Map<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("user", storedUser);
		expectedMap.put("newPassword", newPassword);
		expectedMap.put("host", "http://localhost:8080");

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(//
				EasyMock.eq(toAddress), //
				EasyMock.eq("subject"), //
				EasyMock.eq("private/pgStudents/mail/new_password_confirmation.ftl"),//
				EasyMock.eq(expectedMap), //
				EasyMock.isNull(InternetAddress.class))).andReturn(preparatorMock);
		mailsenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("intentional exception"));
		replayAllMocks();

		serviceUT.resetPassword("aaaa");

		verifyAllMocks();
		Assert.assertEquals(oldPassword, storedUser.getPassword());
	}
}