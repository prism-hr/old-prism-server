package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class AdminMailSender extends MailSender {
	private final Logger log = Logger.getLogger(AdminMailSender.class);

	public AdminMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		super(mimeMessagePreparatorFactory, mailSender);

	}

	Map<String, Object> createModel(ApplicationForm applicationForm, RegisteredUser administrator, RegisteredUser reviewer) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("admin", administrator);
		model.put("application", applicationForm);
		model.put("host", Environment.getInstance().getApplicationHostName());
		model.put("applicant", applicationForm.getApplicant());
		model.put("reviewer", reviewer);
		return model;
	}

	public void sendReminderToAdmins(ApplicationForm form, String subjectMessage, String templatename) {
		for (RegisteredUser administrator : form.getProgram().getAdministrators()) {
			try {
				sendReminderToAdmin(form, administrator, subjectMessage, templatename);
			} catch (Throwable e) {
				e.printStackTrace();
				log.warn("error while sending email to " + administrator.getEmail(), e);
			}
		}
	}
	
	public void sendAdminReviewNotification(RegisteredUser admin, ApplicationForm form, RegisteredUser reviewer) throws UnsupportedEncodingException {
			InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());
			javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Notification - review added",
					"private/staff/admin/mail/review_submission_notification.ftl", createModel(form, admin, reviewer)));
		
	}


	public void sendReminderToAdmin(ApplicationForm applicationForm, RegisteredUser administrator, String subjectMessage, String templatename)
			throws UnsupportedEncodingException {
		InternetAddress toAddress = new InternetAddress(administrator.getEmail(), administrator.getFirstName() + " " + administrator.getLastName());

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application " + applicationForm.getId() + " by "
				+ applicationForm.getApplicant().getFirstName() + " " + applicationForm.getApplicant().getLastName() + " " + subjectMessage, templatename,
				createModel(applicationForm, administrator, null)));

	}

}
