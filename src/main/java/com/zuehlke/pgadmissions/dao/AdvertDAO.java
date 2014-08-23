package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.State;

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
    
}
