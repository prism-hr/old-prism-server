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
import com.zuehlke.pgadmissions.mail.ApplicantMailSender;

public class StateChangeNotificationTask extends TimerTask {
	private final Logger log = Logger.getLogger(StateChangeNotificationTask.class);
	private final SessionFactory sessionFactory;
	private final ApplicationFormDAO applicationFormDAO;
	private final ApplicantMailSender applicantMailSender;
	private final String subjectMessage;
	private final String emailTemplate;
	private final NotificationType notificationType;
	private final ApplicationFormStatus newStatus;

	public StateChangeNotificationTask(SessionFactory sessionFactory, ApplicationFormDAO applicationFormDAO, ApplicantMailSender applicantMailSender,
			NotificationType notificationType, ApplicationFormStatus newStatus, String subjectMessage, String emailTemplate) {
		this.sessionFactory = sessionFactory;
		this.applicationFormDAO = applicationFormDAO;
		this.applicantMailSender = applicantMailSender;
		this.notificationType = notificationType;
		this.newStatus = newStatus;
		this.subjectMessage = subjectMessage;
		this.emailTemplate = emailTemplate;

	}

	@Override
	public void run() {
		log.info(notificationType +  " Notification Task Running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

		List<ApplicationForm> applications = applicationFormDAO.getApplicationsDueNotificationForStateChangeEvent(notificationType, newStatus);

		transaction.commit();
		for (ApplicationForm application : applications) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(application);
			try {

				applicantMailSender.sendMovedToReviewNotification(application, subjectMessage, emailTemplate);
				NotificationRecord notificationRecord = application.getNotificationForType(notificationType);
				if (notificationRecord == null) {
					notificationRecord = new NotificationRecord(notificationType);
					application.getNotificationRecords().add(notificationRecord);
				}
				notificationRecord.setNotificationDate(new Date());
				applicationFormDAO.save(application);
				transaction.commit();
				log.info("move to "+  newStatus + " notification send to " + application.getApplicant().getEmail());
			} catch (Throwable e) {
				transaction.rollback();
				log.info("error in move to  "+  newStatus + " notification to " + application.getApplicant().getEmail(), e);
			}

		}
		log.info(notificationType + " Notification Task complete");

	}

}
