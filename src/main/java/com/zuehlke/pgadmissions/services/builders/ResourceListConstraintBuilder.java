package com.zuehlke.pgadmissions.services.builders;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;

public class ResourceListConstraintBuilder extends ConstraintBuilder {

    public static <T extends Resource> DetachedCriteria getVisibleResourcesCriteria(User user, Class<T> resourceClass, List<PrismScope> parentScopeIds,
                                                                                    ResourceListFilterDTO filterDTO) {
        DetachedCriteria criteria = DetachedCriteria.forClass(resourceClass) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("userRoles", "userRole", JoinType.LEFT_OUTER_JOIN);

        DetachedCriteria application = DetachedCriteria.forClass(UserRole.class) //
                .setProjection(Projections.groupProperty("application.id")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eqProperty("role", "stateActionAssignment.role")) //
                .add(Restrictions.isNotNull(PrismScope.getResourceScope(resourceClass).getLowerCaseName()));

        boolean getUrgentOnly = filterDTO == null ? false : BooleanUtils.toBoolean(filterDTO.getUrgentOnly());

        if (getUrgentOnly) {
            application.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
        }

        Junction disjunction = Restrictions.disjunction() //
                .add(Subqueries.propertyIn("id", application));

        for (PrismScope parentScopeId : parentScopeIds) {
            String parentResourceReference = parentScopeId.getLowerCaseName();

            DetachedCriteria stateCriteria = DetachedCriteria.forClass(StateAction.class) //
                    .setProjection(Projections.groupProperty("state.id")) //
                    .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                    .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                    .add(Restrictions.eq("role.scope.id", parentScopeId));

            if (getUrgentOnly) {
                stateCriteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
            }

            disjunction.add(Restrictions.conjunction() //
                    .add(Subqueries.propertyIn(parentResourceReference, //
                            DetachedCriteria.forClass(UserRole.class) //
                                    .setProjection(Projections.groupProperty(parentResourceReference + ".id")) //
                                    .add(Restrictions.eq("user", user)) //
                                    .add(Restrictions.isNotNull(parentResourceReference)))) //
                    .add(Subqueries.propertyIn("state", stateCriteria)));
        }

        criteria.add(disjunction);

        if (filterDTO == null || filterDTO.getConstraints() == null) {
            return criteria;
        }

        Junction conditions = Restrictions.conjunction();
        if (filterDTO.getMatchMode() == FilterMatchMode.ANY) {
            conditions = Restrictions.disjunction();
        }

        for (ResourceListFilterConstraintDTO constraintDTO : filterDTO.getConstraints()) {
            FilterProperty property = constraintDTO.getFilterProperty();
            String propertyName = property.getPropertyName();

            if (FilterProperty.isPermittedFilterProperty(PrismScope.getResourceScope(resourceClass), property)) {
                Boolean negated = BooleanUtils.toBoolean(constraintDTO.getNegated());
                switch (property) {
                    case CLOSING_DATE:
                        ResourceListConstraintBuilder.appendClosingDateFilterCriterion(resourceClass, conditions, propertyName,
                                constraintDTO.getFilterExpression(), constraintDTO.getValueDateStart(), constraintDTO.getValueDateClose(),
                                negated);
                        break;
                    case CODE:
                    case REFERRER:
                        ResourceListConstraintBuilder.appendStringFilterCriterion(conditions, propertyName, constraintDTO.getValueString(),
                                negated);
                        break;
                    case CONFIRMED_START_DATE:
                        ResourceListConstraintBuilder.appendDateFilterCriterion(conditions, propertyName, constraintDTO.getFilterExpression(),
                                constraintDTO.getValueDateStart(), constraintDTO.getValueDateClose(), negated);
                        break;
                    case CREATED_TIMESTAMP:
                    case UPDATED_TIMESTAMP:
                        ResourceListConstraintBuilder.appendDateTimeFilterCriterion(conditions, propertyName, constraintDTO.getFilterExpression(),
                                constraintDTO.getValueDateTimeStart(), constraintDTO.getValueDateTimeClose(), negated);
                        break;
                    case DUE_DATE:
                        ResourceListConstraintBuilder.appendClosingDateFilterCriterion(resourceClass, conditions, propertyName,
                                constraintDTO.getFilterExpression(), constraintDTO.getValueDateStart(), constraintDTO.getValueDateClose(),
                                negated);
                        break;
                    case INSTITUTION:
                    case PROGRAM:
                    case PROJECT:
                        ResourceListConstraintBuilder.appendParentResourceFilterCriterion(resourceClass, conditions, propertyName, constraintDTO.getValueString(),
                                negated);
                        break;
                    case RATING:
                        ResourceListConstraintBuilder.appendDecimalFilterCriterion(conditions, propertyName, constraintDTO.getFilterExpression(),
                                constraintDTO.getValueDecimalStart(), constraintDTO.getValueDecimalClose(), negated);
                        break;
                    case STATE_GROUP:
                        ResourceListConstraintBuilder.appendStateGroupFilterCriterion(conditions, propertyName, constraintDTO.getValueStateGroup(),
                                negated);
                        break;
                    case SUBMITTED_TIMESTAMP:
                        ResourceListConstraintBuilder.appendDateTimeFilterCriterion(conditions, propertyName, constraintDTO.getFilterExpression(),
                                constraintDTO.getValueDateTimeStart(), constraintDTO.getValueDateTimeClose(), negated);
                        break;
                    case USER:
                        ResourceListConstraintBuilder
                                .appendUserFilterCriterion(conditions, propertyName, constraintDTO.getValueString(), negated);
                        break;
                    case USER_ROLE:
                        ResourceListConstraintBuilder.appendUserRoleFilterCriterion(conditions, propertyName, constraintDTO.getValueString(),
                                constraintDTO.getValueRoles(), negated);
                        break;
                }
            } else {
                throwResourceFilterListMissingPropertyError(resourceClass, propertyName);
            }
        }

        return criteria.add(conditions);
    }

