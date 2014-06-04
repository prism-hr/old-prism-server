package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.mail.NotificationService;
import com.zuehlke.pgadmissions.mail.NotificationDescriptor;
import com.zuehlke.pgadmissions.services.UserService;

@Service
public class MailSendingTimer {

    private final Logger log = LoggerFactory.getLogger(MailSendingTimer.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "${email.digest.cron}")
    public void run() {
        log.trace("Sending task notification to users");
        for (NotificationDescriptor taskNotification : userService.getUsersDueTaskNotification()) {
            notificationService.sendEmailNotification(taskNotification.getRecipient(), taskNotification.getResource(),
                    taskNotification.getNotificationTemplate(), null);
        }

    }


}