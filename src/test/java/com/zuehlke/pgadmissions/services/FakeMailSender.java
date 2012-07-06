package com.zuehlke.pgadmissions.services;

import java.io.InputStream;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class FakeMailSender implements JavaMailSender {

	@Override
	public void send(SimpleMailMessage arg0) throws MailException {
		// stub
	}

	@Override
	public void send(SimpleMailMessage[] arg0) throws MailException {
		// stub
	}

	@Override
	public MimeMessage createMimeMessage() {
		return null;
	}

	@Override
	public MimeMessage createMimeMessage(InputStream arg0) throws MailException {
		return null;
	}

	@Override
	public void send(MimeMessage arg0) throws MailException {
		// stub
	}

	@Override
	public void send(MimeMessage[] arg0) throws MailException {
		// stub
	}

	@Override
	public void send(MimeMessagePreparator arg0) throws MailException {
		try {
			TestMessage testMessage = new TestMessage();
			arg0.prepare(testMessage);
			System.out.println("RECIPIENTS: " + StringUtils.join(testMessage.getAllRecipients(), ", "));
			System.out.println("   SUBJECT: " + testMessage.getSubject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send(MimeMessagePreparator[] arg0) throws MailException {
		// stub
	}

	class TestMessage extends MimeMessage {
		public TestMessage() {
			super((Session) null);
		}
	}
}
