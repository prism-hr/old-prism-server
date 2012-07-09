package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.ApprovalRestartRequestMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ApprovalRestartRequestReminderTimerTask extends TimerTask {

	private final Logger log = Logger.getLogger(ApprovalRestartRequestNotificationTimerTask.class);
	private final ApprovalRestartRequestMailSender mailSender;
	private final SessionFactory sessionFactory;
	private final ApplicationsService applicationsService;

	public ApprovalRestartRequestReminderTimerTask(SessionFactory sessionFactory, ApprovalRestartRequestMailSender mailSender,
			ApplicationsService applicationsService) {
		this.sessionFactory = sessionFactory;
		this.mailSender = mailSender;
		this.applicationsService = applicationsService;
	}

	@Override
	public void run() {
		log.info("ApprovalRestartRequestReminderTimerTask Running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

		List<ApplicationForm> applications = applicationsService.getApplicationsDueApprovalRestartRequestReminder();

		transaction.commit();
		for (ApplicationForm application : applications) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(application);
			try {

				mailSender.sendRequestRestartApprovalReminder(application);
				NotificationRecord notificationRecord = application.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER);
				if (notificationRecord == null) {
					notificationRecord = new NotificationRecord();
					notificationRecord.setNotificationType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER);					
					application.getNotificationRecords().add(notificationRecord);
				}
				notificationRecord.setDate(new Date());
				applicationsService.save(application);
				transaction.commit();
				log.info("Approval restart request reminder send for " + application.getApplicationNumber());
			} catch (Throwable e) {
				e.printStackTrace();
				log.info("error in sending Approval restart request reminder for " + application.getApplicationNumber());
				transaction.rollback();
			}

		}
		log.info("ApprovalRestartRequestReminderTimerTask Complete");

	}

}
