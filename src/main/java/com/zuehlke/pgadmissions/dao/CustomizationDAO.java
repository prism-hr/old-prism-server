package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType.getSystemAdvertType;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

@Repository
@SuppressWarnings("unchecked")
public class CustomizationDAO {

	@Autowired
	private SessionFactory sessionFactory;

	public WorkflowConfiguration getConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
	        WorkflowDefinition definition, boolean translationMode) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
		        .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), locale, advertType, translationMode)) //
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
	        PrismAdvertType advertType, WorkflowDefinition definition, boolean translationMode) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
		        .add(getResourceLocalizationCriterion(resource, definition.getScope().getId(), locale, advertType, translationMode)) //
		        .add(Restrictions.eq(configurationType.getDefinitionPropertyName(), definition));

		addActiveVersionCriterion(configurationType, criteria);

		return (List<WorkflowConfiguration>) criteria.addOrder(Order.desc("program")) //
		        .addOrder(Order.desc("institution")) //
		        .addOrder(Order.desc("system")) //
		        .addOrder(Order.asc("systemDefault")) //
		        .list();
	}

	public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
	        PrismAdvertType advertType, Enum<?> category, boolean translationMode) {
		String definitionReference = configurationType.getDefinitionPropertyName();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
		        .createAlias(definitionReference, definitionReference, JoinType.INNER_JOIN) //
		        .add(Restrictions.eq(definitionReference + ".scope.id", scope)) //
		        .add(getResourceLocalizationCriterion(resource, scope, locale, advertType, translationMode));

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

	public List<WorkflowConfiguration> getConfigurations(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
	        PrismAdvertType advertType, boolean translationMode) {
		return getConfigurations(configurationType, resource, scope, locale, advertType, null, translationMode);
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

	public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
	        Enum<?> definitionId) {
		Query query = sessionFactory.getCurrentSession().createQuery( //
		        getUpdateOperation(configurationType) //
		                + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
		                + "and " + configurationType.getDefinitionPropertyName() + ".id" + " = :definitionId " //
		                + getLocaleCriterionUpdate(locale) //
		                + getAdvertTypeCriterionUpdate(advertType)) //
		        .setParameter("resource", resource) //
		        .setParameter("definitionId", definitionId);
		applyLocalizationConstraintsRestoreDefault(locale, advertType, query);
		query.executeUpdate();
	}

	public void restoreDefaultConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
	        PrismAdvertType advertType) {
		Query query = sessionFactory.getCurrentSession().createQuery( //
		        getUpdateOperation(configurationType) //
		                + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
		                + "and " + getScopeConstraint(configurationType) //
		                + getLocaleCriterionUpdate(locale) //
		                + getAdvertTypeCriterionUpdate(advertType)) //
		        .setParameter("resource", resource) //
		        .setParameter("scope", scope);
		applyLocalizationConstraintsRestoreDefault(locale, advertType, query);
		query.executeUpdate();
	}

	public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
	        Enum<?> definitionId) {
		PrismScope resourceScope = resource.getResourceScope();

		String updateOperation = getUpdateOperation(configurationType);
		String definitionCriterion = "where " + configurationType.getDefinitionPropertyName() + ".id" + " = :definitionId ";

		String localeCriterion = getLocaleCriterionUpdate(locale);
		String advertTypeCriterion = getAdvertTypeCriterionUpdate(advertType);

		Query query;
		if (resourceScope == SYSTEM) {
			query = sessionFactory.getCurrentSession().createQuery( //
			        updateOperation //
			                + definitionCriterion //
			                + getSystemInheritanceCriterion(localeCriterion, advertTypeCriterion));
		} else if (resourceScope == INSTITUTION) {
			query = sessionFactory.getCurrentSession().createQuery( //
			        updateOperation //
			                + definitionCriterion //
			                + getInstitionInheritanceCriterion(localeCriterion, advertTypeCriterion));
		} else {
			throw new Error();
		}

		query.setParameter(resourceScope.getLowerCamelName(), resource) //
		        .setParameter("definitionId", definitionId);

		applyLocalizationConstraintsRestoreGlobal(locale, advertType, query);
		query.executeUpdate();
	}

	public void restoreGlobalConfiguration(PrismConfiguration configurationType, Resource resource, PrismScope scope, PrismLocale locale,
	        PrismAdvertType advertType) {
		PrismScope resourceScope = resource.getResourceScope();

		String updateOperation = getUpdateOperation(configurationType);
		String definitionCriterion = "where " + getScopeConstraint(configurationType);

		String localeCriterion = getLocaleCriterionUpdate(locale);
		String advertTypeCriterion = getAdvertTypeCriterionUpdate(advertType);

		Query query;
		if (resourceScope.equals(SYSTEM)) {
			query = sessionFactory.getCurrentSession().createQuery( //
			        updateOperation //
			                + definitionCriterion //
			                + getSystemInheritanceCriterion(localeCriterion, advertTypeCriterion));
		} else if (resourceScope.equals(INSTITUTION)) {
			query = sessionFactory.getCurrentSession().createQuery( //
			        updateOperation //
			                + definitionCriterion //
			                + getInstitionInheritanceCriterion(localeCriterion, advertTypeCriterion));
		} else {
			throw new Error();
		}

		query.setParameter(resourceScope.getLowerCamelName(), resource) //
		        .setParameter("scope", scope);

		applyLocalizationConstraintsRestoreGlobal(locale, advertType, query);
		query.executeUpdate();
	}

	public <T extends WorkflowDefinition> List<T> listDefinitions(PrismConfiguration configurationType, PrismScope scope) {
		return (List<T>) sessionFactory.getCurrentSession().createCriteria(configurationType.getDefinitionClass()) //
		        .add(Restrictions.eq("scope.id", scope)) //
		        .addOrder(Order.asc("id")) //
		        .list();
	}

	public Integer getActiveConfigurationVersion(PrismConfiguration configurationType, Resource resource, PrismLocale locale, PrismAdvertType advertType,
	        PrismScope scope) {
		String definitionReference = configurationType.getDefinitionPropertyName();
		return (Integer) sessionFactory.getCurrentSession().createCriteria(configurationType.getConfigurationClass()) //
		        .setProjection(Projections.property("version")) //
		        .add(getResourceLocalizationCriterion(resource, scope, locale, advertType, false)) //
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

	private Junction getResourceLocalizationCriterion(Resource resource, PrismScope scope, PrismLocale locale, PrismAdvertType advertType,
	        boolean translationMode) {
		Criterion localeCriterion = getLocaleCriterionSelect(locale, translationMode);
		Criterion advertTypeCriterion = getAdvertTypeCriterionSelect(scope, advertType);

		return Restrictions.disjunction() //
		        .add(Restrictions.conjunction() //
		                .add(Restrictions.eq("system", resource.getSystem())) //
		                .add(localeCriterion) //
		                .add(advertTypeCriterion)) //
		        .add(Restrictions.conjunction() //
		                .add(Restrictions.eq("institution", resource.getInstitution())) //
		                .add(advertTypeCriterion)) //
		        .add(Restrictions.eq("program", resource.getProgram()));
	}

	private Criterion getLocaleCriterionSelect(PrismLocale locale, boolean translationMode) {
		if (locale == null) {
			return Restrictions.eq("locale", getSystemLocale());
		} else if (translationMode) {
			return Restrictions.eq("locale", locale);
		}
		return Restrictions.in("locale", Lists.newArrayList(locale, getSystemLocale()));
	}

	private Criterion getAdvertTypeCriterionSelect(PrismScope scope, PrismAdvertType advertType) {
		return scope.ordinal() < PROGRAM.ordinal() ? Restrictions.isNull("advertType") : advertType == null ? Restrictions.eq("advertType",
		        getSystemAdvertType()) : Restrictions.in("advertType", Arrays.asList(advertType, getSystemAdvertType()));
	}

	private String getLocaleCriterionUpdate(PrismLocale locale) {
		return locale == null ? "and locale is null " : "and locale = :locale ";
	}

	private String getAdvertTypeCriterionUpdate(PrismAdvertType advertType) {
		return advertType == null ? "and advertType is null " : //
		        "and (program is not null and " //
		                + "advertType is null " //
		                + "or advertType = :advertType) ";
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

	private void applyLocalizationConstraintsRestoreDefault(PrismLocale locale, PrismAdvertType advertType, Query query) {
		if (locale != null) {
			query.setParameter("locale", locale);
		}

		if (advertType != null) {
			query.setParameter("advertType", advertType);
		}
	}

	private void applyLocalizationConstraintsRestoreGlobal(PrismLocale locale, PrismAdvertType advertType, Query query) {
		applyLocalizationConstraintsRestoreDefault(locale, advertType, query);

		if (advertType != null) {
			query.setParameter("advertTypeName", advertType.name());
		}
	}

	private static String getSystemInheritanceCriterion(String localeCriterion, String advertTypeCriterion) {
		return "and (institution in (" //
		        + "from Institution " //
		        + "where system = :system) " //
		        + localeCriterion + " "//
		        + advertTypeCriterion //
		        + "or program in (" //
		        + "from Program " //
		        + "where system = :system " //
		        + "and advertType in (" + "from AdvertType " + "where code like :advertTypeName)) " //
		        + localeCriterion //
		        + advertTypeCriterion + ")";
	}

	private static String getInstitionInheritanceCriterion(String localeCriterion, String advertTypeCriterion) {
		return "and (program in (" //
		        + "from Program " //
		        + "where institution = :institution " + "and advertType in (" + "from AdvertType " + "where code like :advertTypeName)) " //
		        + localeCriterion //
		        + advertTypeCriterion + ")";
	}

}
