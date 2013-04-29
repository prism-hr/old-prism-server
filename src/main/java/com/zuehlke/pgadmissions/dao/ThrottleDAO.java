package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Throttle;

@Repository
public class ThrottleDAO {
	
	private final SessionFactory sessionFactory;
	
	public ThrottleDAO() {
		this(null);
	}
	
	@Autowired
	public ThrottleDAO(SessionFactory sessionFactory) {
		this.sessionFactory=sessionFactory;
	}
	
	public void save(Throttle throttle) {
		sessionFactory.getCurrentSession().saveOrUpdate(throttle);
	}
	
	public void update(Throttle throttle) {
		sessionFactory.getCurrentSession().update(throttle);
	}
	
	
	public Throttle get() {
		return (Throttle) sessionFactory.getCurrentSession()
				.createCriteria(Throttle.class).setMaxResults(1).uniqueResult();
	}
	
	public Throttle getById(Integer id) {
		return (Throttle) sessionFactory.getCurrentSession()
				.get(Throttle.class, id);
	}
}
