package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.mail.SupervisorMailSender;

public class SupervisorNotificationTask extends TimerTask {
	private final Logger log = Logger.getLogger(SupervisorNotificationTask.class);
	private final SessionFactory sessionFactory;
	private final SupervisorMailSender mailSender;
	private final SupervisorDAO supervisorDAO;

	public SupervisorNotificationTask(SessionFactory sessionFactory, SupervisorMailSender mailSender, SupervisorDAO supervisorDAO) {
		this.sessionFactory = sessionFactory;
		this.mailSender = mailSender;
		this.supervisorDAO = supervisorDAO;
	}

	@Override
	public void run() {
	    log.info("Supervisor Notification Task Running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<Supervisor> supervisorsDueNotification = supervisorDAO.getSupervisorsDueNotification();

		transaction.commit();
		for (Supervisor supervisor : supervisorsDueNotification) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(supervisor);
			try {
				mailSender.sendSupervisorNotification(supervisor);
				supervisor.setLastNotified(new Date());
				supervisorDAO.save(supervisor);
				transaction.commit();
				log.info("Notification sent to supervisor " + supervisor.getUser().getEmail());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("Error while sending notification to supervisor " + supervisor.getUser().getEmail(), e);
			}
		}
		log.info("Supervisor Notification Task Complete");
	}
}
