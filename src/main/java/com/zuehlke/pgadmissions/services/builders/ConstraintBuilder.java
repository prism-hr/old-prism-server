package com.zuehlke.pgadmissions.services.builders;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;

public class ConstraintBuilder {
    
    public static void appendStringFilterCriterion(Junction conditions, String property, String value, boolean negated) {
        Criterion restriction = Restrictions.ilike(property, value, MatchMode.ANYWHERE);
        applyOrNegateFilterCriterion(conditions, restriction, negated);
    }

    public static void appendDateTimeFilterCriterion(Junction conditions, String property, FilterExpression expression, DateTime valueStart,
            DateTime valueClose, boolean negated) {
        Criterion restriction = getRangeFilterCriterion(property, expression, valueClose, valueStart);
        applyOrNegateFilterCriterion(conditions, restriction, negated);
    }

    public static void appendDateFilterCriterion(Junction conditions, String property, FilterExpression expression, LocalDate valueStart,
            LocalDate valueClose, boolean negated) {
        Criterion restriction = getRangeFilterCriterion(property, expression, valueStart, valueClose);
        applyOrNegateFilterCriterion(conditions, restriction, negated);
    }

    public static void appendDecimalFilterCriterion(Junction conditions, String property, FilterExpression expression, BigDecimal valueStart,
            BigDecimal valueClose, boolean negated) {
        Criterion restriction = getRangeFilterCriterion(property, expression, valueStart, valueClose);
        applyOrNegateFilterCriterion(conditions, restriction, negated);
    }
    
    public static void appendPropertyInFilterCriterion(Junction conditions, String property, List<?> valueIds, boolean negated) {
        for (Object value : valueIds) {
            applyOrNegateFilterCriterion(conditions, Restrictions.eq(property, value), negated);
        }
    }

    protected static Criterion getRangeFilterCriterion(String property, FilterExpression expression, Object valueStart, Object valueClose) {
        switch (expression) {
        case BETWEEN:
            return Restrictions.between(property, valueStart, valueClose);
        case EQUAL:
            return getRangeEqualRestriction(property, valueStart);
        case GREATER:
            return Restrictions.ge(property, valueStart);
        case LESSER:
            return Restrictions.le(property, valueClose);
        default:
            throw new Error("Invalid filter expression: " + expression.name() + " for property: " + property);
        }
    }
    
    protected static void applyOrNegateFilterCriterion(Junction conditions, Criterion restriction, boolean negated) {
        if (negated) {
            restriction = Restrictions.not(restriction);
        }
        conditions.add(restriction);
    }
    
    private static Criterion getRangeEqualRestriction(String property, Object valueStart) {
        if (valueStart.getClass().equals(DateTime.class)) {
            DateTime valueStartDateTime = (DateTime) valueStart;
            return Restrictions.between(property, valueStartDateTime, valueStartDateTime.plusDays(1).minusSeconds(1));
        }
        return Restrictions.eq(property, valueStart);
    }

}
