package com.zuehlke.pgadmissions.services.builders;

import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.NOT_SPECIFIED;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListConstraint;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.workflow.selectors.filter.PrismResourceListFilterSelector;

@Component
public class PrismResourceListConstraintBuilder {

    @Inject
    private ApplicationContext applicationContext;

    public void appendFilter(Junction conditions, PrismScope resourceScope, ResourceListFilterConstraintDTO constraint) {
        PrismResourceListConstraint filterProperty = constraint.getFilterProperty();
        PrismResourceListFilterExpression filterExpression = constraint.getFilterExpression();

        String resourceQualifier = resourceScope.getLowerCamelName() + ".";
        String filterPropertyName = filterProperty.getPropertyName();
        filterPropertyName = filterPropertyName.startsWith(resourceQualifier) ? filterPropertyName.replace(resourceQualifier, "") : filterPropertyName;

        Boolean negated = BooleanUtils.toBooleanObject(constraint.getNegated());

        if (filterExpression == NOT_SPECIFIED) {
            conditions.add(Restrictions.isNull(filterPropertyName));
        } else {
            Class<? extends PrismResourceListFilterSelector<?>> filterValueSelector = filterProperty.getPropertyValueSelector();
            if (filterValueSelector == null) {
                switch (filterProperty.getPropertyType()) {
                case DATE:
                    appendDateFilter(conditions, filterPropertyName, filterExpression, negated, constraint.getValueDateStart(), constraint.getValueDateClose());
                    break;
                case DATE_TIME:
                    appendDateTimeFilter(conditions, filterPropertyName, filterExpression, negated, constraint.computeValueDateTimeStart(),
                            constraint.computeValueDateTimeClose());
                    break;
                case DECIMAL:
                    appendDecimalFilter(conditions, filterPropertyName, filterExpression, negated, constraint.getValueDecimalStart(),
                            constraint.getValueDecimalClose());
                    break;
                case STRING:
                    appendStringFilter(conditions, filterPropertyName, negated, constraint.getValueString());
                    break;
                default:
                    throw new UnsupportedOperationException();
                }
            } else {
                appendInCollectionFilter(conditions, filterPropertyName, constraint.getFilterExpression(), negated, //
                        applicationContext.getBean(filterProperty.getPropertyValueSelector()).getPossible(resourceScope, constraint));
            }
        }
    }

    private static void appendStringFilter(Junction conditions, String property, boolean negated, String value) {
        Criterion restriction = Restrictions.ilike(property, value, MatchMode.ANYWHERE);
        applyOrNegateFilterCriterion(conditions, property, restriction, negated);
    }

    private static void appendDateFilter(Junction conditions, String property, PrismResourceListFilterExpression expression, boolean negated,
            LocalDate valueStart,
            LocalDate valueClose) {
        Criterion restriction = getRangeFilterCriterion(property, expression, valueStart, valueClose);
        applyOrNegateFilterCriterion(conditions, property, restriction, negated);
    }

    private static void appendDateTimeFilter(Junction conditions, String property, PrismResourceListFilterExpression expression, boolean negated,
            DateTime valueStart, DateTime valueClose) {
        Criterion restriction = getRangeFilterCriterion(property, expression, valueClose, valueStart);
        applyOrNegateFilterCriterion(conditions, property, restriction, negated);
    }

    private static void appendDecimalFilter(Junction conditions, String property, PrismResourceListFilterExpression constraint, boolean negated,
            BigDecimal valueStart, BigDecimal valueClose) {
        Criterion restriction = getRangeFilterCriterion(property, constraint, valueStart, valueClose);
        applyOrNegateFilterCriterion(conditions, property, restriction, negated);
    }

    private static void appendInCollectionFilter(Junction conditions, String property, PrismResourceListFilterExpression constraint, boolean negated,
            List<?> valueIds) {
        valueIds = valueIds.isEmpty() ? Lists.newArrayList(0) : valueIds;
        Junction inConditions = negated ? Restrictions.conjunction() : Restrictions.disjunction();
        for (Object value : valueIds) {
            applyOrNegateFilterCriterion(inConditions, property, Restrictions.eq(property, value), negated);
        }
        conditions.add(inConditions);
    }

    private static <T extends Comparable<? super T>> Criterion getRangeFilterCriterion(String property, PrismResourceListFilterExpression constraint,
            T valueStart, T valueClose) {
        switch (constraint) {
        case BETWEEN:
            return getRangeFilterCriterionBetween(property, valueStart, valueClose);
        case EQUAL:
            return getRangeFilterRestrictionEqual(property, valueStart);
        case GREATER:
            return Restrictions.ge(property, valueStart == null ? valueClose : valueStart);
        case LESSER:
            return Restrictions.le(property, valueStart == null ? valueClose : valueStart);
        default:
            throw new Error("Invalid filter expression: " + constraint.name() + " for property: " + property);
        }
    }

    private static <T extends Comparable<? super T>> Criterion getRangeFilterCriterionBetween(String property, T valueStart, T valueClose) {
        T actualValueStart;
        T actualValueClose;

        if (valueStart.compareTo(valueClose) < 1) {
            actualValueStart = valueStart;
            actualValueClose = valueClose;
        } else {
            actualValueStart = valueClose;
            actualValueClose = valueStart;
        }

        return Restrictions.between(property, actualValueStart, actualValueClose);
    }

    private static Criterion getRangeFilterRestrictionEqual(String property, Object valueStart) {
        if (valueStart.getClass().equals(DateTime.class)) {
            DateTime valueStartDateTime = (DateTime) valueStart;
            return Restrictions.between(property, valueStartDateTime, valueStartDateTime.plusDays(1).minusSeconds(1));
        }
        return Restrictions.eq(property, valueStart);
    }

    private static void applyOrNegateFilterCriterion(Junction conditions, String property, Criterion restriction, boolean negated) {
        conditions.add(Restrictions.conjunction() //
                .add(Restrictions.isNotNull(property)) //
                .add(negated ? Restrictions.not(restriction) : restriction));
    }

}
