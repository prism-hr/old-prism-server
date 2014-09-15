package com.zuehlke.pgadmissions.domain.definitions;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public enum FilterSortOrder {

    ASCENDING, //
    DESCENDING;

    public static Criterion getPagingRestriction(String filterSortColumn, FilterSortOrder filterSortOrder, String filterSortColumValue) {
        if (filterSortOrder == ASCENDING) {
            return Restrictions.gt(filterSortColumn, filterSortColumValue);
        } else {
            return Restrictions.lt(filterSortColumn, filterSortColumValue);
        }
    }

    public static Order getOrderExpression(String filterSortColumn, FilterSortOrder filterSortOrder) {
        if (filterSortOrder == ASCENDING) {
            return Order.asc(filterSortColumn);
        } else {
            return Order.desc(filterSortColumn);
        }
    }

}
