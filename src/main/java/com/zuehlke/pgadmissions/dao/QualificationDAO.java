package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Qualification;

@Repository
public class QualificationDAO {

    @Autowired
	private SessionFactory sessionFactory;
	
    public Qualification getById(Integer id) {
        return (Qualification) sessionFactory.getCurrentSession().get(Qualification.class, id);
    }
    
    public void save(Qualification qualification) {
        sessionFactory.getCurrentSession().saveOrUpdate(qualification);
    }
    
	public void delete(Qualification qualification) {
		sessionFactory.getCurrentSession().delete(qualification);
	}
	
}
