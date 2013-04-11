package com.zuehlke.pgadmissions.mail.refactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.services.UserService;

@Service
public class ScheduledMailSendingService extends AbstractScheduledMailSendingService {

    private final Logger log = LoggerFactory.getLogger(ScheduledMailSendingService.class);
    
    public ScheduledMailSendingService() {
        this(null, null);
    }
    
    @Autowired
    public ScheduledMailSendingService(final EmailTemplateAwareMailSender mailSender, final UserService userService) {
        super(mailSender, userService);
    }
    
    @Transactional
    @Scheduled(fixedRate = 300000, fixedDelay = 40000)
    public void sendAdminApprovedNotifications() {
        log.info("Admin Approved Notification Task Running");
        
        sendEmail(new PrismEmailMessage());
        
        log.info("Admin Approved Notification Task Complete");
    }
}
