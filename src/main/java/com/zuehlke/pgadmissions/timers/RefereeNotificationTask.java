package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.RefereeMailService;
import com.zuehlke.pgadmissions.services.RefereeService;

public class RefereeNotificationTask extends TimerTask {
	private final Logger log = Logger.getLogger(RefereeNotificationTask.class);
	private final SessionFactory sessionFactory;
	private final RefereeMailService refereeMailService;
	private final RefereeDAO refereeDAO;
	private final RefereeService refereeService;

	public RefereeNotificationTask(SessionFactory sessionFactory, RefereeMailService refereeMailService, RefereeDAO refereeDAO, RefereeService refereeService) {
		this.sessionFactory = sessionFactory;
		this.refereeMailService = refereeMailService;
		this.refereeDAO = refereeDAO;
		this.refereeService = refereeService;
	}

	@Override
	public void run() {
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
				log.info("notification send to referee " +  referee.getEmail());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("error while sending notification to referee " + referee.getEmail(), e);

			}

		}

	}
}
