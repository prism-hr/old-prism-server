package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REVIEWER_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REVIEWER_REMINDER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class ReviewerMailSender extends MailSender {

	public ReviewerMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender javaMailSender, MessageSource msgSource, EmailTemplateService emailTemplateService) {
		super(mimeMessagePreparatorFactory, javaMailSender, msgSource, emailTemplateService);
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
		EmailTemplate template = getDefaultEmailtemplate(REVIEWER_NOTIFICATION);
		internalSendReviewerMail(reviewer, "review.request", REVIEWER_NOTIFICATION, template.getContent());
	}

	public void sendReviewerReminder(Reviewer reviewer) {
		EmailTemplate template = getDefaultEmailtemplate(REVIEWER_REMINDER);
		internalSendReviewerMail(reviewer, "review.request.reminder", REVIEWER_REMINDER, template.getContent());
	}

	private void internalSendReviewerMail(Reviewer reviewer, String messageCode, EmailTemplateName templateName, String templateContent) {
		InternetAddress toAddress = createAddress(reviewer.getUser());
		String subject = resolveMessage(messageCode, reviewer.getReviewRound().getApplication());

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, templateName, templateContent, createModel(reviewer), null));
	}
}
