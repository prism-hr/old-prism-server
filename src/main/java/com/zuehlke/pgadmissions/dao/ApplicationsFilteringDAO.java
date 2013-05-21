package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;

@Repository
public class ApplicationsFilteringDAO {

    private final SessionFactory sessionFactory;

    public ApplicationsFilteringDAO() {
        this(null);
    }

    @Autowired
    public ApplicationsFilteringDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public ApplicationsFiltering merge(ApplicationsFiltering filtering) {
        return (ApplicationsFiltering) sessionFactory.getCurrentSession().merge(filtering);
    }
}
