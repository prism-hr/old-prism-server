package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPROVED_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEW_SUBMISSION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REJECTED_NOTIFICATION_ADMIN;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REVIEWER_ASSIGNED_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REVIEW_SUBMISSION_NOTIFICATION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

public class AdminMailSender extends StateChangeMailSender {

	private final ConfigurationService personService;

    public AdminMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender,
            MessageSource msgSource, ConfigurationService personService, EmailTemplateService emailTemplateService) {
		super(mimeMessagePreparatorFactory, mailSender, msgSource, emailTemplateService);
		this.personService = personService;
	}

	Map<String, Object> createModel(ApplicationForm applicationForm) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", applicationForm);
		model.put("host", Environment.getInstance().getApplicationHostName());
		model.put("applicant", applicationForm.getApplicant());
		model.put("admissionOfferServiceLevel", Environment.getInstance().getAdmissionsOfferServiceLevel());
		return model;
	}

	@Override
	public void sendMailsForApplication(ApplicationForm form, String messageCode, EmailTemplateName templateName, String templateContent, NotificationType notificationType) {
		Map<String, Object> model = createModel(form);
		if (notificationType == NotificationType.APPROVAL_REMINDER) {
			sendApproverApprovalReminder(form, messageCode, templateName, templateContent, model);
		} else {
			internalSend(form, messageCode, templateName, templateContent, model);
		}
	}

	public void sendApproverApprovalReminder(ApplicationForm form, String messageCode, EmailTemplateName templateName, String templateContent, Map<String, Object> model) {
		ApplicationFormStatus previousStage = form.getOutcomeOfStage();
		String subject = resolveMessage(messageCode, form, previousStage);
		List<RegisteredUser> programApprovers = form.getProgram().getApprovers();
		for (RegisteredUser approver : programApprovers) {
			InternetAddress toAddress = createAddress(approver);
			model.put("approver", approver);
			delegateToMailSender(toAddress, null, subject, templateName, templateContent, model);
		}
	}

	public void sendAdminReviewNotification(ApplicationForm form, RegisteredUser reviewer) {
		Map<String, Object> model = createModel(form);
		EmailTemplate template = getDefaultEmailtemplate(REVIEW_SUBMISSION_NOTIFICATION);
		model.put("reviewer", reviewer);
		internalSend(form, "review.provided.admin", REVIEW_SUBMISSION_NOTIFICATION, template.getContent(), model);
	}

	public void sendAdminInterviewNotification(ApplicationForm form, RegisteredUser interviewer) {
		Map<String, Object> model = createModel(form);
		EmailTemplate template = getDefaultEmailtemplate(INTERVIEW_SUBMISSION_NOTIFICATION);
		model.put("interviewer", interviewer);
		internalSend(form, "interview.feedback.notification", INTERVIEW_SUBMISSION_NOTIFICATION, template.getContent(), model);
	}

	public void sendAdminRejectNotification(ApplicationForm application, RegisteredUser approver) {
		Map<String, Object> model = createModel(application);
		model.put("approver", approver);
		model.put("reason", application.getRejection().getRejectionReason());
		model.put("previousStage", application.getOutcomeOfStage());
		EmailTemplate template = getDefaultEmailtemplate(REJECTED_NOTIFICATION_ADMIN);

		Set<String> alreadyNotifiedUsers = new HashSet<String>();

		List<RegisteredUser> administrators = new ArrayList<RegisteredUser>(application.getProgram().getAdministrators());
		administrators = removeUsersThatHaveAlreadyBeenNotified(administrators, alreadyNotifiedUsers);
		if (!administrators.isEmpty()) {
			internalSend(application, administrators, "rejection.notification.admin", REJECTED_NOTIFICATION_ADMIN, template.getContent(), model, true);
		}
		
		List<RegisteredUser> supervisorUsers = getSupervisorUsers(application);
		supervisorUsers = removeUsersThatHaveAlreadyBeenNotified(supervisorUsers, alreadyNotifiedUsers);
		if (!supervisorUsers.isEmpty()) {
			internalSend(application, supervisorUsers, "rejection.notification.admin", REJECTED_NOTIFICATION_ADMIN, template.getContent(), model, false);
		}
		
		List<RegisteredUser> approvers =new ArrayList<RegisteredUser>(application.getProgram().getApprovers());
		approvers = removeUsersThatHaveAlreadyBeenNotified(approvers, alreadyNotifiedUsers);
		if (!approvers.isEmpty()) {
			internalSend(application, approvers, "rejection.notification.admin", REJECTED_NOTIFICATION_ADMIN, template.getContent(), model, false);
		}
	}

	private List<RegisteredUser> removeUsersThatHaveAlreadyBeenNotified(final List<RegisteredUser> users, final Set<String> alreadyNotifiedUsers) {
	    List<RegisteredUser> cleansedUsers = new ArrayList<RegisteredUser>();
	    for (RegisteredUser user : users) {
	        if (!alreadyNotifiedUsers.contains(user.getEmail())) {
	            cleansedUsers.add(user);
	            alreadyNotifiedUsers.add(user.getEmail());
	        }
	    }
	    return cleansedUsers;
	}
	
	public void sendReviewerAssignedNotification(ApplicationForm applicationForm, Reviewer newReviewer) {
		Map<String, Object> model = createModel(applicationForm);
		EmailTemplate template = getDefaultEmailtemplate(REVIEWER_ASSIGNED_NOTIFICATION);
		model.put("newReviewer", newReviewer);
		internalSend(applicationForm, "reviewer.assigned.admin", REVIEWER_ASSIGNED_NOTIFICATION, template.getContent(), model);
	}

	private void internalSend(ApplicationForm applicationForm, String messageCode, EmailTemplateName templateName, String templateContent, Map<String, Object> model) {
		List<RegisteredUser> programAdmins = applicationForm.getProgram().getAdministrators();
		internalSend(applicationForm, programAdmins, messageCode, templateName, templateContent, model, true);
	}

	void internalSend(ApplicationForm form, List<RegisteredUser> recipients, String messageCode, EmailTemplateName templateName, String templateContent, Map<String, Object> model, boolean ccIfApplicationAdmin) {
		RegisteredUser applicationAdmin = form.getApplicationAdministrator();
		ApplicationFormStatus previousStage = form.getOutcomeOfStage();

		String subject = resolveMessage(messageCode, form, previousStage);
		if (applicationAdmin == null || !ccIfApplicationAdmin) {
			for (RegisteredUser admin : recipients) {
				InternetAddress toAddress = createAddress(admin);

				model.put("admin", admin);
				delegateToMailSender(toAddress, null, subject, templateName, templateContent, model);
			}
		} else { // send one email to application admin, CC to program admins
			InternetAddress[] ccAddresses = new InternetAddress[recipients.size()];
			int index = 0;
			for (RegisteredUser admin : recipients) {
				ccAddresses[index] = createAddress(admin);
				index++;
			}
			InternetAddress toAddress = createAddress(applicationAdmin);
			model.put("admin", applicationAdmin);
			delegateToMailSender(toAddress, ccAddresses, subject, templateName, templateContent, model);
		}
	}

	private void delegateToMailSender(InternetAddress toAddress, InternetAddress[] ccAddresses, String subject, EmailTemplateName templateName, String templateContent, Map<String, Object> model) {
		MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses, subject, templateName, templateContent, model, null);
		javaMailSender.send(msgPreparator);
	}

	public void sendAdminAndSupervisorApprovedNotification(ApplicationForm application, RegisteredUser approver) {
		Map<String, Object> model = createModel(application);
		model.put("approver", approver);
		model.put("previousStage", application.getOutcomeOfStage());
		model.put("registryContacts", personService.getAllRegistryUsers());
		EmailTemplate template = getDefaultEmailtemplate(APPROVED_NOTIFICATION);

		Set<String> alreadyNotifiedUsers = new HashSet<String>();

		List<RegisteredUser> administrators = new ArrayList<RegisteredUser>(application.getProgram().getAdministrators());
		administrators.remove(approver);
		administrators = removeUsersThatHaveAlreadyBeenNotified(administrators, alreadyNotifiedUsers);
		if (!administrators.isEmpty()) {
			internalSend(application, administrators, "approved.notification", APPROVED_NOTIFICATION, template.getContent(), model, true);
		}

		List<RegisteredUser> supervisorUsers = getSupervisorUsers(application);
		supervisorUsers = removeUsersThatHaveAlreadyBeenNotified(supervisorUsers, alreadyNotifiedUsers);
		if (!supervisorUsers.isEmpty()) {
			internalSend(application, supervisorUsers, "approved.notification", APPROVED_NOTIFICATION, template.getContent(), model, false);
		}
	}

	private List<RegisteredUser> getSupervisorUsers(ApplicationForm application) {
		List<RegisteredUser> supervisorUsers = new ArrayList<RegisteredUser>();
		if (application.getLatestApprovalRound() != null) {
			List<Supervisor> supervisors = application.getLatestApprovalRound().getSupervisors();
			for (Supervisor supervisor : supervisors) {
				supervisorUsers.add(supervisor.getUser());
			}
		}
		return supervisorUsers;
	}
}
