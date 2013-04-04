package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_PASSWORD_CONFIRMATION;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

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

import com.zuehlke.pgadmissions.dao.ApplicationsFilterDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.UserFactory;

public class UserServiceResetPasswordTest {

	private UserService serviceUT;

	private UserDAO userDAOMock;
	private ApplicationsFilterDAO applicationsFilterDAOmock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private JavaMailSender mailsenderMock;
	private MessageSource msgSourceMock;
	private EncryptionUtils encryptionUtilsMock;
	private EmailTemplateService templateServiceMock;

	@Before
	public void setUp() {
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		mailsenderMock = EasyMock.createMock(JavaMailSender.class);

		userDAOMock = EasyMock.createMock(UserDAO.class);
		RoleDAO roleDAOMock = EasyMock.createMock(RoleDAO.class);
		applicationsFilterDAOmock = EasyMock.createMock(ApplicationsFilterDAO.class);
		UserFactory userFactoryMock = EasyMock.createMock(UserFactory.class);
		msgSourceMock = createMock(MessageSource.class);
		templateServiceMock = createMock(EmailTemplateService.class);

		serviceUT = new UserService(userDAOMock, roleDAOMock, userFactoryMock, mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock, applicationsFilterDAOmock, templateServiceMock);
	}

	private void replayAllMocks() {
		EasyMock.replay(userDAOMock, mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock, templateServiceMock);
	}

	private void verifyAllMocks() {
		EasyMock.verify(userDAOMock, mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock, templateServiceMock);
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

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(msgSourceMock.getMessage("user.password.reset", null, null)).andReturn("subject");
		InternetAddress toAddress = new InternetAddress("first@last.com");

		Map<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("user", storedUser);
		expectedMap.put("newPassword", newPassword);
		expectedMap.put("host", Environment.getInstance().getApplicationHostName());
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("New password confirmation template").name(NEW_PASSWORD_CONFIRMATION).build();
		expect(templateServiceMock.getActiveEmailTemplate(NEW_PASSWORD_CONFIRMATION)).andReturn(template);

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(//
				EasyMock.eq(toAddress), //
				eq("subject"), //
				eq(NEW_PASSWORD_CONFIRMATION),
				EasyMock.eq(template.getContent()),//
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
				.username("firstlast").email("first@last.com").password(oldPassword).build();
		EasyMock.expect(userDAOMock.getUserByEmail("aaaa")).andReturn(storedUser);

		String newPassword = "this is better";
		EasyMock.expect(encryptionUtilsMock.generateUserPassword()).andReturn(newPassword);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(msgSourceMock.getMessage("user.password.reset", null, null)).andReturn("subject");
		InternetAddress toAddress = new InternetAddress("first@last.com");

		Map<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("user", storedUser);
		expectedMap.put("newPassword", newPassword);
		expectedMap.put("host", Environment.getInstance().getApplicationHostName());
		
		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("New password confirmation template").name(NEW_PASSWORD_CONFIRMATION).build();
		expect(templateServiceMock.getActiveEmailTemplate(NEW_PASSWORD_CONFIRMATION)).andReturn(template);

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(//
				EasyMock.eq(toAddress), //
				EasyMock.eq("subject"), //
				eq(NEW_PASSWORD_CONFIRMATION),
				EasyMock.eq(template.getContent()),//
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