package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.IUniqueResource;
import com.zuehlke.pgadmissions.domain.ResourceDynamic;

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

    public <T extends IUniqueResource> T getDuplicateEntity(T resource) {
        return (T) entityDAO.getDuplicateEntity(resource);
    }

    public <T extends IUniqueResource> T getOrCreate(T transientResource) {
        T persistentResource = (T) getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
            persistentResource = transientResource;
        }
        return persistentResource;
    }

    public java.io.Serializable save(Object entity) {
        return entityDAO.save(entity);
    }

    public void update(Object entity) {
        entityDAO.update(entity);
    }

    public void delete(Object entity) {
        entityDAO.delete(entity);
    }

    public LocalDate getResourceDueDate(ResourceDynamic resource, LocalDate customDueDateBaseline) {
        LocalDate dueDateBaseline = customDueDateBaseline;
        if (dueDateBaseline == null) {
            dueDateBaseline = resource.getDueDateBaseline();
        }
        // TODO: State duration service ... add the service level
        return dueDateBaseline;
    }

}
