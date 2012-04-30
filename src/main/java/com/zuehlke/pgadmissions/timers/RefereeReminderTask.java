package com.zuehlke.pgadmissions.timers;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.MailService;

public class RefereeReminderTask extends TimerTask {

	private final SessionFactory sessionFactory;

	private final Logger log = Logger.getLogger(RefereeReminderTask.class);

	private final MailService mailService;

	public RefereeReminderTask(SessionFactory sessionFactory, MailService mailService) {
		this.sessionFactory = sessionFactory;
		this.mailService = mailService;

	}

	@Override
	public void run() {
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<Referee> refereesDueAReminder = mailService.getRefereesDueAReminder();
		transaction.commit();
		for (Referee referee : refereesDueAReminder) {
			transaction =sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(referee);
			try {				
				mailService.sendRefereeReminderAndUpdateLastNotified(referee);
				transaction.commit();				
				log.info("reminder send to referee " +  referee.getEmail());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("error while sending email", e);

			}

		}

	}

}
