package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;

import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

public abstract class StateChangeMailSender extends MailSender {

	public StateChangeMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		super(mimeMessagePreparatorFactory, mailSender);
	}

	public abstract void sendMailsForApplication(ApplicationForm form, String message, String templatename) throws UnsupportedEncodingException; 

}