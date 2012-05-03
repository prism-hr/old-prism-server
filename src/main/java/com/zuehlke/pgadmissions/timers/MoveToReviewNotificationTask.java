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
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.ApplicantMailSender;

public class MoveToReviewNotificationTask extends TimerTask {
	private final Logger log = Logger.getLogger(MoveToReviewNotificationTask.class);
	private final SessionFactory sessionFactory;
	private final ApplicationFormDAO applicationFormDAO;
	private final ApplicantMailSender applicantMailSender;

	public MoveToReviewNotificationTask(SessionFactory sessionFactory, ApplicationFormDAO applicationFormDAO, ApplicantMailSender applicantMailSender) {
		this.sessionFactory = sessionFactory;
		this.applicationFormDAO = applicationFormDAO;
		this.applicantMailSender = applicantMailSender;

	}

	@Override
	public void run() {
		log.info("Application Moved To Review Notification Task Running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<ApplicationForm> applications = applicationFormDAO.getApplicationsDueApplicantReviewNotification();

		transaction.commit();
		for (ApplicationForm application : applications) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(application);
			try {
				applicantMailSender.sendMovedToReviewNotification(application);
				NotificationRecord notificationRecord = application.getNotificationForType(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION);
				if (notificationRecord == null) {
					notificationRecord = new NotificationRecord(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION);
					application.getNotificationRecords().add(notificationRecord);
				}
				notificationRecord.setNotificationDate(new Date());
				applicationFormDAO.save(application);
				transaction.commit();
				log.info("move to review notification send to " + application.getApplicant().getEmail());
			} catch (Throwable e) {
				transaction.rollback();
				log.info("error in move to review notification to " + application.getApplicant().getEmail(), e);
			}

		}
		log.info("Application Moved To Review Notification Task complete");

	}

}
