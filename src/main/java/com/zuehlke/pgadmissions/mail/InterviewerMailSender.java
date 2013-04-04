package com.zuehlke.pgadmissions.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class InterviewerMailSender extends MailSender {

	public InterviewerMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource msgSource) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
	}

	Map<String, Object> createModel(Interviewer interviewer) {
		ApplicationForm form = interviewer.getInterview().getApplication();
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		String adminsEmails = getAdminsEmailsCommaSeparatedAsString(administrators);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminsEmails);
		model.put("interviewer", interviewer);
		model.put("application", form);

		model.put("applicant", form.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	public void sendInterviewerNotification(Interviewer interviewer) {
		internalSendMail(interviewer, "interview.notification.interviewer", "private/interviewers/mail/interviewer_notification_email.ftl");
	}

	public void sendInterviewerReminder(Interviewer interviewer, boolean firstReminder) {
		String subject = "interview.feedback.request.reminder";
		String template = "private/interviewers/mail/interviewer_reminder_email.ftl";
		if (firstReminder) {
			subject = "interview.feedback.request";
			template = "private/interviewers/mail/interviewer_reminder_email_first.ftl";
		}
		internalSendMail(interviewer, subject, template);
	}

	private void internalSendMail(Interviewer interviewer, String subjectCode, String template) {
		InternetAddress toAddress = createAddress(interviewer.getUser());
		String subject = resolveMessage(subjectCode, interviewer.getInterview().getApplication());

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,//
				template, createModel(interviewer), null));
	}
}
