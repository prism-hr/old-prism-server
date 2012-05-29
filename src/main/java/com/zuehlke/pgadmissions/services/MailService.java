package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
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

		String mailSubject = resolveMessage("application.update", form.getId(), form.getProgram().getTitle());
		for (RegisteredUser admin : administrators) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("admin", admin);
				model.put("application", form);
				model.put("host", Environment.getInstance().getApplicationHostName());
				InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());

				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, mailSubject,//
						"private/staff/admin/mail/application_updated_confirmation.ftl", model, null));

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

	private void internalSendWithdraw(RegisteredUser recipient, ApplicationForm application) {
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("user", recipient);
			model.put("application", application);
			model.put("applicant", application.getApplicant());
			model.put("host", Environment.getInstance().getApplicationHostName());

			InternetAddress toAddress = new InternetAddress(recipient.getEmail(), recipient.getFirstName() + " " + recipient.getLastName());
			String mailSubject = resolveMessage("application.withdrawal", application.getId(), application.getProgram().getTitle());

			mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress,// 
					mailSubject, "private/staff/mail/application_withdrawn_notification.ftl", model, null));
		} catch (Throwable e) {
			log.warn("error while sending email", e);
		}
	}

	private String resolveMessage(String code, Object... args) {
		return msgSource.getMessage(code, args, null);
	}
}
