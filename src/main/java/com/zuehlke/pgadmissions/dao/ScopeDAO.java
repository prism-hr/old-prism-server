package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;

@Repository
public class ScopeDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
 
    public <T extends Resource> Scope getByType(Class<T> resourceType) {
        return (Scope) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .add(Restrictions.eq("id", PrismScope.valueOf(resourceType.getSimpleName().toUpperCase()))) //
                .uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<Scope> getParentScopes(Scope scope) {
        return (List<Scope>) sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .add(Restrictions.lt("precedence", scope.getPrecedence())) //
                .addOrder(Order.desc("precedence")) //
                .list();
    }
    
    public <T extends Resource> List<Scope> getParentScopesByType(Class<T> resourceType) {
        Scope scope = getByType(resourceType);
        return getParentScopes(scope);
    }
    
}
