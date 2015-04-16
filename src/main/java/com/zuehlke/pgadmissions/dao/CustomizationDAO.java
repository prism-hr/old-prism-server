package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

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
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

@Repository
@SuppressWarnings("unchecked")
public class CustomizationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowDefinition definition, boolean translationMode) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), locale, programType, translationMode)) //
                .add(Restrictions.eq(configurationType.getDefinitionPropertyName(), definition));

        addActiveVersionCriterion(configurationType, criteria);

        return (WorkflowConfiguration) addResourceLocalizationOrder(criteria)
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismLocale locale,
            PrismProgramType programType, WorkflowDefinition definition, boolean translationMode) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), locale, programType, translationMode)) //
                .add(Restrictions.eq(configurationType.getDefinitionPropertyName(), definition));

        addActiveVersionCriterion(configurationType, criteria);

        return (List<WorkflowConfiguration>) addResourceLocalizationOrder(criteria) //
                .list();
    }

    public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType, Enum<?> category, boolean translationMode) {
        String definitionReference = configurationType.getDefinitionPropertyName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .createAlias(definitionReference, definitionReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(definitionReference + ".scope.id", scope)) //
                .add(getResourceLocalizationCriterion(resource, scope, locale, programType, translationMode));

        if (category != null) {
            criteria.add(Restrictions.eq(definitionReference + ".category", category));
        }

        addActiveVersionCriterion(configurationType, criteria);

        for (String orderColumn : configurationType.getOrderColumns()) {
            criteria.addOrder(Order.asc(definitionReference + "." + orderColumn));
        }

        return (List<WorkflowConfiguration>) addResourceLocalizationOrder(criteria) //
                .list();
    }

    public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType, boolean translationMode) {
        return getConfigurations(configurationType, resource, scope, locale, programType, null, translationMode);
    }

    public WorkflowConfiguration getConfigurationWithVersion(PrismConfiguration configurationType, WorkflowDefinition definition, Integer version) {
        return (WorkflowConfiguration) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(Restrictions.eq(configurationType.getDefinitionPropertyName(), definition)) //
                .add(Restrictions.eq("version", version)) //
                .uniqueResult();
    }

    public List<WorkflowConfiguration> getConfigurationsWithVersion(PrismConfiguration configurationType, Integer version) {
        return (List<WorkflowConfiguration>) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(Restrictions.eq("version", version)) //
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
        applyLocalizationConstraintsRestore(locale, programType, query);
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
        applyLocalizationConstraintsRestore(locale, programType, query);
        query.executeUpdate();
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismProgramType programType,
            Enum<?> definitionId) {
        PrismScope resourceScope = resource.getResourceScope();

        String updateOperation = getUpdateOperation(configurationType);
        String definitionCriterion = "where " + configurationType.getDefinitionPropertyName() + ".id" + " = :definitionId ";

        String localeCriterion = getLocaleCriterionUpdate(locale);
        String programTypeCriterion = getProgramTypeCriterionUpdate(programType);

        Query query = getResourceInheritanceCriterion(resourceScope, updateOperation, definitionCriterion, localeCriterion, programTypeCriterion);
        query.setParameter(resourceScope.getLowerCamelName(), resource) //
                .setParameter("definitionId", definitionId);
        applyLocalizationConstraintsRestore(locale, programType, query);
        query.executeUpdate();
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
            PrismProgramType programType) {
        PrismScope resourceScope = resource.getResourceScope();

        String updateOperation = getUpdateOperation(configurationType);
        String definitionCriterion = "where " + getScopeConstraint(configurationType);

        String localeCriterion = getLocaleCriterionUpdate(locale);
        String programTypeCriterion = getProgramTypeCriterionUpdate(programType);

        Query query = getResourceInheritanceCriterion(resourceScope, updateOperation, definitionCriterion, localeCriterion, programTypeCriterion);
        query.setParameter(resourceScope.getLowerCamelName(), resource) //
                .setParameter("scope", scope);
        applyLocalizationConstraintsRestore(locale, programType, query);
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
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .setProjection(Projections.property("version")) //
                .add(getResourceLocalizationCriterion(resource, scope, locale, programType, false)) //
                .createAlias(definitionReference, definitionReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(definitionReference + ".scope.id", scope)) //
                .add(Restrictions.eq("active", true));
        
        return (Integer) addResourceLocalizationOrder(criteria) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    private Junction getResourceLocalizationCriterion(Resource resource, PrismScope scope, PrismLocale locale, PrismProgramType programType,
            boolean translationMode) {
        Criterion localeCriterion = getLocaleCriterionSelect(locale, translationMode);
        Criterion programTypeCriterion = getProgramTypeCriterionSelect(programType, translationMode);

        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("system", resource.getSystem())) //
                        .add(localeCriterion) //
                        .add(programTypeCriterion)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(localeCriterion) //
                        .add(programTypeCriterion)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("program", resource.getProgram())) //
                        .add(localeCriterion)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(localeCriterion));
    }

    private Criteria addResourceLocalizationOrder(Criteria criteria) {
        return criteria.addOrder(Order.desc("project"))
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault"));
    }

    private Criterion getLocaleCriterionSelect(PrismLocale locale, boolean translationMode) {
        if (locale == null) {
            return Restrictions.eq("locale", getSystemLocale());
        } else if (translationMode) {
            return Restrictions.eq("locale", locale);
        }
        return Restrictions.in("locale", Lists.newArrayList(locale, getSystemLocale()));
    }

    private Criterion getProgramTypeCriterionSelect(PrismProgramType programType, boolean translationMode) {
        if (programType == null) {
            return Restrictions.eq("programType", getSystemProgramType());
        } else if (translationMode) {
            return Restrictions.eq("programType", programType);
        }
        return Restrictions.in("programType", Lists.newArrayList(programType, getSystemProgramType()));
    }

    private String getLocaleCriterionUpdate(PrismLocale locale) {
        return "and locale = :locale ";
    }

    private String getProgramTypeCriterionUpdate(PrismProgramType programType) {
        return "and programType = :programType ";
    }

    private void addActiveVersionCriterion(PrismConfiguration configurationType, Criteria criteria) {
        if (configurationType.isVersioned()) {
            criteria.add(Restrictions.eq("active", true));
        }
    }

    private String getUpdateOperation(PrismConfiguration configurationType) {
        String configurationReference = configurationType.getConfigurationClass().getSimpleName();
        if (configurationType.isVersioned()) {
            return "update " + configurationReference + " set active = false ";
        }
        return "delete " + configurationReference + " ";
    }

    private String getScopeConstraint(PrismConfiguration configurationType) {
        return configurationType.getDefinitionPropertyName() + " in (" //
                + "from " + configurationType.getDefinitionClass().getSimpleName() + " " //
                + "where scope.id = :scope) ";
    }

    private void applyLocalizationConstraintsRestore(PrismLocale locale, PrismProgramType programType, Query query) {
        query.setParameter("locale", locale);
        query.setParameter("programType", programType);
    }

    private Query getResourceInheritanceCriterion(PrismScope resourceScope, String updateOperation, String definitionCriterion, String localeCriterion,
            String programTypeCriterion) throws Error {
        String expression = updateOperation + definitionCriterion;
        if (resourceScope == SYSTEM) {
            return sessionFactory.getCurrentSession().createQuery( //
                    expression + getSystemInheritanceCriterion(localeCriterion, programTypeCriterion));
        } else if (resourceScope == INSTITUTION) {
            return sessionFactory.getCurrentSession().createQuery( //
                    expression + getInstitionInheritanceCriterion(localeCriterion, programTypeCriterion));
        } else if (resourceScope == PROGRAM) {
            return sessionFactory.getCurrentSession().createQuery( //
                    expression + getProgramInheritanceCriterion(localeCriterion, programTypeCriterion));
        }
        throw new Error();
    }

    private static String getSystemInheritanceCriterion(String localeCriterion, String programTypeCriterion) {
        return "and (institution in (" //
                + "from Institution " //
                + "where system = :system) " //
                + "or program in (" //
                + "from Program " //
                + "where system = :system) " //
                + "or project in (" //
                + "from Project " //
                + "where system = :system)) " //
                + localeCriterion //
                + programTypeCriterion + ")";
    }

    private static String getInstitionInheritanceCriterion(String localeCriterion, String programTypeCriterion) {
        return "and (program in (" //
                + "from Program " //
                + "where institution = :institution) "
                + "or project in (" //
                + "from Project " //
                + "where institution = :institution)) " //
                + localeCriterion //
                + programTypeCriterion + ")";
    }

    private static String getProgramInheritanceCriterion(String localeCriterion, String programTypeCriterion) {
        return "and (project in (" //
                + "from Project " //
                + "where program = :program)) " //
                + localeCriterion //
                + programTypeCriterion + ")";
    }

}
