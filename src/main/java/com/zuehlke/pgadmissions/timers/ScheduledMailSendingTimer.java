package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.mail.ScheduledMailSendingService;

@Service
public class ScheduledMailSendingTimer {

    private final Logger log = LoggerFactory.getLogger(ScheduledMailSendingTimer.class);
    
    private final ScheduledMailSendingService mailService;
    
    public ScheduledMailSendingTimer() {
        this(null);
    }
    
    @Autowired
    public ScheduledMailSendingTimer(final ScheduledMailSendingService mailService) {
        this.mailService = mailService;
    }
    
    @Scheduled(cron = "${email.digest.cron}")
    public void run() {
        log.info("Running ScheduledMailSendingService Task");
        mailService.scheduleApprovalReminder();
        mailService.scheduleInterviewFeedbackEvaluationRequest();
        mailService.scheduleInterviewFeedbackEvaluationReminder();
        mailService.scheduleReviewReminder();
        mailService.scheduleReviewRequest();
        mailService.scheduleUpdateConfirmation();
        mailService.scheduleValidationRequest();
        mailService.scheduleValidationReminder();
        mailService.scheduleRestartApprovalRequest();
        mailService.scheduleRestartApprovalReminder();
        mailService.scheduleApprovedConfirmation();
        mailService.scheduleInterviewAdministrationReminder();
        mailService.scheduleInterviewFeedbackConfirmation();
        mailService.scheduleInterviewFeedbackRequest();
        mailService.scheduleInterviewFeedbackReminder();
        mailService.scheduleApplicationUnderApprovalNotification();
        mailService.scheduleRejectionConfirmationToAdministrator();
        mailService.scheduleReviewSubmittedConfirmation();
        mailService.scheduleReviewEvaluationRequest();
        mailService.scheduleReviewEvaluationReminder();
        mailService.scheduleConfirmSupervisionRequest();
        mailService.scheduleConfirmSupervisionReminder();
        mailService.scheduleApplicationUnderReviewNotification();
        mailService.sendDigestsToUsers();
        log.info("Finished ScheduledMailSendingService Task");
    }
}
