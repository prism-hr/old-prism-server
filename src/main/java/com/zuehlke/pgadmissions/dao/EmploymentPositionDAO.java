package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.EmploymentPosition;

@Repository
public class EmploymentPositionDAO {

	private final SessionFactory sessionFactory;
	
	public EmploymentPositionDAO(){
		this(null);
	}
	
	@Autowired
	public EmploymentPositionDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}
	public void delete(EmploymentPosition position) {
		sessionFactory.getCurrentSession().delete(position);
		
	}
	public void save(EmploymentPosition employmentPosition) {
		sessionFactory.getCurrentSession().saveOrUpdate(employmentPosition);
		
	}
	public EmploymentPosition getEmploymentPositionById(Integer id) {
		return (EmploymentPosition) sessionFactory.getCurrentSession().get(EmploymentPosition.class, id);
	}

}
