package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

@Repository
@SuppressWarnings("unchecked")
public class CustomizationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowDefinition definition) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), locale, programType)) //
                .add(Restrictions.eq(configurationType.getDefinitionPropertyName(), definition));

        addActiveVersionCriterion(configurationType, criteria);

        return (WorkflowConfiguration) criteria.addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, WorkflowDefinition definition) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), locale, programType)) //
                .add(Restrictions.eq(configurationType.getDefinitionPropertyName(), definition));

        addActiveVersionCriterion(configurationType, criteria);

        return (List<WorkflowConfiguration>) criteria.addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public WorkflowConfiguration getConfigurationWithVersion(PrismConfiguration configurationType, WorkflowDefinition definition, Integer version) {
        return (WorkflowConfiguration) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(Restrictions.eq(configurationType.getDefinitionPropertyName(), definition)) //
                .add(Restrictions.eq("version", version)) //
                .uniqueResult();
    }

    public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType) {
        String definitionReference = configurationType.getDefinitionPropertyName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .createAlias(definitionReference, definitionReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(definitionReference + ".scope.id", scope)) //
                .add(getResourceLocalizationCriterion(resource, scope, locale, programType));

        addActiveVersionCriterion(configurationType, criteria);

        for (String orderColumn : configurationType.getOrderColumns()) {
            criteria.addOrder(Order.asc(definitionReference + "." + orderColumn));
        }

        return (List<WorkflowConfiguration>) criteria.addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public List<WorkflowConfiguration> getConfigurationsWithVersion(PrismConfiguration configurationType, Integer version) {
        return (List<WorkflowConfiguration>) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(Restrictions.eq("version", version)) //
                .list();
    }

    public List<DisplayPropertyConfiguration> getDisplayPropertyConfiguration(Resource resource, PrismScope scope,
            PrismDisplayPropertyCategory displayPropertyCategory, PrismLocale locale, PrismProgramType programType) {
        return (List<DisplayPropertyConfiguration>) sessionFactory.getCurrentSession().createCriteria(DisplayPropertyConfiguration.class) //
                .createAlias("displayPropertyDefinition", "displayPropertyDefinition", JoinType.INNER_JOIN) //
                .add(getResourceLocalizationCriterion(resource, scope, locale, programType)) //
                .add(Restrictions.eq("displayPropertyDefinition.category", displayPropertyCategory)) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            Enum<?> definitionId) {
        Query query = sessionFactory.getCurrentSession().createQuery( //
                getUpdateOperation(configurationType) //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and " + configurationType.getDefinitionPropertyName() + ".id" + " = :definitionId " //
                        + getLocaleCriterionUpdate(locale) //
                        + getProgramTypeCriterionUpdate(programType)) //
                .setParameter("resource", resource) //
                .setParameter("definitionId", definitionId);
        applyLocalizationConstraintsRestoreDefault(locale, programType, query);
        query.executeUpdate();
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType) {
        Query query = sessionFactory.getCurrentSession().createQuery( //
                getUpdateOperation(configurationType) //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and " + getScopeConstraint(configurationType) //
                        + getLocaleCriterionUpdate(locale) //
                        + getProgramTypeCriterionUpdate(programType)) //
                .setParameter("resource", resource) //
                .setParameter("scope", scope);
        applyLocalizationConstraintsRestoreDefault(locale, programType, query);
        query.executeUpdate();
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            Enum<?> definitionId) {
        PrismScope resourceScope = resource.getResourceScope();

        String updateOperation = getUpdateOperation(configurationType);
        String definitionCriterion = "where " + configurationType.getDefinitionPropertyName() + ".id" + " = :definitionId ";

        String localeCriterion = getLocaleCriterionUpdate(locale);
        String programTypeCriterion = getProgramTypeCriterionUpdate(programType);

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getSystemInheritanceCriterion(localeCriterion, programTypeCriterion));
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getInstitionInheritanceCriterion(localeCriterion, programTypeCriterion));
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCamelName(), resource) //
                .setParameter("definitionId", definitionId);

        applyLocalizationConstraintsRestoreGlobal(locale, programType, query);
        query.executeUpdate();
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType) {
        PrismScope resourceScope = resource.getResourceScope();

        String updateOperation = getUpdateOperation(configurationType);
        String definitionCriterion = "where " + getScopeConstraint(configurationType);

        String localeCriterion = getLocaleCriterionUpdate(locale);
        String programTypeCriterion = getProgramTypeCriterionUpdate(programType);

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getSystemInheritanceCriterion(localeCriterion, programTypeCriterion));
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getInstitionInheritanceCriterion(localeCriterion, programTypeCriterion));
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCamelName(), resource) //
                .setParameter("scope", scope);

        applyLocalizationConstraintsRestoreGlobal(locale, programType, query);
        query.executeUpdate();
    }

    public <T extends WorkflowDefinition> List<T> listDefinitions(PrismConfiguration configurationType, PrismScope scope) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(configurationType.getDefinitionClass()) //
                .add(Restrictions.eq("scope.id", scope)) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public Integer getActiveConfigurationVersion(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            PrismScope scope) {
        String definitionReference = configurationType.getDefinitionPropertyName();
        return (Integer) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .setProjection(Projections.property("version")) //
                .add(getResourceLocalizationCriterion(resource, scope, locale, programType)) //
                .createAlias(definitionReference, definitionReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(definitionReference + ".scope.id", scope)) //
                .add(Restrictions.eq("active", true)) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    private Junction getResourceLocalizationCriterion(Resource resource, PrismScope scope, PrismLocale locale, PrismProgramType programType) {
        Criterion localeCriterion = getLocaleCriterionSelect(locale);
        Criterion programTypeCriterion = getProgramTypeCriterionSelect(scope, programType);

        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("system", resource.getSystem())) //
                        .add(localeCriterion) //
                        .add(programTypeCriterion)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(programTypeCriterion)) //
                .add(Restrictions.eq("program", resource.getProgram()));
    }

    private Criterion getLocaleCriterionSelect(PrismLocale locale) {
        return locale == null ? Restrictions.eq("locale", getSystemLocale()) : Restrictions.in("locale", Lists.newArrayList(locale, getSystemLocale()));
    }

    private Criterion getProgramTypeCriterionSelect(PrismScope scope, PrismProgramType programType) {
        return scope.ordinal() < PROGRAM.ordinal() ? Restrictions.isNull("programType") : programType == null ? Restrictions.eq("programType",
                getSystemProgramType()) : Restrictions.in("programType", Arrays.asList(programType, getSystemProgramType()));
    }

    private String getLocaleCriterionUpdate(PrismLocale locale) {
        return locale == null ? "and locale is null " : "and locale = :locale ";
    }

    private String getProgramTypeCriterionUpdate(PrismProgramType programType) {
        return programType == null ? "and programType is null " : //
                "and (program is not null and " //
                        + "programType is null " //
                        + "or programType = :programType) ";
    }

    private void addActiveVersionCriterion(PrismConfiguration configurationType, Criteria criteria) {
        if (configurationType.isVersioned()) {
            criteria.add(Restrictions.eq("active", true));
        }
    }

    private String getUpdateOperation(PrismConfiguration configurationType) {
        String configurationReference = configurationType.getConfigurationClass().getSimpleName();
        return configurationType.isVersioned() ? "update " + configurationReference + " set active = false " : "delete " + configurationReference + " ";
    }

    private String getScopeConstraint(PrismConfiguration configurationType) {
        return configurationType.getDefinitionPropertyName() + " in (" //
                + "from " + configurationType.getDefinitionClass().getSimpleName() + " " //
                + "where scope.id = :scope) ";
    }

    private void applyLocalizationConstraintsRestoreDefault(PrismLocale locale, PrismProgramType programType, Query query) {
        if (locale != null) {
            query.setParameter("locale", locale);
        }

        if (programType != null) {
            query.setParameter("programType", programType);
        }
    }

    private void applyLocalizationConstraintsRestoreGlobal(PrismLocale locale, PrismProgramType programType, Query query) {
        applyLocalizationConstraintsRestoreDefault(locale, programType, query);

        if (programType != null) {
            query.setParameter("programTypeName", programType.name());
        }
    }

    private static String getSystemInheritanceCriterion(String localeCriterion, String programTypeCriterion) {
        return "and (institution in (" //
                + "from Institution " //
                + "where system = :system) " //
                + localeCriterion + " "//
                + programTypeCriterion //
                + "or program in (" //
                + "from Program " //
                + "where system = :system " //
                + "and programType in (" + "from ProgramType " + "where code like :programTypeName)) " //
                + localeCriterion //
                + programTypeCriterion + ")";
    }

    private static String getInstitionInheritanceCriterion(String localeCriterion, String programTypeCriterion) {
        return "and (program in (" //
                + "from Program " //
                + "where institution = :institution " + "and programType in (" + "from ProgramType " + "where code like :programTypeName)) " //
                + localeCriterion //
                + programTypeCriterion + ")";
    }

}
