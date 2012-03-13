package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Telephone;

@Repository
public class TelephoneDAO {

	private final SessionFactory sessionFactory;
	
	TelephoneDAO(){
		this(null);
	}
	
	@Autowired
	public TelephoneDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional
	public void save(Telephone telephone) {
		sessionFactory.getCurrentSession().saveOrUpdate(telephone);
	}

	
}
