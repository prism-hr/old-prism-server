package com.zuehlke.pgadmissions.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;

@Service
@Transactional
public class EntityService {

    @Autowired
    private EntityDAO entityDAO;

    public <T> T getById(Class<T> klass, Object id) {
        return entityDAO.getById(klass, id);
    }

    public <T> T getByProperty(Class<T> klass, String propertyName, Object propertyValue) {
        return entityDAO.getByProperty(klass, propertyName, propertyValue);
    }

    public <T> T getByPropertyNotNull(Class<T> klass, String propertyName) {
        return entityDAO.getByPropertyNotNull(klass, propertyName);
    }

    public <T> T getByProperties(Class<T> klass, Map<String, Object> properties) {
        return entityDAO.getByProperties(klass, properties);
    }

    public <T> List<T> list(Class<T> klass) {
        return entityDAO.list(klass);
    }

    public <T> List<T> listByProperty(Class<T> klass, String propertyName, Object propertyValue) {
        return entityDAO.listByProperty(klass, propertyName, propertyValue);
    }

    public <T> List<T> listByProperties(Class<T> klass, Map<String, Object> properties) {
        return entityDAO.listByProperties(klass, properties);
    }

    public <T extends IUniqueEntity> T getDuplicateEntity(T uniqueResource) throws DeduplicationException {
        return (T) entityDAO.getDuplicateEntity(uniqueResource);
    }

    public <T extends IUniqueEntity> T getOrCreate(T transientResource) throws DeduplicationException {
        T persistentResource = (T) getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
            persistentResource = transientResource;
        }
        return persistentResource;
    }

    public <T extends IUniqueEntity> T createOrUpdate(T transientResource) throws DeduplicationException {
        T persistentResource = (T) getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
        } else {
            try {
                Object persistentId = PropertyUtils.getSimpleProperty(persistentResource, "id");
                PropertyUtils.setSimpleProperty(transientResource, "id", persistentId);
                evict(persistentResource);
                update(transientResource);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return transientResource;
    }

    public Serializable save(Object entity) {
        return entityDAO.save(entity);
    }

    public void update(Object entity) {
        entityDAO.update(entity);
    }

    public void delete(Object entity) {
        entityDAO.delete(entity);
    }

    public void flush() {
        entityDAO.flush();
    }

    public void clear() {
        entityDAO.clear();
    }

    public void merge(Object entity) {
        entityDAO.merge(entity);
    }

    public void evict(Object entity) {
        entityDAO.evict(entity);
    }
    
    public <T> void deleteAll(Class<T> classReference) {
        entityDAO.deleteAll(classReference);
    }
    
    public <T> Integer getNotNullValueCount(Class<T> entityClass, String property, Map<String, Object> filters) {
        return entityDAO.getNotNullValueCount(entityClass, property, filters);
    }
    
}
