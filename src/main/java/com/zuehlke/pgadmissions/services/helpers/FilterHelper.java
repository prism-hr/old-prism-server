package com.zuehlke.pgadmissions.services.helpers;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.ObjectFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.RatingFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StringFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.UserRoleFilterDTO;

public class FilterHelper {
    
    public static void appendParentResourceFilterCriterion(Junction filterConditions, String filterProperty, StringFilterDTO filterDefinition) {
        Criterion restriction = Subqueries.propertyIn(filterProperty + ".id", //
                DetachedCriteria.forClass(PrismScope.valueOf(filterProperty.toUpperCase()).getResourceClass()) //
                        .setProjection(Projections.property("id")) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ilike("code", filterDefinition.getFilter(), MatchMode.ANYWHERE)) //
                                .add(Restrictions.ilike("title", filterDefinition.getFilter(), MatchMode.ANYWHERE))));
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }

    public static void appendStringFilterCriterion(Junction filterConditions, String filterProperty, StringFilterDTO filterDefinition) {
        Criterion restriction = Restrictions.ilike(filterProperty, filterDefinition.getFilter(), MatchMode.ANYWHERE);
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }
    
    public static void appendStateFilterCriterion(Junction filterConditions, String filterProperty, StateFilterDTO filterDefinition) {
        Criterion restriction = Restrictions.eq(filterProperty + ".id", filterDefinition.getFilter());
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }
    
    public static void appendDateTimeFilterCriterion(Junction filterConditions, String filterProperty, DateFilterDTO filterDefinition) {
        Criterion restriction = getRangeFilterCriterion(filterProperty, filterDefinition.getRangeStartAsDateTime(), filterDefinition.getRangeCloseAsDateTime());
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }
    
    public static void appendDateFilterCriterion(Junction filterConditions, String filterProperty, DateFilterDTO filterDefinition) {
        Criterion restriction = getRangeFilterCriterion(filterProperty, filterDefinition.getRangeStart(), filterDefinition.getRangeClose());
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }

    public static void appendRatingFilterCriterion(Junction filterConditions, String filterProperty, RatingFilterDTO filterDefinition) {
        Criterion restriction = getRangeFilterCriterion(filterProperty, filterDefinition.getRangeStart(), filterDefinition.getRangeClose());
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }
    
    public static void appendUserFilterCriterion(Junction filterConditions, String filterProperty, StringFilterDTO filterTerm) {
        filterConditions.add(Subqueries.in(filterProperty + ".id", // 
                DetachedCriteria.forClass(User.class) //
                        .setProjection(Projections.property("id")) //
                        .add(Restrictions.disjunction()
                                .add(Restrictions.ilike("fullName", filterTerm.getFilter(), MatchMode.ANYWHERE))
                                .add(Restrictions.ilike("email", filterTerm.getFilter(), MatchMode.ANYWHERE)))));
    }
    
    public static void appendUserRoleFilterCriterion(Junction filterConditions, String filterProperty, UserRoleFilterDTO filterTerm) {
        filterConditions.add(Subqueries.in(filterProperty + ".id", // 
                DetachedCriteria.forClass(UserRole.class) //
                        .setProjection(Projections.property("id")) //
                        .createAlias("user", "user", JoinType.INNER_JOIN) //
                        .add(Restrictions.in("role.id", filterTerm.getUserRoles())) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.ilike("user.fullName", filterTerm.getFilter(), MatchMode.ANYWHERE)) //
                                .add(Restrictions.ilike("user.email", filterTerm.getFilter(), MatchMode.ANYWHERE)))));
    }
    
    private static Criterion getRangeFilterCriterion(String filterProperty, Object rangeStart, Object rangeClose) {
        Criterion restriction;
        if (rangeStart == null) {
            restriction = Restrictions.le(filterProperty, rangeClose);
        } else if (rangeClose == null) {
            restriction = Restrictions.ge(filterProperty, rangeStart);
        } else {
            restriction = Restrictions.between(filterProperty, rangeStart, rangeClose);
        }
        return restriction;
    }
    
    private static void applyOrNegateFilterCriterion(Junction filterConditions, ObjectFilterDTO filterDefinition, Criterion restriction) {
        if (filterDefinition.isNegated()) {
            restriction = Restrictions.not(restriction);
        }
        filterConditions.add(restriction);
    }
    
}
