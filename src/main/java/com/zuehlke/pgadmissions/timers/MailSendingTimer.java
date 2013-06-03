package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.mail.ScheduledMailSendingService;

@Service
public class MailSendingTimer {

    private final Logger log = LoggerFactory.getLogger(MailSendingTimer.class);
    
    private final ScheduledMailSendingService mailService;
    
    public MailSendingTimer() {
        this(null);
    }
    
    @Autowired
    public MailSendingTimer(final ScheduledMailSendingService mailService) {
        this.mailService = mailService;
    }
    
    @Scheduled(cron = "${email.reminder.digest.cron}")
    public void scheduleTaskReminders() {
        log.info("Running scheduleTaskReminders Task");
        mailService.scheduleApprovalReminder();
        mailService.scheduleInterviewFeedbackEvaluationReminder();
        mailService.scheduleReviewReminder();
        mailService.scheduleValidationReminder();
        mailService.scheduleRestartApprovalReminder();
        mailService.scheduleInterviewAdministrationReminder();
        mailService.scheduleRegistryRevalidationReminder();
        mailService.scheduleInterviewFeedbackReminder();
        mailService.scheduleReviewEvaluationReminder();
        mailService.scheduleConfirmSupervisionReminder();
        mailService.sendDigestsToUsers();
        log.info("Finished scheduleTaskReminders Task");
    }
    
    //@Scheduled(cron = "${email.notification.digest.cron}")
    public void scheduleTaskNotifications() {
        log.info("Running scheduleTaskNotifications Task");
        mailService.scheduleApprovalRequest();
        mailService.scheduleReviewRequest();
        mailService.scheduleValidationRequest();
        mailService.scheduleRestartApprovalRequest();
        mailService.scheduleInterviewAdministrationRequest();
        mailService.scheduleRegistryRevalidationRequest();
        mailService.scheduleInterviewFeedbackRequest();
        mailService.scheduleConfirmSupervisionRequest();
        mailService.sendDigestsToUsers();
        log.info("Finished scheduleTaskNotifications Task");
    }
    
    //@Scheduled(cron = "${email.update.digest.cron}")
    public void scheduleUpdateNotifications() {
        log.info("Running scheduleUpdateNotifications Task");
        mailService.scheduleUpdateConfirmation();
        mailService.scheduleApprovedConfirmation();
        mailService.scheduleInterviewFeedbackConfirmation();
        mailService.scheduleApplicationUnderApprovalNotification();
        mailService.scheduleRejectionConfirmationToAdministratorsAndSupervisor();
        mailService.scheduleReviewSubmittedConfirmation();
        mailService.scheduleApplicationUnderReviewNotification();
        mailService.scheduleApplicationUnderInterviewNotification();
        mailService.sendDigestsToUsers();
        log.info("Finished scheduleUpdateNotifications Task");
    }
    
    @Scheduled(cron = "${email.schedule.period.chron}")
    public void sendReferenceReminder() {
        mailService.sendReferenceReminder();
    }
    
    @Scheduled(cron = "${email.schedule.period.chron}")
    public void sendInterviewParticipantVoteReminder() {
        mailService.sendInterviewParticipantVoteReminder();
    }
    
    @Scheduled(cron = "${email.schedule.period.chron}")
    public void sendNewUserInvitation() {
        mailService.sendNewUserInvitation();
    }
}
