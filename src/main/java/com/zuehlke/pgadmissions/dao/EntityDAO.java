package com.zuehlke.pgadmissions.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PrismResource;

@Repository
public class EntityDAO {

    private SessionFactory sessionFactory;

    public EntityDAO() {
    }

    @Autowired
    public EntityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T getById(Class<T> klass, int id) {
        return (T) sessionFactory.getCurrentSession().createCriteria(klass).add(Restrictions.eq("id", id)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public <T> T getBy(Class<T> klass, String propertyName, Object propertyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(klass).add(Restrictions.eq(propertyName, propertyValue)).uniqueResult();
    }

    public Serializable save(Object entity) {
        return sessionFactory.getCurrentSession().save(entity);
    }

    public void update(Object entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @SuppressWarnings("unchecked")
    public <T> T getDuplicateEntity(Class<T> klass, HashMap<String, Object> constraints) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(klass); 
        
        if (klass.isAssignableFrom(PrismResource.class)) {
            criteria.createAlias("state", "state", JoinType.INNER_JOIN);
            criteria.add(Restrictions.eq("state.duplicatableState", false));
        }

        for (Map.Entry<String, Object> uniqueConstraint : constraints.entrySet()) {
            criteria.add(Restrictions.eq(uniqueConstraint.getKey(), uniqueConstraint.getValue()));
        }
            
        return (T) criteria.uniqueResult();
    }
    
}
