package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismLocale.getSystemLocale;
import static com.zuehlke.pgadmissions.domain.definitions.PrismProgramType.getSystemProgramType;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.WordUtils;
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

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> T getConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, U definition) {
        return (T) sessionFactory.getCurrentSession().createCriteria(configurationClass) //
                .add(getFilterCondition(resource, locale, programType, definition.getScope().getId())) //
                .add(Restrictions.eq(getLowerCaseClassReference(definition), definition)) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public <T extends WorkflowConfiguration> List<T> getConfigurationVersion(Class<T> configurationClass, Integer version) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(configurationClass) //
                .add(Restrictions.eq("version", version)) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public <T extends WorkflowDefinition> List<T> listDefinitions(Class<T> configurationClass, PrismScope definitionScope) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(configurationClass) //
                .createAlias("scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("scope.id", definitionScope)) //
                .addOrder(Order.asc("scope.id")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> List<T> listConfigurations(Resource resource, PrismScope definitionScope,
            PrismLocale locale, PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass) {
        String definitionReference = getLowerCaseClassReference(definitionClass);

        Criterion localeCriterion = getLocaleCriterion(locale);
        Criterion programTypeCriterion = getProgramTypeCriterion(programType, definitionScope);

        return (List<T>) sessionFactory.getCurrentSession().createCriteria(configurationClass) //
                .createAlias(definitionReference, definitionReference, JoinType.INNER_JOIN) //
                .createAlias(definitionReference + ".scope", "definitionScope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("definitionScope.id", definitionScope)).add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("system", resource.getSystem())) //
                                .add(localeCriterion) //
                                .add(programTypeCriterion)) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("system", resource.getInstitution())) //
                                .add(programTypeCriterion)) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.asc(definitionReference)) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreDefaultConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        String localeConstraint = locale == null ? "" : "and locale = :locale";
        String programTypeConstraint = programType == null ? "" : "and programType = :programType ";

        Query query = sessionFactory.getCurrentSession().createQuery( //
                "delete " + getUpperCaseClassReference(configurationClass) + " " //
                        + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + "and " + getDefinitionScopeConstraint(definitionClass) //
                        + localeConstraint + " " //
                        + programTypeConstraint) //
                .setParameter("resource", resource) //
                .setParameter("definitionScope", definitionScope);

        applyLocalizationConstraints(locale, programType, query);
        query.executeUpdate();
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreDefaultConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, U definition) {
        String localeConstraint = locale == null ? "" : "and locale = :locale";
        String programTypeConstraint = programType == null ? "" : "and programType = :programType ";
        String definitionReference = getLowerCaseClassReference(definition);

        Query query = sessionFactory.getCurrentSession().createQuery( //
                "delete " + configurationClass.getSimpleName() + " " //
                        + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + "and " + definitionReference + " = :definition " //
                        + localeConstraint + " " //
                        + programTypeConstraint) //
                .setParameter("resource", resource) //
                .setParameter("definition", definition);

        applyLocalizationConstraints(locale, programType, query);
        query.executeUpdate();
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreDefaultConfigurationVersion(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        String localeConstraint = locale == null ? "" : "and locale = :locale";
        String programTypeConstraint = programType == null ? "" : "and programType = :programType ";

        Query query = sessionFactory.getCurrentSession().createQuery( //
                "update " + getUpperCaseClassReference(configurationClass) + " " //
                        + "set active = false " //
                        + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        +  "and " + getDefinitionScopeConstraint(definitionClass) //
                        + localeConstraint //
                        + programTypeConstraint) //
                .setParameter("resource", resource) //
                .setParameter("definitionScope", definitionScope);

        applyLocalizationConstraints(locale, programType, query);
        query.executeUpdate();
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreDefaultConfigurationVersion(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, U definition) {
        String localeConstraint = locale == null ? "" : "and locale = :locale";
        String programTypeConstraint = programType == null ? "" : "and programType = :programType ";

        Query query = sessionFactory.getCurrentSession().createQuery( //
                "update " + getUpperCaseClassReference(configurationClass) + " " //
                        + "set active = false " + "where " + resource.getResourceScope().getLowerCaseName() + " = :resource " //
                        + "and " + getLowerCaseClassReference(definition) + " = :definition " //
                        + localeConstraint //
                        + programTypeConstraint) //
                .setParameter("resource", resource) //
                .setParameter("definition", definition);

        applyLocalizationConstraints(locale, programType, query);
        query.executeUpdate();
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreGlobalConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        PrismScope resourceScope = resource.getResourceScope();
        String configurationReference = getUpperCaseClassReference(configurationClass);

        String definitionConstraint = "where " + getDefinitionScopeConstraint(definitionClass);
        String programTypeCriterion = programType == null ? "and programType is null " : "and programType = :programType ";
        String localeCriterion = locale == null ? "and locale is null " : " and locale = :locale ";

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "delete " + configurationReference + " " //
                            + definitionConstraint //
                            + "and (institution in (" //
                            + "from Institution " //
                            + "where system = :system) " //
                            + programTypeCriterion //
                            + localeCriterion + " "//
                            + "or program in (" //
                            + "from Program " //
                            + "where system = :system) " //
                            + programTypeCriterion //
                            + localeCriterion + ")");
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "delete " + configurationReference + " " //
                            + definitionConstraint //
                            + "and (program in (" //
                            + "from Program " //
                            + "where institution = :institution) " //
                            + programTypeCriterion //
                            + localeCriterion + ")");
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCaseName(), resource) //
                .setParameter("definitionScope", definitionScope) //
                .setParameter("locale", locale) //
                .setParameter("programType", programType) //
                .executeUpdate();
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreGlobalConfiguration(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, U definition) {
        PrismScope resourceScope = resource.getResourceScope();
        String configurationReference = getUpperCaseClassReference(configurationClass);

        String definitionConstraint = "where " + getLowerCaseClassReference(definition) + " = :definition ";
        String programTypeCriterion = programType == null ? "and programType is null " : "and programType = :programType ";
        String localeCriterion = locale == null ? "and locale is null " : " and locale = :locale ";

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "delete " + configurationReference + " " //
                            + definitionConstraint //
                            + "and (institution in (" //
                            + "from Institution " //
                            + "where system = :system) " //
                            + programTypeCriterion //
                            + localeCriterion + " "//
                            + "or program in (" //
                            + "from Program " //
                            + "where system = :system) " //
                            + programTypeCriterion //
                            + localeCriterion + ")");
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "delete " + configurationReference + " " //
                            + definitionConstraint //
                            + "and (program in (" //
                            + "from Program " //
                            + "where institution = :institution) " //
                            + programTypeCriterion //
                            + localeCriterion + ")");
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCaseName(), resource) //
                .setParameter("definition", definition) //
                .setParameter("locale", locale) //
                .setParameter("programType", programType) //
                .executeUpdate();
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreGlobalConfigurationVersion(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, Class<U> definitionClass, PrismScope definitionScope) {
        PrismScope resourceScope = resource.getResourceScope();
        String configurationReference = getUpperCaseClassReference(configurationClass);

        String definitionConstraint = "where " + getDefinitionScopeConstraint(definitionClass);
        String programTypeCriterion = programType == null ? "and programType is null " : "and programType = :programType ";
        String localeCriterion = locale == null ? "and locale is null " : " and locale = :locale ";

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "update " + configurationReference + " " //
                            + "set active = false " //
                            + definitionConstraint //
                            + "and (institution in (" //
                            + "from Institution " //
                            + "where system = :system) " //
                            + programTypeCriterion //
                            + localeCriterion + " "//
                            + "or program in (" //
                            + "from Program " //
                            + "where system = :system) " //
                            + programTypeCriterion //
                            + localeCriterion + ")");
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "update " + configurationReference + " " //
                            + "set active = false " //
                            + definitionConstraint //
                            + "and (program in (" //
                            + "from Program " //
                            + "where institution = :institution) " //
                            + programTypeCriterion //
                            + localeCriterion + ")");
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCaseName(), resource) //
                .setParameter("definitionScope", definitionScope) //
                .setParameter("locale", locale) //
                .setParameter("programType", programType) //
                .executeUpdate();
    }

    public <T extends WorkflowConfiguration, U extends WorkflowDefinition> void restoreGlobalConfigurationVersion(Resource resource, PrismLocale locale,
            PrismProgramType programType, Class<T> configurationClass, U definition) {
        PrismScope resourceScope = resource.getResourceScope();
        String configurationReference = getUpperCaseClassReference(configurationClass);

        String definitionConstraint = "where " + getLowerCaseClassReference(definition) + " = :definition ";
        String programTypeCriterion = programType == null ? "and programType is null " : "and programType = :programType ";
        String localeCriterion = locale == null ? "and locale is null " : " and locale = :locale ";

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "update " + configurationReference + " " //
                            + "set active = false " //
                            + definitionConstraint //
                            + "and (institution in (" //
                            + "from Institution " //
                            + "where system = :system) " //
                            + programTypeCriterion //
                            + localeCriterion + " "//
                            + "or program in (" //
                            + "from Program " //
                            + "where system = :system) " //
                            + programTypeCriterion //
                            + localeCriterion + ")");
        } else if (resourceScope == INSTITUTION) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "update " + configurationReference + " " //
                            + "set active = false " //
                            + definitionConstraint //
                            + "and (program in (" //
                            + "from Program " //
                            + "where institution = :institution) " //
                            + programTypeCriterion //
                            + localeCriterion + ")");
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCaseName(), resource) //
                .setParameter("definition", definition) //
                .setParameter("locale", locale) //
                .setParameter("programType", programType) //
                .executeUpdate();
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

    private Junction getFilterCondition(Resource resource, PrismLocale locale, PrismProgramType programType, PrismScope definitionScope) {
        Criterion localeCriterion = getLocaleCriterion(locale);
        Criterion programTypeCriterion = getProgramTypeCriterion(programType, definitionScope);

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

    private Criterion getLocaleCriterion(PrismLocale locale) {
        return locale == null ? Restrictions.eq("locale", getSystemLocale()) : Restrictions.in("locale", Lists.newArrayList(locale, getSystemLocale()));
    }

    private Criterion getProgramTypeCriterion(PrismProgramType programType, PrismScope definitionScope) {
        return definitionScope.getPrecedence() < PROGRAM.getPrecedence() ? Restrictions.isNull("programType") : programType == null ? Restrictions.eq(
                "programType", getSystemProgramType()) : Restrictions.in("programType", Arrays.asList(programType, getSystemProgramType()));
    }

    private void applyLocalizationConstraints(PrismLocale locale, PrismProgramType programType, Query query) {
        if (locale != null) {
            query.setParameter("locale", locale);
        }

        if (programType != null) {
            query.setParameter("programType", programType);
        }
    }

    private <U extends WorkflowDefinition> String getDefinitionScopeConstraint(Class<U> definitionClass) {
        return getLowerCaseClassReference(definitionClass) + " in (" //
                + "from " + getUpperCaseClassReference(definitionClass) + " " //
                + "where scope.id =: definitionScope) ";
    }

    private static String getUpperCaseClassReference(Class<?> objectClass) {
        return objectClass.getSimpleName();
    }

    private static String getLowerCaseClassReference(Object object) {
        return WordUtils.uncapitalize(getLowerCaseClassReference(object.getClass()));
    }

    private static String getLowerCaseClassReference(Class<?> objectClass) {
        return WordUtils.uncapitalize(objectClass.getSimpleName());
    }

}