    public static <T extends Resource> Criteria appendResourceListDisplayFilterExpression(Class<T> resourceClass, Criteria criteria, FilterSortOrder order,
                                                                                          String lastSequenceId) {
        if (lastSequenceId != null) {
            appendResourceListPagingExpression(criteria, order, lastSequenceId);
        }

        appendResourceListOrderExpression(criteria, order);
        appendResourceListLimitExpression(resourceClass, criteria);

        return criteria;
    }

    private static <T extends Resource> void appendClosingDateFilterCriterion(Class<T> resourceClass, Junction conditions, String property,
                                                                              FilterExpression expression, LocalDate valueDateStart, LocalDate valueDateClose, boolean negated) {
        Junction closingDateRestriction;
        if (negated) {
            closingDateRestriction = Restrictions.conjunction();
        } else {
            closingDateRestriction = Restrictions.disjunction();
        }
        appendDateFilterCriterion(closingDateRestriction, property, expression, valueDateStart, valueDateClose, negated);
        appendDateFilterCriterion(closingDateRestriction, "previous" + WordUtils.capitalize(property), expression, valueDateStart, valueDateClose, negated);
        conditions.add(closingDateRestriction);
    }

    private static <T extends Resource> void appendParentResourceFilterCriterion(Class<T> resourceClass, Junction conditions, String property,
                                                                                 String valueString, boolean negated) {
        Criterion restriction = Subqueries.propertyIn(property + ".id", //
                DetachedCriteria.forClass(PrismScope.valueOf(property.toUpperCase()).getResourceClass()) //
                        .setProjection(Projections.property("id")) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ilike("code", valueString, MatchMode.ANYWHERE)) //
                                .add(Restrictions.ilike("title", valueString, MatchMode.ANYWHERE))));
        applyOrNegateFilterCriterion(conditions, restriction, negated);
    }

    private static void appendStateGroupFilterCriterion(Junction conditions, String property, PrismStateGroup valueStateGroup, boolean negated) {
        Criterion restriction = Restrictions.eq(property + ".id", valueStateGroup);
        applyOrNegateFilterCriterion(conditions, restriction, negated);
    }

    private static void appendUserFilterCriterion(Junction conditions, String property, String valueString, boolean negated) {
        Criterion restriction = conditions.add(Subqueries.in(property + ".id", //
                DetachedCriteria.forClass(User.class) //
                        .setProjection(Projections.property("id")) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ilike("fullName", valueString, MatchMode.ANYWHERE)) //
                                .add(Restrictions.ilike("email", valueString, MatchMode.ANYWHERE)))));
        applyOrNegateFilterCriterion(conditions, restriction, negated);
    }

    private static void appendUserRoleFilterCriterion(Junction conditions, String property, String valueString, List<PrismRole> valueRoles, boolean negated) {
        Criterion restriction = conditions.add(Subqueries.in(property + ".id", //
                DetachedCriteria.forClass(UserRole.class) //
                        .setProjection(Projections.property("id")) //
                        .createAlias("user", "user", JoinType.INNER_JOIN) //
                        .add(Restrictions.in("role.id", valueRoles)) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ilike("user.fullName", valueString, MatchMode.ANYWHERE)) //
                                .add(Restrictions.ilike("user.email", valueString, MatchMode.ANYWHERE)))));
        applyOrNegateFilterCriterion(conditions, restriction, negated);
    }

    private static void appendResourceListPagingExpression(Criteria criteria, FilterSortOrder sortOrder, String lastSequenceIdentifier) {
        Criterion pagingCondition;
        if (sortOrder == FilterSortOrder.DESCENDING) {
            pagingCondition = Restrictions.lt("sequenceIdentifier", lastSequenceIdentifier);
        } else {
            pagingCondition = Restrictions.gt("sequenceIdentifier", lastSequenceIdentifier);
        }
        criteria.add(pagingCondition);
    }

    private static void appendResourceListOrderExpression(Criteria criteria, FilterSortOrder sortOrder) {
        if (sortOrder == FilterSortOrder.DESCENDING) {
            criteria.addOrder(Order.desc("sequenceIdentifier"));
        } else {
            criteria.addOrder(Order.asc("sequenceIdentifier"));
        }
    }

    private static <T extends Resource> void appendResourceListLimitExpression(Class<T> resourceClass, Criteria criteria) {
        Integer recordsToRetrieve = PrismScope.getResourceScope(resourceClass).getResourceListRecordsToRetrieve();
        if (recordsToRetrieve == null) {
            return;
        }
        criteria.setMaxResults(recordsToRetrieve);
    }

    private static <T extends Resource> void throwResourceFilterListMissingPropertyError(Class<T> resourceClass, String property) {
        throw new Error(resourceClass.getSimpleName() + " does not have a " + property + " property");
    }

}
