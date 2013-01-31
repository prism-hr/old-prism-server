package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.AdminMailSender;

public class AdminApprovedNotificationTask extends TimerTask {

	private final Logger log = Logger.getLogger(AdminInterviewFeedbackNotificationTask.class);
	private final AdminMailSender adminMailSender;
	private final SessionFactory sessionFactory;
	private final ApplicationFormDAO applicationDAO;

	public AdminApprovedNotificationTask() {
		this(null, null, null);
	}

	@Autowired
	public AdminApprovedNotificationTask(SessionFactory sessionFactory, AdminMailSender adminMailSender, ApplicationFormDAO applicationDAO) {
		this.sessionFactory = sessionFactory;
		this.adminMailSender = adminMailSender;
		this.applicationDAO = applicationDAO;
	}

	@Override
	public void run() {
	    log.info("Admin Approved Notification Task Running");
	    try {
    		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<ApplicationForm> applications = applicationDAO.getApplicationsDueApprovedNotifications();
    		transaction.commit();
    		for (ApplicationForm application : applications) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(application);
    			RegisteredUser approver = application.getApprover();
    			try {
    				adminMailSender.sendAdminAndSupervisorApprovedNotification(application, approver);
    				NotificationRecord notificationRecord = application.getNotificationForType(NotificationType.APPROVED_NOTIFICATION);
    				if (notificationRecord == null) {
    					notificationRecord = new NotificationRecord(NotificationType.APPROVED_NOTIFICATION);
    					application.addNotificationRecord(notificationRecord);
    				}
    				notificationRecord.setDate(new Date());
    				applicationDAO.save(application);
    				transaction.commit();
    				log.info("Notification approved sent for application: " + application.getApplicationNumber());
    			} catch (Exception e) {
    				transaction.rollback();
    				log.warn("Error in sending approved notification for application: " + application.getApplicationNumber(), e);
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Admin Approved Notification Task", e);
	    }
		log.info("Admin Approved Notification Task Complete");
	}
}
