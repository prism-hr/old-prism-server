package com.zuehlke.pgadmissions.dao;

import java.io.Serializable;

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

}
