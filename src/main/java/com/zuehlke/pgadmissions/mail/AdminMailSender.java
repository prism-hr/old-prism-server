package com.zuehlke.pgadmissions.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.PersonService;
import com.zuehlke.pgadmissions.utils.Environment;

public class AdminMailSender extends StateChangeMailSender {
	
	private final ApplicationsService applicationService;
	private final PersonService personService;
	
	public AdminMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender,// 
			ApplicationsService applicationService,MessageSource msgSource, PersonService personService) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource);
		this.applicationService = applicationService;
		this.personService = personService;

	}

	Map<String, Object> createModel(ApplicationForm applicationForm) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", applicationForm);
		model.put("host", Environment.getInstance().getApplicationHostName());
		model.put("applicant", applicationForm.getApplicant());
		return model;
	}

	@Override
	public void sendMailsForApplication(ApplicationForm form, String messageCode, String templatename, NotificationType notificationType) {
		Map<String, Object> model = createModel(form);
		if(notificationType == NotificationType.APPROVAL_REMINDER){
			sendApproverApprovalReminder(form, messageCode, templatename, model);
		}else{
			internalSend(form, messageCode, templatename, model);
		}
	}

	public void sendApproverApprovalReminder(ApplicationForm form, String messageCode, String templatename, Map<String, Object> model) {
		ApplicationFormStatus previousStage = applicationService.getStageComingFrom(form);
		String subject = resolveMessage(messageCode, form, previousStage);
		List<RegisteredUser> programApprovers = form.getProgram().getApprovers();
		for (RegisteredUser approver : programApprovers) {
			InternetAddress toAddress = createAddress(approver);
			model.put("approver", approver);
			delegateToMailSender(toAddress, null, subject, templatename, model);
		}
		
	}

	public void sendAdminReviewNotification(ApplicationForm form, RegisteredUser reviewer) {
		Map<String, Object> model = createModel(form);
		model.put("reviewer", reviewer);

		internalSend(form, "review.provided.admin", "private/staff/admin/mail/review_submission_notification.ftl", model);
	}

	public void sendAdminInterviewNotification(ApplicationForm form, RegisteredUser interviewer) {
		Map<String, Object> model = createModel(form);
		model.put("interviewer", interviewer);
		internalSend(form, "interview.feedback.notification", "private/staff/admin/mail/interview_submission_notification.ftl", model);
	}

	public void sendAdminRejectNotification(ApplicationForm application, RegisteredUser approver) {
		Map<String, Object> model = createModel(application);
		model.put("approver", approver);
		model.put("reason", application.getRejection().getRejectionReason());
		model.put("previousStage", applicationService.getStageComingFrom(application));

		List<RegisteredUser> administrators = new ArrayList<RegisteredUser>(application.getProgram().getAdministrators());
		administrators.remove(approver);
		if (!administrators.isEmpty()) {
			internalSend(application, administrators, "rejection.notification", "private/staff/admin/mail/rejected_notification.ftl", model);
		}
	}

	public void sendReviewerAssignedNotification(ApplicationForm applicationForm, Reviewer newReviewer) {
		Map<String, Object> model = createModel(applicationForm);
		model.put("newReviewer", newReviewer);

		internalSend(applicationForm, "reviewer.assigned.admin", "private/staff/admin/mail/reviewer_assigned_notification.ftl", model);
	}

	private void internalSend(ApplicationForm applicationForm, String messageCode, String template, Map<String, Object> model) {
		List<RegisteredUser> programAdmins = applicationForm.getProgram().getAdministrators();
		internalSend(applicationForm, programAdmins, messageCode, template, model);
	}

	private void internalSend(ApplicationForm form, List<RegisteredUser> adminRecipients, String messageCode, String template, Map<String, Object> model) {
		RegisteredUser applicationAdmin = form.getApplicationAdministrator();
		ApplicationFormStatus previousStage = applicationService.getStageComingFrom(form);
		
		String subject = resolveMessage(messageCode, form, previousStage);
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

	private void delegateToMailSender(InternetAddress toAddress, InternetAddress[] ccAddresses, String subject, String template, Map<String, Object> model) {
		MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses, subject, template, model, null);
		javaMailSender.send(msgPreparator);
	}

	public void sendAdminApprovedNotification(ApplicationForm application, RegisteredUser approver) {
		Map<String, Object> model = createModel(application);
		model.put("approver", approver);
		model.put("previousStage", applicationService.getStageComingFrom(application));
		model.put("registryContacts", personService.getAllRegistryUsers());

		List<RegisteredUser> administrators = new ArrayList<RegisteredUser>(application.getProgram().getAdministrators());
		administrators.remove(approver);
		if (!administrators.isEmpty()) {
			internalSend(application, administrators, "approved.notification", "private/staff/admin/mail/approved_notification.ftl", model);
		}
		
	}
}
