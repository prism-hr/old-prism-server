package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.FilterConstraint;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.definitions.ApplicationListSortCategory;
import com.zuehlke.pgadmissions.domain.definitions.ResourceListSortOrder;

public class ApplicationsFilteringBuilder {

    private Integer id;

    private List<FilterConstraint> filters = new ArrayList<FilterConstraint>();

    private ApplicationListSortCategory sortCategory = ApplicationListSortCategory.APPLICATION_DATE;

    private ResourceListSortOrder order = ResourceListSortOrder.DESCENDING;

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

    public ApplicationsFilteringBuilder sortCategory(ApplicationListSortCategory sortCategory) {
        this.sortCategory = sortCategory;
        return this;
    }

    public ApplicationsFilteringBuilder order(ResourceListSortOrder order) {
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
        filtering.getFilterConstraints().addAll(filters);
        filtering.setSortCategory(sortCategory);
        filtering.setSortOrder(order);
        filtering.setPage(blockCount);
        filtering.setSatisfyAllConditions(useDisjunction);
        return filtering;
    }

}
