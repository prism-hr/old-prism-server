package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
public class MailSendingTask {

    private final Logger log = LoggerFactory.getLogger(MailSendingTask.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;
    
    @Scheduled(cron = "${email.individual.cron}")
    public void sendIndividualMessages() {
        log.trace("Sending task requests");
        // TODO : reimplement

        log.trace("Sending update notifications");
        notificationService.sendPendingUpdateNotifications(PrismNotificationType.INDIVIDUAL);
    }

    @Scheduled(cron = "${email.syndicated.cron}")
    public void sendSyndicatedMessages() {
        log.trace("Sending task requests");
        // TODO : reimplement

        log.trace("Sending update notifications");
        notificationService.sendPendingUpdateNotifications(PrismNotificationType.SYNDICATED);
    }

}
