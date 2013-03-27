package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.mail.ReviewerMailSender;

public class ReviewerReminderTask extends TimerTask {
	
    private final Logger log = LoggerFactory.getLogger(ReviewerReminderTask.class);
	
    private final SessionFactory sessionFactory;
	
    private final ReviewerMailSender reviewerMailSender;
	
    private final ReviewerDAO reviewerDAO;

    public ReviewerReminderTask(SessionFactory sessionFactory, ReviewerMailSender reviewerMailSender,
            ReviewerDAO reviewerDAO) {
	this.sessionFactory = sessionFactory;
	
		this.reviewerMailSender = reviewerMailSender;
		this.reviewerDAO = reviewerDAO;
	}

	@Override
	public void run() {
	    log.info("Reviewer Reminder Task Running");
	    Transaction transaction = null;
	    try {
    		transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<Reviewer> reviewersDuereminder = reviewerDAO.getReviewersDueReminder();
    		transaction.commit();
    		for (Reviewer reviewer : reviewersDuereminder) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(reviewer);
    			try {
    				reviewerMailSender.sendReviewerReminder(reviewer);
    				reviewer.setLastNotified(new Date());
    				reviewerDAO.save(reviewer);
    				transaction.commit();
    				log.info("Notification reminder sent to reviewer " + reviewer.getUser().getEmail());
    			} catch (Exception e) {
    			    log.warn("Error while sending reminder to reviewer " + reviewer.getUser().getEmail(), e);
    				transaction.rollback();
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Reviewer Reminder Task", e);
	        transaction.rollback();
	    }
		log.info("Reviewer Reminder Task Complete");
	}
}
