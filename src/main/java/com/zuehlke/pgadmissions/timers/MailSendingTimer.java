package com.zuehlke.pgadmissions.timers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.mail.ScheduledMailSendingService;

@Service
public class MailSendingTimer {

    private final ScheduledMailSendingService mailService;

    public MailSendingTimer() {
        this(null);
    }

    @Autowired
    public MailSendingTimer(final ScheduledMailSendingService mailService) {
        this.mailService = mailService;
    }

    @Scheduled(cron = "${email.digest.cron}")
    public void run() {
        mailService.sendDigestsToUsers();
        mailService.sendReferenceReminder();
        mailService.sendInterviewParticipantVoteReminder();
    }

    @Scheduled(cron = "${email.schedule.period.cron}")
    public void sendNewUserInvitation() {
        mailService.sendNewUserInvitation();
    }
}