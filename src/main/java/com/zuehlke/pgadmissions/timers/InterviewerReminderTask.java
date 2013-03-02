package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.mail.InterviewerMailSender;

public class InterviewerReminderTask extends TimerTask {
	
    private final Logger log = LoggerFactory.getLogger(InterviewerReminderTask.class);
	
    private final SessionFactory sessionFactory;
	
    private final InterviewerMailSender interviewerMailSender;
	
    private final InterviewerDAO interviewerDAO;

    public InterviewerReminderTask(SessionFactory sessionFactory, InterviewerMailSender interviewerMailSender,
            InterviewerDAO interviewerDAO) {
		this.sessionFactory = sessionFactory;
		this.interviewerMailSender = interviewerMailSender;
		this.interviewerDAO = interviewerDAO;
	}

	@Override
	public void run() {
	    log.info("Interviewer reminder Task Running");
	    Transaction transaction = null;
	    try {
    		transaction = sessionFactory.getCurrentSession().beginTransaction();
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
    				log.info("Notification Reminder sent to interviewer " + interviewer.getUser().getEmail());
    			} catch (Exception e) {
    			    log.warn("Error while sending reminder to interviewer " + interviewer.getUser().getEmail(), e);
    				transaction.rollback();
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Interviewer reminder Task", e);
	        transaction.rollback();
	    }
		log.info("Interviewer reminder Task Complete");
	}
}
