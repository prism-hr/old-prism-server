package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

@Repository
public class ScopeDAO {
    
    @Autowired
    private EntityDAO entityDAO;
    
    @Autowired
    private SessionFactory sessionFactory;
 
    public <T extends Resource> Scope getByResourceClass(Class<T> resourceClass) {
        return entityDAO.getByProperty(Scope.class, "id", PrismScope.getResourceScope(resourceClass));
    }
    
    @SuppressWarnings("unchecked")
    public List<Scope> getScopesAscending() {
        return (List<Scope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .addOrder(Order.asc("precedence")) //
                .list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Scope> getScopesDescending() {
        return (List<Scope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .addOrder(Order.desc("precedence")) //
                .list();
    }
    
    public <T extends Resource> List<Scope> getParentScopes(Class<T> resourceClass) {
        Scope scope = getByResourceClass(resourceClass);
        return getParentScopes(scope);
    }
    
    public <T extends Resource> List<Scope> getChildScopes(Class<T> resourceClass) {
        Scope scope = getByResourceClass(resourceClass);
        return getChildScopes(scope);
    }
    
    @SuppressWarnings("unchecked")
    private List<Scope> getParentScopes(Scope scope) {
        return (List<Scope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .add(Restrictions.lt("precedence", scope.getPrecedence())) //
                .addOrder(Order.desc("precedence")) //
                .list();
    }
    
    @SuppressWarnings("unchecked")
    private List<Scope> getChildScopes(Scope scope) {
        return (List<Scope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .add(Restrictions.gt("precedence", scope.getPrecedence())) //
                .addOrder(Order.asc("precedence")) //
                .list();
    }
    
}
