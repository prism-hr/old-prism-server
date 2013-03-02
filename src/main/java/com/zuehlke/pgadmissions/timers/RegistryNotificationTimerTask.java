package com.zuehlke.pgadmissions.timers;

import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.mail.RegistryMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.CommentFactory;

public class RegistryNotificationTimerTask extends TimerTask {

    private final Logger log = LoggerFactory.getLogger(RegistryNotificationTimerTask.class);
    
	private final RegistryMailSender registryMailSender;
	
	private final SessionFactory sessionFactory;
	
	private final ApplicationsService applicationsService;
	
	private final ConfigurationService configurationService;
	
	private final CommentFactory commentFactory;
	
	private final CommentService commentService;

	public RegistryNotificationTimerTask() {
		this(null, null, null, null, null, null);
	}

	public RegistryNotificationTimerTask(SessionFactory sessionFactory, RegistryMailSender registryMailSender, ApplicationsService applicationsService,
			ConfigurationService configurationService, CommentFactory commentFactory, CommentService commentService) {
		this.sessionFactory = sessionFactory;
		this.registryMailSender = registryMailSender;
		this.applicationsService = applicationsService;
		this.configurationService = configurationService;
		this.commentFactory = commentFactory;
		this.commentService = commentService;
	}

	@Override
	public void run() {
	    log.info("Registry Notification Task Running");
	    Transaction transaction = null;
	    try {
    		transaction = sessionFactory.getCurrentSession().beginTransaction();
    		List<ApplicationForm> applications = applicationsService.getApplicationsDueRegistryNotification();
    		List<Person> registryContacts = configurationService.getAllRegistryUsers();
    		transaction.commit();
    		for (ApplicationForm applicationForm : applications) {
    			transaction = sessionFactory.getCurrentSession().beginTransaction();
    			sessionFactory.getCurrentSession().refresh(applicationForm);
    			try {
    				registryMailSender.sendApplicationToRegistryContacts(applicationForm, registryContacts);
    				applicationForm.setRegistryUsersDueNotification(false);
    				Comment comment = commentFactory.createComment(applicationForm, applicationForm.getAdminRequestedRegistry(), getCommentText(registryContacts), CommentType.GENERIC, null);
    				commentService.save(comment);
    				applicationsService.save(applicationForm);
    				transaction.commit();
    				log.info("Notification sent to registry persons for application " + applicationForm.getApplicationNumber());
    			} catch (Exception e) {
    			    log.warn("Error while sending notification to registry persons for application " + applicationForm.getApplicationNumber(), e);
    				transaction.rollback();
    			}
    		}
	    } catch (Exception e) {
	        log.warn("Error in executing Registry Notification Task", e);
	        transaction.rollback();
	    }
		log.info("Registry Notification Task Complete");
	}

	private String getCommentText(List<Person> registryContacts) {
		StringBuilder sb = new StringBuilder();
		sb.append("Referred to UCL Admissions for advice on eligibility and fees status. Referral send to ");
		for (int i = 0; i < registryContacts.size(); i++) {
			Person contact = registryContacts.get(i);
			if (i > 0 && i < registryContacts.size() - 1) {
				sb.append(", ");
			}
			if (registryContacts.size() > 1 && i == (registryContacts.size() - 1)) {
				sb.append(" and ");
			}
			sb.append(contact.getFirstname() + " " + contact.getLastname() + " (" + contact.getEmail() + ")");
		}
		sb.append(".");
		return sb.toString();
	}
}