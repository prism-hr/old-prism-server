package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
				.createAlias("application", "application")
				.add(Restrictions.eq("application.status", ApplicationFormStatus.REVIEW)).list();
	}

	public void save(Reviewer reviewer) {
		sessionFactory.getCurrentSession().saveOrUpdate(reviewer);
		
	}
}
