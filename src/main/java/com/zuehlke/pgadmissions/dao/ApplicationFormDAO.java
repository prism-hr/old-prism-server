package com.zuehlke.pgadmissions.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
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

    public void refresh(ApplicationForm applicationForm){
        sessionFactory.getCurrentSession().refresh(applicationForm);
    }
	
	public ApplicationForm get(Integer id) {
		return (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getAllApplications() {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

	}

	@SuppressWarnings("unchecked")
	public List<Qualification> getQualificationsByApplication(ApplicationForm application) {
		return sessionFactory.getCurrentSession().createCriteria(Qualification.class).add(Restrictions.eq("application", application))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueUserReminder(NotificationType notificationType, ApplicationFormStatus status) {

		Date today = Calendar.getInstance().getTime();
		ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).uniqueResult();
		Date subtractInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());

		DetachedCriteria anyRemindersCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", notificationType))
				.add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));

		DetachedCriteria overDueRemindersCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", notificationType)).add(Restrictions.lt("notificationRecord.date", subtractInterval))
				.add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));

		return sessionFactory
				.getCurrentSession()
				.createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.eq("status", status))
				.add(Restrictions.lt("dueDate", today))
				.add(Restrictions.or(Subqueries.exists(overDueRemindersCriteria.setProjection(Projections.property("notificationRecord.id"))),
						Subqueries.notExists(anyRemindersCriteria.setProjection(Projections.property("notificationRecord.id")))))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueUpdateNotification() {
	    // Kevin: This should resolve a mysterious issue we had on production. For 
        // some reason we had multiple email schedulers of the same class running in parallel
        // which then created duplicate notification records for the same type 
        // such as UPDATED_NOTIFICATION.
	    //
	    // This SQL query makes sure that we only select the notification_record with the highest
	    // update date and ignores duplicates of the same notification type.
	    Date oneHourAgo = DateUtils.addHours(Calendar.getInstance().getTime(), -1);
	    final String selectQuery = "" 
		        + "SELECT appform.* " 
		        + "FROM NOTIFICATION_RECORD notification, APPLICATION_FORM appform " 
		        + "WHERE notification.application_form_id = appform.id " 
		        + "AND notification.notification_date IN ( " 
		        + "SELECT MAX(b.notification_date) " 
		        + "FROM NOTIFICATION_RECORD b " 
		        + "WHERE notification_type = ? " 
		        + "AND notification.application_form_id = b.application_form_id) " 
		        + "AND notification.notification_date < appform.last_updated " 
		        + "AND notification.notification_date < ?";
	    
	    Query query = sessionFactory.getCurrentSession().createSQLQuery(selectQuery).addEntity(ApplicationForm.class);
	    return query.setString(0, NotificationType.UPDATED_NOTIFICATION.toString()).setDate(1, oneHourAgo).list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueNotificationForStateChangeEvent(NotificationType notificationType, ApplicationFormStatus newStatus) {
		DetachedCriteria notificationCriteriaOne = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", notificationType))
				.add(Property.forName("notificationRecord.application.id").eqProperty("event.application.id"));

		DetachedCriteria notificationCriteriaTwo = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", notificationType))
				.add(Property.forName("notificationRecord.application.id").eqProperty("event.application.id"));

		DetachedCriteria reviewEventsCriteria = DetachedCriteria
				.forClass(StateChangeEvent.class, "event")
				.add(Restrictions.eq("newStatus", newStatus))
				.add(Restrictions.or(Subqueries.notExists(notificationCriteriaOne.setProjection(Projections.property("notificationRecord.id"))),
						Subqueries.propertyGt("date", notificationCriteriaTwo.setProjection(Projections.max("notificationRecord.date")))))
				.add(Property.forName("event.application").eqProperty("applicationForm.id"));

		List<ApplicationFormStatus> invalidStateList = new ArrayList<ApplicationFormStatus>();
		invalidStateList.add(ApplicationFormStatus.WITHDRAWN);
		if (!ApplicationFormStatus.REJECTED.equals(newStatus)) {
			invalidStateList.add(ApplicationFormStatus.REJECTED);
		}
		if (!ApplicationFormStatus.APPROVED.equals(newStatus)) {
			invalidStateList.add(ApplicationFormStatus.APPROVED);
		}
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.not(Restrictions.in("status", invalidStateList)))
				.add(Subqueries.exists(reviewEventsCriteria.setProjection(Projections.property("event.id"))))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueRejectNotifications() {
		Session session = sessionFactory.getCurrentSession();
        List<ApplicationForm> result = session.createCriteria(ApplicationForm.class)
                .add(Restrictions.eq("status", ApplicationFormStatus.REJECTED))
                .add(Restrictions.isNull("rejectNotificationDate")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
		return result;
	}

	public Long getApplicationsInProgramThisYear(Program program, String year) {
		Date startYear = null;
		
		try {
			startYear = new SimpleDateFormat("yyyy").parse(year);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		
		Date endYear = DateUtils.addYears(startYear, 1);

		return (Long) sessionFactory.getCurrentSession()
                .createCriteria(ApplicationForm.class)
                .setProjection(Projections.rowCount())
                .add(Restrictions.eq("program", program))
                .add(Restrictions.between("applicationTimestamp", startYear, endYear))
                .uniqueResult();
	}

	public ApplicationForm getApplicationByApplicationNumber(String applicationNumber) {
		return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
				.add(Restrictions.eq("applicationNumber", applicationNumber)).uniqueResult();

	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueApprovalNotifications() {
		DetachedCriteria appronalNotificationCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPROVAL_NOTIFICATION))
				.add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));
		
		DetachedCriteria confirmedSupervisorCriteria = DetachedCriteria.forClass(ApprovalRound.class, "approvalRound")
                .createAlias("supervisors", "s", JoinType.INNER_JOIN)
		        .add(Property.forName("applicationForm.latestApprovalRound").eqProperty("approvalRound.id"))
		        .add(Restrictions.eq("s.isPrimary", true))
		        .add(Restrictions.eq("s.confirmedSupervision", true));

		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL))
				.add(Subqueries.notExists(appronalNotificationCriteria.setProjection(Projections.property("notificationRecord.id"))))
				.add(Subqueries.exists(confirmedSupervisorCriteria.setProjection(Projections.property("approvalRound.id"))))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueApprovedNotifications() {
		DetachedCriteria appronalNotificationCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPROVED_NOTIFICATION))
				.add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));

		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.eq("status", ApplicationFormStatus.APPROVED))
				.add(Subqueries.notExists(appronalNotificationCriteria.setProjection(Projections.property("notificationRecord.id"))))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueRegistryNotification() {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("registryUsersDueNotification", true))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueApprovalRequestNotification() {
		DetachedCriteria approvalRestartRequestNotificationCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION))
				.add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL)).add(Restrictions.eq("pendingApprovalRestart", true))
				.add(Subqueries.notExists(approvalRestartRequestNotificationCriteria.setProjection(Projections.property("notificationRecord.id"))))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationDueApprovalRestartRequestReminder() {
		Date now = Calendar.getInstance().getTime();
		ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).uniqueResult();

		Date oneReminderIntervalAgo = DateUtils.addMinutes(now, -reminderInterval.getDurationInMinutes());
		
		DetachedCriteria approvalRestartRequestNotificationCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION))
				.add(Restrictions.lt("date", oneReminderIntervalAgo)).add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));
		
		DetachedCriteria approvalRestartRequestReminderCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPROVAL_RESTART_REQUEST_REMINDER))
				.add(Restrictions.lt("date", oneReminderIntervalAgo)).add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));
		
		DetachedCriteria anyRequestReminderCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPROVAL_RESTART_REQUEST_REMINDER))
				.add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));
		
		return sessionFactory
				.getCurrentSession()
				.createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL))
				.add(Restrictions.eq("pendingApprovalRestart", true))
				.add(Restrictions.or(
						Restrictions.and(
								Subqueries.exists(approvalRestartRequestNotificationCriteria.setProjection(Projections.property("notificationRecord.id"))), 
								Subqueries.notExists(anyRequestReminderCriteria.setProjection(Projections.property("notificationRecord.id")))) ,
						Subqueries.exists( approvalRestartRequestReminderCriteria.setProjection(Projections.property("notificationRecord.id")))))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueMovedToApprovalNotifications() {
		DetachedCriteria mvoedToApprovalNotificationCriteria = DetachedCriteria.forClass(NotificationRecord.class, "notificationRecord")
				.add(Restrictions.eq("notificationType", NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION))
				.add(Property.forName("notificationRecord.application").eqProperty("applicationForm.id"));

		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL))
				.add(Subqueries.notExists(mvoedToApprovalNotificationCriteria.setProjection(Projections.property("notificationRecord.id"))))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

    @SuppressWarnings("unchecked")
    public List<ApplicationForm> getAllApplicationsByStatus(ApplicationFormStatus status) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("status", status))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
}
