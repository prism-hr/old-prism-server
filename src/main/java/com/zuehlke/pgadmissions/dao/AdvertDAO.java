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
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
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
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("program"))
                                .add(Restrictions.in("program.state", activeProgramStates))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("project"))
                                .add(Restrictions.in("project.state", activeProjectStates)))) //
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
                .add(Restrictions.isNotNull("application.submittedTimestamp")) //
                .add(Restrictions.neProperty("advert", "application.advert")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("recommendedAdvert.program"))
                                .add(Restrictions.in("program.state", activeProgramStates))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("recommendedAdvert.project"))
                                .add(Restrictions.in("project.state", activeProjectStates)))) //
                .addOrder(Order.desc("application.submittedTimestamp")) //
                .addOrder(Order.desc("recommendedAdvert.sequenceIdentifier")) //
                .setMaxResults(25) //
                .list();
    }

    public List<Advert> getAdvertsWithElapsedClosingDates(LocalDate baseline) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .createAlias("closingDate", "closingDate", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("closingDates", "otherClosingDate", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction()
                        .add(Restrictions.lt("closingDate.closingDate", baseline))
                        .add(Restrictions.conjunction()
                                .add(Restrictions.isNull("closingDate.id"))
                                .add(Restrictions.ge("otherClosingDate.closingDate", baseline)))) //
                .list();
    }

    public AdvertClosingDate getNextAdvertClosingDate(Advert advert, LocalDate baseline) {
        return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(AdvertClosingDate.class) //
                .setProjection(Projections.min("closingDate")) //
                .add(Restrictions.eq("id", advert.getId())) //
                .add(Restrictions.ge("closingDate", baseline)) //
                .uniqueResult();
    }

}
