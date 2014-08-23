package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;

@Repository
@SuppressWarnings("unchecked")
public class AdvertDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Advert> getActiveAdverts(List<State> activeProgramStates, List<State> activeProjectStates) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("program"))
                                .add(Restrictions.in("program.state", activeProgramStates))) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("project"))
                                .add(Restrictions.in("project.state", activeProjectStates)))
                .add(Restrictions.ge("publishDate", new LocalDate()))) //
                .addOrder(Order.desc("sequenceIdentifier")) //
                .list();
    }

    public List<Advert> getRecommendedAdverts(User user, List<State> activeProgramStates, List<State> activeProjectStates) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("application.advert")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.applications", "application", JoinType.INNER_JOIN) //
                .createAlias("application.advert", "recommendedAdvert", JoinType.INNER_JOIN) //
                .createAlias("recommendedAdvert.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("recommendedAdvert.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.ne("application.user", user)) //
                .add(Restrictions.neProperty("advert", "application.advert")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("recommendedAdvert.program"))
                                .add(Restrictions.in("program.state", activeProgramStates))) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("recommendedAdvert.project"))
                                .add(Restrictions.in("project.state", activeProjectStates)))
                .add(Restrictions.ge("recommendedAdvert.publishDate", new LocalDate()))) //
                .addOrder(Order.desc("recommendedAdvert.sequenceIdentifier")) //
                .setMaxResults(25) //
                .list();
    }
    
}
