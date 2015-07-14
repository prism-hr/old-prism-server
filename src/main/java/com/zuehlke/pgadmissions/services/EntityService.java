package com.zuehlke.pgadmissions.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

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

    public <T extends UniqueEntity> T getDuplicateEntity(T uniqueResource) throws DeduplicationException {
        return entityDAO.getDuplicateEntity(uniqueResource);
    }

    public <T extends UniqueEntity> T getOrCreate(T transientResource) throws DeduplicationException {
        T persistentResource = getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
            persistentResource = transientResource;
        }
        return persistentResource;
    }

    public <T extends UniqueEntity> T createOrUpdate(T transientResource) throws DeduplicationException {
        T persistentResource = getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
            persistentResource = transientResource;
        } else {
            persistentResource = replace(persistentResource, transientResource);
        }
        return persistentResource;
    }

    public <T extends UniqueEntity> T replace(T persistentResource, T transientResource) {
        persistentResource = overwriteProperties(persistentResource, transientResource);
        return persistentResource;
    }

    public Serializable save(Object entity) {
        return entityDAO.save(entity);
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
    
    public Object merge(Object entity) {
        return entityDAO.merge(entity);
    }

    public void evict(Object entity) {
        entityDAO.evict(entity);
    }

    public <T> void deleteAll(Class<T> classReference) {
        entityDAO.deleteAll(classReference);
    }

    @SuppressWarnings("unchecked")
    private <T extends UniqueEntity> T overwriteProperties(T persistentResource, T transientResource) {
        Object persistentId = PrismReflectionUtils.getProperty(persistentResource, "id");
        PrismReflectionUtils.setProperty(transientResource, "id", persistentId);
        return (T) merge(transientResource);
    }

}
