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
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.AdminMailSender;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

public class RegisteredUserReminderTimerTask extends TimerTask {
    
    private final Logger log = LoggerFactory.getLogger(RegisteredUserReminderTimerTask.class);
	
    private final SessionFactory sessionFactory;
	
    private final ApplicationFormDAO applicationFormDAO;
	
    private final AdminMailSender adminMailSender;
	
    private final NotificationType notificationType;
	
    private final ApplicationFormStatus status;
	
    private final String subjectCode;
	
	
    private final String firstSubjectCode;
	
    
    private final EmailTemplateService emailTemplateService;
    
    private final EmailTemplateName firstEmailTemplateName;
  
    private final EmailTemplateName emailTemplateName;

	public RegisteredUserReminderTimerTask(SessionFactory sessionFactory, ApplicationFormDAO applicationFormDAO, AdminMailSender adminMailSender,
			NotificationType notificationType, ApplicationFormStatus status, String fisrtSubjectCode, EmailTemplateName firstEmailTemplateName,
			String subjectCode, EmailTemplateName emailTemplateName, EmailTemplateService emailTemplateService) {
				this.sessionFactory = sessionFactory;
				this.applicationFormDAO = applicationFormDAO;
				this.adminMailSender = adminMailSender;
				this.notificationType = notificationType;
				this.status = status;
				this.firstSubjectCode = fisrtSubjectCode;
				this.firstEmailTemplateName = firstEmailTemplateName;
				this.subjectCode = subjectCode;
				this.emailTemplateName = emailTemplateName;
				this.emailTemplateService = emailTemplateService;
	}

	@Override
	public void run() {
	    log.info(notificationType +  " Reminder Task Running");
	    Transaction transaction = null;
	    try {
	    	EmailTemplate firstTemplate = emailTemplateService.getActiveEmailTemplate(firstEmailTemplateName);
	    	EmailTemplate template = emailTemplateService.getActiveEmailTemplate(emailTemplateName);
    		transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<ApplicationForm> applications = applicationFormDAO.getApplicationsDueUserReminder(notificationType, status);
    		transaction.commit();
    		for (ApplicationForm application : applications) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(application);
    			try {
    				NotificationRecord notificationRecord = application.getNotificationForType(notificationType);
    				
    				String useSubjectCode = subjectCode;
    				EmailTemplate useEmailTemplate = template;
    				EmailTemplateName templateName = this.emailTemplateName;
    				if (notificationRecord == null) {
    					notificationRecord = new NotificationRecord(notificationType);
    					application.addNotificationRecord(notificationRecord);
    					useSubjectCode = firstSubjectCode;
    					useEmailTemplate = firstTemplate;
    					templateName = firstEmailTemplateName;
    				}
    				adminMailSender.sendMailsForApplication(application, useSubjectCode, templateName, useEmailTemplate.getContent(), notificationType);
    				notificationRecord.setDate(new Date());
    				applicationFormDAO.save(application);
    				transaction.commit();
    				log.info("Notification " + status + " reminders sent to " + application.getId());
    			} catch (Exception e) {
    				log.warn("Error in sending " + status + " reminders for " + application.getId(), e);
    				transaction.rollback();
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing " + notificationType +  " Reminder Task", e);
	        transaction.rollback();
	    }
		log.info(notificationType +  " Reminder Task Complete");
	}
}
