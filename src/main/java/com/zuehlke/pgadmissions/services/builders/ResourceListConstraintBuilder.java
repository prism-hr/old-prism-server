package com.zuehlke.pgadmissions.services.builders;

import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;

public class ResourceListConstraintBuilder extends ConstraintBuilder {

    public static final String SEQUENCE_IDENTIFIER = "sequenceIdentifier";

    public static void appendFilterCriterion(Criteria criteria, Junction conditions, ResourceListFilterDTO filter) {
        if (filter.isUrgentOnly()) {
            criteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
        }

        if (conditions != null) {
            criteria.add(conditions);
        }
    }
    
    public static Criteria appendLimitCriterion(Criteria criteria, ResourceListFilterDTO filter, String lastSequenceIdentifier, Integer maxRecords) {
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

    public static <T extends Resource> void throwResourceFilterListMissingPropertyError(PrismScope scopeId, FilterProperty property) {
        throw new Error(scopeId.name() + " does not have a " + property.name() + " property");
    }

}
