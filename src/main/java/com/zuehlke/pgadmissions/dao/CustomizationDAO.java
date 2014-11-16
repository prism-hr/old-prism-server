package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismWorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResource;

@Repository
@SuppressWarnings("unchecked")
public class CustomizationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends WorkflowResource> T getConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale, PrismProgramType programType,
            String keyIndex, WorkflowDefinition keyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(getFilterCondition(resource, locale, programType, keyValue.getScope().getId())) //
                .add(Restrictions.eq(keyIndex, keyValue)) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<DisplayPropertyConfiguration> getDisplayProperties(Resource resource, PrismLocale locale, PrismProgramType programType,
            PrismDisplayPropertyCategory displayPropertyCategory, PrismScope propertyScope) {
        return (List<DisplayPropertyConfiguration>) sessionFactory.getCurrentSession().createCriteria(DisplayPropertyConfiguration.class) //
                .createAlias("displayPropertyDefinition", "displayPropertyDefinition", JoinType.INNER_JOIN) //
                .add(getFilterCondition(resource, locale, programType, propertyScope)) //
                .add(Restrictions.eq("displayPropertyDefinition.displayPropertyCategory", displayPropertyCategory)) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public <T extends WorkflowDefinition> List<WorkflowDefinition> listDefinitions(Class<T> entityClass, PrismScope scope) {
        return (List<WorkflowDefinition>) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .createAlias("scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("scope.id", scope)) //
                .addOrder(Order.asc("scope.id")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public <T extends WorkflowResource> List<T> listConfigurations(PrismWorkflowConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType) {
        Criterion programTypeConstraint = programType == null ? Restrictions.isNull("programType") : Restrictions.in("programType",
                Lists.newArrayList(programType, getSystemProgramType()));

        Criterion localeConstraint = resource.getResourceScope() == PrismScope.SYSTEM ? Restrictions
                .in("locale", Lists.newArrayList(locale, getSystemLocale())) : Restrictions.isNull("locale");

        return (List<T>) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("system", resource.getSystem())) //
                                .add(localeConstraint) //
                                .add(programTypeConstraint)) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("institution", resource.getInstitution())) //
                                .add(programTypeConstraint)) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.asc(configurationType.getDefinitionPropertyName())) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public <T extends WorkflowConfiguration> void restoreDefaultConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale,
            PrismProgramType programType) {
        String localeConstraint = locale == null ? "" : "and locale = " + locale.name() + " ";
        String programTypeConstraint = programType == null ? "" : "and programType = " + programType.name();
        sessionFactory.getCurrentSession().createQuery( //
                "delete " + entityClass.getClass().getSimpleName() + " " //
                        + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + localeConstraint //
                        + programTypeConstraint) //
                .setParameter("resource", resource) //
                .executeUpdate();
    }

    public <T extends WorkflowConfiguration> T getConfigurationStrict(Class<T> entityClass, Resource resource, PrismLocale locale, PrismProgramType programType) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .add(Restrictions.eq("locale", locale)) //
                .add(Restrictions.eqOrIsNull("programType", programType)) //
                .uniqueResult();
    }

    public <T extends WorkflowConfiguration> T getConfigurationStrict(Class<T> entityClass, Resource resource, PrismLocale locale,
            PrismProgramType programType, String keyIndex, WorkflowDefinition keyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq(keyIndex, keyValue)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .add(Restrictions.eq("locale", locale)) //
                .add(Restrictions.eqOrIsNull("programType", programType)) //
                .uniqueResult();
    }

    public <T extends WorkflowConfiguration> void restoreGlobalConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale,
            PrismProgramType programType) {
        PrismScope resourceScope = resource.getResourceScope();

        String programTypeConstraint = programType == null ? "and programType is null " : "and programType = :programType ";
        String localeConstraint = locale == null ? "and locale is null " : " and locale = :locale ";

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "delete " + entityClass.getSimpleName() + " " //
                            + "and (institution in (" //
                            + "from Institution " //
                            + "where system = :system) " //
                            + programTypeConstraint //
                            + localeConstraint + " "//
                            + "or program in (" //
                            + "from Program " //
                            + "where system = :system) " //
                            + programTypeConstraint //
                            + localeConstraint + ")");
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "delete " + entityClass.getSimpleName() + " " //
                            + "and (program in (" //
                            + "from Program " //
                            + "where institution = :institution) " //
                            + programTypeConstraint //
                            + localeConstraint + ")");
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCaseName(), resource);

        if (programType != null) {
            query.setParameter("programType", programType);
        }

        if (locale != null) {
            query.setParameter("locale", locale);
        }

        query.executeUpdate();
    }

    private Junction getFilterCondition(Resource resource, PrismLocale locale, PrismProgramType programType, PrismScope definitionScope) {
        Criterion programTypeRestriction;
        if (programType == null || definitionScope.getPrecedence() < PROGRAM.getPrecedence()) {
            programTypeRestriction = Restrictions.isNull("programType");
        } else {
            programTypeRestriction = Restrictions.in("programType", Lists.newArrayList(programType, getSystemProgramType()));
        }

        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("system", resource.getSystem())) //
                        .add(Restrictions.in("locale", Lists.newArrayList(locale, getSystemLocale()))) //
                        .add(programTypeRestriction)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(programTypeRestriction)) //
                .add(Restrictions.eq("program", resource.getProgram()));
    }

}
