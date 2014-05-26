package com.zuehlke.pgadmissions.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.IDeduplicatableResource;

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
    public <T> T getDuplicateEntity(Class<T> klass, IDeduplicatableResource.UniqueResourceSignature signature) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(klass);
        Disjunction indexes = Restrictions.disjunction();
        
        List<HashMap<String, Object>> propertyWrapper = signature.getProperties();
        if (propertyWrapper.size() > 0) {
            for (HashMap<String, Object> properties : propertyWrapper) {
                Conjunction index = Restrictions.conjunction();
                for (Map.Entry<String, Object> property : properties.entrySet()) {
                    if (property.getKey() == null || property.getValue() == null) {
                        throw new Error("Invalid resource signature");
                    }
                    index.add(Restrictions.eq(property.getKey(), property.getValue()));
                }
                indexes.add(index);
            }
            for (Map.Entry<String, Object> exclusion : signature.getExclusions().entrySet()) {
                criteria.add(Restrictions.ne(exclusion.getKey(), exclusion.getValue()));
            }
            return (T) criteria.uniqueResult();
        }
        
        throw new Error("Invalid resource signature");
    }
    
    public void delete(Object entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
    
}
