package com.zuehlke.pgadmissions.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.utils.Environment;

public class ReviewerMailSender extends MailSender {

	public ReviewerMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender javaMailSender, MessageSource msgSource) {
		super(mimeMessagePreparatorFactory, javaMailSender, msgSource);
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

	public void sendReviewerNotification(Reviewer reviewer) {
		internalSendReviewerMail(reviewer, "review.request", "private/reviewers/mail/reviewer_notification_email.ftl");
	}

	public void sendReviewerReminder(Reviewer reviewer) {
		internalSendReviewerMail(reviewer, "review.request.reminder", "private/reviewers/mail/reviewer_reminder_email.ftl");
	}

	private void internalSendReviewerMail(Reviewer reviewer, String messageCode, String template) {
		InternetAddress toAddress = createAddress(reviewer.getUser());
		String subject = resolveMessage(messageCode, reviewer.getReviewRound().getApplication());

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, template, createModel(reviewer), null));
	}
}
