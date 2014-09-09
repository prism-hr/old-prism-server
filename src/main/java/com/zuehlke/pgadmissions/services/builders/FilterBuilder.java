package com.zuehlke.pgadmissions.services.builders;

import java.math.BigDecimal;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class FilterBuilder {

    public static void appendStringFilterCriterion(Junction filterConditions, String filterProperty, String value, boolean negated) {
        Criterion restriction = Restrictions.ilike(filterProperty, value, MatchMode.ANYWHERE);
        applyOrNegateFilterCriterion(filterConditions, restriction, negated);
    }
    
    public static void appendDateTimeFilterCriterion(Junction filterConditions, String filterProperty, DateTime valueStart, DateTime valueClose, boolean negated) {
        Criterion restriction = getRangeFilterCriterion(filterProperty, valueClose, valueStart);
        applyOrNegateFilterCriterion(filterConditions, restriction, negated);
    }
    
    public static void appendDateFilterCriterion(Junction filterConditions, String filterProperty, LocalDate valueStart, LocalDate valueClose, boolean negated) {
        Criterion restriction = getRangeFilterCriterion(filterProperty, valueStart, valueClose);
        applyOrNegateFilterCriterion(filterConditions, restriction, negated);
    }

    public static void appendDecimalFilterCriterion(Junction filterConditions, String filterProperty, BigDecimal valueStart, BigDecimal valueClose, boolean negated) {
        Criterion restriction = getRangeFilterCriterion(filterProperty, valueStart, valueClose);
        applyOrNegateFilterCriterion(filterConditions, restriction, negated);
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
    
    protected static void applyOrNegateFilterCriterion(Junction filterConditions, Criterion restriction, boolean negated) {
        if (negated) {
            restriction = Restrictions.not(restriction);
        }
        filterConditions.add(restriction);
    }
    
}

