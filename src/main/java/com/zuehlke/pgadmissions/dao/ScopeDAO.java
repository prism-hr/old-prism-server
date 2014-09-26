package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

@Repository
@SuppressWarnings("unchecked")
public class ScopeDAO {
    
    @Autowired
    private EntityDAO entityDAO;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    public List<PrismScope> getScopesDescending() {
        return (List<PrismScope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .setProjection(Projections.property("id")) //
                .addOrder(Order.desc("precedence")) //
                .list();
    }
  
    public List<PrismScope> getParentScopesDescending(PrismScope scopeId) {
        return (List<PrismScope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.lt("precedence", scopeId.getPrecedence())) //
                .addOrder(Order.desc("precedence")) //
                .list();
    }
    
}