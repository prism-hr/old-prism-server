package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EntityService {
    
    @Autowired
    private EntityService entityDAO;

    public <T> T getById(Class<T> klass, int id) {
        return entityDAO.getById(klass, id);
    }

    public <T> T getBy(Class<T> klass, String propertyName, Object propertyValue) {
        return entityDAO.getBy(klass, propertyName, propertyValue);
    }
    
    public <T> T getDuplicateEntity(Class<T> klass, HashMap<String, Object> properties) {
        return (T) entityDAO.getDuplicateEntity(klass, properties);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> klass, HashMap<String, Object> properties) {
        try {
            T entity = (T) ConstructorUtils.invokeConstructor(klass, null);
            for (Map.Entry<String, Object> constraint : properties.entrySet()) {
                PropertyUtils.setProperty(entity, constraint.getKey(), constraint.getValue());
            }       
            return entity;
        } catch (Exception e) {
            throw new Error("Could not create new resource of type: " + klass.getSimpleName(), e);
        }
    }
    
    public <T> T getOrCreate(Class<T> klass, HashMap<String, Object> properties) throws Exception {
        T entity = getDuplicateEntity(klass, properties);
        if (entity == null) {
            entity = create(klass, properties);
        }
        return entity;
    }

    public void save(Object entity) {
        entityDAO.save(entity);
    }

    public void update(Object entity) {
        entityDAO.update(entity);
    }
    
    public void delete(Object entity) {
        entityDAO.delete(entity);
    }

}
