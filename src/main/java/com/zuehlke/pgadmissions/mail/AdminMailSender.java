package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.utils.Environment;

public class AdminMailSender extends StateChangeMailSender {
	private final Logger log = Logger.getLogger(AdminMailSender.class);

	public AdminMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender) {
		super(mimeMessagePreparatorFactory, mailSender);

	}

	Map<String, Object> createModel(ApplicationForm applicationForm) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", applicationForm);
		model.put("host", Environment.getInstance().getApplicationHostName());
		model.put("applicant", applicationForm.getApplicant());
		return model;
	}

	@Override
	public void sendMailsForApplication(ApplicationForm form, String subjectMessage, String templatename) {
		Map<String, Object> model = createModel(form);

		String subject = "Application " + form.getId() + // 
				" by " + form.getApplicant().getFirstName() + " " + form.getApplicant().getLastName()// 
				+ " " + subjectMessage;

		internalSend(form, subject, templatename, model);
	}

	public void sendAdminReviewNotification(ApplicationForm form, RegisteredUser reviewer) {
		Map<String, Object> model = createModel(form);
		model.put("reviewer", reviewer);

		internalSend(form, "Notification - review added", "private/staff/admin/mail/review_submission_notification.ftl", model);
	}

	public void sendAdminInterviewNotification(ApplicationForm form, RegisteredUser interviewer) {
		Map<String, Object> model = createModel(form);
		model.put("interviewer", interviewer);
		internalSend(form, "Notification - Interview added", "private/staff/admin/mail/interview_submission_notification.ftl", model);
	}

	public void sendAdminRejectNotification(ApplicationForm application, RegisteredUser approver) {
		Map<String, Object> model = createModel(application);
		model.put("approver", approver);
		model.put("reason", application.getRejection().getRejectionReason());

		List<RegisteredUser> administrators = new ArrayList<RegisteredUser>(application.getProgram().getAdministrators());
		administrators.remove(approver);
		if (!administrators.isEmpty()) {
			internalSend(application, administrators, "Notification - rejected application", "private/staff/admin/mail/rejected_notification.ftl", model);
		}
	}

	public void sendReviewerAssignedNotification(ApplicationForm applicationForm, Reviewer newReviewer) {
		Map<String, Object> model = createModel(applicationForm);
		model.put("newReviewer", newReviewer);

		internalSend(applicationForm, "Notification - Reviewer assigned", "private/staff/admin/mail/reviewer_assigned_notification.ftl", model);
	}

	private void internalSend(ApplicationForm applicationForm, String subject, String template, Map<String, Object> model) {
		List<RegisteredUser> programAdmins = applicationForm.getProgram().getAdministrators();
		internalSend(applicationForm, programAdmins, subject, template, model);
	}

	private void internalSend(ApplicationForm applicationForm, List<RegisteredUser> adminRecipients, String subject, String template, Map<String, Object> model) {
		RegisteredUser applicationAdmin = applicationForm.getApplicationAdministrator();
		if (applicationAdmin == null) { // send email to all program administrators
			for (RegisteredUser admin : adminRecipients) {
				InternetAddress toAddress = createAddress(admin);

				model.put("admin", admin);
				delegateToMailSender(toAddress, null, subject, template, model);
			}
		} else { // send one email to application admin, CC to program admins
			InternetAddress[] ccAddresses = new InternetAddress[adminRecipients.size()];
			int index = 0;
			for (RegisteredUser admin : adminRecipients) {
				ccAddresses[index] = createAddress(admin);
				index++;
			}
			InternetAddress toAddress = createAddress(applicationAdmin);
			model.put("admin", applicationAdmin);
			delegateToMailSender(toAddress, ccAddresses, subject, template, model);
		}
	}

	private InternetAddress createAddress(RegisteredUser user) {
		try {
			return new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());
		} catch (UnsupportedEncodingException uee) {// this shouldn't happen...
			log.error("error creating email-address: " + user.getEmail(), uee);
			throw new RuntimeException(uee);
		}
	}

	private void delegateToMailSender(InternetAddress toAddress, InternetAddress[] ccAddresses, String subject, String template, Map<String, Object> model) {
		MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses, subject, template, model);
		javaMailSender.send(msgPreparator);
	}
}
