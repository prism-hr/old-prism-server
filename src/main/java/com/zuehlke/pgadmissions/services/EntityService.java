package com.zuehlke.pgadmissions.services;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.IUniqueResource;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class EntityService {

    @Autowired
    private EntityDAO entityDAO;

    public <T> T getById(Class<T> klass, int id) {
        return entityDAO.getById(klass, id);
    }

    public <T> T getByCode(Class<T> klass, String code) {
        return entityDAO.getByProperty(klass, "code", code);
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

    public <T> List<T> getAll(Class<T> klass) {
        return entityDAO.getAll(klass);
    }

    public <T extends IUniqueResource> T getDuplicateEntity(T uniqueResource) {
        return (T) entityDAO.getDuplicateEntity(uniqueResource);
    }

    public <T extends IUniqueResource> T getOrCreate(T transientResource) {
        T persistentResource = (T) getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
            persistentResource = transientResource;
        }
        return persistentResource;
    }
    
    public <T extends IUniqueResource> void createOrUpdate(T transientResource) {
        T persistentResource = (T) getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
        } else {
            try {
                Object persistentId = PropertyUtils.getSimpleProperty(persistentResource, "id");
                PropertyUtils.setSimpleProperty(transientResource, "id", persistentId);
                update(transientResource);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
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

}
