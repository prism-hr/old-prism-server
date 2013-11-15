package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.InterviewVoteComment;

@Repository
public class InterviewVoteCommentDAO {

    private final SessionFactory sessionFactory;

    public InterviewVoteCommentDAO() {
        this(null);
    }

    @Autowired
    public InterviewVoteCommentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(InterviewVoteComment interviewVoteComment) {
        sessionFactory.getCurrentSession().saveOrUpdate(interviewVoteComment);
    }
    
}
