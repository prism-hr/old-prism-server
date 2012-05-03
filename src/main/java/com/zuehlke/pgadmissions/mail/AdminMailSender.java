package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class AdminMailSender extends MailSender {
	
	

	public AdminMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		super(mimeMessagePreparatorFactory, mailSender);
	}

	public Map<String, Object> createModel(RegisteredUser admin, ApplicationForm form, RegisteredUser reviewer) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("admin", admin);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("reviewer", reviewer);
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	public void sendAdminReviewNotification(RegisteredUser admin, ApplicationForm form, RegisteredUser reviewer) throws UnsupportedEncodingException {
			InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());
			javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Notification - review added",
					"private/staff/admin/mail/review_submission_notification.ftl", createModel(admin, form, reviewer)));
		
	}

}
