package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Qualification;

@Repository
public class QualificationDAO {

    private SessionFactory sessionFactory;

    public QualificationDAO() {
    }

    @Autowired
    public QualificationDAO(SessionFactory sessionFactory) {
        super();
        this.sessionFactory = sessionFactory;
    }

    public Qualification getById(Integer id) {
        return (Qualification) sessionFactory.getCurrentSession().get(Qualification.class, id);
    }

    public void delete(Qualification qualification) {
        sessionFactory.getCurrentSession().delete(qualification);
    }

}
