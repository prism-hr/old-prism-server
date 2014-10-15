package com.zuehlke.pgadmissions.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;

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
    public <T> T getByProperties(Class<T> klass, Map<String, Object> properties) {
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
    public <T> List<T> list(Class<T> klass) {
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
    public <T extends IUniqueEntity> T getDuplicateEntity(T uniqueResource) throws DeduplicationException {
        IUniqueEntity.ResourceSignature signature = uniqueResource.getResourceSignature();
        
        Class<T> resourceClass = (Class<T>) uniqueResource.getClass();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceClass);

        HashMap<String, Object> properties = signature.getProperties();
        if (properties.size() > 0) {
            for (Map.Entry<String, Object> property : properties.entrySet()) {
                String key = property.getKey();
                if (key == null) {
                    throw new Error("Tried to deduplicate entity with null property key " + property.getKey());
                }
                Object value = property.getValue();
                criteria.add(value == null ? Restrictions.isNull(key) : Restrictions.eq(key, value));
            }

            HashMultimap<String, Object> exclusions = signature.getExclusions();
            for (String key : exclusions.keySet()) {
                criteria.add(Restrictions.not(Restrictions.in(key, exclusions.get(key))));
            }

            try {
                return (T) criteria.uniqueResult();
            } catch (Exception e) {
                throw new DeduplicationException("Tried to deduplicate entity " + signature.toString() + " with more than one potential duplicate", e);
            }
        }

        throw new Error("Tried to deduplicate entity " + resourceClass.getSimpleName() + " with empty resource signature");
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

    public Object merge(Object entity) {
        return sessionFactory.getCurrentSession().merge(entity);
    }

    public <T> void deleteAll(Class<T> classReference) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete " + classReference.getSimpleName()) //
                .executeUpdate();
    }

}
