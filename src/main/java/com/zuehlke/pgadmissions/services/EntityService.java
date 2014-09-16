package com.zuehlke.pgadmissions.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.utils.IntrospectionUtils;

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
        return entityDAO.getDuplicateEntity(uniqueResource);
    }

    public <T extends IUniqueEntity> T getOrCreate(T transientResource) throws DeduplicationException {
        T persistentResource = getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
            persistentResource = transientResource;
        }
        return persistentResource;
    }

    public <T extends IUniqueEntity> T createOrUpdate(T transientResource) throws DeduplicationException {
        T persistentResource = getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
        } else {
            Object persistentId = IntrospectionUtils.getProperty(persistentResource, "id");
            IntrospectionUtils.setProperty(transientResource, "id", persistentId);
            evict(persistentResource);
            update(transientResource);
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

    public void evict(Object... entities) {
        for (Object entity : entities) {
            entityDAO.evict(entity);
        }
    }

    public void flushAndEvict(Object... entities) {
        flush();
        evict(entities);
    }

    public <T> void deleteAll(Class<T> classReference) {
        entityDAO.deleteAll(classReference);
    }

}
