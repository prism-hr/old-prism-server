package com.zuehlke.pgadmissions.services;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.mail.util.PropUtil;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.IUniqueResource;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceTransient;

@Service
@Transactional
public class EntityService {

    @Autowired
    private EntityDAO entityDAO;

    public <T> T getById(Class<T> klass, int id) {
        return entityDAO.getById(klass, id);
    }

    public <T> T getBy(Class<T> klass, String propertyName, Object propertyValue) {
        return entityDAO.getBy(klass, propertyName, propertyValue);
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

    public void save(Object entity) {
        entityDAO.save(entity);
    }

    public void update(Object entity) {
        entityDAO.update(entity);
    }

    public void delete(Object entity) {
        entityDAO.delete(entity);
    }

    public String generateNewResourceCode(PrismResourceTransient resource) {
        try {
            DateTime createdTimestamp = (DateTime) PropertyUtils.getSimpleProperty(resource, "createdTimestamp");
            return resource.getCodePrefix() + "-" + createdTimestamp.getYear() + "-" + String.format("%010d", resource.getId());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
