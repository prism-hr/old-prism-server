package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.Referee;
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
        mailService.scheduleApprovalRequestAndReminder();
        mailService.scheduleInterviewFeedbackEvaluationReminder();
        mailService.scheduleReviewRequestAndReminder();
        mailService.scheduleUpdateConfirmation();
        mailService.scheduleValidationRequestAndReminder();
        mailService.scheduleRestartApprovalRequestAndReminder();
        mailService.scheduleApprovedConfirmation();
        mailService.scheduleInterviewAdministrationRequestAndReminder();
        mailService.scheduleInterviewFeedbackConfirmation();
        mailService.scheduleInterviewFeedbackRequestAndReminder();
        mailService.scheduleApplicationUnderApprovalNotification();
        mailService.scheduleRejectionConfirmationToAdministrator();
        mailService.scheduleReviewSubmittedConfirmation();
        mailService.scheduleReviewEvaluationReminder();
        mailService.scheduleConfirmSupervisionRequestAndReminder();
        mailService.scheduleApplicationUnderReviewNotification();
        mailService.sendDigestsToUsers();
        log.info("Finished ScheduledMailSendingService Task");
    }
    
    @Scheduled(cron = "${email.schedule.period.chron}")
    public void sendReferenceReminder() {
        for (Referee referee : mailService.getRefereesDueAReminder()) {
            mailService.sendReferenceReminder(referee);
        }
    }
    
    @Scheduled(cron = "${email.schedule.period.chron}")
    public void sendNewUserInvitation() {
        mailService.sendNewUserInvitation();
    }
    
    @Scheduled(cron = "${email.schedule.period.chron}")
    public void sendValidationRequestToRegistry() {
        mailService.sendValidationRequestToRegistry();
    }
}
