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

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

@Repository
public class ReviewerDAO {

	private final SessionFactory sessionFactory;

	ReviewerDAO() {
		this(null);
	}

	@Autowired
	public ReviewerDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}

	@SuppressWarnings("unchecked")
	public List<Reviewer> getReviewersDueNotification() {
		return sessionFactory.getCurrentSession()
				.createCriteria(Reviewer.class)
				.createAlias("reviewRound.application", "application")
				.add(Restrictions.eqProperty("application.latestReviewRound", "reviewRound"))
				.add(Restrictions.eq("application.status", ApplicationFormStatus.REVIEW))
				.add(Restrictions.isNull("lastNotified")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	public void save(Reviewer reviewer) {
		sessionFactory.getCurrentSession().saveOrUpdate(reviewer);

	}

	public Reviewer getReviewerById(Integer id) {
		return (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class, id);
	}

	public Reviewer getReviewerByUser(RegisteredUser user) {
		return (Reviewer) sessionFactory.getCurrentSession().createCriteria(Reviewer.class).add(Restrictions.eq("user", user)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Reviewer> getReviewersDueReminder() {
		List<Reviewer> reviewersDueReminder = new ArrayList<Reviewer>();
		
		Date today = Calendar.getInstance().getTime();
		ReminderInterval reminderInterval = (ReminderInterval)sessionFactory.getCurrentSession().createCriteria(ReminderInterval.class).uniqueResult();
		Date dateWithSubtractedInterval = DateUtils.addMinutes(today, -reminderInterval.getDurationInMinutes());
	
		List<Reviewer> reviewers = sessionFactory.getCurrentSession()
				.createCriteria(Reviewer.class, "reviewer")
				.add(Restrictions.le("lastNotified", dateWithSubtractedInterval))
				.createAlias("reviewRound.application", "application")
				.add(Restrictions.eqProperty("application.latestReviewRound", "reviewRound"))
				.add(Restrictions.eq("application.status", ApplicationFormStatus.REVIEW)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		for (Reviewer reviewer : reviewers) {
			if(reviewer.getReview() == null){
				reviewersDueReminder.add(reviewer);
			}
		}
		return reviewersDueReminder;
	}

	@SuppressWarnings("unchecked")
	public List<Reviewer> getReviewersRequireAdminNotification() {
		return sessionFactory.getCurrentSession()
				.createCriteria(Reviewer.class)
				.createAlias("reviewRound.application", "application")
				.add(Restrictions.eqProperty("application.latestReviewRound", "reviewRound"))
				.add(Restrictions.eq("requiresAdminNotification", CheckedStatus.YES))
				.add(Restrictions.isNull("dateAdminsNotified")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

}
