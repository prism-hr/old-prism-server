package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class RefereeDAO {

	private final SessionFactory sessionFactory;

	RefereeDAO() {
		this(null);
	}

	@Autowired
	public RefereeDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(Referee referee) {
		sessionFactory.getCurrentSession().saveOrUpdate(referee);
	}

	public Referee getRefereeById(Integer id) {
		return (Referee) sessionFactory.getCurrentSession().get(Referee.class, id);
	}

	public void delete(Referee referee) {
		sessionFactory.getCurrentSession().delete(referee);

	}

	public Referee getRefereeByActivationCode(String activationCode) {
		return (Referee) sessionFactory.getCurrentSession().createCriteria(Referee.class).add(Restrictions.eq("activationCode", activationCode)).uniqueResult();

	}

	@SuppressWarnings("unchecked")
	public List<Referee> getRefereesDueAReminder() {

		Date today = Calendar.getInstance().getTime();
		ReminderInterval reminderInterval = (ReminderInterval)sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).uniqueResult();
		Date dateWithSubtractedInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());

	
		return (List<Referee>) sessionFactory.getCurrentSession()
					.createCriteria(Referee.class)
					.createAlias("application", "application")
					.add(Restrictions.eq("declined", false))
					.add(Restrictions.isNull("reference"))
					.add(Restrictions.isNotNull("user"))
					.add(Restrictions.not(Restrictions.in("application.status", new ApplicationFormStatus[]{ApplicationFormStatus.WITHDRAWN, ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED, ApplicationFormStatus.UNSUBMITTED})))
					.add(Restrictions.le("lastNotified", dateWithSubtractedInterval))
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Referee> getRefereesWhoDidntProvideReferenceYet(ApplicationForm form) {
		return (List<Referee>) sessionFactory.getCurrentSession()
				.createCriteria(Referee.class)
				.add(Restrictions.eq("declined", false))
				.add(Restrictions.eq("application", form))
				.add(Restrictions.isNull("reference"))
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Referee> getRefereesDueNotification() {		
		return sessionFactory.getCurrentSession()
				.createCriteria(Referee.class)
				.add(Restrictions.isNull("lastNotified"))
				.add(Restrictions.eq("declined", false))
				.add(Restrictions.isNull("reference"))
				.createAlias("application", "application")
				.add(Restrictions.not(Restrictions.in("application.status", new ApplicationFormStatus[]{ApplicationFormStatus.WITHDRAWN, ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED, ApplicationFormStatus.VALIDATION, ApplicationFormStatus.UNSUBMITTED})))
				.list();
	}
}
