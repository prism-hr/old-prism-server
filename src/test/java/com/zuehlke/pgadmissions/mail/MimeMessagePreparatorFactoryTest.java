package com.zuehlke.pgadmissions.mail;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.sun.mail.handlers.message_rfc822;
import com.zuehlke.pgadmissions.utils.Environment;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class MimeMessagePreparatorFactoryTest {

	private MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private FreeMarkerConfig freeMarkerConfigMock;
	Configuration configMock;

	Map<String, Object> model;
	private String subject;
	private String template;
	private InternetAddress[] tos;
	private InternetAddress replyToAddress;

	@Before
	public void setUp() throws AddressException, UnsupportedEncodingException {
		freeMarkerConfigMock = EasyMock.createMock(FreeMarkerConfig.class);
		configMock = EasyMock.createMock(Configuration.class);
		EasyMock.expect(freeMarkerConfigMock.getConfiguration()).andReturn(configMock);

		tos = new InternetAddress[] { new InternetAddress("email@bla.com"), new InternetAddress("dummy@test.com") };
		replyToAddress = new InternetAddress("replytome@test.com", "Jane Hurrah");
		model = new HashMap<String, Object>();
		model.put("test", "testValue");
		subject = "subject";
		template = "template";
	}


	
	@Test
	public void shouldPopulatedMimeMessageForSingleRecipientInProduction() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());

		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true);
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0], subject, template, model, replyToAddress);

		MimeMessage testMessage = new TestMessage();

		EasyMock.replay(freeMarkerConfigMock, configMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock);
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		Assert.assertEquals(1, recipients.length);
		Assert.assertEquals("email@bla.com", ((InternetAddress) recipients[0]).getAddress());
		

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		Assert.assertNull(ccRecipients);

		Assert.assertEquals("subject", testMessage.getSubject());
		Assert.assertEquals("ladida", testMessage.getContent());
		Assert.assertEquals("text/html", testMessage.getDataHandler().getContentType());
		Assert.assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		Assert.assertEquals(1,testMessage.getReplyTo().length);
		Assert.assertEquals("replytome@test.com", ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		Assert.assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
	}

	@Test
	public void shouldPopulatedMimeMessageForSingleRecipientWitCCsInProduction() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());

		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true);
		InternetAddress cc1 = new InternetAddress("cc1@bla.com");
		InternetAddress cc2 = new InternetAddress("cc2@bla.com");
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0],  new InternetAddress[] { cc1, cc2 }, subject, template, model, replyToAddress);

		MimeMessage testMessage = new TestMessage();

		EasyMock.replay(freeMarkerConfigMock, configMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock);
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		Assert.assertEquals(1, recipients.length);
		Assert.assertEquals("email@bla.com", ((InternetAddress) recipients[0]).getAddress());
		

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		Assert.assertEquals(2, ccRecipients.length);
		Assert.assertEquals("cc1@bla.com", ((InternetAddress) ccRecipients[0]).getAddress());
		Assert.assertEquals("cc2@bla.com", ((InternetAddress) ccRecipients[1]).getAddress());

		Assert.assertEquals("subject", testMessage.getSubject());
		Assert.assertEquals("ladida", testMessage.getContent());
		Assert.assertEquals("text/html", testMessage.getDataHandler().getContentType());
		Assert.assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		Assert.assertEquals(1,testMessage.getReplyTo().length);
		Assert.assertEquals("replytome@test.com", ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		Assert.assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
	}

	@Test
	public void shouldPopulatedMimeMessageInProduction() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());

		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true);
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos, subject, template, model, replyToAddress);

		MimeMessage testMessage = new TestMessage();

		EasyMock.replay(freeMarkerConfigMock, configMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock);
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		Assert.assertEquals(2, recipients.length);
		Assert.assertEquals("email@bla.com", ((InternetAddress) recipients[0]).getAddress());
		Assert.assertEquals("dummy@test.com", ((InternetAddress) recipients[1]).getAddress());

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		Assert.assertNull(ccRecipients);

		Assert.assertEquals("subject", testMessage.getSubject());
		Assert.assertEquals("ladida", testMessage.getContent());
		Assert.assertEquals("text/html", testMessage.getDataHandler().getContentType());
		Assert.assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		Assert.assertEquals(1,testMessage.getReplyTo().length);
		Assert.assertEquals("replytome@test.com", ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		Assert.assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
	}
	
	@Test
	public void shouldPopulatedMimeMessageInProductionWithCCs() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());

		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true);
		InternetAddress cc1 = new InternetAddress("cc1@bla.com");
		InternetAddress cc2 = new InternetAddress("cc2@bla.com");
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos, new InternetAddress[] { cc1, cc2 }, subject, template, model, replyToAddress);

		MimeMessage testMessage = new TestMessage();

		EasyMock.replay(freeMarkerConfigMock, configMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock);
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		Assert.assertEquals(2, recipients.length);
		Assert.assertEquals("email@bla.com", ((InternetAddress) recipients[0]).getAddress());
		Assert.assertEquals("dummy@test.com", ((InternetAddress) recipients[1]).getAddress());

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		Assert.assertEquals(2, ccRecipients.length);
		Assert.assertEquals("cc1@bla.com", ((InternetAddress) ccRecipients[0]).getAddress());
		Assert.assertEquals("cc2@bla.com", ((InternetAddress) ccRecipients[1]).getAddress());

		Assert.assertEquals("subject", testMessage.getSubject());
		Assert.assertEquals("ladida", testMessage.getContent());
		Assert.assertEquals("text/html", testMessage.getDataHandler().getContentType());
		Assert.assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		Assert.assertEquals(1,testMessage.getReplyTo().length);
		Assert.assertEquals("replytome@test.com", ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		Assert.assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
	}

	@Test
	public void shouldPopulatedMimeMessageInDev() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());

		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, false);
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos, subject, template, model, replyToAddress);

		MimeMessage testMessage = new TestMessage();

		EasyMock.replay(freeMarkerConfigMock, configMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock);
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		Assert.assertEquals(2, recipients.length);
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[0]).getAddress());
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[1]).getAddress());

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		Assert.assertNull(ccRecipients);

		Assert.assertEquals("subject", testMessage.getSubject());
		Assert.assertEquals("ladida", testMessage.getContent());
		Assert.assertEquals("text/html", testMessage.getDataHandler().getContentType());
		Assert.assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		Assert.assertEquals(1,testMessage.getReplyTo().length);
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		Assert.assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
	}

	
	@Test
	public void shouldPopulatedMimeMessageForSingleRecipientInDev() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());

		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, false);
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0], subject, template, model, replyToAddress);

		MimeMessage testMessage = new TestMessage();

		EasyMock.replay(freeMarkerConfigMock, configMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock);
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		Assert.assertEquals(1, recipients.length);
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[0]).getAddress());
		


		Assert.assertEquals("subject", testMessage.getSubject());
		Assert.assertEquals("ladida", testMessage.getContent());
		Assert.assertEquals("text/html", testMessage.getDataHandler().getContentType());
		Assert.assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		Assert.assertEquals(1,testMessage.getReplyTo().length);
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		Assert.assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
	}
	
	@Test
	public void shouldPopulatedMimeMessageForSingleRecipientWithCCsInDev() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());

		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, false);
		InternetAddress cc1 = new InternetAddress("cc1@bla.com");
		InternetAddress cc2 = new InternetAddress("cc2@bla.com");
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0], new InternetAddress[] { cc1, cc2 }, subject, template, model, replyToAddress);

		MimeMessage testMessage = new TestMessage();

		EasyMock.replay(freeMarkerConfigMock, configMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock);
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		Assert.assertEquals(1, recipients.length);
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[0]).getAddress());
		

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		Assert.assertNull(ccRecipients);

		Assert.assertEquals("subject <NON-PROD-Message: CC to: [cc1@bla.com, cc2@bla.com]>", testMessage.getSubject());
		Assert.assertEquals("ladida", testMessage.getContent());
		Assert.assertEquals("text/html", testMessage.getDataHandler().getContentType());
		Assert.assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		Assert.assertEquals(1,testMessage.getReplyTo().length);
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		Assert.assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
	}
	@Test
	public void shouldPopulatedMimeMessageInDevWithCCs() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());

		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, false);
		InternetAddress cc1 = new InternetAddress("cc1@bla.com");
		InternetAddress cc2 = new InternetAddress("cc2@bla.com");
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos, new InternetAddress[] { cc1, cc2 }, subject, template, model, replyToAddress);

		MimeMessage testMessage = new TestMessage();

		EasyMock.replay(freeMarkerConfigMock, configMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock);
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		Assert.assertEquals(2, recipients.length);
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[0]).getAddress());
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[1]).getAddress());

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		Assert.assertNull(ccRecipients);

		Assert.assertEquals("subject <NON-PROD-Message: CC to: [cc1@bla.com, cc2@bla.com]>", testMessage.getSubject());
		Assert.assertEquals("ladida", testMessage.getContent());
		Assert.assertEquals("text/html", testMessage.getDataHandler().getContentType());
		Assert.assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		Assert.assertEquals(1,testMessage.getReplyTo().length);
		Assert.assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		Assert.assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
	}

	class TestTemplate extends Template {

		public TestTemplate() throws Exception {
			super(null, new StringReader(""), null);
		}

		@Override
		public void process(Object rootMap, Writer out) {
			Assert.assertEquals(model, rootMap);
			Assert.assertTrue(rootMap instanceof Map);
			@SuppressWarnings("unchecked")
			Object value = ((Map<String, Object>) rootMap).get("test");
			Assert.assertEquals("testValue", value);
			try {
				out.write("ladida");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class TestMessage extends MimeMessage {
		public TestMessage() {
			super((Session) null);
		}
	}
}
