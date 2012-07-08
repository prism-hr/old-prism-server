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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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
		Date twentyFourHoursAgo = DateUtils.addHours(Calendar.getInstance().getTime(), -24);
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).createAlias("notificationRecords", "notificationRecord")
				.add(Restrictions.eq("notificationRecord.notificationType", NotificationType.UPDATED_NOTIFICATION))
				.add(Restrictions.lt("notificationRecord.date", twentyFourHoursAgo)).add(Restrictions.ltProperty("notificationRecord.date", "lastUpdated"))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

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
	public List<ApplicationForm> getVisibleApplications(RegisteredUser user) {
		if (user.isInRole(Authority.APPLICANT)) {
			// the only other role permitted if you are an applicant
			// is referee
			List<ApplicationForm> applications = applicationsOfWhichApplicant(user);
			List<ApplicationForm> applicationsOfWhichReferee = applicationsOfWhichReferee(user);
			for (ApplicationForm applicationForm : applicationsOfWhichReferee) {
				if (!applications.contains(applicationForm)) {
					applications.add(applicationForm);
				}
			}
			return applications;

		}
		if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
			return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
					.add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED))).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.list();
		}

		List<ApplicationForm> apps = new ArrayList<ApplicationForm>();
		if (user.isInRole(Authority.REFEREE)) {
			if (!applicationsOfWhichReferee(user).isEmpty()) {
				apps.addAll(applicationsOfWhichReferee(user));
			}
		}

		if (!user.getProgramsOfWhichAdministrator().isEmpty()) {
			List<ApplicationForm> submittedApplicationsInProgramsOfWhichAdmin = getSubmittedApplicationsInProgramsOfWhichAdmin(user);
			for (ApplicationForm applicationForm : submittedApplicationsInProgramsOfWhichAdmin) {
				if (!apps.contains(applicationForm)) {
					apps.add(applicationForm);
				}
			}

		}
		List<ApplicationForm> applicationsOfWhichApplicationAdministrator = getSubmittedApplicationsOfWhichApplicationAdministrator(user);
		for (ApplicationForm applicationForm : applicationsOfWhichApplicationAdministrator) {
			if (!apps.contains(applicationForm)) {
				apps.add(applicationForm);
			}
		}
		if (!user.getProgramsOfWhichApprover().isEmpty()) {
			List<ApplicationForm> approverApps = getApprovedApplicationsInProgramsOfWhichApprover(user);
			for (ApplicationForm applicationForm : approverApps) {
				if (!apps.contains(applicationForm)) {
					apps.add(applicationForm);
				}
			}
		}

		List<ApplicationForm> reviewerApps = getApplicationsCurrentlyInReviewOfWhichReviewerOfLatestRound(user);
		for (ApplicationForm applicationForm : reviewerApps) {
			if (!apps.contains(applicationForm)) {
				apps.add(applicationForm);
			}
		}

		List<ApplicationForm> interviewerApps = getApplicationsCurrentlyInInterviewOfWhichInterviewerOfLatestInterview(user);
		for (ApplicationForm applicationForm : interviewerApps) {
			if (!apps.contains(applicationForm)) {
				apps.add(applicationForm);
			}
		}

		List<ApplicationForm> supervisorApps = getApplicationsCurrentlyInApprovalOrApprovedOfWhichSupervisorOfLatestApprovalRound(user);
		for (ApplicationForm applicationForm : supervisorApps) {
			if (!apps.contains(applicationForm)) {
				apps.add(applicationForm);
			}
		}
		return apps;
	}

	@SuppressWarnings("unchecked")
	private List<ApplicationForm> applicationsOfWhichReferee(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).createAlias("referees", "referee")
				.add(Restrictions.eq("referee.user", user)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	private List<ApplicationForm> applicationsOfWhichApplicant(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("applicant", user))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	private List<ApplicationForm> getSubmittedApplicationsOfWhichApplicationAdministrator(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
				.add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED))).add(Restrictions.eq("applicationAdministrator", user))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	private List<ApplicationForm> getSubmittedApplicationsInProgramsOfWhichAdmin(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
				.add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED)))
				.add(Restrictions.in("program", user.getProgramsOfWhichAdministrator())).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	private List<ApplicationForm> getApprovedApplicationsInProgramsOfWhichApprover(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL))
				.add(Restrictions.in("program", user.getProgramsOfWhichApprover())).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	private List<ApplicationForm> getApplicationsCurrentlyInReviewOfWhichReviewerOfLatestRound(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("status", ApplicationFormStatus.REVIEW))
				.createAlias("latestReviewRound", "latestReviewRound").createAlias("latestReviewRound.reviewers", "reviewer")
				.add(Restrictions.eq("reviewer.user", user)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	private List<ApplicationForm> getApplicationsCurrentlyInInterviewOfWhichInterviewerOfLatestInterview(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("status", ApplicationFormStatus.INTERVIEW))
				.createAlias("latestInterview", "latestInterview").createAlias("latestInterview.interviewers", "interviewer")
				.add(Restrictions.eq("interviewer.user", user)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	private List<ApplicationForm> getApplicationsCurrentlyInApprovalOrApprovedOfWhichSupervisorOfLatestApprovalRound(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
				.add(Restrictions.in("status", Arrays.asList(ApplicationFormStatus.APPROVAL, ApplicationFormStatus.APPROVED)))
				.createAlias("latestApprovalRound", "latestApprovalRound").createAlias("latestApprovalRound.supervisors", "supervisor")
				.add(Restrictions.eq("supervisor.user", user)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueRejectNotifications() {
		Session session = sessionFactory.getCurrentSession();
		List<ApplicationForm> result = session.createCriteria(ApplicationForm.class).add(Restrictions.eq("status", ApplicationFormStatus.REJECTED))
				.add(Restrictions.isNull("rejectNotificationDate")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
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

}
