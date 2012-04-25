package com.zuehlke.pgadmissions.timers;

import java.util.Calendar;
import java.util.Date;
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
				if(isLastMailSentTwoWeeksOld(applicationForm)){
					service.sendMailToAdminsAndChangeLastReminderDate(applicationForm);
				}
			}
			transaction.commit();		
		}
		
		public boolean isLastMailSentTwoWeeksOld(ApplicationForm applicationForm){
			Date lastDateMailWasSent = applicationForm.getLastEmailReminderDate();
			Calendar calendar  = Calendar.getInstance();
			Date today = calendar.getTime();
			int daysBetween = 0;
			if(lastDateMailWasSent!=null){
			while (today.after(lastDateMailWasSent)) {
				 calendar.add(Calendar.DAY_OF_MONTH, -1);  
				 today = calendar.getTime();
				 daysBetween++;
				}
			}
			if(daysBetween >= 14){
				return true;
			}
			return false;
		}

		public SessionFactory getSessionFactory() {
			return sessionFactory;
		}

		public void setSessionFactory(SessionFactory sessionFactory) {
			this.sessionFactory = sessionFactory;
		}



}
