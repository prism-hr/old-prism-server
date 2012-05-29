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

	private static final String NEW_REF = "private/referees/mail/referee_notification_email.ftl";
	private static final String EXISTING_REF = "private/referees/mail/existing_user_referee_notification_email.ftl";

	private static final String NEW_REF_REMINDER = "private/referees/mail/referee_reminder_email.ftl";
	private static final String EXISTING_REF_REMINDER = "private/referees/mail/existing_user_referee_reminder_email.ftl";

	public RefereeMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource msgSource) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
	}

	public void sendRefereeReminder(Referee referee) throws UnsupportedEncodingException {
		String subject = resolveMessage("reference.request.reminder", referee.getApplication());
		String templateName;
		InternetAddress toAddress;
		if (referee.getUser() != null && referee.getUser().isEnabled()) {
			toAddress = createAddress(referee.getUser());
			templateName = EXISTING_REF_REMINDER;
		} else {
			toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
			templateName = NEW_REF_REMINDER;
		}
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, templateName, createModel(referee), null));
	}

	public void sendRefereeNotification(Referee referee) throws UnsupportedEncodingException {
		String subject = resolveMessage("reference.request", referee.getApplication());
		String templateName;
		InternetAddress toAddress;
		if (referee.getUser() != null && referee.getUser().isEnabled()) {
			toAddress = createAddress(referee.getUser());
			templateName = EXISTING_REF;
		} else {
			toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
			templateName = NEW_REF;
		}
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, templateName, createModel(referee), null));
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
