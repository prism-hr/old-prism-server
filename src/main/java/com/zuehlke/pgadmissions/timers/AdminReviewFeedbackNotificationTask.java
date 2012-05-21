package com.zuehlke.pgadmissions.timers;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.mail.AdminMailSender;
import com.zuehlke.pgadmissions.services.CommentService;

public class AdminReviewFeedbackNotificationTask extends TimerTask{

	private final Logger log = Logger.getLogger(AdminReviewFeedbackNotificationTask.class);
	private final CommentService commentService;
	private final AdminMailSender adminMailSender;
	private final SessionFactory sessionFactory;

	public AdminReviewFeedbackNotificationTask() {
		this(null, null, null);
	}
	
	@Autowired
	public AdminReviewFeedbackNotificationTask(SessionFactory sessionFactory, AdminMailSender adminMailSender, CommentService commentService) {
			this.sessionFactory = sessionFactory;
			this.adminMailSender = adminMailSender;
			this.commentService = commentService;
	}

	@Override
	public void run() {
		log.info("Review Comment Notification Task Running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<ReviewComment> comments = commentService.getReviewCommentsDueNotification();
		transaction.commit();
		for (ReviewComment comment : comments) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(comment);
			List<RegisteredUser> admins = comment.getApplication().getProgram().getAdministrators();
			try {	
				for (RegisteredUser admin : admins) {
					adminMailSender.sendAdminReviewNotification(admin, comment.getApplication(), comment.getUser());
				}
				comment.setAdminsNotified(CheckedStatus.YES);
				commentService.save(comment);
				transaction.commit();				
				log.info("notification sent to admins for review comment " +  comment.getId());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("error while sending notification to admins for comment " + comment.getId(), e);

			}

		}
		log.info("Review Comment Task complete");
	}
	
	

}
