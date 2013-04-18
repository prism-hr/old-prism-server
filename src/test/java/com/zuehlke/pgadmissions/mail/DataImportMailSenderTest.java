package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.IMPORT_ERROR;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;

public class DataImportMailSenderTest {
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private DataImporterMailSender dataImporterMailSender;
	private MessageSource msgSourceMock;
	private UserService userServiceMock;
	private SessionFactory sessionFactoryMock;
	private EmailTemplateService templateServiceMock;

	@Test
	public void shouldReturnCorrectlyPopulatedModel() {

		RegisteredUser user = new RegisteredUserBuilder().email("alice@test.com").id(9).build();
		String message = "Error message";

		Map<String, Object> model = dataImporterMailSender.createModel(user, message);
		assertEquals(user, model.get("user"));
		assertEquals(message, model.get("message"));
		assertEquals(Environment.getInstance().getApplicationHostName(), model.get("host"));
		Assert.assertNotNull(model.get("time"));
	}

	@Test
	public void sendErrorMessageTest() throws Exception {
		RegisteredUser user1 = new RegisteredUserBuilder().id(2).email("admin@test.com").firstName("bob").lastName("the builder").build();
		RegisteredUser user2 = new RegisteredUserBuilder().id(1).email("bob@test.com").firstName("bob").lastName("the builder").build();
		List<RegisteredUser> users = Arrays.asList(user1, user2);
		

		InternetAddress expAddr1 = new InternetAddress("admin@test.com", "bob the builder");
		InternetAddress expAddr2 = new InternetAddress("bob@test.com", "bob the builder");

		EmailTemplate template = new EmailTemplateBuilder().active(true)
				.content("Import error template").name(IMPORT_ERROR).build();
		expect(templateServiceMock.getActiveEmailTemplate(IMPORT_ERROR)).andReturn(template);

		final Map<String, Object> model = new HashMap<String, Object>();
		dataImporterMailSender = new DataImporterMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, userServiceMock, sessionFactoryMock, templateServiceMock) {
			@Override
			Map<String, Object> createModel(RegisteredUser user, String message) {
				return model;
			}
		};
		
		Session sessionMock = EasyMock.createMock(Session.class);
		Transaction transactionMock = EasyMock.createMock(Transaction.class);
		
		EasyMock.expect(sessionFactoryMock.getCurrentSession()).andReturn(sessionMock);
		EasyMock.expect(sessionMock.beginTransaction()).andReturn(transactionMock);
		
		EasyMock.expect(userServiceMock.getUsersInRole(Authority.SUPERADMINISTRATOR)).andReturn(users);
		
		transactionMock.commit();

		EasyMock.expect(msgSourceMock.getMessage(EasyMock.eq("reference.data.import.error"), EasyMock.aryEq(new Object[] {}), EasyMock.eq((Locale) null))).andReturn("subject").anyTimes();

		MimeMessagePreparator mimePrepMock = EasyMock.createMock(MimeMessagePreparator.class);
		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr1, "subject", IMPORT_ERROR, template.getContent(), model, null)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);

		EasyMock.expect(mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(expAddr2, "subject", IMPORT_ERROR, template.getContent(), model, null)).andReturn(mimePrepMock);
		javaMailSenderMock.send(mimePrepMock);
		
		EasyMock.expectLastCall();
		EasyMock.replay(mimePrepMock, javaMailSenderMock, templateServiceMock, mimeMessagePreparatorFactoryMock, msgSourceMock, userServiceMock, sessionFactoryMock, sessionMock, transactionMock);

		dataImporterMailSender.sendErrorMessage("message");

		EasyMock.verify(mimePrepMock, javaMailSenderMock, templateServiceMock, mimeMessagePreparatorFactoryMock, msgSourceMock, userServiceMock, sessionFactoryMock, sessionMock, transactionMock);
	}

	@Before
	public void setUp() {
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		sessionFactoryMock = EasyMock.createMock(SessionFactory.class);
		templateServiceMock = createMock(EmailTemplateService.class);
		dataImporterMailSender = new DataImporterMailSender(mimeMessagePreparatorFactoryMock, javaMailSenderMock, msgSourceMock, userServiceMock, sessionFactoryMock, templateServiceMock);
	}
}
