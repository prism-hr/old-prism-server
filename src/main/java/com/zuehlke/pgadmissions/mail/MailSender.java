package com.zuehlke.pgadmissions.mail;

import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

public abstract class MailSender {

	protected final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	protected final JavaMailSender javaMailSender;

	public MailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.javaMailSender = mailSender;
	
	}

	protected String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		StringBuilder adminsMails = new StringBuilder();
		for (RegisteredUser admin : administrators) {
			if (adminsMails.length() > 0) {
				adminsMails.append(", ");
			}
			adminsMails.append(admin.getEmail());
	
		}
		return adminsMails.toString();
	}

}