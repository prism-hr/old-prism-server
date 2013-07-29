package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
