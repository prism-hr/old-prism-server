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
    

    public Object getById(Class<?> klass, int id) {
        return entityDAO.getById(klass, id);
    }
    
}
