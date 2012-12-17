package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.mail.RefereeMailSender;
import com.zuehlke.pgadmissions.services.RefereeService;

public class RefereeNotificationTask extends TimerTask {
	private final Logger log = Logger.getLogger(RefereeNotificationTask.class);
	private final SessionFactory sessionFactory;
	private final RefereeMailSender refereeMailService;
	private final RefereeDAO refereeDAO;
	private final RefereeService refereeService;

	public RefereeNotificationTask(SessionFactory sessionFactory, RefereeMailSender refereeMailService, RefereeDAO refereeDAO, RefereeService refereeService) {
		this.sessionFactory = sessionFactory;
		this.refereeMailService = refereeMailService;
		this.refereeDAO = refereeDAO;
		this.refereeService = refereeService;
	}

	@Override
	public void run() {
	    log.info("Referee Notification Task Running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<Referee> refereesDueNotification = refereeDAO.getRefereesDueNotification();
		refereeService.processRefereesRoles(refereesDueNotification);
		transaction.commit();
		for (Referee referee : refereesDueNotification) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(referee);
			try {				
				refereeMailService.sendRefereeNotification(referee);
				referee.setLastNotified(new Date());
				refereeDAO.save(referee);
				transaction.commit();				
				log.info("Notification sent to referee " +  referee.getEmail());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("Error while sending notification to referee " + referee.getEmail(), e);

			}
		}
		log.info("Referee Notification Task Complete"); 
	}
}
