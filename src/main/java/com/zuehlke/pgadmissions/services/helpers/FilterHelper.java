package com.zuehlke.pgadmissions.services.helpers;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.AbstractFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DecimalFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StringFilterDTO;

public class FilterHelper {

    public static void appendStringFilterCriterion(Junction filterConditions, String filterProperty, StringFilterDTO filterDefinition) {
        Criterion restriction = Restrictions.ilike(filterProperty, filterDefinition.getString(), MatchMode.ANYWHERE);
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

    public static void appendRatingFilterCriterion(Junction filterConditions, String filterProperty, DecimalFilterDTO filterDefinition) {
        Criterion restriction = getRangeFilterCriterion(filterProperty, filterDefinition.getRangeStart(), filterDefinition.getRangeClose());
        applyOrNegateFilterCriterion(filterConditions, filterDefinition, restriction);
    }
    
    protected static Criterion getRangeFilterCriterion(String filterProperty, Object rangeStart, Object rangeClose) {
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
    
    protected static void applyOrNegateFilterCriterion(Junction filterConditions, AbstractFilterDTO filterDefinition, Criterion restriction) {
        if (filterDefinition.isNegated()) {
            restriction = Restrictions.not(restriction);
        }
        filterConditions.add(restriction);
    }
    
}
