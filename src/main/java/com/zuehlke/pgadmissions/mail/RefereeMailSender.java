package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class RefereeMailSender extends MailSender {

	private static final String REFEREE_NOTIFICATION = "private/referees/mail/referee_notification_email.ftl";

	private static final String REFEREE_REMINDER = "private/referees/mail/referee_reminder_email.ftl";

	public RefereeMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource msgSource) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
	}

	public void sendRefereeReminder(Referee referee) throws UnsupportedEncodingException {
		String subject = resolveMessage("reference.request.reminder", referee.getApplication());
		InternetAddress toAddress;
		if (referee.getUser() != null && referee.getUser().isEnabled()) {
			toAddress = createAddress(referee.getUser());
		} else {
			toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
		}
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, REFEREE_REMINDER, createModel(referee), null));
	}

	public void sendRefereeNotification(Referee referee) throws UnsupportedEncodingException {
		String subject = resolveMessage("reference.request", referee.getApplication());
		InternetAddress toAddress;
		if (referee.getUser() != null && referee.getUser().isEnabled()) {
			toAddress = createAddress(referee.getUser());
		} else {
			toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
		}
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, REFEREE_NOTIFICATION, createModel(referee), null));
	}

	Map<String, Object> createModel(Referee referee) {
		ApplicationForm form = referee.getApplication();
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);
		model.put("referee", referee);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

}
