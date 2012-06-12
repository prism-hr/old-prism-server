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

public class InterviewerReminderTask extends TimerTask {
	private final Logger log = Logger.getLogger(InterviewerReminderTask.class);
	private final SessionFactory sessionFactory;
	private final InterviewerMailSender interviewerMailSender;
	private final InterviewerDAO interviewerDAO;

	public InterviewerReminderTask(SessionFactory sessionFactory, InterviewerMailSender interviewerMailSender, InterviewerDAO interviewerDAO) {
		this.sessionFactory = sessionFactory;
		this.interviewerMailSender = interviewerMailSender;
		this.interviewerDAO = interviewerDAO;
	}

	@Override
	public void run() {
		log.info("Interviewer reminder Task Running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<Interviewer> interviewersDuereminder = interviewerDAO.getInterviewersDueReminder();

		transaction.commit();
		
		for (Interviewer interviewer : interviewersDuereminder) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(interviewer);
			try {
				interviewerMailSender.sendInterviewerReminder(interviewer, interviewer.isFirstAdminNotification());
				interviewer.setFirstAdminNotification(false);
				interviewer.setLastNotified(new Date());
				interviewerDAO.save(interviewer);
				transaction.commit();
				log.info("reminder send to interviewer " + interviewer.getUser().getEmail());
			} catch (Throwable e) {
				e.printStackTrace();
				transaction.rollback();
				log.warn("error while sending reminder to interviewer " + interviewer.getUser().getEmail(), e);

			}

		}
		log.info("Interviewer reminder Task complete");

	}

}
