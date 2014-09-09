package com.zuehlke.pgadmissions.services.builders;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.FilterFetchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;

public class ResourceListFilterBuilder extends FilterBuilder {

    public static <T extends Resource> DetachedCriteria getVisibleResourcesCriteria(User user, Class<T> resourceClass, List<PrismScope> parentScopeIds,
            ResourceListFilterDTO filterDTO, FilterFetchMode fetchMode) {
        if (resourceClass.equals(System.class)) {
            throw new Error("System is not a listable resource type");
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(resourceClass) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("userRoles", "userRole", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Subqueries.propertyIn("id", //
                                DetachedCriteria.forClass(UserRole.class) //
                                        .setProjection(Projections.groupProperty("application.id")) //
                                        .createAlias("role", "role", JoinType.INNER_JOIN) //
                                        .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                                        .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                                        .add(Restrictions.eq("user", user)) //
                                        .add(Restrictions.isNotNull(PrismScope.getResourceScope(resourceClass).getLowerCaseName())))));

        boolean getUrgentOnly = filterDTO.isUrgentOnly();

        if (getUrgentOnly) {
            criteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
        }

        for (PrismScope parentScopeId : parentScopeIds) {
            String parentResourceReference = parentScopeId.getLowerCaseName();

            DetachedCriteria stateCriteria = DetachedCriteria.forClass(StateAction.class)
                    //
                    .setProjection(Projections.groupProperty("state.id")).createAlias("stateActionAssignments", "stateActionAssigment", JoinType.INNER_JOIN)
                    .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN).add(Restrictions.eq("role.scope.id", parentScopeId));

            if (filterDTO.isUrgentOnly()) {
                stateCriteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
            }

            criteria.add(Restrictions.conjunction() //
                    .add(Subqueries.propertyIn(parentResourceReference, //
                            DetachedCriteria.forClass(UserRole.class) //
                                    .setProjection(Projections.groupProperty(parentResourceReference + ".id")) //
                                    .add(Restrictions.eq("user", user)).add(Restrictions.isNotNull(parentResourceReference)))) //
                    .add(Subqueries.propertyIn("state", //
                            stateCriteria)));
        }

        Junction filterConditions = Restrictions.conjunction();
        if (filterDTO.getMatchMode() == FilterMatchMode.ANY) {
            filterConditions = Restrictions.disjunction();
        }

        for (ResourceListFilterConstraintDTO constraintDTO : filterDTO.getConstraints()) {
            FilterProperty filterProperty = constraintDTO.getFilterProperty();
            String filterPropertyName = filterProperty.getPropertyName();

            switch (filterProperty) {
            case CLOSING_DATE:
                ResourceListFilterBuilder.appendClosingDateFilterCriterion(resourceClass, filterConditions, filterPropertyName,
                        constraintDTO.getValueDateStart(), constraintDTO.getValueDateClose(), constraintDTO.isNegated());
                break;
            case CODE:
            case REFERRER:
                ResourceListFilterBuilder.appendStringFilterCriterion(filterConditions, filterPropertyName, constraintDTO.getValueString(),
                        constraintDTO.isNegated());
                break;
            case CONFIRMED_START_DATE:
                ResourceListFilterBuilder.appendConfirmedStartDateFilterCriterion(resourceClass, filterConditions, filterPropertyName,
                        constraintDTO.getValueDateStart(), constraintDTO.getValueDateClose(), constraintDTO.isNegated());
                break;
            case CREATED_TIMESTAMP:
            case UPDATED_TIMESTAMP:
                ResourceListFilterBuilder.appendDateTimeFilterCriterion(filterConditions, filterPropertyName, constraintDTO.getValueDateTimeStart(),
                        constraintDTO.getValueDateTimeClose(), constraintDTO.isNegated());
                break;
            case DUE_DATE:
                ResourceListFilterBuilder.appendClosingDateFilterCriterion(resourceClass, filterConditions, filterPropertyName,
                        constraintDTO.getValueDateStart(), constraintDTO.getValueDateClose(), constraintDTO.isNegated());
                break;
            case INSTITUTION:
            case PROGRAM:
            case PROJECT:
                ResourceListFilterBuilder.appendParentResourceFilterCriterion(resourceClass, filterConditions, filterPropertyName,
                        constraintDTO.getValueString(), constraintDTO.isNegated());
                break;
            case RATING:
                filterPropertyName = resourceClass.equals(Application.class) ? "ratingAverage" : "applicationRatingAverage";
                ResourceListFilterBuilder.appendDecimalFilterCriterion(filterConditions, filterPropertyName, constraintDTO.getValueDecimalStart(),
                        constraintDTO.getValueDecimalClose(), constraintDTO.isNegated());
                break;
            case STATE_GROUP:
                ResourceListFilterBuilder.appendStateGroupFilterCriterion(filterConditions, filterPropertyName, constraintDTO.getValueStateGroup(),
                        constraintDTO.isNegated());
                break;
            case SUBMITTED_TIMESTAMP:
                ResourceListFilterBuilder.appendSubmittedTimestampFilterCriterion(resourceClass, filterConditions, filterPropertyName,
                        constraintDTO.getValueDateTimeStart(), constraintDTO.getValueDateTimeClose(), constraintDTO.isNegated());
                break;
            case USER:
                ResourceListFilterBuilder.appendUserFilterCriterion(filterConditions, filterPropertyName, constraintDTO.getValueString(),
                        constraintDTO.isNegated());
                break;
            case USER_ROLE:
                ResourceListFilterBuilder.appendUserRoleFilterCriterion(filterConditions, filterPropertyName, constraintDTO.getValueString(),
                        constraintDTO.getValueRoles(), constraintDTO.isNegated());
                break;
            }
        }

