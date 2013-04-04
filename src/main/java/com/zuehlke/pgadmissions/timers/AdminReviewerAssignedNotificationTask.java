package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.mail.AdminMailSender;

public class AdminReviewerAssignedNotificationTask extends TimerTask {
	
    private final Logger log = LoggerFactory.getLogger(AdminReviewerAssignedNotificationTask.class);
	
	private final SessionFactory sessionFactory;
	
	private final AdminMailSender adminMailSender;
	
	private final ReviewerDAO reviewerDAO;

	public AdminReviewerAssignedNotificationTask(SessionFactory sessionFactory, AdminMailSender adminMailSender, ReviewerDAO reviewerDAO) {
		this.sessionFactory = sessionFactory;
		this.adminMailSender = adminMailSender;
		this.reviewerDAO = reviewerDAO;
	}

	@Override
	public void run() {
	    log.info("Assigned Reviewer Notification Task Running");
	    Transaction transaction = null;
	    try {
    	    transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<Reviewer> reviewers = reviewerDAO.getReviewersRequireAdminNotification();
    		transaction.commit();
    		for (Reviewer reviewer : reviewers) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(reviewer);
    			ApplicationForm application = reviewer.getReviewRound().getApplication();
    			try {
    				adminMailSender.sendReviewerAssignedNotification(application, reviewer);
    				reviewer.setDateAdminsNotified(new Date());
    				reviewerDAO.save(reviewer);
    				transaction.commit();
    				log.info("Notification Reviewer assigned sent to admins for reviewer " + reviewer.getUser().getEmail());
    			} catch (Exception e) {
    			    log.warn("Error while sending notification to admins for reviewer " + reviewer.getUser().getEmail(), e);
    				transaction.rollback();
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Assigned Reviewer Notification Task", e);
	        transaction.rollback();
	    }
		log.info("Assigned Reviewer Notification Task Complete");
	}
}
