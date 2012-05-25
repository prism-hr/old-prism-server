package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.utils.Environment;

public class ReviewerMailSender extends MailSender {

	public ReviewerMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender javaMailSender) {
		super(mimeMessagePreparatorFactory, javaMailSender);
	}

	Map<String, Object> createModel(Reviewer reviewer) {
		ApplicationForm form = reviewer.getReviewRound().getApplication();
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);
		model.put("reviewer", reviewer);
		model.put("application", form);

		model.put("applicant", form.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	public void sendReviewerNotification(Reviewer reviewer) throws UnsupportedEncodingException {
		InternetAddress toAddress = new InternetAddress(reviewer.getUser().getEmail(), reviewer.getUser().getFirstName() + " "
				+ reviewer.getUser().getLastName());
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application " + reviewer.getReviewRound().getApplication().getId() + " for "
				+ reviewer.getReviewRound().getApplication().getProgram().getTitle() + " - Reviewer Notification",
				"private/reviewers/mail/reviewer_notification_email.ftl", createModel(reviewer), null));

	}

	public void sendReviewerReminder(Reviewer reviewer) throws UnsupportedEncodingException {
		InternetAddress toAddress = new InternetAddress(reviewer.getUser().getEmail(), reviewer.getUser().getFirstName() + " "
				+ reviewer.getUser().getLastName());
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application " + reviewer.getReviewRound().getApplication().getId() + " for "
				+ reviewer.getReviewRound().getApplication().getProgram().getTitle() + " - Review Reminder",
				"private/reviewers/mail/reviewer_reminder_email.ftl", createModel(reviewer), null));

		
	}

}
