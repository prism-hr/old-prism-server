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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.mail.AdminMailSender;

public class AdminRejectNotificationTask extends TimerTask {
	private final Logger log = Logger.getLogger(AdminRejectNotificationTask.class);
	private final SessionFactory sessionFactory;
	private final AdminMailSender adminMailSender;
	private final ApplicationFormDAO applicationDAO;

	public AdminRejectNotificationTask(SessionFactory sessionFactory, AdminMailSender adminMailSender, ApplicationFormDAO applicationDAO) {
		this.sessionFactory = sessionFactory;
		this.adminMailSender = adminMailSender;
		this.applicationDAO = applicationDAO;
	}

	@Override
	public void run() {
		log.info("Admin Reject Notification Task Running");
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueRejectNotifications();
		transaction.commit();

		for (ApplicationForm application : applications) {
			sendRejectNotificationsForApplication(sessionFactory.getCurrentSession(), application);
		}
		log.info("Admin Reject Notification Task complete");
	}

	private void sendRejectNotificationsForApplication(Session session, ApplicationForm application) {
		Transaction transaction = session.beginTransaction();
		session.refresh(application);

		List<RegisteredUser> admins = application.getProgram().getAdministrators();
		RegisteredUser approver = application.getApprover();
		try {
			for (RegisteredUser administrator : admins) {
				if (!administrator.equals(approver)) {
					adminMailSender.sendAdminRejectNotification(administrator, application, approver);
				}
			}
			application.setRejectNotificationDate(new Date());
			applicationDAO.save(application);
			transaction.commit();
			log.info("reject notification sent for application: " + application.getId());
		} catch (Throwable e) {
			transaction.rollback();
			log.info("error in sending reject notification for application: " + application.getId(), e);
		}
	}
}
