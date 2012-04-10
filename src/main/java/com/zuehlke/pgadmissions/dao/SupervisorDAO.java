package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Supervisor;

@Repository
public class SupervisorDAO {

private final SessionFactory sessionFactory;
	
SupervisorDAO(){
		this(null);
	}
	
	@Autowired
	public SupervisorDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Supervisor getSupervisorWithId(Integer id) {
		return (Supervisor) sessionFactory.getCurrentSession().get(
				Supervisor.class, id);
	}
}
