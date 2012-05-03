package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class RefereeMailSender extends MailSender {

	public RefereeMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		super(mimeMessagePreparatorFactory, mailSender);
	}

	public void sendRefereeReminder(Referee referee) throws UnsupportedEncodingException {

		if (referee.getUser() != null && referee.getUser().isEnabled()) {
			InternetAddress toAddress = new InternetAddress(referee.getUser().getEmail(), referee.getUser().getFirstName() + " "
					+ referee.getUser().getLastName());
			javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Reminder - reference required",
					"private/referees/mail/existing_user_referee_reminder_email.ftl", createModel(referee)));
		} else {
			InternetAddress toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
			javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Reminder - reference required",
					"private/referees/mail/referee_reminder_email.ftl", createModel(referee)));
		}

	}

	public void sendRefereeNotification(Referee referee) throws UnsupportedEncodingException {

		if (referee.getUser() != null && referee.getUser().isEnabled()) {
			InternetAddress toAddress = new InternetAddress(referee.getUser().getEmail(), referee.getUser().getFirstName() + " "
					+ referee.getUser().getLastName());
			javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Referee Notification",
					"private/referees/mail/existing_user_referee_notification_email.ftl", createModel(referee)));
		} else {
			InternetAddress toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
			javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Referee Notification",
					"private/referees/mail/referee_notification_email.ftl", createModel(referee)));
		}

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
