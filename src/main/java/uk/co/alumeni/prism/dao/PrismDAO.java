package uk.co.alumeni.prism.dao;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_DOMICILE;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.AgeRange;
import uk.co.alumeni.prism.domain.Definition;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.display.DisplayPropertyConfiguration;

@Repository
public class PrismDAO {

    @Inject
    private SessionFactory sessionFactory;

    public AgeRange getAgeRange(Integer age) {
        return (AgeRange) sessionFactory.getCurrentSession().createCriteria(AgeRange.class)
                .add(Restrictions.disjunction()
                        .add(Restrictions.conjunction()
                                .add(Restrictions.isNull("lowerBound"))
                                .add(Restrictions.ge("upperBound", age)))
                        .add(Restrictions.conjunction()
                                .add(Restrictions.le("lowerBound", age))
                                .add(Restrictions.ge("upperBound", age)))
                        .add(Restrictions.conjunction()
                                .add(Restrictions.le("lowerBound", age))
                                .add(Restrictions.isNull("upperBound"))))
                .uniqueResult();
    }

    public PrismDisplayPropertyDefinition getDomicileDisplayPropertyByName(String name) {
        return (PrismDisplayPropertyDefinition) sessionFactory.getCurrentSession().createCriteria(DisplayPropertyConfiguration.class)
                .setProjection(Projections.property("definition.id"))
                .createAlias("definition", "definition", JoinType.INNER_JOIN)
                .add(Restrictions.eq("definition.category", SYSTEM_DOMICILE))
                .add(Restrictions.like("value", name, MatchMode.ANYWHERE))
                .add(Restrictions.eq("systemDefault", true))
                .setMaxResults(1)
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public <T extends Definition<?>> List<T> getDefinitions(Class<T> definitionClass) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(definitionClass)
                .addOrder(Order.asc("ordinal"))
                .list();
    }

}
