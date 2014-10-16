package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayProperty;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResource;

@Repository
@SuppressWarnings("unchecked")
public class CustomizationDAO {

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

    public <T extends WorkflowResource> T getConfigurationStrict(Class<T> entityClass, Resource resource, String keyIndex, WorkflowDefinition keyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq(keyIndex, keyValue)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .uniqueResult();
    }

    public <T extends WorkflowResource> void restoreGlobalizedConfiguration(Class<T> workflowResourceClass, String keyIndex, WorkflowDefinition keyValue,
            Resource globalizedResource, PrismScope globalizedResourceScope) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete :workflowResourceClass " //
                        + "where :keyIndex = :keyValue " //
                        + "and (institution in (" //
                                + "from Institution " //
                                + "where system = :system " + "and locale = :locale) " //
                        + "or program in (" //
                                + "from Program " //
                                + "where system = :system " + "and locale = :locale) " //
                        + "or program in (" //
                                + "from Program " //
                                + "where institution = :institution " + "and locale = :locale))") //
                .setParameter("workflowResourceClass", workflowResourceClass.getSimpleName()) //
                .setParameter("keyIndex", keyValue) //
                .setParameter("keyValue", keyValue) //
                .setParameter("system", globalizedResourceScope == PrismScope.SYSTEM ? globalizedResource : null) //
                .setParameter("locale", globalizedResource.getLocale()) //
                .setParameter("institution", globalizedResourceScope == PrismScope.INSTITUTION ? globalizedResource : null) //
                .executeUpdate();
    }
    
    public List<DisplayProperty> getDisplayProperties(Resource resource, PrismLocale locale, PrismDisplayCategory category) {
        return (List<DisplayProperty>) sessionFactory.getCurrentSession().createCriteria(DisplayProperty.class) //
                .add(Restrictions.eq("displayCategory.id", category)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("system", resource.getSystem())) //
                                .add(Restrictions.in("locale", Arrays.asList(PrismLocale.getSystemLocale(), locale)))) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.asc("propertyIndex")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("propertyDefault")) //
                .list();
    }

}