package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_REMINDER;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class RefereeMailSender extends MailSender {



	public RefereeMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource msgSource, EmailTemplateService emailTemplateService) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource, emailTemplateService);
	}

	public void sendRefereeReminder(Referee referee) throws UnsupportedEncodingException {
		String subject = resolveMessage("reference.request.reminder", referee.getApplication());
		EmailTemplate template = getDefaultEmailtemplate(REFEREE_REMINDER);
		InternetAddress toAddress;
		if (referee.getUser() != null && referee.getUser().isEnabled()) {
			toAddress = createAddress(referee.getUser());
		} else {
			toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
		}
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, REFEREE_REMINDER, template.getContent(), createModel(referee), null));
	}

	public void sendRefereeNotification(Referee referee) throws UnsupportedEncodingException {
		String subject = resolveMessage("reference.request", referee.getApplication());
		EmailTemplate template = getDefaultEmailtemplate(REFEREE_NOTIFICATION);
		InternetAddress toAddress;
		if (referee.getUser() != null && referee.getUser().isEnabled()) {
			toAddress = createAddress(referee.getUser());
		} else {
			toAddress = new InternetAddress(referee.getEmail(), referee.getFirstname() + " " + referee.getLastname());
		}
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, REFEREE_NOTIFICATION, template.getContent(), createModel(referee), null));
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
