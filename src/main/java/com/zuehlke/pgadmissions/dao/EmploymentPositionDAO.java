package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.EmploymentPosition;

@Repository
public class EmploymentPositionDAO {

    @Autowired
	private SessionFactory sessionFactory;

	public EmploymentPosition getById(Integer id) {
        return (EmploymentPosition) sessionFactory.getCurrentSession().get(EmploymentPosition.class, id);
    }
	
	public void save(EmploymentPosition employmentPosition) {
        sessionFactory.getCurrentSession().saveOrUpdate(employmentPosition); 
    }

	public void delete(EmploymentPosition position) {
		sessionFactory.getCurrentSession().delete(position);		
	}

}
