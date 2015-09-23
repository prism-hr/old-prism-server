package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_DOMICILE;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.AgeRange;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;

@Repository
public class PrismDAO {

    @Inject
    private SessionFactory sessionFactory;

    public AgeRange getAgeRange(Integer age) {
        return (AgeRange) sessionFactory.getCurrentSession().createCriteria(AgeRange.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ge("lowerBound", age)) //
                        .add(Restrictions.isNull("lowerBound"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.le("upperBound", age)) //
                        .add(Restrictions.isNull("upperBound"))) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

    public PrismDisplayPropertyDefinition getDomicileDisplayPropertyByName(String name) {
        return (PrismDisplayPropertyDefinition) //
        sessionFactory.getCurrentSession().createCriteria(DisplayPropertyConfiguration.class) //
                .setProjection(Projections.property("definition.id")) //
                .createAlias("definition", "definition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("definition.category", SYSTEM_DOMICILE)) //
                .add(Restrictions.like("value", name, MatchMode.ANYWHERE)) //
                .add(Restrictions.eq("systemDefault", true)) //
                .setMaxResults(1) //
                .uniqueResult();
    }

}
