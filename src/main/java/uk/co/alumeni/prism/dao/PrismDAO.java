package uk.co.alumeni.prism.dao;

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

import javax.inject.Inject;
import java.util.List;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_DOMICILE;

@Repository
public class PrismDAO {

    @Inject
    private SessionFactory sessionFactory;

    public AgeRange getAgeRangeFromAge(Integer age) {
        return (AgeRange) sessionFactory.getCurrentSession().createCriteria(AgeRange.class)
                .add(Restrictions.le("lowerBound", age))
                .add(Restrictions.disjunction()
                        .add(Restrictions.ge("upperBound", age))
                        .add(Restrictions.isNull("upperBound")))
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public <T extends Definition<?>> List<T> getDefinitions(Class<T> definitionClass) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(definitionClass)
                .addOrder(Order.asc("ordinal"))
                .list();
    }

}
