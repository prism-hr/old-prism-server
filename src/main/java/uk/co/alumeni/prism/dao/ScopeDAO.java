package uk.co.alumeni.prism.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.workflow.Scope;

@Repository
@SuppressWarnings("unchecked")
public class ScopeDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<PrismScope> getScopesDescending() {
        return (List<PrismScope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .setProjection(Projections.property("id")) //
                .addOrder(Order.desc("ordinal")) //
                .list();
    }

    public List<PrismScope> getEnclosingScopesDescending(PrismScope prismScope, PrismScope finalScope) {
        return (List<PrismScope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.le("ordinal", prismScope.ordinal())) //
                .add(Restrictions.ge("ordinal", finalScope.ordinal()))
                .addOrder(Order.asc("ordinal")) //
                .list();
    }

    public List<PrismScope> getParentScopesDescending(PrismScope prismScope, PrismScope finalScope) {
        return (List<PrismScope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.lt("ordinal", prismScope.ordinal())) //
                .add(Restrictions.ge("ordinal", finalScope.ordinal()))
                .addOrder(Order.asc("ordinal")) //
                .list();
    }

    public List<PrismScope> getChildScopesAscending(PrismScope prismScope, PrismScope finalScope) {
        return (List<PrismScope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.gt("ordinal", prismScope.ordinal())) //
                .add(Restrictions.le("ordinal", finalScope.ordinal()))
                .addOrder(Order.asc("ordinal")) //
                .list();
    }

}
