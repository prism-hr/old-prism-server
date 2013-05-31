package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ProgramAdvert;

@Repository
public class ProgramAdvertDAO {

    private final SessionFactory sessionFactory;

    public ProgramAdvertDAO() {
        this(null);
    }

    @Autowired
    public ProgramAdvertDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ProgramAdvert programAdvert) {
        sessionFactory.getCurrentSession().saveOrUpdate(programAdvert);
    }

    public ProgramAdvert getProgramAdvertByProgramId(Integer programId) {
        return (ProgramAdvert) sessionFactory.getCurrentSession().createCriteria(ProgramAdvert.class).createCriteria("program")
                        .add(Restrictions.eq("id", programId)).uniqueResult();
    }

}
