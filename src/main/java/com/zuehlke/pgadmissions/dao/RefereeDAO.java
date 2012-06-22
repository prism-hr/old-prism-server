package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
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

		List<Referee> refDueRem = new ArrayList<Referee>();
		List<Referee> referees = (List<Referee>) sessionFactory.getCurrentSession()
					.createCriteria(Referee.class)
					.createAlias("application", "application")
					.add(Restrictions.eq("declined", false))
//					.add(Restrictions.isNull("reference"))
					.add(Restrictions.isNotNull("user"))
					.add(Restrictions.not(Restrictions.in("application.status", new ApplicationFormStatus[]{ApplicationFormStatus.WITHDRAWN, ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED, ApplicationFormStatus.UNSUBMITTED})))
					.add(Restrictions.le("lastNotified", dateWithSubtractedInterval))
				.list();
		for (Referee referee : referees) {
			if(!referee.hasProvidedReference()){
				refDueRem.add(referee);
			}
		}
		return refDueRem;
	}
	
	@SuppressWarnings("unchecked")
	public List<Referee> getRefereesWhoDidntProvideReferenceYet(ApplicationForm form) {
		List<Referee> haveProvidedRef = new ArrayList<Referee>();
		List<Referee> referees = (List<Referee>) sessionFactory.getCurrentSession()
				.createCriteria(Referee.class)
				.add(Restrictions.eq("declined", false))
				.add(Restrictions.eq("application", form))
//				.add(Restrictions.isNull("reference"))
				.list();
		List<ReferenceComment> references = (List<ReferenceComment>) sessionFactory.getCurrentSession()
				.createCriteria(ReferenceComment.class)
				.list();
		for (Referee referee : referees) {
			for (ReferenceComment referenceComment : references) {
				if(referenceComment.getReferee().equals(referee)){
					haveProvidedRef.add(referee);
				}
			}
		}
		referees.removeAll(haveProvidedRef);
		return referees;
	}
	
	@SuppressWarnings("unchecked")
	public List<Referee> getRefereesDueNotification() {		
		List<Referee> haveProvidedRef = new ArrayList<Referee>();
		List<Referee> referees = sessionFactory.getCurrentSession()
				.createCriteria(Referee.class)
				.add(Restrictions.isNull("lastNotified"))
				.add(Restrictions.eq("declined", false))
//				.add(Restrictions.isNull("reference"))
				.createAlias("application", "application")
				.add(Restrictions.not(Restrictions.in("application.status", new ApplicationFormStatus[]{ApplicationFormStatus.WITHDRAWN, ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED, ApplicationFormStatus.VALIDATION, ApplicationFormStatus.UNSUBMITTED})))
				.list();
		List<ReferenceComment> references = (List<ReferenceComment>) sessionFactory.getCurrentSession()
				.createCriteria(ReferenceComment.class)
				.list();
		for (Referee referee : referees) {
			for (ReferenceComment referenceComment : references) {
				if(referenceComment.getReferee().equals(referee)){
					haveProvidedRef.add(referee);
				}
			}
		}
		referees.removeAll(haveProvidedRef);
		return referees;
	}
}
