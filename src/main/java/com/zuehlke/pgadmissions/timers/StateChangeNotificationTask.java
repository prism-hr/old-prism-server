package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.StateChangeMailSender;

public class StateChangeNotificationTask extends TimerTask {
    
    private final Logger log = LoggerFactory.getLogger(StateChangeNotificationTask.class);
    
	private final SessionFactory sessionFactory;
	
	private final ApplicationFormDAO applicationFormDAO;
	
	private final StateChangeMailSender applicantMailSender;
	
	private final String messageCode;
	
	private final String emailTemplate;
	
	private final NotificationType notificationType;
	
	private final ApplicationFormStatus newStatus;

    public StateChangeNotificationTask(SessionFactory sessionFactory, ApplicationFormDAO applicationFormDAO,
            StateChangeMailSender applicantMailSender, NotificationType notificationType,
            ApplicationFormStatus newStatus, String messageCode, String emailTemplate) {
		this.sessionFactory = sessionFactory;
		this.applicationFormDAO = applicationFormDAO;
		this.applicantMailSender = applicantMailSender;
		this.notificationType = notificationType;
		this.newStatus = newStatus;
		this.messageCode = messageCode;
		this.emailTemplate = emailTemplate;
	}

	@Override
	public void run() {
	    log.info(notificationType +  " Notification Task Running");
	    Transaction transaction = null;
	    try {
    		transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<ApplicationForm> applications = applicationFormDAO.getApplicationsDueNotificationForStateChangeEvent(notificationType, newStatus);
    		transaction.commit();
    		for (ApplicationForm application : applications) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(application);
    			try {
    				applicantMailSender.sendMailsForApplication(application, messageCode, emailTemplate, null);
    				NotificationRecord notificationRecord = application.getNotificationForType(notificationType);
    				if (notificationRecord == null) {
    					notificationRecord = new NotificationRecord(notificationType);
    					application.addNotificationRecord(notificationRecord);
    				}
    				notificationRecord.setDate(new Date());
    				applicationFormDAO.save(application);
    				transaction.commit();
    				log.info("Notification move to " + newStatus + " notification sent for " + application.getId());
    			} catch (Exception e) {
    			    log.warn("Error in move to  "+  newStatus + " notification for " + application.getId(), e);
    				transaction.rollback();
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing " + notificationType +  " Notification Task", e);
	        transaction.rollback();
	    }
		log.info(notificationType + " Notification Task Complete");
	}
}
