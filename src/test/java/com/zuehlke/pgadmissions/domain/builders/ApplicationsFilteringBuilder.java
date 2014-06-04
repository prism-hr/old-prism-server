package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.FilterConstraint;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationsFilteringBuilder {

    private Integer id;

    private List<FilterConstraint> filters = new ArrayList<FilterConstraint>();

    private SortCategory sortCategory = SortCategory.APPLICATION_DATE;

    private SortOrder order = SortOrder.DESCENDING;

    private Integer blockCount;

    private Boolean useDisjunction = false;

    public ApplicationsFilteringBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationsFilteringBuilder filters(FilterConstraint... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }

    public ApplicationsFilteringBuilder sortCategory(SortCategory sortCategory) {
        this.sortCategory = sortCategory;
        return this;
    }

    public ApplicationsFilteringBuilder order(SortOrder order) {
        this.order = order;
        return this;
    }

    public ApplicationsFilteringBuilder blockCount(Integer blockCount) {
        this.blockCount = blockCount;
        return this;
    }

    public ApplicationsFilteringBuilder useDisjunction(Boolean useDisjunction) {
        this.useDisjunction = useDisjunction;
        return this;
    }

    public Filter build() {
        Filter filtering = new Filter();
        filtering.setId(id);
        filtering.getFilters().addAll(filters);
        filtering.setSortCategory(sortCategory);
        filtering.setSortOrder(order);
        filtering.setBlockCount(blockCount);
        filtering.setSatisfyAllConditions(useDisjunction);
        return filtering;
    }

}
