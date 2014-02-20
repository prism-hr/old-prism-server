package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ProgramFeed;

@Repository
@SuppressWarnings("unchecked")
public class ProgramFeedDAO {

    private final SessionFactory sessionFactory;

    public ProgramFeedDAO() {
        this(null);
    }

    @Autowired
    public ProgramFeedDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<ProgramFeed> getAllProgramFeeds() {
        return sessionFactory.getCurrentSession().createCriteria(ProgramFeed.class).list();
    }

}
