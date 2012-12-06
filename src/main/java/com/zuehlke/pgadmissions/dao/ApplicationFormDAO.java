package com.zuehlke.pgadmissions.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

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
	
	public List<ApplicationForm> getVisibleApplications(RegisteredUser user) {
	    return this.getVisibleApplications(user, SortCategory.DEFAULT, SortOrder.DESCENDING, 1, 25);
	}
	
	@SuppressWarnings("unchecked")
    public List<ApplicationForm> getVisibleApplications(RegisteredUser user, SortCategory sortCategory, SortOrder sortOrder, int pageCount, int itemsPerPage) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
            criteria.add(getAllApplicationsForSuperAdministrator());
        } else {
            
            Disjunction disjunction = Restrictions.disjunction();
            
            if (user.isInRole(Authority.APPLICANT)) {
                disjunction.add(Subqueries.propertyIn("id", applicationsOfWhichApplicant(user)));
                disjunction.add(Subqueries.propertyIn("id", applicationsOfWhichReferee(user)));
            }
            
            if (user.isInRole(Authority.REFEREE)) {
                disjunction.add(Subqueries.propertyIn("id", applicationsOfWhichReferee(user)));
            }
            
            if (!user.getProgramsOfWhichAdministrator().isEmpty()) {
                disjunction.add(Subqueries.propertyIn("id", getSubmittedApplicationsInProgramsOfWhichAdmin(user)));
            }

            if (!user.getProgramsOfWhichApprover().isEmpty()) {
                disjunction.add(Subqueries.propertyIn("id", getApprovedApplicationsInProgramsOfWhichApprover(user)));
            }
            
            disjunction.add(Subqueries.propertyIn("id", getSubmittedApplicationsOfWhichApplicationAdministrator(user)));

            disjunction.add(Subqueries.propertyIn("id", getApplicationsCurrentlyInReviewOfWhichReviewerOfLatestRound(user)));
            
            disjunction.add(Subqueries.propertyIn("id", getApplicationsCurrentlyInInterviewOfWhichInterviewerOfLatestInterview(user)));
            
            disjunction.add(Subqueries.propertyIn("id", getApplicationsCurrentlyInApprovalOrApprovedOfWhichSupervisorOfLatestApprovalRound(user)));                

            criteria.add(disjunction);
        }
        
        criteria = sortCategory.setOrderCriteria(sortOrder, criteria);
        
        return criteria.setMaxResults(pageCount * itemsPerPage).setFirstResult((pageCount - 1) * itemsPerPage).list();
        //return criteria.setMaxResults(pageCount * itemsPerPage).setFirstResult(0).list();
    }

	private Criterion getAllApplicationsForSuperAdministrator() {
	    return Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED));
	}
	
	private DetachedCriteria applicationsOfWhichReferee(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .createAlias("referees", "referee")
                .add(Restrictions.eq("referee.user", user));
	}

	private DetachedCriteria applicationsOfWhichApplicant(RegisteredUser user) {
		return DetachedCriteria.forClass(ApplicationForm.class)
		        .setProjection(Projections.property("id"))
		        .add(Restrictions.eq("applicant", user));    
	}

	private DetachedCriteria getSubmittedApplicationsOfWhichApplicationAdministrator(RegisteredUser user) {
	    return DetachedCriteria.forClass(ApplicationForm.class)
	            .setProjection(Projections.property("id"))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED)))
                .add(Restrictions.eq("applicationAdministrator", user));
	}

	private DetachedCriteria getSubmittedApplicationsInProgramsOfWhichAdmin(RegisteredUser user) {
	    return DetachedCriteria.forClass(ApplicationForm.class)
	            .setProjection(Projections.property("id"))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED)))
                .add(Restrictions.in("program", user.getProgramsOfWhichAdministrator()));
	}

	private DetachedCriteria getApprovedApplicationsInProgramsOfWhichApprover(RegisteredUser user) {
	    return DetachedCriteria.forClass(ApplicationForm.class)
	            .setProjection(Projections.property("id"))
                .add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL))
                .add(Restrictions.in("program", user.getProgramsOfWhichApprover()));
	}

	private DetachedCriteria getApplicationsCurrentlyInReviewOfWhichReviewerOfLatestRound(RegisteredUser user) {
	    return DetachedCriteria.forClass(ApplicationForm.class)
	            .setProjection(Projections.property("id"))
                .add(Restrictions.eq("status", ApplicationFormStatus.REVIEW))
                .createAlias("latestReviewRound", "latestReviewRound")
                .createAlias("latestReviewRound.reviewers", "reviewer")
                .add(Restrictions.eq("reviewer.user", user));
	}

	private DetachedCriteria getApplicationsCurrentlyInInterviewOfWhichInterviewerOfLatestInterview(RegisteredUser user) {
	    return DetachedCriteria.forClass(ApplicationForm.class)
	            .setProjection(Projections.property("id"))
                .add(Restrictions.eq("status", ApplicationFormStatus.INTERVIEW))
                .createAlias("latestInterview", "latestInterview")
                .createAlias("latestInterview.interviewers", "interviewer")
                .add(Restrictions.eq("interviewer.user", user));
	}

	private DetachedCriteria getApplicationsCurrentlyInApprovalOrApprovedOfWhichSupervisorOfLatestApprovalRound(RegisteredUser user) {
	    return DetachedCriteria.forClass(ApplicationForm.class)
	            .setProjection(Projections.property("id"))
                .add(Restrictions.in("status", Arrays.asList(ApplicationFormStatus.APPROVAL, ApplicationFormStatus.APPROVED)))
                .createAlias("latestApprovalRound", "latestApprovalRound")
                .createAlias("latestApprovalRound.supervisors", "supervisor")
                .add(Restrictions.eq("supervisor.user", user));
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

	public int getApplicationsInProgramThisYear(Program program, String year) {
		Date startYear = null;
		try {
			startYear = new SimpleDateFormat("yyyy").parse(year);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		Date endYear = DateUtils.addYears(startYear, 1);
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("program", program))
				.add(Restrictions.between("applicationTimestamp", startYear, endYear)).list().size();

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

		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class, "applicationForm")
				.add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL))
				.add(Subqueries.notExists(appronalNotificationCriteria.setProjection(Projections.property("notificationRecord.id"))))
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
        return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)	            
                .add(Restrictions.eq("status", status))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
}
