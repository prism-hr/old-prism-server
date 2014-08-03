package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Filter;

@Repository
public class ApplicationsFilteringDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    public Filter merge(Filter filtering) {
        sessionFactory.getCurrentSession().saveOrUpdate(filtering);
        return filtering;
    }
    
}