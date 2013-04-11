package com.zuehlke.pgadmissions.timers;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.mail.SupervisorMailSender;

public class SupervisorNotificationTask extends TimerTask {
	
    private final Logger log = LoggerFactory.getLogger(SupervisorNotificationTask.class);
	
    private final SessionFactory sessionFactory;
	
    private final SupervisorMailSender mailSender;
	
    private final SupervisorDAO supervisorDAO;
    

    public SupervisorNotificationTask(final SessionFactory sessionFactory, final SupervisorMailSender mailSender,
            final SupervisorDAO supervisorDAO) {
		this.sessionFactory = sessionFactory;
		this.mailSender = mailSender;
		this.supervisorDAO = supervisorDAO;
	}

	@Override
	public void run() {
	    confirmSupervisionNotificationTask();
	}
	
	private void confirmSupervisionNotificationTask() {
	    log.info("Primary Supervisor Confirmation Notification Task Running");
        Transaction transaction = null;
        try {
            transaction = sessionFactory.getCurrentSession().beginTransaction();
            List<Supervisor> supervisorsDueNotification = supervisorDAO.getPrimarySupervisorsDueNotification();
            transaction.commit();
            for (Supervisor supervisor : supervisorsDueNotification) {
                transaction = sessionFactory.getCurrentSession().beginTransaction();
                sessionFactory.getCurrentSession().refresh(supervisor);
                try {
                	ApplicationForm form = supervisor.getApprovalRound().getApplication();
					mailSender.sendPrimarySupervisorConfirmationNotificationAndCopyAdmins(supervisor, form.getProgram().getAdministrators());
                    supervisor.setLastNotified(new Date());
                    supervisorDAO.save(supervisor);
                    transaction.commit();
                    log.info("Notification sent to supervisor " + supervisor.getUser().getEmail());
                } catch (Exception e) {
                    log.warn("Error while sending notification to supervisor " + supervisor.getUser().getEmail(), e);
                    transaction.rollback();
                }
            }
        } catch (Exception e) {
            log.warn("Error in executing Primary Supervisor Confirmation Notification Task", e);
            transaction.rollback();
        }
        log.info("Primary Supervisor Confirmation Notification Task Complete");
	}
	
	/**
     * @deprecated This method is now being replaced by the {@link #confirmSupervisionNotificationTask() confirmSupervisionNotificationTask} method.
     */
    @Deprecated
    @SuppressWarnings("unused")
    private void supervisorNotificationTask() {
        log.info("Supervisor Notification Task Running");
        Transaction transaction = null;
        try {
            transaction = sessionFactory.getCurrentSession().beginTransaction();
            List<Supervisor> supervisorsDueNotification = supervisorDAO.getSupervisorsDueNotification();
            transaction.commit();
            for (Supervisor supervisor : supervisorsDueNotification) {
                transaction = sessionFactory.getCurrentSession().beginTransaction();
                sessionFactory.getCurrentSession().refresh(supervisor);
                try {
                    mailSender.sendSupervisorNotification(supervisor);
                    supervisor.setLastNotified(new Date());
                    supervisorDAO.save(supervisor);
                    transaction.commit();
                    log.info("Notification sent to supervisor " + supervisor.getUser().getEmail());
                } catch (Exception e) {
                    log.warn("Error while sending notification to supervisor " + supervisor.getUser().getEmail(), e);
                    transaction.rollback();
                }
            }
        } catch (Exception e) {
            log.warn("Error in executing Supervisor Notification Task", e);
            transaction.rollback();
        }
        log.info("Supervisor Notification Task Complete");
    }
}
