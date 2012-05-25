package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class InterviewerMailSender extends MailSender {

	public InterviewerMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		super(mimeMessagePreparatorFactory, mailSender);	
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

	public void sendInterviewerNotification(Interviewer interviewer) throws UnsupportedEncodingException {
		InternetAddress toAddress = new InternetAddress(interviewer.getUser().getEmail(), interviewer.getUser().getFirstName() + " "
				+ interviewer.getUser().getLastName());
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application " + interviewer.getInterview().getApplication().getId() + " for "
				+ interviewer.getInterview().getApplication().getProgram().getTitle() + " - Interviewer Notification",
				"private/interviewers/mail/interviewer_notification_email.ftl", createModel(interviewer), null));

	}

	public void sendInterviewerReminder(Interviewer interviewer) throws UnsupportedEncodingException {
		InternetAddress toAddress = new InternetAddress(interviewer.getUser().getEmail(), interviewer.getUser().getFirstName() + " "
				+ interviewer.getUser().getLastName());
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application " + interviewer.getInterview().getApplication().getId() + " for "
				+ interviewer.getInterview().getApplication().getProgram().getTitle() + " - Interview Feedback Reminder",
				"private/interviewers/mail/interviewer_reminder_email.ftl", createModel(interviewer), null));

		
	}
}
