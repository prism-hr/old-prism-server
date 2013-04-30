package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ReviewRound;

@Repository
public class ReviewRoundDAO {

	private final SessionFactory sessionFactory;

	public ReviewRoundDAO(){
		this(null);
	}
	
	@Autowired
	public ReviewRoundDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public ReviewRound getReviewRoundById(Integer id) {
		return (ReviewRound) sessionFactory.getCurrentSession().get(ReviewRound.class, id);
	}

	public void save(ReviewRound reviewRound) {
		sessionFactory.getCurrentSession().saveOrUpdate(reviewRound);
	}
}
