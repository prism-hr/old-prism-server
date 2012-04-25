package com.zuehlke.pgadmissions.timers;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.RefereeService;

public class RefereeReminderTask extends TimerTask {

	private final SessionFactory sessionFactory;
	private final RefereeService refereeService;
	private final Logger log = Logger.getLogger(RefereeReminderTask.class);
	
	public RefereeReminderTask(SessionFactory sessionFactory, RefereeService refereeService) {
		this.sessionFactory = sessionFactory;
		this.refereeService = refereeService;

	}

	@Override
	public void run() {
		
		List<Referee> refereesDueAReminder = refereeService.getRefereesDueAReminder();
		for (Referee referee : refereesDueAReminder) {
			Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
			try{
				refereeService.sendReminderAndUpdateLastNotified(referee);
				transaction.commit();
			}catch (Throwable e) {
				transaction.rollback();
				log.warn("error while sending email", e);
				
			}

		}		

	}

}
