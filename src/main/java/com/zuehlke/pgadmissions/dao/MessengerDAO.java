package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Messenger;

@Repository
public class MessengerDAO {

	private final SessionFactory sessionFactory;
	
	MessengerDAO(){
		this(null);
	}
	
	@Autowired
	public MessengerDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional
	public void save(Messenger messenger) {
		sessionFactory.getCurrentSession().saveOrUpdate(messenger);
	}

	
}
