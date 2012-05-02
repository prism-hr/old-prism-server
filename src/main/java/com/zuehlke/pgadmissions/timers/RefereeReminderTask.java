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

public class RefereeReminderTask extends TimerTask {
	private final Logger log = Logger.getLogger(RefereeReminderTask.class);
	
	private final SessionFactory sessionFactory;	
	private final RefereeDAO refereeDAO;
	private final RefereeMailService mailService;

	

	
	public RefereeReminderTask(SessionFactory sessionFactory, RefereeMailService mailService, RefereeDAO refereeDAO) {
		this.sessionFactory = sessionFactory;
		this.mailService = mailService;
		this.refereeDAO = refereeDAO;	
	}

	@Override
	public void run() {
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<Referee> refereesDueAReminder = refereeDAO.getRefereesDueAReminder();
		transaction.commit();
		for (Referee referee : refereesDueAReminder) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(referee);
			try {				
				mailService.sendRefereeReminder(referee);
				referee.setLastNotified(new Date());
				refereeDAO.save(referee);
				transaction.commit();				
				log.info("reminder send to referee " +  referee.getEmail());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("error while sending reminder to referee " + referee.getEmail(), e);

			}

		}

	}

}
