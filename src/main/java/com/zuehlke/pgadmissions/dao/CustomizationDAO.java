package com.zuehlke.pgadmissions.dao;

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

import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
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

    public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, PrismProgramType programType,
            WorkflowDefinition definition, boolean translationMode) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), programType, translationMode)) //
                .add(Restrictions.eq(configurationType.getDefinitionPropertyName(), definition));

        addActiveVersionCriterion(configurationType, criteria);

        return (WorkflowConfiguration) criteria.addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource,
            PrismProgramType programType, WorkflowDefinition definition, boolean translationMode) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), programType, translationMode)) //
                .add(Restrictions.eq(configurationType.getDefinitionPropertyName(), definition));

        addActiveVersionCriterion(configurationType, criteria);

        return (List<WorkflowConfiguration>) criteria.addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismProgramType programType, Enum<?> category, boolean translationMode) {
        String definitionReference = configurationType.getDefinitionPropertyName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .createAlias(definitionReference, definitionReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(definitionReference + ".scope.id", scope)) //
                .add(getResourceLocalizationCriterion(resource, scope, programType, translationMode));

        if (category != null) {
            criteria.add(Restrictions.eq(definitionReference + ".category", category));
        }

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

    public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismProgramType programType, boolean translationMode) {
        return getConfigurations(configurationType, resource, scope, programType, null, translationMode);
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

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismProgramType programType,
            Enum<?> definitionId) {
        Query query = sessionFactory.getCurrentSession().createQuery( //
                getUpdateOperation(configurationType) //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and " + configurationType.getDefinitionPropertyName() + ".id" + " = :definitionId " //
                        + getProgramTypeCriterionUpdate(programType)) //
                .setParameter("resource", resource) //
                .setParameter("definitionId", definitionId);
        applyLocalizationConstraintsRestoreDefault(programType, query);
        query.executeUpdate();
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope,
            PrismProgramType programType) {
        Query query = sessionFactory.getCurrentSession().createQuery( //
                getUpdateOperation(configurationType) //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and " + getScopeConstraint(configurationType) //
                        + getProgramTypeCriterionUpdate(programType)) //
                .setParameter("resource", resource) //
                .setParameter("scope", scope);
        applyLocalizationConstraintsRestoreDefault(programType, query);
        query.executeUpdate();
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismProgramType programType,
            Enum<?> definitionId) {
        PrismScope resourceScope = resource.getResourceScope();

        String updateOperation = getUpdateOperation(configurationType);
        String definitionCriterion = "where " + configurationType.getDefinitionPropertyName() + ".id" + " = :definitionId ";

        String programTypeCriterion = getProgramTypeCriterionUpdate(programType);

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getSystemInheritanceCriterion(programTypeCriterion));
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getInstitionInheritanceCriterion(programTypeCriterion));
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCamelName(), resource) //
                .setParameter("definitionId", definitionId);

        applyLocalizationConstraintsRestoreGlobal(programType, query);
        query.executeUpdate();
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismProgramType programType) {
        PrismScope resourceScope = resource.getResourceScope();

        String updateOperation = getUpdateOperation(configurationType);
        String definitionCriterion = "where " + getScopeConstraint(configurationType);

        String programTypeCriterion = getProgramTypeCriterionUpdate(programType);

        Query query;
        if (resourceScope.equals(SYSTEM)) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getSystemInheritanceCriterion(programTypeCriterion));
        } else if (resourceScope.equals(INSTITUTION)) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getInstitionInheritanceCriterion(programTypeCriterion));
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCamelName(), resource) //
                .setParameter("scope", scope);

        applyLocalizationConstraintsRestoreGlobal(programType, query);
        query.executeUpdate();
    }

    public <T extends WorkflowDefinition> List<T> listDefinitions(PrismConfiguration configurationType, PrismScope scope) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(configurationType.getDefinitionClass()) //
                .add(Restrictions.eq("scope.id", scope)) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public Integer getActiveConfigurationVersion(PrismConfiguration configurationType, Resource resource, PrismProgramType programType,
            PrismScope scope) {
        String definitionReference = configurationType.getDefinitionPropertyName();
        return (Integer) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .setProjection(Projections.property("version")) //
                .add(getResourceLocalizationCriterion(resource, scope, programType, false)) //
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

    private Junction getResourceLocalizationCriterion(Resource resource, PrismScope scope, PrismProgramType programType,
            boolean translationMode) {
        Criterion programTypeCriterion = getProgramTypeCriterionSelect(scope, programType);
        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("system", resource.getSystem())) //
                        .add(programTypeCriterion)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(programTypeCriterion)) //
                .add(Restrictions.eq("program", resource.getProgram()));
    }

    private Criterion getProgramTypeCriterionSelect(PrismScope scope, PrismProgramType programType) {
        return scope.ordinal() < PROGRAM.ordinal() ? Restrictions.isNull("programType") : programType == null ? Restrictions.eq("programType",
                getSystemProgramType()) : Restrictions.in("programType", Arrays.asList(programType, getSystemProgramType()));
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

    private void applyLocalizationConstraintsRestoreDefault(PrismProgramType programType, Query query) {
        if (programType != null) {
            query.setParameter("programType", programType);
        }
    }

    private void applyLocalizationConstraintsRestoreGlobal(PrismProgramType programType, Query query) {
        applyLocalizationConstraintsRestoreDefault(programType, query);
        if (programType != null) {
            query.setParameter("programTypeName", programType.name());
        }
    }

    private static String getSystemInheritanceCriterion(String programTypeCriterion) {
        return "and (institution in (" //
                + "from Institution " //
                + "where system = :system) " //
                + programTypeCriterion //
                + "or program in (" //
                + "from Program " //
                + "where system = :system " //
                + "and programType in (" + "from ProgramType " + "where code like :programTypeName)) " //
                + programTypeCriterion + ")";
    }

    private static String getInstitionInheritanceCriterion(String programTypeCriterion) {
        return "and (program in (" //
                + "from Program " //
                + "where institution = :institution " + "and programType in (" + "from ProgramType " + "where code like :programTypeName)) " //
                + programTypeCriterion + ")";
    }

}
