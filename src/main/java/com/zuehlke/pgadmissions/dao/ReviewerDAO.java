package com.zuehlke.pgadmissions.dao;

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

import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
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
		return sessionFactory.getCurrentSession().createCriteria(Reviewer.class).add(Restrictions.isNull("lastNotified"))
				.createAlias("application", "application").add(Restrictions.eq("application.status", ApplicationFormStatus.REVIEW)).list();
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
		DetachedCriteria reviewCriteria = DetachedCriteria.forClass(ReviewComment.class, "review")
				.add(Restrictions.isNotNull("reviewer"))
				.add(Property.forName("review.reviewer").eqProperty("reviewer.id"));
		
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneWeekAgo = DateUtils.addDays(today, -6);	
		return sessionFactory.getCurrentSession()
				.createCriteria(Reviewer.class, "reviewer")
				.add(Restrictions.le("lastNotified", oneWeekAgo))
				.createAlias("application", "application").add(Restrictions.eq("application.status", ApplicationFormStatus.REVIEW))
				//.add(Subqueries.notExists(reviewCriteria.setProjection(Projections.property("review.id"))))				
				.add(Restrictions.isNull("review"))
				.list();
	}

}
