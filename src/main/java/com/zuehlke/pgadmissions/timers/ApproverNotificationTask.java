package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.ApproverMailSender;

public class ApproverNotificationTask extends TimerTask {
	private final Logger log = Logger.getLogger(ApproverNotificationTask.class);
	private final SessionFactory sessionFactory;
	private final ApproverMailSender approverMailSender;
	private final ApplicationFormDAO applicationDAO;

	public ApproverNotificationTask(SessionFactory sessionFactory, ApproverMailSender approverMailSender, ApplicationFormDAO applicationDAO) {
		this.sessionFactory = sessionFactory;
		this.approverMailSender = approverMailSender;
		this.applicationDAO = applicationDAO;
	}

	@Override
	public void run() {
		log.info("Application In Approval Notification To Approvers Task Running");
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueApprovalNotifications();
		
		transaction.commit();

		for (ApplicationForm application : applications) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(application);
			try {
				approverMailSender.sendApprovalNotificationToApprovers(application);
				NotificationRecord notificationRecord = application.getNotificationForType(NotificationType.APPROVAL_NOTIFICATION);
				if (notificationRecord == null) {
					notificationRecord = new NotificationRecord(NotificationType.APPROVAL_NOTIFICATION);
					application.getNotificationRecords().add(notificationRecord);
				}
				notificationRecord.setDate(new Date());
				applicationDAO.save(application);
				transaction.commit();
				log.info("Application In Approval notifications send to approvers for " + application.getId());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("error while sending email", e);

			}
		}
		log.info("Application In Approval Notification To Approvers Task  complete");
	}
}
