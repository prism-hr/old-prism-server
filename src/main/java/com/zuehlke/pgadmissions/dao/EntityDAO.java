package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EntityDAO {

	private SessionFactory sessionFactory;
	
    public EntityDAO() {
    }

    @Autowired
    public EntityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public Object getById(Class<?> klass, int id) {
        return sessionFactory.getCurrentSession().createCriteria(klass)
                .add(Restrictions.eq("id", id)).uniqueResult();
    }
    
}
