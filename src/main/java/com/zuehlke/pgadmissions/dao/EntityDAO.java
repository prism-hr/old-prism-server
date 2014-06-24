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

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.IUniqueResource;

@Repository
public class EntityDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public <T> T getById(Class<T> klass, Object id) {
        return (T) sessionFactory.getCurrentSession().createCriteria(klass) //
                .add(Restrictions.eq("id", id)) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public <T> T getByProperty(Class<T> klass, String propertyName, Object propertyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(klass) //
                .add(Restrictions.eq(propertyName, propertyValue)) //
                .uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getByPropertyNotNull(Class<T> klass, String propertyName) {
        return (T) sessionFactory.getCurrentSession().createCriteria(klass) //
                .add(Restrictions.isNotNull(propertyName)) //
                .uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getByProperties(Class<T> klass, HashMap<String, Object> properties) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(klass);
        
        for (String property : properties.keySet()) {
            if (property == null) {
                criteria.add(Restrictions.isNull(property));
            } else {
                criteria.add(Restrictions.eq(property, properties.get(property)));
            }
        }
        
        return (T) criteria.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> klass) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(klass) //
                .list();
    }

    public Serializable save(Object entity) {
        return sessionFactory.getCurrentSession().save(entity);
    }

    public void update(Object entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @SuppressWarnings("unchecked")
    public <T extends IUniqueResource> T getDuplicateEntity(T uniqueResource) {
        IUniqueResource.ResourceSignature signature = uniqueResource.getResourceSignature(); 
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(uniqueResource.getClass());
        Disjunction indices = Restrictions.disjunction();
        
        List<HashMap<String, Object>> propertyWrapper = signature.getProperties();
        if (propertyWrapper.size() > 0) {
            for (HashMap<String, Object> properties : propertyWrapper) {
                Conjunction index = Restrictions.conjunction();
                for (Map.Entry<String, Object> property : properties.entrySet()) {
                    if (property.getKey() == null) {
                        throw new Error(IUniqueResource.UNIQUE_IDENTIFICATION_ERROR);
                    }
                    if (property.getValue() == null) {
                        index.add(Restrictions.isNull(property.getKey()));
                    } else {
                        index.add(Restrictions.eq(property.getKey(), property.getValue()));
                    }
                }
                indices.add(index);
            }
            
            criteria.add(indices);
            
            HashMultimap<String, Object> exclusions = signature.getExclusions();
            for (String key : exclusions.keySet()) {
                criteria.add(Restrictions.not(Restrictions.in(key, exclusions.get(key))));
            }
            
            return (T) criteria.uniqueResult();
        }

        throw new Error(IUniqueResource.UNIQUE_IDENTIFICATION_ERROR);
    }

    public void delete(Object entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    public void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    public void clear() {
        sessionFactory.getCurrentSession().clear();
    }

    public <T> T getByCode(Class<T> klass, String code) {
        return getByProperty(klass, "code", code);
    }
}
