package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;

@Repository
public class ApplicationReviewDAO {

	private final SessionFactory sessionFactory;
	
	ApplicationReviewDAO(){
		this(null);
	}
	
	@Autowired
	public ApplicationReviewDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(ApplicationReview review) {
		sessionFactory.getCurrentSession().saveOrUpdate(review);
	}
	
	public ApplicationReview get(Integer id) {
		return (ApplicationReview) sessionFactory.getCurrentSession().get(
				ApplicationReview.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<ApplicationReview> getReviewsByApplication(ApplicationForm application) {
		return sessionFactory.getCurrentSession()
				.createCriteria(ApplicationReview.class)
				.add(Restrictions.eq("application", application)).list();
	}
	
}
