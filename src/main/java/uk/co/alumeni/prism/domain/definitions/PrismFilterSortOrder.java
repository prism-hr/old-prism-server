package uk.co.alumeni.prism.domain.definitions;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public enum PrismFilterSortOrder {

	ASCENDING, //
	DESCENDING;

	public static Criterion getPagingRestriction(String filterSortColumn, PrismFilterSortOrder filterSortOrder, String filterSortColumnValue) {
		if (filterSortOrder == ASCENDING) {
			return Restrictions.gt(filterSortColumn, filterSortColumnValue);
		} else {
			return Restrictions.lt(filterSortColumn, filterSortColumnValue);
		}
	}

	public static Order getOrderExpression(String filterSortColumn, PrismFilterSortOrder filterSortOrder) {
		if (filterSortOrder == ASCENDING) {
			return Order.asc(filterSortColumn);
		} else {
			return Order.desc(filterSortColumn);
		}
	}

}
