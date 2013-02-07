package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class InterviewerDAO {

	private final SessionFactory sessionFactory;

	InterviewerDAO() {
		this(null);
	}

	@Autowired
	public InterviewerDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}

	public void save(Interviewer interviewer) {
		sessionFactory.getCurrentSession().saveOrUpdate(interviewer);

	}

	public Interviewer getInterviewerById(Integer id) {
		return (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class, id);
	}

	public Interviewer getInterviewerByUser(RegisteredUser user) {
		return (Interviewer) sessionFactory.getCurrentSession().createCriteria(Interviewer.class).add(Restrictions.eq("user", user)).uniqueResult();
	}

	
	@SuppressWarnings("unchecked")
	public List<Interviewer> getInterviewersDueNotification() {
		return sessionFactory.getCurrentSession().createCriteria(Interviewer.class)
				.add(Restrictions.isNull("lastNotified"))
				.createAlias("interview.application", "application")
				.add(Restrictions.eq("application.status", ApplicationFormStatus.INTERVIEW))
				.add(Restrictions.eqProperty("interview", "application.latestInterview"))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();

	}

	@SuppressWarnings("unchecked")
	public List<Interviewer> getInterviewersDueReminder() {
		List<Interviewer> interviewersDueReminder = new ArrayList<Interviewer>();
		
		Date today = Calendar.getInstance().getTime();
		ReminderInterval reminderInterval = (ReminderInterval)sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).uniqueResult();
		Date dateWithSubtractedInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());

		List<Interviewer> interviewers = sessionFactory.getCurrentSession().createCriteria(Interviewer.class).createAlias("interview.application", "application")
				.add(Restrictions.eqProperty("interview", "application.latestInterview"))
				.add(Restrictions.eq("application.status", ApplicationFormStatus.INTERVIEW)).add(Restrictions.lt("application.dueDate", dateWithSubtractedInterval))
				.add(Restrictions.lt("lastNotified", dateWithSubtractedInterval)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		for (Interviewer interviewer : interviewers) {
			if (interviewer.getInterviewComment() == null) {
				interviewersDueReminder.add(interviewer);
			}
		}
		return interviewersDueReminder;
	}

	@SuppressWarnings("unchecked")
	public List<Interviewer> getInterviewersRequireAdminNotification() {
		return sessionFactory.getCurrentSession()
				.createCriteria(Interviewer.class)
				.createAlias("interview.application", "application")
				.add(Restrictions.eqProperty("application.latestInterview", "interview"))
				.add(Restrictions.eq("requiresAdminNotification", true))
				.add(Restrictions.isNull("dateAdminsNotified")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

}
