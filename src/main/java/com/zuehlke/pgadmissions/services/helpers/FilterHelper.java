package com.zuehlke.pgadmissions.services.helpers;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.ObjectFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.RatingFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StringFilterDTO;

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
        Criterion restriction = Restrictions.between(filterProperty, filterDefinition.getRangeStartAsDateTime(), filterDefinition.getRangeCloseAsDateTime()); 
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }
    
    public static void appendDateFilterCriterion(Junction filterConditions, String filterProperty, DateFilterDTO filterDefinition) {
        Criterion restriction = Restrictions.between(filterProperty, filterDefinition.getRangeStart(), filterDefinition.getRangeClose());
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }

    public static void appendRatingFilterCriterion(Junction filterConditions, String filterProperty, RatingFilterDTO filterDefinition) {
        Criterion restriction = Restrictions.between(filterProperty, filterDefinition.getRangeStart(), filterDefinition.getRangeClose());
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
    
    private static void applyOrNegateFilterCriterion(Junction filterConditions, ObjectFilterDTO filterDefinition, Criterion restriction) {
        if (filterDefinition.isNegated()) {
            restriction = Restrictions.not(restriction);
        }
        filterConditions.add(restriction);
    }
    
}
