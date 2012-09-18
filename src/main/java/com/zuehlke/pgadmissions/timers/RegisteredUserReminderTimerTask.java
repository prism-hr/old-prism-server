package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.AdminMailSender;

public class RegisteredUserReminderTimerTask extends TimerTask {
	private final Logger log = Logger.getLogger(RegisteredUserReminderTimerTask.class);
	private final SessionFactory sessionFactory;
	private final ApplicationFormDAO applicationFormDAO;
	private final AdminMailSender adminMailSender;
	private final NotificationType notificationType;
	private final ApplicationFormStatus status;
	private final String subjectCode;
	private final String emailTemplate;
	private final String firstSubjectCode;
	private final String firstEmailTemplate;

	public RegisteredUserReminderTimerTask(SessionFactory sessionFactory, ApplicationFormDAO applicationFormDAO, AdminMailSender adminMailSender,
			NotificationType notificationType, ApplicationFormStatus status, String fisrtSubjectCode, String firstEmailTemplate,
			String subjectCode, String emailTemplate) {
				this.sessionFactory = sessionFactory;

				this.applicationFormDAO = applicationFormDAO;
				this.adminMailSender = adminMailSender;
				this.notificationType = notificationType;
				this.status = status;
				this.firstSubjectCode = fisrtSubjectCode;
				this.firstEmailTemplate = firstEmailTemplate;
				this.subjectCode = subjectCode;
				this.emailTemplate = emailTemplate;
	}

	@Override
	public void run() {
		log.info(notificationType +  " Reminder Task Running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

		List<ApplicationForm> applications = applicationFormDAO.getApplicationsDueUserReminder(notificationType, status);

		transaction.commit();
		for (ApplicationForm application : applications) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(application);
			try {
				NotificationRecord notificationRecord = application.getNotificationForType(notificationType);
				
				String useSubjectCode = subjectCode;
				String useEmailTemplate = emailTemplate;
				if (notificationRecord == null) {
					notificationRecord = new NotificationRecord(notificationType);
					application.getNotificationRecords().add(notificationRecord);
					useSubjectCode = firstSubjectCode;
					useEmailTemplate = firstEmailTemplate;
				}
				adminMailSender.sendMailsForApplication(application, useSubjectCode, useEmailTemplate, notificationType);
				notificationRecord.setDate(new Date());
				applicationFormDAO.save(application);
				transaction.commit();
				log.info(status + " reminders sent to " + application.getId());
			} catch (Throwable e) {
				log.info("error in sending " + status + " reminders for " + application.getId(), e);
				transaction.rollback();
			}

		}
		log.info(notificationType +  " Reminder Task complete");
	}

}
