package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ProgramClosingDate;

@Repository
@SuppressWarnings("unchecked")
public class ProgramClosingDateDAO {

    private final SessionFactory sessionFactory;

    ProgramClosingDateDAO() {
        this(null);
    }

    @Autowired
    public ProgramClosingDateDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ProgramClosingDate programClosingDate) {
        sessionFactory.getCurrentSession().saveOrUpdate(programClosingDate);
    }
}
