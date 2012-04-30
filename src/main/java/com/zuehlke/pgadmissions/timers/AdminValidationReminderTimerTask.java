package com.zuehlke.pgadmissions.timers;

import java.util.List;
import java.util.TimerTask;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.MailService;
public class AdminValidationReminderTimerTask extends TimerTask{


		private ApplicationsService applicationsService;
		private MailService service;
		private SessionFactory sessionFactory;
		protected Transaction transaction;
		
		public ApplicationsService getApplicationsService() {
			return applicationsService;
		}
		
		public void setApplicationsService(ApplicationsService applicationsService) {
			this.applicationsService = applicationsService;
		}
		
		public MailService getService() {
			return service;
		}
		
		public void setService(MailService service) {
			this.service = service;
		}
		
		
		@Override
		public void run() {
			transaction = getSessionFactory().getCurrentSession().beginTransaction();
			List<ApplicationForm> applications = applicationsService.getAllApplicationsStillInValidationStageAndAfterDueDate();
			for (ApplicationForm applicationForm : applications) {				
				service.sendValidationReminderMailToAdminsAndChangeLastReminderDate(applicationForm);
				
			}
			transaction.commit();		
		}
		
		
		public SessionFactory getSessionFactory() {
			return sessionFactory;
		}

		public void setSessionFactory(SessionFactory sessionFactory) {
			this.sessionFactory = sessionFactory;
		}



}
