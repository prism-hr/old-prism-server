package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.utils.Environment;

public class AdminMailSender extends StateChangeMailSender {
	private final Logger log = Logger.getLogger(AdminMailSender.class);

	public AdminMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		super(mimeMessagePreparatorFactory, mailSender);

	}

	Map<String, Object> createModel(ApplicationForm applicationForm, RegisteredUser administrator, RegisteredUser reviewer, RegisteredUser interviewer) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("admin", administrator);
		model.put("application", applicationForm);
		model.put("host", Environment.getInstance().getApplicationHostName());
		model.put("applicant", applicationForm.getApplicant());
		model.put("reviewer", reviewer);
		model.put("interviewer", interviewer);
		return model;
	}
	
	@Override
	public void sendMailsForApplication(ApplicationForm form, String subjectMessage, String templatename) {
		for (RegisteredUser administrator : form.getProgram().getAdministrators()) {
			try {
				sendMailToAdmin(form, administrator, subjectMessage, templatename);
			} catch (Throwable e) {
				e.printStackTrace();
				log.warn("error while sending email to " + administrator.getEmail(), e);
			}
		}
	}
	
	public void sendAdminReviewNotification(RegisteredUser admin, ApplicationForm form, RegisteredUser reviewer) throws UnsupportedEncodingException {
			InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());
			javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Notification - review added",
					"private/staff/admin/mail/review_submission_notification.ftl", createModel(form, admin, reviewer, null)));
		
	}

	public void sendAdminInterviewNotification(RegisteredUser admin, ApplicationForm form, RegisteredUser interviewer) throws UnsupportedEncodingException {
		InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());
		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Notification - Interview added",
				"private/staff/admin/mail/interview_submission_notification.ftl", createModel(form, admin, null, interviewer)));
		
	}
	

	void sendMailToAdmin(ApplicationForm applicationForm, RegisteredUser administrator, String subjectMessage, String templatename)
			throws UnsupportedEncodingException {
		InternetAddress toAddress = new InternetAddress(administrator.getEmail(), administrator.getFirstName() + " " + administrator.getLastName());

		javaMailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application " + applicationForm.getId() + " by "
				+ applicationForm.getApplicant().getFirstName() + " " + applicationForm.getApplicant().getLastName() + " " + subjectMessage, templatename,
				createModel(applicationForm, administrator, null, null)));

	}

	public void sendAdminRejectNotification(RegisteredUser admin, ApplicationForm form, RegisteredUser approver) throws UnsupportedEncodingException {
		InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());
		Map<String, Object> model = createModel(form, admin, null, null);
		model.put("approver", approver);
		model.put("reasons", form.getRejectReasons());
		String templateName = "private/staff/admin/mail/rejected_notification.ftl";

		MimeMessagePreparator mimePrep = mimeMessagePreparatorFactory.getMimeMessagePreparator(//
				toAddress, "Notification - rejected application", templateName, model);
		javaMailSender.send(mimePrep);
	}
}
