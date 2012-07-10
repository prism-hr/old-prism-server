package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public abstract class MailSender {

	protected final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	protected final JavaMailSender javaMailSender;
	private final MessageSource messageSource;

	public MailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource messageSource) {
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.javaMailSender = mailSender;
		this.messageSource = messageSource;
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

	protected String resolveMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}

	protected String resolveMessage(String code, ApplicationForm form, ApplicationFormStatus previousStage) {
		if (previousStage == null) {
			return resolveMessage(code, form);
		}
		RegisteredUser applicant = form.getApplicant();
		if (applicant == null) {
			throw new IllegalArgumentException("applicant must be provided!");
		}
		Object[] args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName(),
				previousStage.displayValue() };

		return messageSource.getMessage(code, args, null);
	}

	protected String resolveMessage(String code, ApplicationForm form) {
		RegisteredUser applicant = form.getApplicant();
		Object[] args;
		if (applicant == null) {
			args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle() };
		} else {
			args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName() };
		}
		return messageSource.getMessage(code, args, null);
	}

	protected final InternetAddress createAddress(RegisteredUser user) {
		try {
			return new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());
		} catch (UnsupportedEncodingException uee) {// this shouldn't happen...
			throw new RuntimeException(uee);
		}
	}

}