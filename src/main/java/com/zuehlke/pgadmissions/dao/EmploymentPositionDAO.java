package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.EmploymentPosition;

@Repository
public class EmploymentPositionDAO {

	private final SessionFactory sessionFactory;
	EmploymentPositionDAO(){
		this(null);
	}
	@Autowired
	public EmploymentPositionDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}
	public void delete(EmploymentPosition position) {
		sessionFactory.getCurrentSession().delete(position);
		
	}

}
