package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.ApprovalRestartRequestMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;



public class ApprovalRestartRequestNotificationTimerTask extends TimerTask {
	private final Logger log = Logger.getLogger(ApprovalRestartRequestNotificationTimerTask.class);
	private final ApprovalRestartRequestMailSender mailSender;
	private final SessionFactory sessionFactory;
	private final ApplicationsService applicationsService;

	public ApprovalRestartRequestNotificationTimerTask(SessionFactory sessionFactory, ApprovalRestartRequestMailSender mailSender,
			ApplicationsService applicationsService) {
				this.sessionFactory = sessionFactory;
				this.mailSender = mailSender;
				this.applicationsService = applicationsService;
	}

	@Override
	public void run() {
	    log.info("Approval Restart Request Notification Timer Task Running");
	    try {
    		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<ApplicationForm> applications = applicationsService.getApplicationsDueApprovalRestartRequestNotification();
    		transaction.commit();
    		for (ApplicationForm application : applications) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(application);
    			try {
    				mailSender.sendRequestRestartApproval(application);
    				NotificationRecord notificationRecord = new NotificationRecord();
    				notificationRecord.setNotificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION);
    				notificationRecord.setDate(new Date());
    				application.addNotificationRecord(notificationRecord);
    				applicationsService.save(application);
    				transaction.commit();
    				log.info("Notification approval restart request sent for " + application.getApplicationNumber());
    			} catch (Exception e) {
    			    transaction.rollback();
    				log.warn("Error in sending Approval restart request notification for " + application.getApplicationNumber(), e);
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Approval Restart Request Notification Timer Task", e);	    
	    }
		log.info("Approval Restart Request Notification Timer Task Complete");
	}
}
