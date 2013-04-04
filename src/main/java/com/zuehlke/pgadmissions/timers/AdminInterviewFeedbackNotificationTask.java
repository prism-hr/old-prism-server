package com.zuehlke.pgadmissions.timers;

import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.mail.AdminMailSender;
import com.zuehlke.pgadmissions.services.CommentService;

public class AdminInterviewFeedbackNotificationTask extends TimerTask {

    private final Logger log = LoggerFactory.getLogger(AdminInterviewFeedbackNotificationTask.class);
	
	private final CommentService commentService;
	
	private final AdminMailSender adminMailSender;
	
	private final SessionFactory sessionFactory;

	public AdminInterviewFeedbackNotificationTask() {
		this(null, null, null);
	}

	@Autowired
	public AdminInterviewFeedbackNotificationTask(SessionFactory sessionFactory, AdminMailSender adminMailSender, CommentService commentService) {
		this.sessionFactory = sessionFactory;
		this.adminMailSender = adminMailSender;
		this.commentService = commentService;
	}

	@Override
	public void run() {
	    log.info("Interview Comment Notification Task Running");
	    Transaction transaction = null;
	    try {
    		transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<InterviewComment> comments = commentService.getInterviewCommentsDueNotification();
    		transaction.commit();
    		for (InterviewComment comment : comments) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(comment);
    			try {
    				adminMailSender.sendAdminInterviewNotification(comment.getApplication(), comment.getUser());
    				comment.setAdminsNotified(true);
    				commentService.save(comment);
    				transaction.commit();
    				log.info("Notification sent to admins for interview comment " + comment.getId());
    			} catch (Exception e) {
    			    log.warn("Error while sending notification to admins for comment " + comment.getId(), e);
    				transaction.rollback();
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Interview Comment Notification Task", e);
	        transaction.rollback();
	    }
		log.info("Interview Comment Notification Task Complete");
	}
}
