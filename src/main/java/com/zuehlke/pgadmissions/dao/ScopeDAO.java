package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Scope;

@Repository
@SuppressWarnings("unchecked")
public class ScopeDAO {
    
    @Autowired
    private EntityDAO entityDAO;
    
    @Autowired
    private SessionFactory sessionFactory;

    public List<Scope> getScopesAscending() {
        return (List<Scope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .addOrder(Order.asc("precedence")) //
                .list();
    }
    
    public List<Scope> getScopesDescending() {
        return (List<Scope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .addOrder(Order.desc("precedence")) //
                .list();
    }
    
    public List<Scope> getParentScopes(Scope scope) {
        return (List<Scope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .add(Restrictions.lt("precedence", scope.getPrecedence())) //
                .addOrder(Order.desc("precedence")) //
                .list();
    }
    
    public List<Scope> getChildScopes(Scope scope) {
        return (List<Scope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .add(Restrictions.gt("precedence", scope.getPrecedence())) //
                .addOrder(Order.asc("precedence")) //
                .list();
    }
    
}
