package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.Environment;

public class MailService {

	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final ApplicationsService applicationsService;

	private final Logger log = Logger.getLogger(MailService.class);
	private final MessageSource msgSource;

	public MailService() {
		this(null, null, null, null);
	}

	@Autowired
	public MailService(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender,//
			ApplicationsService applicationsService, MessageSource msgSource) {

		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
		this.applicationsService = applicationsService;
		this.msgSource = msgSource;
	}

	@Transactional
	private void createOrUpdateUpdateNotificationRecord(ApplicationForm form) {
		NotificationRecord notificationRecord = form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION);
		if (notificationRecord == null) {
			notificationRecord = new NotificationRecord();
			notificationRecord.setNotificationType(NotificationType.UPDATED_NOTIFICATION);
			form.getNotificationRecords().add(notificationRecord);
		}
		notificationRecord.setDate(new Date());
		applicationsService.save(form);
	}

	@Transactional
	public void sendApplicationUpdatedMailToAdmins(ApplicationForm form) {
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();

		String mailSubject = resolveMessage("application.update", form.getApplicationNumber(), form.getProgram().getTitle());
		for (RegisteredUser admin : administrators) {
			try {
				Map<String, Object> model = createModel(form);
				model.put("admin", admin);
				InternetAddress toAddress = createAddress(admin);

				delegateToMailSender(toAddress, null, mailSubject, "private/staff/admin/mail/application_updated_confirmation.ftl", model);
			} catch (Throwable e) {
				e.printStackTrace();
				log.warn("error while sending email", e);
			}
		}
		createOrUpdateUpdateNotificationRecord(form);
	}

	@Transactional
	public void sendWithdrawMailToReferees(List<Referee> referees) {
		for (Referee referee : referees) {
			RegisteredUser user = referee.getUser();
			internalSendWithdraw(user, referee.getApplication());
		}
	}

	@Transactional
	public void sendWithdrawToAdmins(ApplicationForm form) {
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		for (RegisteredUser admin : administrators) {
			internalSendWithdraw(admin, form);
		}
	}

	@Transactional
	public void sendWithdrawToReviewers(ApplicationForm form) {
		List<RegisteredUser> reviewers = form.getProgram().getProgramReviewers();
		for (RegisteredUser reviewer : reviewers) {
			internalSendWithdraw(reviewer, form);
		}
	}

	@Transactional
	public void sendRequestRestartApproval(ApplicationForm form, RegisteredUser userRequesting) {
		Program program = form.getProgram();

		List<RegisteredUser> adminRecipients = program.getAdministrators();
		RegisteredUser applicationAdmin = form.getApplicationAdministrator();

		String subject = resolveMessage("application.request.restart.approval", form.getApplicationNumber(), program.getTitle());
		String template = "private/staff/admin/mail/restart_approval_request.ftl";
		Map<String, Object> model = createModel(form);
		model.put("approver", userRequesting);
		
		try {
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
		} catch (Throwable e) {
			e.printStackTrace();
			log.warn("error while sending email", e);
		}
	}

	private void internalSendWithdraw(RegisteredUser recipient, ApplicationForm application) {
		try {
			Map<String, Object> model = createModel(application);
			model.put("user", recipient);

			InternetAddress toAddress = createAddress(recipient);
			String mailSubject = resolveMessage("application.withdrawal", application.getApplicationNumber(), application.getProgram().getTitle());

			delegateToMailSender(toAddress, null, mailSubject, "private/staff/mail/application_withdrawn_notification.ftl", model);
		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}
	}

	protected Map<String, Object> createModel(ApplicationForm application) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", application);
		model.put("applicant", application.getApplicant());
		model.put("host", Environment.getInstance().getApplicationHostName());
		return model;
	}

	private String resolveMessage(String code, Object... args) {
		return msgSource.getMessage(code, args, null);
	}

	private InternetAddress createAddress(RegisteredUser user) {
		try {
			return new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());
		} catch (UnsupportedEncodingException uee) {// this shouldn't happen...
			throw new RuntimeException(uee);
		}
	}

	private void delegateToMailSender(InternetAddress toAddress, InternetAddress[] ccAddresses, String subject, String template, Map<String, Object> model) {
		MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, ccAddresses, subject, template, model, null);
		mailsender.send(msgPreparator);
	}
}
