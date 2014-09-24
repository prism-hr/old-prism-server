package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationsFilteringBuilder {

    private Integer id;

    private List<ApplicationsFilter> filters = new ArrayList<ApplicationsFilter>();

    private SortCategory sortCategory = SortCategory.APPLICATION_DATE;

    private SortOrder order = SortOrder.DESCENDING;

    private Integer blockCount;

    private Boolean useDisjunction = false;

    public ApplicationsFilteringBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationsFilteringBuilder filters(ApplicationsFilter... filters) {
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

    public ApplicationsFiltering build() {
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        filtering.setId(id);
        filtering.setFilters(filters);
        filtering.setSortCategory(sortCategory);
        filtering.setOrder(order);
        filtering.setBlockCount(blockCount);
        filtering.setUseDisjunction(useDisjunction);
        return filtering;
    }

}