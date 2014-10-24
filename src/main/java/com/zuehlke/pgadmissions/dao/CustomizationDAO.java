package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
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

    public <T extends WorkflowResource> T getConfiguration(Class<T> entityClass, Resource resource, PrismLocale userLocale, String keyIndex,
            WorkflowDefinition keyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(getFilterCondition(resource, userLocale, keyValue.getScope().getId())) //
                .add(Restrictions.eq(keyIndex, keyValue)) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("programType"))
                .addOrder(Order.desc("system")) //
                .addOrder(Order.desc("programType")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public <T extends WorkflowResource> T getConfigurationToEdit(Class<T> entityClass, Resource resource, PrismProgramType programType, PrismLocale locale,
            String keyIndex, WorkflowDefinition keyValue) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq(keyIndex, keyValue)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource));
        
        if (programType == null) {
            criteria.add(Restrictions.isNull("programType"));
        } else {
            criteria.add(Restrictions.eq("programType", programType));
        }
        
        if (locale == null) {
            criteria.add(Restrictions.isNull("locale"));
        } else {
            criteria.add(Restrictions.eq("locale", locale));
        }
        
        return (T) criteria.uniqueResult();
    }

    public List<DisplayProperty> getDisplayProperties(Resource resource, PrismLocale userLocale, PrismDisplayCategory category) {
        return (List<DisplayProperty>) sessionFactory.getCurrentSession().createCriteria(DisplayProperty.class) //
                .add(getFilterCondition(resource, userLocale, category.getScope())) //
                .add(Restrictions.eq("displayCategory.id", category)) //
                .addOrder(Order.asc("propertyIndex")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public <T extends WorkflowResource> void restoreGlobalConfiguration(Class<T> entityClass, Resource resource, PrismProgramType programType,
            PrismLocale locale, String keyIndex, WorkflowDefinition keyValue) {
        Query query;
        PrismScope resourceScope = resource.getResourceScope();
        
        String programTypeConstraint = programType == null ? "" : "and programType = :programType ";
        String localeConstraint = locale == null ? "" : "and locale = :locale) ";
        
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "delete :workflowResourceClass " //
                        + "where :keyIndex = :keyValue " //
                        + "and (institution in (" //
                            + "from Institution " //
                            + "where system = :system " //
                                + programTypeConstraint //
                                + localeConstraint //
                        + "or program in (" //
                            + "from Program " //
                            + "where system = :system " //
                                + programTypeConstraint //
                                + localeConstraint + ")") //
                    .setParameter("workflowResourceClass", entityClass.getSimpleName()) //
                    .setParameter("keyIndex", keyValue) //
                    .setParameter("keyValue", keyValue) //
                    .setParameter(resourceScope.getLowerCaseName(), resource);
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "delete :workflowResourceClass " //
                        + "where :keyIndex = :keyValue " //
                        + "and (program in (" //
                            + "from Program " //
                            + "where institution = :institution " //
                                + programTypeConstraint //
                                + localeConstraint + ")");
        } else {
            throw new Error();
        }
        
        query.setParameter("workflowResourceClass", entityClass.getSimpleName()) //
                .setParameter("keyIndex", keyValue) //
                .setParameter("keyValue", keyValue) //
                .setParameter("institution", resource);
        
        if (programType != null) {
            query.setParameter("programType", programType);
        }
        
        if (locale != null) {
            query.setParameter("locale", locale);
        }
        
        query.executeUpdate();
    }

    private Junction getFilterCondition(Resource resource, PrismLocale userLocale, PrismScope configuredScope) {
        Junction restriction = Restrictions.disjunction();
        Junction restrictionSystem = Restrictions.conjunction();
        Junction restrictionInstitution = Restrictions.conjunction();

        restrictionSystem.add(Restrictions.eq("system", resource.getSystem()));
        restrictionInstitution.add(Restrictions.eq("institution", resource.getInstitution()));

        if (!configuredScope.isProgramTypeConfigurationOwner()) {
            PrismProgramType programType = resource.getProgram().getProgramType().getPrismProgramType();
            restrictionSystem.add(Restrictions.eqOrIsNull("programType", programType));
            restrictionInstitution.add(Restrictions.eqOrIsNull("programType", programType));
        }

        restrictionSystem.add(Restrictions.eq("locale", resource.getResourceScope() == SYSTEM ? userLocale : resource.getLocale()));

        restriction.add(restrictionSystem);
        restriction.add(restrictionInstitution);
        restriction.add(Restrictions.eq("program", resource.getProgram()));

        return restriction;
    }

}
