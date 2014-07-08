package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;

@Service
@Transactional
public class EntityService {

    @Autowired
    private EntityDAO entityDAO;

    public <T> T getById(Class<T> klass, int id) {
        return entityDAO.getById(klass, id);
    }

    public <T> T getByCode(Class<T> klass, String code) {
        return entityDAO.getByCode(klass, code);
    }

    public <T> T getByProperty(Class<T> klass, String propertyName, Object propertyValue) {
        return entityDAO.getByProperty(klass, propertyName, propertyValue);
    }

    public <T> T getByPropertyNotNull(Class<T> klass, String propertyName) {
        return entityDAO.getByPropertyNotNull(klass, propertyName);
    }

    public <T> T getByProperties(Class<T> klass, HashMap<String, Object> properties) {
        return entityDAO.getByProperties(klass, properties);
    }

    public <T> List<T> list(Class<T> klass) {
        return entityDAO.list(klass);
    }
    
    public <T> List<T> listByProperty(Class<T> klass, String propertyName, Object propertyValue) {
        return entityDAO.listByProperty(klass, propertyName, propertyValue);
    }

    public <T extends IUniqueEntity> T getDuplicateEntity(T uniqueResource) {
        return (T) entityDAO.getDuplicateEntity(uniqueResource);
    }

    public <T extends IUniqueEntity> T getOrCreate(T transientResource) {
        T persistentResource = (T) getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
            persistentResource = transientResource;
        }
        return persistentResource;
    }
    
    public <T extends IUniqueEntity> T createOrUpdate(T transientResource) {
        T persistentResource = (T) getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
        } else {
            try {
                Object persistentId = PropertyUtils.getSimpleProperty(persistentResource, "id");
                PropertyUtils.setSimpleProperty(transientResource, "id", persistentId);   
                merge(transientResource);
                evict(persistentResource);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return transientResource;
    }

    public java.io.Serializable save(Object entity) {
        return entityDAO.save(entity);
    }

    public void save(Object... entities) {
        for (Object entity : entities) {
            entityDAO.save(entity);
        }
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
    
}
