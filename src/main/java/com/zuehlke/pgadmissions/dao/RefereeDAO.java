package com.zuehlke.pgadmissions.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class RefereeDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(ApplicationReferee referee) {
        sessionFactory.getCurrentSession().saveOrUpdate(referee);
    }

    public ApplicationReferee getRefereeById(Integer id) {
        return (ApplicationReferee) sessionFactory.getCurrentSession().get(ApplicationReferee.class, id);
    }

    public void delete(ApplicationReferee referee) {
        sessionFactory.getCurrentSession().delete(referee);
    }

    public List<Integer> getRefereesDueReminder() {
        // TODO use ApplicationFormUserRole
        Date dateWithSubtractedInterval = new Date();
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class)
                .setProjection(Projections.groupProperty("id"))
                .createAlias("application", "application", JoinType.INNER_JOIN)
                .createAlias("reference", "referenceComment", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.isNull("referenceComment.id"))
                .add(Restrictions.eq("declined", false))
                .add(Restrictions.isNotNull("user"))
                .add(Restrictions.not(Restrictions.in("application.status", new PrismState[]{PrismState.APPLICATION_WITHDRAWN,
                        PrismState.APPLICATION_APPROVED, PrismState.APPLICATION_REJECTED, PrismState.APPLICATION_UNSUBMITTED})))
                .add(Restrictions.le("lastNotified", dateWithSubtractedInterval)).list();
    }

    public List<ApplicationReferee> getRefereesWhoDidntProvideReferenceYet(Application form) {
        return (List<ApplicationReferee>) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class)
                .createAlias("reference", "referenceComment", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.isNull("referenceComment.id"))
                .add(Restrictions.eq("declined", false))
                .add(Restrictions.eq("application", form)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public ApplicationReferee getRefereeByUser(User user) {
        return (ApplicationReferee) sessionFactory.getCurrentSession().createCriteria(ApplicationReferee.class).add(Restrictions.eq("user", user)).uniqueResult();
    }

    public void refresh(ApplicationReferee referee) {
        sessionFactory.getCurrentSession().refresh(referee);
    }
    
}