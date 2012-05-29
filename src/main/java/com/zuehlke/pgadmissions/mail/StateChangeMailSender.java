package com.zuehlke.pgadmissions.mail;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

public abstract class StateChangeMailSender extends MailSender {

	public StateChangeMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource msgSource) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
	}

	public abstract void sendMailsForApplication(ApplicationForm form, String messageCode, String templatename);

}