package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

	public MailService() {
		this(null, null, null);
	}

	@Autowired
	public MailService(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender, ApplicationsService applicationsService) {
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.mailsender = mailsender;
		this.applicationsService = applicationsService;
	}



	
	@Transactional
	private void createOrUpdateUpdateNotificationRecord(ApplicationForm form) {
		NotificationRecord notificationRecord = form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION);
		if(notificationRecord == null){
			notificationRecord = new NotificationRecord();
			notificationRecord.setNotificationType(NotificationType.UPDATED_NOTIFICATION);
			form.getNotificationRecords().add(notificationRecord);
		}
		notificationRecord.setNotificationDate(new Date());		
		applicationsService.save(form);
	}
	
	@Transactional
	public void sendApplicationUpdatedMailToAdmins(ApplicationForm form) {
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();

		for (RegisteredUser admin : administrators) {
			try {										
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("admin", admin);
				model.put("application", form);
				model.put("host", Environment.getInstance().getApplicationHostName());
				InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application Updated", "private/staff/admin/mail/application_updated_confirmation.ftl", model));			
				
				
			} catch (Throwable e) {
				e.printStackTrace();
				log.warn("error while sending email", e);
			}
		}
		createOrUpdateUpdateNotificationRecord(form);
	}
	


	public void sendWithdrawMailToReferees(
			List<Referee> referees) {
		for (Referee referee : referees) {
			try {
				RegisteredUser user = referee.getUser();
				System.out.println(user.getEmail());
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("user", user);
				model.put("application", referee.getApplication());
				model.put("applicant", referee.getApplication().getApplicant());
				model.put("host", Environment.getInstance().getApplicationHostName());
				InternetAddress toAddress = new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application Withdrawn", "private/staff/mail/application_withdrawn_notification.ftl", model));
			} catch (Throwable e) {
				log.warn("error while sending email", e);
			}
			
		}
		
	}

	public void sendWithdrawToAdmins(ApplicationForm form) {
		List<RegisteredUser> administrators = form.getProgram().getAdministrators();
		for (RegisteredUser admin : administrators) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("user", admin);
				model.put("application", form);
				model.put("applicant", form.getApplicant());
				model.put("host", Environment.getInstance().getApplicationHostName());
				InternetAddress toAddress = new InternetAddress(admin.getEmail(), admin.getFirstName() + " " + admin.getLastName());
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application Withdrawn", "private/staff/mail/application_withdrawn_notification.ftl", model));
			} catch (Throwable e) {
				log.warn("error while sending email", e);
			}
			
		}
		
	}

	public void sendWithdrawToReviewers(ApplicationForm form) {
		List<RegisteredUser> reviewers = form.getProgram().getReviewers();
		for (RegisteredUser reviewer : reviewers) {
			try {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("user", reviewer);
				model.put("application", form);
				model.put("host", Environment.getInstance().getApplicationHostName());
				model.put("applicant", form.getApplicant());
				InternetAddress toAddress = new InternetAddress(reviewer.getEmail(), reviewer.getFirstName() + " " + reviewer.getLastName());
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "Application Withdrawn", "private/staff/mail/application_withdrawn_notification.ftl", model));
			} catch (Throwable e) {
				log.warn("error while sending email", e);
			}
			
		}
		
		
	}

}
