package com.zuehlke.pgadmissions.services.builders;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;

public class ResourceListConstraintBuilder extends ConstraintBuilder {

    public static final String SEQUENCE_IDENTIFIER = "sequenceIdentifier";

    public static Criteria appendLimitCriterion(Criteria criteria, ResourceListFilterDTO filter, String lastSequenceIdentifier, Integer maxRecords) {
        return appendLimitCriterion(criteria, filter, null, lastSequenceIdentifier, maxRecords);
    }
    
    public static Criteria appendLimitCriterion(Criteria criteria, ResourceListFilterDTO filter, Junction conditions, String lastSequenceIdentifier,
            Integer maxRecords) {

        if (filter.isUrgentOnly()) {
            criteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
        }

        if (conditions != null) {
            criteria.add(conditions);
        }
        
        FilterSortOrder sortOrder = filter.getSortOrder();

        if (lastSequenceIdentifier != null) {
            criteria.add(FilterSortOrder.getPagingRestriction(SEQUENCE_IDENTIFIER, sortOrder, lastSequenceIdentifier));
        }

        criteria.addOrder(FilterSortOrder.getOrderExpression(SEQUENCE_IDENTIFIER, sortOrder));

        if (maxRecords != null) {
            criteria.setMaxResults(maxRecords);
        }

        return criteria;
    }

    public static void appendClosingDateFilterCriterion(Junction conditions, String property, FilterExpression expression, LocalDate valueDateStart,
            LocalDate valueDateClose, boolean negated) {
        Junction restriction = Restrictions.disjunction().add(getRangeFilterCriterion(property, expression, valueDateStart, valueDateClose))
                .add(getRangeFilterCriterion("previous" + WordUtils.capitalize(property), expression, valueDateStart, valueDateClose));
        applyOrNegateFilterCriterion(conditions, restriction, negated);
    }

    public static void appendParentResourceFilterCriterion(Junction conditions, String property, List<Integer> parentResourceIds, boolean negated) {
        applyOrNegateFilterCriterion(conditions, Restrictions.in(property, parentResourceIds), negated);
    }

    public static void appendStateGroupFilterCriterion(Junction conditions, String property, List<PrismState> stateIds, boolean negated) {
       appendPropertyInFilterCriterion(conditions, property, stateIds, negated);
    }

    public static void appendUserFilterCriterion(Junction conditions, String property, List<Integer> userIds, boolean negated) {
        appendPropertyInFilterCriterion(conditions, property, userIds, negated);
    }

    public static void appendUserRoleFilterCriterion(PrismScope scopeId, Junction conditions, String property, List<Integer> userRoleIds, boolean negated) {
        applyOrNegateFilterCriterion(conditions, Restrictions.in(property, userRoleIds), negated);
    }

    public static <T extends Resource> void throwResourceFilterListMissingPropertyError(PrismScope scopeId, FilterProperty property) {
        throw new Error(scopeId.name() + " does not have a " + property.name() + " property");
    }

}
