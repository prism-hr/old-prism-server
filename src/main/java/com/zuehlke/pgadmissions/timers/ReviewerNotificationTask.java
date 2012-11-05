package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.mail.ReviewerMailSender;

public class ReviewerNotificationTask extends TimerTask {
	private final Logger log = Logger.getLogger(ReviewerNotificationTask.class);
	private final SessionFactory sessionFactory;
	private final ReviewerMailSender reviewerMailSender;
	private final ReviewerDAO reviewerDAO;

	public ReviewerNotificationTask(SessionFactory sessionFactory, ReviewerMailSender reviewerMailSender, ReviewerDAO reviewerDAO) {
		this.sessionFactory = sessionFactory;
		this.reviewerMailSender = reviewerMailSender;
		this.reviewerDAO = reviewerDAO;
	}

	@Override
	public void run() {
	    if (log.isDebugEnabled()) { log.debug("Reviewer Notification Task Running"); }
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<Reviewer> reviewersDueNotification = reviewerDAO.getReviewersDueNotification();

		transaction.commit();
		for (Reviewer reviewer : reviewersDueNotification) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(reviewer);
			try {
				reviewerMailSender.sendReviewerNotification(reviewer);
				reviewer.setLastNotified(new Date());
				reviewerDAO.save(reviewer);
				transaction.commit();
				log.info("Notification sent to reviewer " + reviewer.getUser().getEmail());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("Error while sending notification to reviewer " + reviewer.getUser().getEmail(), e);
			}
		}
		if (log.isDebugEnabled()) { log.debug("Reviewer Notification Task Complete"); }
	}
}
