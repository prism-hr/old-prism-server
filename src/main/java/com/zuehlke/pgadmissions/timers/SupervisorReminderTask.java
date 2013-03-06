package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.mail.SupervisorMailSender;

public class SupervisorReminderTask extends TimerTask {
    
    private final Logger log = LoggerFactory.getLogger(SupervisorReminderTask.class);
    
    private final SessionFactory sessionFactory;
    
    private final SupervisorMailSender mailSender;
    
    private final SupervisorDAO supervisorDAO;

    public SupervisorReminderTask(final SessionFactory sessionFactory, final SupervisorMailSender mailSender,
            final SupervisorDAO supervisorDAO) {
        this.sessionFactory = sessionFactory;
        this.mailSender = mailSender;
        this.supervisorDAO = supervisorDAO;
    }

    @Override
    public void run() {
        confirmSupervisionReminderNotificationTask();
    }
    
    private void confirmSupervisionReminderNotificationTask() {
        log.info("Primary Supervisor Confirmation Notification Reminder Task Running");
        Transaction transaction = null;
        try {
            transaction = sessionFactory.getCurrentSession().beginTransaction();
            List<Supervisor> supervisorsDueNotification = supervisorDAO.getPrimarySupervisorsDueReminder();
            transaction.commit();
            for (Supervisor supervisor : supervisorsDueNotification) {
                transaction = sessionFactory.getCurrentSession().beginTransaction();
                sessionFactory.getCurrentSession().refresh(supervisor);
                try {
                    mailSender.sendPrimarySupervisorConfirmationNotificationReminder(supervisor);
                    supervisor.setLastNotified(new Date());
                    supervisorDAO.save(supervisor);
                    transaction.commit();
                    log.info("Reminder notification sent to supervisor " + supervisor.getUser().getEmail());
                } catch (Exception e) {
                    log.warn("Error while sending reminder notification to supervisor " + supervisor.getUser().getEmail(), e);
                    transaction.rollback();
                }
            }
        } catch (Exception e) {
            log.warn("Error in executing Primary Supervisor Confirmation Reminder Notification Task", e);
            transaction.rollback();
        }
        log.info("Primary Supervisor Confirmation Notification Reminder Task Complete");
    }
}
