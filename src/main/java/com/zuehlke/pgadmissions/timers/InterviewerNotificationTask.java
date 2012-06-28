package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.mail.InterviewerMailSender;

public class InterviewerNotificationTask extends TimerTask {
	private final Logger log = Logger.getLogger(InterviewerNotificationTask.class);
	private final SessionFactory sessionFactory;
	private final InterviewerMailSender interviewerMailSender;
	private final InterviewerDAO interviewerDAO;

	public InterviewerNotificationTask(SessionFactory sessionFactory, InterviewerMailSender interviewerMailSender, InterviewerDAO interviewerDAO) {
		this.sessionFactory = sessionFactory;
		this.interviewerMailSender = interviewerMailSender;
		this.interviewerDAO = interviewerDAO;
	}

	@Override
	public void run() {
		log.info("Interviewer Notification Task Running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<Interviewer> interviewersDueNotification = interviewerDAO.getInterviewersDueNotification();

		transaction.commit();
		for (Interviewer interviewer : interviewersDueNotification) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(interviewer);
			try {
				interviewerMailSender.sendInterviewerNotification(interviewer);
				interviewer.setLastNotified(new Date());
				interviewerDAO.save(interviewer);
				transaction.commit();
				log.info("notification send to interviewer " + interviewer.getUser().getEmail());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("error while sending notification to interviewer " + interviewer.getUser().getEmail(), e);

			}

		}
		log.info("Interviewer Notification Task complete");

	}

}
