package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.WorkflowResource;

@Repository
@SuppressWarnings("unchecked")
public class ConfigurationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends WorkflowResource> T getConfiguration(Class<T> entityClass, Resource resource, String uniqifierReference, WorkflowDefinition uniqifier) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq(uniqifierReference, uniqifier)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("system", resource.getSystem())) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

}
