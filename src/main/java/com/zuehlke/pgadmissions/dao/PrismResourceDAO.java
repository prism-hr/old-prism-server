package com.zuehlke.pgadmissions.dao;

import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.User;

@Repository
public class PrismResourceDAO {

    @Autowired
    EntityDAO entityDAO;

    private SessionFactory sessionFactory;

    public PrismResourceDAO() {
    }

    public PrismResource getDuplicateResource(Class<? extends PrismResource> klass, User creator, PrismResource parentResource, Entry<String, Object>...uniqueConstraints) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(klass) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(parentResource.getResourceType().toString().toLowerCase(), parentResource)) //
                .add(Restrictions.eq("user", creator)) //
                .add(Restrictions.eq("state.duplicatableState", false));
        
        for (Entry<String, Object> uniqueConstraint : uniqueConstraints) {
            criteria.add(Restrictions.eq(uniqueConstraint.getKey(), uniqueConstraint.getValue()));
        }
        
        return (PrismResource) criteria.uniqueResult();
    }
    
}
