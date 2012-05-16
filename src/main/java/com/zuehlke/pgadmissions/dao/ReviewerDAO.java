package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

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
				.add(Restrictions.isNull("lastNotified")).list();
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
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneWeekAgo = DateUtils.addDays(today, -6);	
		List<Reviewer> reviewers = sessionFactory.getCurrentSession()
				.createCriteria(Reviewer.class, "reviewer")
				.add(Restrictions.le("lastNotified", oneWeekAgo))
				.createAlias("reviewRound.application", "application")
				.add(Restrictions.eqProperty("application.latestReviewRound", "reviewRound"))
				.add(Restrictions.eq("application.status", ApplicationFormStatus.REVIEW))
				.list();
		
		for (Reviewer reviewer : reviewers) {
			if(reviewer.getReview() == null){
				reviewersDueReminder.add(reviewer);
			}
		}
		return reviewersDueReminder;
	}

}
