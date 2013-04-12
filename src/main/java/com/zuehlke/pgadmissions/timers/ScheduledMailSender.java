package com.zuehlke.pgadmissions.timers;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEW_ADMINISTRATION_REMINDER;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.NotificationRecordDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.utils.Environment;

@Service
@Transactional
public class ScheduledMailSender extends MailSender {

	private final Logger log = LoggerFactory.getLogger(ScheduledMailSender.class);
	
	private final NotificationRecordDAO notificationRecordDAO;
	
	
	public ScheduledMailSender() {
		this(null, null, null, null, null);
	}
	
	@Autowired
    public ScheduledMailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender,
			MessageSource messageSource, EmailTemplateService emailTemplateService, NotificationRecordDAO notificationRecordDAO) {
    	
		super(mimeMessagePreparatorFactory, mailSender, messageSource, emailTemplateService);
		this.notificationRecordDAO = notificationRecordDAO;
	}


	/**
	 * Every day at 12 sends interview administration reminders to interview administrators
	 * for all those applications whose interview administration has been delegated
	 */
    @Scheduled(cron = "0 0 12 * * ?")
    public void sendInterviewReminderNotifications() {
    	log.info("Running \"sendInterviewReminderNotifications\" task");
    	
    	Date timestamp = new Date();
    	DateTime from = new DateTime(timestamp).minusDays(1);
    	
		List<NotificationRecord> notifications = notificationRecordDAO.getNotificationsWithTimeStampGreaterThan(from.toDate() , NotificationType.INTERVIEW_ADMINISTRATION_REMINDER);
    	for (NotificationRecord notification : notifications) {
    		ApplicationForm applicationForm = notification.getApplication();
    		RegisteredUser delegate = notification.getUser();
	    	 try {
	             Map<String, Object> model = new HashMap<String, Object>();
	             model.put("user", delegate);
	             model.put("applicationForm", applicationForm);
	             model.put("host", Environment.getInstance().getApplicationHostName());
	             InternetAddress toAddress = createUserAddress(delegate);
	             String subject = getMessage("application.interview.delegation", null, null);
	             EmailTemplate template = getDefaultEmailtemplate(INTERVIEW_ADMINISTRATION_REMINDER);
	
	             MimeMessagePreparator messagePreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,//
	              		INTERVIEW_ADMINISTRATION_REMINDER, template.getContent(), model, null);
	             javaMailSender.send(messagePreparator);
	             notification.setDate(timestamp);
	
	         } catch (Exception e) {
	             log.warn("error while sending email", e);
	         }
    	}
    }
    
    private InternetAddress createUserAddress(RegisteredUser user) {
        try {
            return new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());
        } catch (UnsupportedEncodingException e) { // this shouldn't happen...
            throw new RuntimeException(e);
        }
    }
}