        return criteria.add(filterConditions);
    }

    private static <T extends Resource> void appendClosingDateFilterCriterion(Class<T> resourceClass, Junction filterConditions, String filterProperty,
            LocalDate valueDateStart, LocalDate valueDateClose, boolean negated) {
        if (resourceClass.equals(Application.class)) {
            Junction closingDateRestriction = Restrictions.disjunction();
            appendDateFilterCriterion(closingDateRestriction, filterProperty, valueDateStart, valueDateClose, negated);
            appendDateFilterCriterion(closingDateRestriction, "previous" + WordUtils.capitalize(filterProperty), valueDateStart, valueDateClose, negated);
            filterConditions.add(closingDateRestriction);
        } else {
            throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
        }
    }

    private static <T extends Resource> void appendConfirmedStartDateFilterCriterion(Class<T> resourceClass, Junction filterConditions, String filterProperty,
            LocalDate valueDateStart, LocalDate valueDateClose, boolean negated) {
        if (resourceClass.equals(Application.class)) {
            appendDateFilterCriterion(filterConditions, filterProperty, valueDateStart, valueDateClose, negated);
        } else {
            throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
        }
    }

    private static <T extends Resource> void appendSubmittedTimestampFilterCriterion(Class<T> resourceClass, Junction filterConditions, String filterProperty,
            DateTime valueDateTimeStart, DateTime valueDateTimeClose, boolean negated) {
        if (resourceClass.equals(Application.class)) {
            appendDateTimeFilterCriterion(filterConditions, filterProperty, valueDateTimeStart, valueDateTimeClose, negated);
        } else {
            throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
        }
    }

    private static <T extends Resource> void appendParentResourceFilterCriterion(Class<T> resourceClass, Junction filterConditions, String filterProperty,
            String valueString, boolean negated) {
        PrismScope listScope = PrismScope.getResourceScope(resourceClass);
        PrismScope filterScope = PrismScope.valueOf(filterProperty.toUpperCase());
        if (filterScope.getPrecedence() < listScope.getPrecedence()) {
            Criterion restriction = Subqueries.propertyIn(filterProperty + ".id", //
                    DetachedCriteria.forClass(PrismScope.valueOf(filterProperty.toUpperCase()).getResourceClass()) //
                            .setProjection(Projections.property("id")) //
                            .add(Restrictions.disjunction() //
                                    .add(Restrictions.ilike("code", valueString, MatchMode.ANYWHERE)) //
                                    .add(Restrictions.ilike("title", valueString, MatchMode.ANYWHERE))));
            applyOrNegateFilterCriterion(filterConditions, restriction, negated);
        } else {
            throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
        }
    }

    private static void appendStateGroupFilterCriterion(Junction filterConditions, String filterProperty, PrismStateGroup valueStateGroup, boolean negated) {
        Criterion restriction = Restrictions.eq(filterProperty + ".id", valueStateGroup);
        applyOrNegateFilterCriterion(filterConditions, restriction, negated);
    }

    private static void appendUserFilterCriterion(Junction filterConditions, String filterProperty, String valueString, boolean negated) {
        Criterion restriction = filterConditions.add(Subqueries.in(filterProperty + ".id", //
                DetachedCriteria.forClass(User.class) //
                        .setProjection(Projections.property("id")) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ilike("fullName", valueString, MatchMode.ANYWHERE)) //
                                .add(Restrictions.ilike("email", valueString, MatchMode.ANYWHERE)))));
        applyOrNegateFilterCriterion(filterConditions, restriction, negated);
    }

    private static void appendUserRoleFilterCriterion(Junction filterConditions, String filterProperty, String valueString, List<PrismRole> valueRoles,
            boolean negated) {
        Criterion restriction = filterConditions.add(Subqueries.in(filterProperty + ".id", //
                DetachedCriteria.forClass(UserRole.class) //
                        .setProjection(Projections.property("id")) //
                        .createAlias("user", "user", JoinType.INNER_JOIN) //
                        .add(Restrictions.in("role.id", valueRoles)) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ilike("user.fullName", valueString, MatchMode.ANYWHERE)) //
                                .add(Restrictions.ilike("user.email", valueString, MatchMode.ANYWHERE)))));
        applyOrNegateFilterCriterion(filterConditions, restriction, negated);
    }

    private static <T extends Resource> void throwResourceFilterListMissingPropertyError(Class<T> resourceClass, String filterProperty) {
        throw new Error(resourceClass.getSimpleName() + " does not have a " + filterProperty + " property");
    }

}
