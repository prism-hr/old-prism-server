package com.zuehlke.pgadmissions.timers;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.mail.RegistryMailSender;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class RegistryNotificationTimerTask extends TimerTask {

		private final Logger log = Logger.getLogger(AdminInterviewFeedbackNotificationTask.class);
		private final RegistryMailSender registryMailSender;
		private final SessionFactory sessionFactory;
		private final ApplicationsService applicationsService;

		public RegistryNotificationTimerTask() {
			this(null, null, null);
		}

		@Autowired
		public RegistryNotificationTimerTask(SessionFactory sessionFactory, RegistryMailSender registryMailSender, ApplicationsService applicationsService) {
			this.sessionFactory = sessionFactory;
			this.registryMailSender = registryMailSender;
			this.applicationsService = applicationsService;
		}

		@Override
		public void run() {
			log.info("Registry Notification Task Running");
			Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
			List<ApplicationForm> applications = applicationsService.getApplicationsDueRegistryNotification();
			transaction.commit();
			for (ApplicationForm applicationForm : applications) {
					transaction = sessionFactory.getCurrentSession().beginTransaction();
					sessionFactory.getCurrentSession().refresh(applicationForm);
					try {
						registryMailSender.sendApplicationToRegistryContacts(applicationForm);
						applicationForm.setRegistryUsersNotified(true);
						applicationsService.save(applicationForm);
						transaction.commit();
						log.info("notification sent to registry persons for application " + applicationForm.getApplicationNumber());
					} catch (Throwable e) {
						transaction.rollback();
						log.warn("error while sending notification to registry persons for application " + applicationForm.getApplicationNumber(), e);
						
					}
			}
			log.info("Interview Comment Task complete");
		}
	}

