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
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.StateChangeMailSender;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

public class ApplicantMoveToApprovalNotificationTask extends TimerTask {

    private final Logger log = LoggerFactory.getLogger(ApplicantMoveToApprovalNotificationTask.class);

    private final SessionFactory sessionFactory;
	
    private final ApplicationFormDAO applicationFormDAO;
	
    private final StateChangeMailSender applicantMailSender;
	
    private final String messageCode;
	

    private final EmailTemplateName emailTemplateName;
    
    private final EmailTemplateService emailTemplateService;

    public ApplicantMoveToApprovalNotificationTask(SessionFactory sessionFactory,
            ApplicationFormDAO applicationFormDAO, StateChangeMailSender applicantMailSender,
		String messageCode, EmailTemplateName emailTemplateName, EmailTemplateService emailTemplateService) {
		this.sessionFactory = sessionFactory;
		this.applicationFormDAO = applicationFormDAO;
		this.applicantMailSender = applicantMailSender;

		this.messageCode = messageCode;
		this.emailTemplateName = emailTemplateName;
		this.emailTemplateService = emailTemplateService;
	}

	@Override
	public void run() {
	    log.info("Applicant Move To Approval Notification Task Running");
	    Transaction transaction = null;
	    try {
	        transaction = sessionFactory.getCurrentSession().beginTransaction();
	    	EmailTemplate template = emailTemplateService.getActiveEmailTemplate(emailTemplateName);
    		List<ApplicationForm> applications = applicationFormDAO.getApplicationsDueMovedToApprovalNotifications();
    		transaction.commit();
    		for (ApplicationForm application : applications) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(application);
    			try {
    				applicantMailSender.sendMailsForApplication(application, messageCode, emailTemplateName, template.getContent(), null);
    				NotificationRecord notificationRecord = application.getNotificationForType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION);
    				if (notificationRecord == null) {
    					notificationRecord = new NotificationRecord(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION);
    					application.addNotificationRecord(notificationRecord);
    				}
    				notificationRecord.setDate(new Date());
    				applicationFormDAO.save(application);
    				transaction.commit();
    				log.info("Notification move to approval sent for " + application.getApplicationNumber());
    			} catch (Exception e) {
    			    log.warn("Error in move to approval notification for " + application.getApplicationNumber(), e);
    				transaction.rollback();
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Applicant Move To Approval Notification Task", e);
	        transaction.rollback();
	    }
		log.info("Applicant Move To Approval Notification Task Complete");
	}
}
