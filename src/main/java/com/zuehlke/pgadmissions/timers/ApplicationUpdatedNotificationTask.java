package com.zuehlke.pgadmissions.timers;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.MailService;

public class ApplicationUpdatedNotificationTask extends TimerTask {

	private final MailService mailService;
	private final ApplicationsService applicationsService;
	private final SessionFactory sessionFactory;

	private final Logger log = Logger.getLogger(ApplicationUpdatedNotificationTask.class);

	public ApplicationUpdatedNotificationTask(SessionFactory sessionFactory, MailService mailService, ApplicationsService applicationsService) {
		this.sessionFactory = sessionFactory;
		this.mailService = mailService;

		this.applicationsService = applicationsService;
	}

	@Override
	public void run() {
		log.info("Application Update Notification task running");
		Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
		List<ApplicationForm> applicationsDueUpdateNotification = applicationsService.getApplicationsDueUpdateNotification();
		transaction.commit();
		for (ApplicationForm applicationForm : applicationsDueUpdateNotification) {
			transaction = sessionFactory.getCurrentSession().beginTransaction();
			sessionFactory.getCurrentSession().refresh(applicationForm);
			try {
				mailService.sendApplicationUpdatedMailToAdmins(applicationForm);
				transaction.commit();
				log.info("update notifiations send  for " + applicationForm.getId());
			} catch (Throwable e) {
				transaction.rollback();
				log.warn("error while sending email", e);

			}

		}
		log.info("Application Update Notification task complete");
	}

}
