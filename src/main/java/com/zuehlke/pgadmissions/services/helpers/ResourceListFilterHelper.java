package com.zuehlke.pgadmissions.services.helpers;

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

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DecimalFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StateGroupFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StringFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.UserRoleFilterDTO;

public class ResourceListFilterHelper extends FilterHelper {
    
    public static <T extends Resource> void appendClosingDateFilterCriteria(Class<T> resourceClass, Junction filterConditions, String filterProperty,
            List<DateFilterDTO> filterConstraints) {
        if (resourceClass.equals(Application.class)) {
            for (DateFilterDTO filterConstraint : filterConstraints) {
                Junction closingDateRestriction = Restrictions.disjunction();
                appendDateFilterCriterion(closingDateRestriction, filterProperty, filterConstraint);
                appendDateFilterCriterion(closingDateRestriction, "previous" + WordUtils.capitalize(filterProperty), filterConstraint);
                filterConditions.add(closingDateRestriction);
            }
        } else {
            throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
        }
    }

    public static void appendCodeFilterCriteria(Junction filterConditions, String filterProperty, List<StringFilterDTO> filterConstraints) {
        for (StringFilterDTO filterConstraint : filterConstraints) {
            appendStringFilterCriterion(filterConditions, filterProperty, filterConstraint);
        }
    }

    public static <T extends Resource> void appendConfirmedStartDateFilterCriteria(Class<T> resourceClass, Junction filterConditions, String filterProperty,
            List<DateFilterDTO> filterConstraints) {
        if (resourceClass.equals(Application.class)) {
            appendDateFilterCriteria(filterConditions, filterProperty, filterConstraints);
        } else {
            throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
        }
    }

    public static void appendDateFilterCriteria(Junction filterConditions, String filterProperty, List<DateFilterDTO> filterConstraints) {
        for (DateFilterDTO filterConstraint : filterConstraints) {
            appendDateFilterCriterion(filterConditions, filterProperty, filterConstraint);
        }
    }

    public static <T extends Resource> void appendSubmittedTimestampFilterCriteria(Class<T> resourceClass, Junction filterConditions, String filterProperty,
            List<DateFilterDTO> filterConstraints) {
        if (resourceClass.equals(Application.class)) {
            appendTimestampFilterCriteria(filterConditions, filterProperty, filterConstraints);
        } else {
            throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
        }
    }

    public static void appendTimestampFilterCriteria(Junction filterConditions, String filterProperty, List<DateFilterDTO> filterConstraints) {
        for (DateFilterDTO filterConstraint : filterConstraints) {
            appendDateTimeFilterCriterion(filterConditions, filterProperty, filterConstraint);
        }
    }

    public static <T extends Resource> void appendRatingFilterCriteria(Class<T> resourceClass, Junction filterConditions, List<DecimalFilterDTO> filterConstraints) {
        String fieldProperty = resourceClass.equals(Application.class) ? "ratingAverage" : "applicationRatingAverage";
        for (DecimalFilterDTO filterConstraint : filterConstraints) {
            appendRatingFilterCriterion(filterConditions, fieldProperty, filterConstraint);
        }
    }
    
    public static <T extends Resource> void appendParentResourceFilterCriteria(Class<T> resourceClass, Junction filterConditions, String filterProperty,
            List<StringFilterDTO> filterConstraints) {
        PrismScope listScope = PrismScope.getResourceScope(resourceClass);
        PrismScope filterScope = PrismScope.valueOf(filterProperty.toUpperCase());
        if (filterScope.getPrecedence() < listScope.getPrecedence()) {
            for (StringFilterDTO filterConstraint : filterConstraints) {
                appendParentResourceFilterCriterion(filterConditions, filterProperty, filterConstraint);
            }
        } else {
            throwResourceFilterListMissingPropertyError(resourceClass, filterProperty);
        }
    }
    
    public static void appendStateFilterCriteria(Junction filterConditions, String filterProperty, List<StateGroupFilterDTO> filterConstraints) {
        for (StateGroupFilterDTO filterConstraint : filterConstraints) {
            appendStateFilterCriterion(filterConditions, filterProperty, filterConstraint);
        }
    }

    public static void appendUserFilterCriteria(Junction filterConditions, String filterProperty, List<StringFilterDTO> filterConstraints) {
        for (StringFilterDTO filterConstraint : filterConstraints) {
            appendUserFilterCriterion(filterConditions, filterProperty, filterConstraint);
        }
    }
    
    public static void appendUserRoleFilterCriteria(Junction filterConditions, String filterProperty, List<UserRoleFilterDTO> filterConstraints) {
        for (UserRoleFilterDTO filterConstraint : filterConstraints) {
            appendUserRoleFilterCriterion(filterConditions, filterProperty, filterConstraint);
        }
    }
    
    public static <T extends Resource> void throwResourceFilterListMissingPropertyError(Class<T> resourceClass, String filterProperty) {
        throw new Error(resourceClass.getSimpleName() + " does not have a " + filterProperty + " property");
    }
    
    private static void appendParentResourceFilterCriterion(Junction filterConditions, String filterProperty, StringFilterDTO filterDefinition) {
        Criterion restriction = Subqueries.propertyIn(filterProperty + ".id", //
                DetachedCriteria.forClass(PrismScope.valueOf(filterProperty.toUpperCase()).getResourceClass()) //
                        .setProjection(Projections.property("id")) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ilike("code", filterDefinition.getString(), MatchMode.ANYWHERE)) //
                                .add(Restrictions.ilike("title", filterDefinition.getString(), MatchMode.ANYWHERE))));
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }
    
    private static void appendUserFilterCriterion(Junction filterConditions, String filterProperty, StringFilterDTO filterTerm) {
        filterConditions.add(Subqueries.in(filterProperty + ".id", // 
                DetachedCriteria.forClass(User.class) //
                        .setProjection(Projections.property("id")) //
                        .add(Restrictions.disjunction()
                                .add(Restrictions.ilike("fullName", filterTerm.getString(), MatchMode.ANYWHERE))
                                .add(Restrictions.ilike("email", filterTerm.getString(), MatchMode.ANYWHERE)))));
    }
    
    private static void appendUserRoleFilterCriterion(Junction filterConditions, String filterProperty, UserRoleFilterDTO filterTerm) {
        filterConditions.add(Subqueries.in(filterProperty + ".id", // 
                DetachedCriteria.forClass(UserRole.class) //
                        .setProjection(Projections.property("id")) //
                        .createAlias("user", "user", JoinType.INNER_JOIN) //
                        .add(Restrictions.in("role.id", filterTerm.getRoles())) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ilike("user.fullName", filterTerm.getString(), MatchMode.ANYWHERE)) //
                                .add(Restrictions.ilike("user.email", filterTerm.getString(), MatchMode.ANYWHERE)))));
    }
    
    private static void appendStateFilterCriterion(Junction filterConditions, String filterProperty, StateGroupFilterDTO filterDefinition) {
        Criterion restriction = Restrictions.eq(filterProperty + ".id", filterDefinition.getStateGroup());
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }
    
}
