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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.resource.Resource;
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
            PrismDisplayPropertyCategory category, PrismScope propertyScope) {
        return (List<DisplayPropertyConfiguration>) sessionFactory.getCurrentSession().createCriteria(DisplayPropertyConfiguration.class) //
                .createAlias("displayProperty", "displayProperty", JoinType.INNER_JOIN) //
                .add(getFilterCondition(resource, locale, programType, propertyScope)) //
                .add(Restrictions.eq("displayProperty.displayCategory", category)) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.asc("systemDefault")) //
                .list();
    }

    public <T extends WorkflowResource> T getConfigurationStrict(Class<T> entityClass, Resource resource, PrismLocale locale, PrismProgramType programType,
            String keyIndex, WorkflowDefinition keyValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq(keyIndex, keyValue)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .add(Restrictions.eq("locale", locale)) //
                .add(Restrictions.eqOrIsNull("programType", programType)) //
                .uniqueResult();
    }

    public <T extends WorkflowResource> void restoreGlobalConfiguration(Class<T> entityClass, Resource resource, PrismLocale locale,
            PrismProgramType programType, String keyIndex, WorkflowDefinition keyValue) {
        PrismScope resourceScope = resource.getResourceScope();

        String programTypeConstraint = programType == null ? "and programType is null " : "and programType = :programType ";
        String localeConstraint = locale == null ? "and locale is null " : " and locale = :locale ";

        Query query;
        if (resourceScope == SYSTEM) {
            query = sessionFactory.getCurrentSession().createQuery( //
                    "delete " + entityClass.getSimpleName() + " " //
                            + "where " + keyIndex + " = :keyValue " //
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
                            + "where " + keyIndex + " = :keyValue " //
                            + "and (program in (" //
                            + "from Program " //
                            + "where institution = :institution) " //
                            + programTypeConstraint //
                            + localeConstraint + ")");
        } else {
            throw new Error();
        }

        query.setParameter(resourceScope.getLowerCaseName(), resource) //
                .setParameter("keyValue", keyValue);

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
