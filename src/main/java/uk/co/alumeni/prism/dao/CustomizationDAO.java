package uk.co.alumeni.prism.dao;

import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityType.getSystemOpportunityType;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

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

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.workflow.WorkflowConfiguration;
import uk.co.alumeni.prism.domain.workflow.WorkflowDefinition;

@Repository
@SuppressWarnings("unchecked")
public class CustomizationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private static String getSystemInheritanceCriterion(String opportunityTypeCriterion) {
        return "and (institution in (" //
                + "from Institution " //
                + "where system = :system) " //
                + "or department in (" //
                + "from Department " //
                + "where system = :system) " //
                + "or program in (" //
                + "from Program " //
                + "where system = :system " //
                + "and opportunityType.id = :opportunityType) "
                + "or project in (" //
                + "from Project " //
                + "where system = :system " //
                + "and opportunityType.id = :opportunityType) "
                + opportunityTypeCriterion + ")";
    }

    private static String getInstitutionInheritanceCriterion(String opportunityTypeCriterion) {
        return "and (department in (" //
                + "from Department " //
                + "where institution = :institution) " //
                + "or program in (" //
                + "from Program " //
                + "where institution = :institution " //
                + "and opportunityType.id = :opportunityType) "
                + "or project in (" //
                + "from Project " //
                + "where institution = :institution " //
                + "and opportunityType.id = :opportunityType) "
                + opportunityTypeCriterion + ")";
    }

    private static String getDepartmentInheritanceCriterion(String opportunityTypeCriterion) {
        return "and (program in (" //
                + "from Program " //
                + "where institution = :department " //
                + "and opportunityType.id = :opportunityType) "
                + "or project in (" //
                + "from Project " //
                + "and opportunityType.id = :opportunityType) "
                + opportunityTypeCriterion + ")";
    }

    private static String getProgramInheritanceCriterion(String opportunityTypeCriterion) {
        return "and (project in (" //
                + "from Project " //
                + "where program = :program " //
                + "and opportunityType.id = :opportunityType) "
                + opportunityTypeCriterion + ")";
    }

    public WorkflowConfiguration<?> getConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType, WorkflowDefinition definition) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), opportunityType)) //
                .add(Restrictions.eq("definition", definition));

        addActiveVersionCriterion(configurationType, criteria);

        return (WorkflowConfiguration<?>) criteria //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("department")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<WorkflowConfiguration<?>> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType,
            WorkflowDefinition definition) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), opportunityType)) //
                .add(Restrictions.eq("definition", definition));

        addActiveVersionCriterion(configurationType, criteria);

        return (List<WorkflowConfiguration<?>>) criteria //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("department")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public List<WorkflowConfiguration<?>> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType,
            Enum<?> category) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .createAlias("definition", "definition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("definition.scope.id", scope)) //
                .add(getResourceLocalizationCriterion(resource, scope, opportunityType));

        if (category != null) {
            criteria.add(Restrictions.eq("definition.category", category));
        }

        addActiveVersionCriterion(configurationType, criteria);

        for (String orderColumn : configurationType.getOrderColumns()) {
            criteria.addOrder(Order.asc("definition." + orderColumn));
        }

        return (List<WorkflowConfiguration<?>>) criteria //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("department")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public List<WorkflowConfiguration<?>> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType,
            boolean configurationMode) {
        return getConfigurations(configurationType, resource, scope, opportunityType, null);
    }

    public WorkflowConfiguration<?> getConfigurationWithVersion(PrismConfiguration configurationType, WorkflowDefinition definition, Integer version) {
        return (WorkflowConfiguration<?>) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(Restrictions.eq("definition", definition)) //
                .add(Restrictions.eq("version", version)) //
                .uniqueResult();
    }

    public List<WorkflowConfiguration<?>> getConfigurationsWithVersion(PrismConfiguration configurationType, Integer version) {
        return (List<WorkflowConfiguration<?>>) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .add(Restrictions.eq("version", version)) //
                .list();
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType, Enum<?> definitionId) {
        Query query = sessionFactory.getCurrentSession().createQuery( //
                getUpdateOperation(configurationType) //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and definition.id" + " = :definitionId " //
                        + getOpportunityTypeCriterionUpdate(opportunityType)) //
                .setParameter("resource", resource) //
                .setParameter("definitionId", definitionId);
        applyLocalizationConstraints(opportunityType, query);
        query.executeUpdate();
    }

    public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType) {
        Query query = sessionFactory.getCurrentSession().createQuery( //
                getUpdateOperation(configurationType) //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and " + getScopeConstraint(configurationType) //
                        + getOpportunityTypeCriterionUpdate(opportunityType)) //
                .setParameter("resource", resource) //
                .setParameter("scope", scope);
        applyLocalizationConstraints(opportunityType, query);
        query.executeUpdate();
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType, Enum<?> definitionId) {
        PrismScope resourceScope = resource.getResourceScope();

        String updateOperation = getUpdateOperation(configurationType);
        String definitionCriterion = "where definition.id" + " = :definitionId ";

        String opportunityTypeCriterion = getOpportunityTypeCriterionUpdate(opportunityType);
        Query query = getRestoreGlobalConfigurationFilter(resourceScope, updateOperation, definitionCriterion, opportunityTypeCriterion);

        query.setParameter(resourceScope.getLowerCamelName(), resource) //
                .setParameter("definitionId", definitionId);

        applyLocalizationConstraints(opportunityType, query);
        query.executeUpdate();
    }

    public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismOpportunityType opportunityType) {
        PrismScope resourceScope = resource.getResourceScope();

        String updateOperation = getUpdateOperation(configurationType);
        String definitionCriterion = "where " + getScopeConstraint(configurationType);

        String opportunityTypeCriterion = getOpportunityTypeCriterionUpdate(opportunityType);

        Query query = getRestoreGlobalConfigurationFilter(resourceScope, updateOperation, definitionCriterion, opportunityTypeCriterion);

        query.setParameter(resourceScope.getLowerCamelName(), resource) //
                .setParameter("scope", scope);

        applyLocalizationConstraints(opportunityType, query);
        query.executeUpdate();
    }

    public <T extends WorkflowDefinition> List<T> listDefinitions(PrismConfiguration configurationType, PrismScope scope) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(configurationType.getDefinitionClass()) //
                .add(Restrictions.eq("scope.id", scope)) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public Integer getActiveConfigurationVersion(PrismConfiguration configurationType, Resource resource, PrismOpportunityType opportunityType) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
                .setProjection(Projections.property("version")) //
                .add(getResourceLocalizationCriterion(resource, resource.getResourceScope(), opportunityType)) //
                .createAlias("definition", "definition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("definition.scope.id", resource.getResourceScope())) //
                .add(Restrictions.eq("active", true)) //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("department")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    private Junction getResourceLocalizationCriterion(Resource resource, PrismScope scope, PrismOpportunityType opportunityType) {
        Criterion opportunityTypeCriterion = getOpportunityTypeCriterionSelect(scope, opportunityType);
        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("system", resource.getSystem())) //
                        .add(opportunityTypeCriterion)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(opportunityTypeCriterion)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("department", resource.getDepartment())) //
                        .add(opportunityTypeCriterion)) //
                .add(Restrictions.eq("program", resource.getProgram()))
                .add(Restrictions.eq("project", resource.getProject()));
    }

    private Criterion getOpportunityTypeCriterionSelect(PrismScope scope, PrismOpportunityType prismOpportunityType) {
        if (scope.ordinal() < PROGRAM.ordinal()) {
            return Restrictions.isNull("opportunityType");
        } else if (prismOpportunityType == null) {
            return Restrictions.eq("opportunityType.id", getSystemOpportunityType());
        }
        return Restrictions.in("opportunityType.id", Arrays.asList(prismOpportunityType, getSystemOpportunityType()));
    }

    private String getOpportunityTypeCriterionUpdate(PrismOpportunityType prismOpportunityType) {
        if (prismOpportunityType == null) {
            return "and opportunityType is null ";
        }
        return "and (program is not null and " //
                + "opportunityType is null " //
                + "or opportunityType.id = :opportunityType) " //
                + "or (project is not null and " //
                + "opportunityType is null " //
                + "or opportunityType.id = :opportunityType) ";
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
        return "definition in (" //
                + "from " + configurationType.getDefinitionClass().getSimpleName() + " " //
                + "where scope.id = :scope) ";
    }

    private void applyLocalizationConstraints(PrismOpportunityType opportunityType, Query query) {
        if (opportunityType != null) {
            query.setParameter("opportunityType", opportunityType);
        }
    }

    private Query getRestoreGlobalConfigurationFilter(PrismScope resourceScope, String updateOperation, String definitionCriterion, String opportunityTypeCriterion) throws Error {
        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getSystemInheritanceCriterion(opportunityTypeCriterion));
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getInstitutionInheritanceCriterion(opportunityTypeCriterion));
        } else if (resourceScope == DEPARTMENT) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getDepartmentInheritanceCriterion(opportunityTypeCriterion));
        } else if (resourceScope == PROGRAM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    updateOperation //
                            + definitionCriterion //
                            + getProgramInheritanceCriterion(opportunityTypeCriterion));
        } else {
            throw new Error();
        }
        return query;
    }

}
