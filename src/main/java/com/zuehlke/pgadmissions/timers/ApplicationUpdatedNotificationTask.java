package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.MailService;

public class ApplicationUpdatedNotificationTask extends TimerTask {

    private final Logger log = Logger.getLogger(ApplicationUpdatedNotificationTask.class);

    private final MailService mailService;
	private final ApplicationFormDAO applicationDAO;
	private final SessionFactory sessionFactory;

	public ApplicationUpdatedNotificationTask(SessionFactory sessionFactory, MailService mailService, ApplicationFormDAO applicationFormDAO) {
		this.sessionFactory = sessionFactory;
		this.mailService = mailService;
		this.applicationDAO = applicationFormDAO;
	}

	@Override
	public void run() {
	    log.info("Application Update Notification Task Running");
	    try {
    		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<ApplicationForm> applicationsDueUpdateNotification = applicationDAO.getApplicationsDueUpdateNotification();
    		transaction.commit();
    		for (ApplicationForm applicationForm : applicationsDueUpdateNotification) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(applicationForm);
    			try {
    				mailService.sendApplicationUpdatedMailToAdmins(applicationForm);
    				NotificationRecord notificationRecord = applicationForm.getNotificationForType(NotificationType.UPDATED_NOTIFICATION);			
    			    if (notificationRecord == null) {
    			        notificationRecord = new NotificationRecord(NotificationType.UPDATED_NOTIFICATION);
    			        applicationForm.addNotificationRecord(notificationRecord);
    			    }
    			    notificationRecord.setDate(new Date());
    			    applicationDAO.save(applicationForm);			
    				transaction.commit();
    				log.info("Notification update sent for " + applicationForm.getId());
    			} catch (Exception e) {
    				transaction.rollback();
    				log.warn("Error while sending email", e);
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Application Update Notification Task", e);
	    }
		log.info("Application Update Notification Task Complete");
	}
}
