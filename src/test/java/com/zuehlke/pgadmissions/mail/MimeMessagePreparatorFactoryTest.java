package com.zuehlke.pgadmissions.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import com.zuehlke.pgadmissions.test.utils.MultiPartMimeMessageParser;
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
		
		List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(testMessage);

		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		assertEquals(1, recipients.length);
		assertEquals("email@bla.com", ((InternetAddress) recipients[0]).getAddress());
		
		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		assertNull(ccRecipients);

		assertEquals("subject", testMessage.getSubject());
		assertEquals("ladida", parsedMessage.get(1));
		assertTrue(StringUtils.contains(testMessage.getDataHandler().getContentType(), "multipart/mixed;"));
		assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		assertEquals(1,testMessage.getReplyTo().length);
		assertEquals("replytome@test.com", ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
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

		List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(testMessage);

		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		assertEquals(1, recipients.length);
		assertEquals("email@bla.com", ((InternetAddress) recipients[0]).getAddress());
		
		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		assertEquals(2, ccRecipients.length);
		assertEquals("cc1@bla.com", ((InternetAddress) ccRecipients[0]).getAddress());
		assertEquals("cc2@bla.com", ((InternetAddress) ccRecipients[1]).getAddress());

		assertEquals("subject", testMessage.getSubject());
		assertEquals("ladida", parsedMessage.get(1));
        assertTrue(StringUtils.contains(testMessage.getDataHandler().getContentType(), "multipart/mixed;"));
		assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		assertEquals(1,testMessage.getReplyTo().length);
		assertEquals("replytome@test.com", ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
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

		List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(testMessage);
		
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		assertEquals(2, recipients.length);
		assertEquals("email@bla.com", ((InternetAddress) recipients[0]).getAddress());
		assertEquals("dummy@test.com", ((InternetAddress) recipients[1]).getAddress());

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		assertNull(ccRecipients);

		assertEquals("subject", testMessage.getSubject());
		assertEquals("ladida", parsedMessage.get(1));
        assertTrue(StringUtils.contains(testMessage.getDataHandler().getContentType(), "multipart/mixed;"));
		assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		assertEquals(1,testMessage.getReplyTo().length);
		assertEquals("replytome@test.com", ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
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

		List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(testMessage);
		
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		assertEquals(2, recipients.length);
		assertEquals("email@bla.com", ((InternetAddress) recipients[0]).getAddress());
		assertEquals("dummy@test.com", ((InternetAddress) recipients[1]).getAddress());

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		assertEquals(2, ccRecipients.length);
		assertEquals("cc1@bla.com", ((InternetAddress) ccRecipients[0]).getAddress());
		assertEquals("cc2@bla.com", ((InternetAddress) ccRecipients[1]).getAddress());

		assertEquals("subject", testMessage.getSubject());
		assertEquals("ladida", parsedMessage.get(1));
        assertTrue(StringUtils.contains(testMessage.getDataHandler().getContentType(), "multipart/mixed;"));
		assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		assertEquals(1,testMessage.getReplyTo().length);
		assertEquals("replytome@test.com", ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
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
		
		List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(testMessage);

		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		assertEquals(2, recipients.length);
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[0]).getAddress());
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[1]).getAddress());

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		assertNull(ccRecipients);

		assertEquals("subject", testMessage.getSubject());
		assertEquals("ladida", parsedMessage.get(1));
        assertTrue(StringUtils.contains(testMessage.getDataHandler().getContentType(), "multipart/mixed;"));
		assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		assertEquals(1,testMessage.getReplyTo().length);
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
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
		
		List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(testMessage);

		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		assertEquals(1, recipients.length);
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[0]).getAddress());
		
		assertEquals("subject", testMessage.getSubject());
		assertEquals("ladida", parsedMessage.get(1));
        assertTrue(StringUtils.contains(testMessage.getDataHandler().getContentType(), "multipart/mixed;"));
		assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		assertEquals(1,testMessage.getReplyTo().length);
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
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

		List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(testMessage);
		
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		assertEquals(1, recipients.length);
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[0]).getAddress());

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		assertNull(ccRecipients);

		assertEquals("subject <NON-PROD-Message: CC to: [cc1@bla.com, cc2@bla.com]>", testMessage.getSubject());
		assertEquals("ladida", parsedMessage.get(1));
        assertTrue(StringUtils.contains(testMessage.getDataHandler().getContentType(), "multipart/mixed;"));
		assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		assertEquals(1,testMessage.getReplyTo().length);
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
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

		List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(testMessage);
		
		Address[] recipients = testMessage.getRecipients(RecipientType.TO);
		assertEquals(2, recipients.length);
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[0]).getAddress());
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress) recipients[1]).getAddress());

		Address[] ccRecipients = testMessage.getRecipients(RecipientType.CC);
		assertNull(ccRecipients);

		assertEquals("subject <NON-PROD-Message: CC to: [cc1@bla.com, cc2@bla.com]>", testMessage.getSubject());
		
		assertEquals("ladida", parsedMessage.get(1));
        assertTrue(StringUtils.contains(testMessage.getDataHandler().getContentType(), "multipart/mixed;"));
		assertFalse(ArrayUtils.isEmpty(testMessage.getFrom()));
		
		assertEquals(1,testMessage.getReplyTo().length);
		assertEquals(Environment.getInstance().getEmailToAddress(), ((InternetAddress)testMessage.getReplyTo()[0]).getAddress());
		assertEquals("Jane Hurrah", ((InternetAddress)testMessage.getReplyTo()[0]).getPersonal());
	}

	@Test
	public void shouldCreateMulipartMessageIfAttacmentsProvided() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());

		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true);
		PdfAttachmentInputSource attachmentOne = EasyMock.createMock(PdfAttachmentInputSource.class);
		EasyMock.expect(attachmentOne.getAttachmentFilename()).andReturn("fileOne");
		PdfAttachmentInputSource attachmentTwo = EasyMock.createMock(PdfAttachmentInputSource.class);
		EasyMock.expect(attachmentTwo.getAttachmentFilename()).andReturn("fileTwo");
		EasyMock.replay(attachmentOne, attachmentTwo);
		
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0], subject, template, model, replyToAddress, attachmentOne, attachmentTwo);
		MimeMessage testMessage = new TestMessage();

		EasyMock.replay(freeMarkerConfigMock, configMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock);
		
		assertTrue(testMessage.getContent() instanceof MimeMultipart);
	}
	
	@Test
	public void shouldAddAttachemtns() throws Exception {
		EasyMock.expect(configMock.getTemplate(template)).andReturn(new TestTemplate());
		final MimeMessageHelper mimeMessageHelperMock = EasyMock.createNiceMock(MimeMessageHelper.class);
		mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true){
			@Override
			MimeMessageHelper getMessageHelper(MimeMessage mimeMessage, boolean isMultipart) throws MessagingException {
				return mimeMessageHelperMock;
			}
		};

		PdfAttachmentInputSource attachmentOne = EasyMock.createMock(PdfAttachmentInputSource.class);
		EasyMock.expect(attachmentOne.getAttachmentFilename()).andReturn("fileOne");
		PdfAttachmentInputSource attachmentTwo = EasyMock.createMock(PdfAttachmentInputSource.class);
		EasyMock.expect(attachmentTwo.getAttachmentFilename()).andReturn("fileTwo");
		EasyMock.replay(attachmentOne, attachmentTwo);
		
		MimeMessagePreparator prep = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0], subject, template, model, replyToAddress, attachmentOne, attachmentTwo);
		MimeMessage testMessage = new TestMessage();
		
		mimeMessageHelperMock.addAttachment("fileOne", attachmentOne, "application/pdf");
		mimeMessageHelperMock.addAttachment("fileTwo", attachmentTwo, "application/pdf");
		
		EasyMock.replay(freeMarkerConfigMock, configMock, mimeMessageHelperMock);
		prep.prepare(testMessage);

		EasyMock.verify(freeMarkerConfigMock, configMock, mimeMessageHelperMock);
	}

	
	class TestTemplate extends Template {

		public TestTemplate() throws Exception {
			super(null, new StringReader(""), null);
		}

		@Override
		public void process(Object rootMap, Writer out) {
			assertEquals(model, rootMap);
			assertTrue(rootMap instanceof Map);
			@SuppressWarnings("unchecked")
			Object value = ((Map<String, Object>) rootMap).get("test");
			assertEquals("testValue", value);
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
