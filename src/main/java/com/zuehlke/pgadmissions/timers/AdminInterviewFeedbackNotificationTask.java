package com.zuehlke.pgadmissions.timers;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.mail.AdminMailSender;
import com.zuehlke.pgadmissions.services.CommentService;

public class AdminInterviewFeedbackNotificationTask extends TimerTask{

	private final Logger log = Logger.getLogger(AdminInterviewFeedbackNotificationTask.class);
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
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<InterviewComment> comments = commentService.getInterviewCommentsDueNotification();
		transaction.commit();
		for (InterviewComment comment : comments) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(comment);
			List<RegisteredUser> admins = comment.getApplication().getProgram().getAdministrators();
			try {	
				for (RegisteredUser admin : admins) {
					adminMailSender.sendAdminInterviewNotification(admin, comment.getApplication(), comment.getUser());
				}
				comment.setAdminsNotified(CheckedStatus.YES);
				commentService.save(comment);
				transaction.commit();				
				log.info("notification sent to admins for interview comment " +  comment.getId());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("error while sending notification to admins for comment " + comment.getId(), e);

			}

		}
		log.info("Interview Comment Task complete");
	}
	
	

}
