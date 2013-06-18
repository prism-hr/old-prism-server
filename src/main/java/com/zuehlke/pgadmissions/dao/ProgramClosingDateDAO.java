package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramInstance;

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
