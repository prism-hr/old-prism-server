package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationsFilter;

@Repository
public class ApplicationsFilterDAO {

    private final SessionFactory sessionFactory;

    ApplicationsFilterDAO() {
        this(null);
    }

    @Autowired
    public ApplicationsFilterDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    public void save(ApplicationsFilter applicationsFilter) {
        sessionFactory.getCurrentSession().saveOrUpdate(applicationsFilter);

    }

    public ApplicationsFilter getApplicationsFilterById(Integer id) {
        return (ApplicationsFilter) sessionFactory.getCurrentSession().get(ApplicationsFilter.class, id);
    }

    public void removeFilter(ApplicationsFilter filter) {
        sessionFactory.getCurrentSession().delete(filter);
    }

}
