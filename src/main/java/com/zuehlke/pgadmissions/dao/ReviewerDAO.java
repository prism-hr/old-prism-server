package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;

@Repository
public class ReviewerDAO {

    private final SessionFactory sessionFactory;

    public ReviewerDAO() {
        this(null);
    }

    @Autowired
    public ReviewerDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Reviewer reviewer) {
        sessionFactory.getCurrentSession().saveOrUpdate(reviewer);
    }

    public Reviewer getReviewerById(Integer id) {
        return (Reviewer) sessionFactory.getCurrentSession().get(Reviewer.class, id);
    }

    public Reviewer getReviewerByUserAndReviewRound(RegisteredUser user, ReviewRound reviewRound) {
        return (Reviewer) sessionFactory.getCurrentSession().createCriteria(Reviewer.class)
        		.add(Restrictions.eq("user", user))
        		.add(Restrictions.eq("reviewRound", reviewRound)).uniqueResult();
    }

}