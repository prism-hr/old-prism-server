package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class ApplicantMailSender extends MailSender {

	public ApplicantMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		super(mimeMessagePreparatorFactory, mailSender);
	}

	Map<String, Object> createModel(ApplicationForm form) {

		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);

		model.put("application", form);

		model.put("applicant", form.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	public void sendMovedToReviewNotification(ApplicationForm form) throws UnsupportedEncodingException {

		InternetAddress toAddress = new InternetAddress(form.getApplicant().getEmail(), form.getApplicant().getFirstName() + " "
				+ form.getApplicant().getLastName());
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application " + form.getId() + " for "
				+ form.getProgram().getTitle() + " now being reviewed", "private/pgStudents/mail/moved_to_review_notification.ftl", createModel(form)));

	}

}
