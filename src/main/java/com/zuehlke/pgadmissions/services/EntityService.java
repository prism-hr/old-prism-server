package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EntityDAO;

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

    public void save(Object entity) {
        entityDAO.save(entity);
    }

    public void update(Object entity) {
        entityDAO.update(entity);
    }

}
