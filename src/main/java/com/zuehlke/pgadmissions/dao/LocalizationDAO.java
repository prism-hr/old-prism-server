package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.DisplayProperty;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.WorkflowResource;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

@Repository
@SuppressWarnings("unchecked")
public class LocalizationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends WorkflowResource> T getConfiguration(Class<T> entityClass, Resource resource, String keyIndex, WorkflowDefinition keyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq(keyIndex, keyValue)) //
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

    public <T extends WorkflowResource> T getConfigurationStrict(Class<NotificationConfiguration> entityClass, Resource resource, String keyIndex,
            WorkflowDefinition keyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq(keyIndex, keyValue)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .uniqueResult();
    }

    public List<DisplayProperty> getDisplayProperties(Resource resource, PrismLocale locale, PrismDisplayCategory... categories) {
        System system = resource.getSystem();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DisplayProperty.class);

        Disjunction categoriesFilter = Restrictions.disjunction();
        for (PrismDisplayCategory category : categories) {
            categoriesFilter.add(Restrictions.eq("displayCategory", category));
        }

        return criteria.add(categoriesFilter) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("system", system)) //
                                .add(Restrictions.eq("locale", locale))) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.asc("propertyIndex")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.asc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("propertyDefault")) //
                .list();
    }

    public <T extends WorkflowResource> void restoreGlobalizedConfiguration(Class<T> workflowResourceClass, String keyIndex, WorkflowDefinition keyValue,
            Resource globalizedResource, PrismScope globalizedResourceScope) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete :workflowResourceClass " //
                        + "where :keyIndex = :keyValue " //
                        + "and (institution in (" //
                                + "from institution " //
                                + "where system = :system) " //
                        + "or program in (" //
                                + "from program " //
                                + "where system = :system) " //
                        + "or program in (" //
                                + "from program " //
                                + "where institution = :institution))") //
                .setParameter("workflowResourceClass", workflowResourceClass.getSimpleName()) //
                .setParameter("keyIndex", keyValue) //
                .setParameter("keyValue", keyValue) //
                .setParameter("system", globalizedResourceScope == PrismScope.SYSTEM ? globalizedResource : null) //
                .setParameter("institution", globalizedResourceScope == PrismScope.INSTITUTION ? globalizedResource : null) //
                .executeUpdate();
    }

}
