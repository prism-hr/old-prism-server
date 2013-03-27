package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationSearchDTO {

    private List<ApplicationsFilter> filters;

    private SortCategory sortCategory;

    private SortOrder order;

    private Integer blockCount;

    public List<ApplicationsFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<ApplicationsFilter> filters) {
        this.filters = filters;
    }

    public SortCategory getSortCategory() {
        return sortCategory;
    }

    public void setSortCategory(SortCategory sortCategory) {
        this.sortCategory = sortCategory;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public Integer getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(Integer blockCount) {
        this.blockCount = blockCount;
    }

}