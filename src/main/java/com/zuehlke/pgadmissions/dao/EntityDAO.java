package com.zuehlke.pgadmissions.dao;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.UniqueEntity.ResourceSignature;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@SuppressWarnings("unchecked")
public class EntityDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T> T getById(Class<T> klass, Object id) {
        return (T) sessionFactory.getCurrentSession().createCriteria(klass) //
                .add(Restrictions.eq("id", id)) //
                .uniqueResult();
    }

    public <T> T getByProperty(Class<T> klass, String propertyName, Object propertyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(klass) //
                .add(Restrictions.eq(propertyName, propertyValue)) //
                .uniqueResult();
    }

    public <T> T getByPropertyNotNull(Class<T> klass, String propertyName) {
        return (T) sessionFactory.getCurrentSession().createCriteria(klass) //
                .add(Restrictions.isNotNull(propertyName)) //
                .uniqueResult();
    }

    public <T> T getByProperties(Class<T> klass, Map<String, Object> properties) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(klass);

        for (String property : properties.keySet()) {
            if (properties.get(property) == null) {
                criteria.add(Restrictions.isNull(property));
            } else {
                criteria.add(Restrictions.eq(property, properties.get(property)));
            }
        }

        return (T) criteria.uniqueResult();
    }

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

    public <T extends UniqueEntity> T getDuplicateEntity(T uniqueResource) throws DeduplicationException {
        ResourceSignature resourceSignature = uniqueResource.getResourceSignature();

        Class<T> resourceClass = (Class<T>) uniqueResource.getClass();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceClass);

        HashMap<String, Object> properties = resourceSignature.getProperties();

        for (Map.Entry<String, Object> property : properties.entrySet()) {
            Object value = property.getValue();
            if (value == null) {
                criteria.add(Restrictions.isNull(property.getKey()));
            } else {
                criteria.add(Restrictions.eq(property.getKey(), property.getValue()));
            }
        }

        HashMultimap<String, Object> exclusions = resourceSignature.getExclusions();
        for (String key : exclusions.keySet()) {
            criteria.add(Restrictions.not(Restrictions.in(key, exclusions.get(key))));
        }

        return (T) criteria.uniqueResult();
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

    public void evict(Object entity) {
        sessionFactory.getCurrentSession().evict(entity);
    }

}
