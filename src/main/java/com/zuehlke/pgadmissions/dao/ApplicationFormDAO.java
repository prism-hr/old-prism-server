package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

@Repository
public class ApplicationFormDAO {

	private final SessionFactory sessionFactory;

	ApplicationFormDAO() {
		this(null);
	}

	@Autowired
	public ApplicationFormDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(ApplicationForm application) {
		sessionFactory.getCurrentSession().saveOrUpdate(application);
	}

	public ApplicationForm get(Integer id) {
		return (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
	}

	public List<ApplicationForm> getApplicationsByApplicant(RegisteredUser applicant) {
		@SuppressWarnings("unchecked")
		List<ApplicationForm> list = sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("applicant", applicant))
				.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getAllApplications() {
		return (List<ApplicationForm>) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).list();

	}

	@SuppressWarnings("unchecked")
	public List<Qualification> getQualificationsByApplication(ApplicationForm application) {
		return sessionFactory.getCurrentSession().createCriteria(Qualification.class).add(Restrictions.eq("application", application)).list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueValidationReminder() {
		Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
		Date oneWeekAgo = DateUtils.addDays(today, -6);
		
		DetachedCriteria anyRemindersCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.VALIDATION_REMINDER))
				.add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));

		DetachedCriteria overDueRemindersCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.VALIDATION_REMINDER)).add(Restrictions.lt("notificationRecord.notificationDate", oneWeekAgo)).add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));
		
		return (List<ApplicationForm>) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.eq("status", ApplicationFormStatus.VALIDATION))
				.add(Restrictions.lt("validationDueDate", today))
				.add(Restrictions.or(
						Subqueries.exists(overDueRemindersCriteria.setProjection(Projections.property("notificationRecord.id"))),
						Subqueries.notExists(anyRemindersCriteria.setProjection(Projections.property("notificationRecord.id")))
					)
				).list();
		
	
		
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueUpdateNotification() {
		Date twentyFourHoursAgo = DateUtils.addHours(Calendar.getInstance().getTime(), -24);
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
				.createAlias("notificationRecords", "notificationRecord")
				.add(Restrictions.eq("notificationRecord.notificationType", NotificationType.UPDATED_NOTIFICATION))
				.add(Restrictions.lt("notificationRecord.notificationDate", twentyFourHoursAgo))
				.add(Restrictions.ltProperty("notificationRecord.notificationDate", "lastUpdated"))
				.list();
		
	}
	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueApplicantReviewNotification() {

		DetachedCriteria notificationCriteriaOne = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION))				
				.add(Property.forName("notificationRecord.application.id").eqProperty("event.application.id"));
				
		DetachedCriteria notificationCriteriaTwo = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION))
				.add(Property.forName("notificationRecord.application.id").eqProperty("event.application.id"));
				
		
		DetachedCriteria reviewEventsCriteria = DetachedCriteria.forClass(Event.class, "event")
				.add(Restrictions.eq("newStatus", ApplicationFormStatus.REVIEW))
				.add(Restrictions.or(Subqueries.notExists(notificationCriteriaOne.setProjection(Projections.property("notificationRecord.id"))), Subqueries.propertyGt("eventDate", notificationCriteriaTwo.setProjection(Projections.max("notificationRecord.notificationDate")))))
				.add(Property.forName("event.application").eqProperty("applicationForm.id"));
		
		
		
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.not(Restrictions.in("status", Arrays.asList(ApplicationFormStatus.WITHDRAWN, ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED))))
				.add(Subqueries.exists(reviewEventsCriteria.setProjection(Projections.property("event.id"))))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueApplicantSubmissionNotification() {
		DetachedCriteria notificationCriteriaOne = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPLICANT_SUBMISSION_NOTIFICATION))				
				.add(Property.forName("notificationRecord.application.id").eqProperty("event.application.id"));
				
		DetachedCriteria notificationCriteriaTwo = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPLICANT_SUBMISSION_NOTIFICATION))
				.add(Property.forName("notificationRecord.application.id").eqProperty("event.application.id"));
				
		
		DetachedCriteria reviewEventsCriteria = DetachedCriteria.forClass(Event.class, "event")
				.add(Restrictions.eq("newStatus", ApplicationFormStatus.VALIDATION))
				.add(Restrictions.or(Subqueries.notExists(notificationCriteriaOne.setProjection(Projections.property("notificationRecord.id"))), Subqueries.propertyGt("eventDate", notificationCriteriaTwo.setProjection(Projections.max("notificationRecord.notificationDate")))))
				.add(Property.forName("event.application").eqProperty("applicationForm.id"));
		
		
		
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.not(Restrictions.in("status", Arrays.asList(ApplicationFormStatus.WITHDRAWN, ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED))))
				.add(Subqueries.exists(reviewEventsCriteria.setProjection(Projections.property("event.id"))))
				.list();
	}

}
