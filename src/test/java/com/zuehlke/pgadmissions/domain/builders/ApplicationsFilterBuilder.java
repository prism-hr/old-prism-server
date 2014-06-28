package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.FilterConstraint;
import com.zuehlke.pgadmissions.domain.definitions.ApplicationListFilterCategory;
import com.zuehlke.pgadmissions.domain.definitions.ResourceListSearchPredicate;

public class ApplicationsFilterBuilder {

    private Integer id;

    private ApplicationListFilterCategory searchCategory;

    private ResourceListSearchPredicate searchPredicate;

    private String searchTerm;

    public ApplicationsFilterBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationsFilterBuilder searchCategory(ApplicationListFilterCategory searchCategory) {
        this.searchCategory = searchCategory;
        return this;
    }

    public ApplicationsFilterBuilder searchPredicate(ResourceListSearchPredicate searchPredicate) {
        this.searchPredicate = searchPredicate;
        return this;
    }

    public ApplicationsFilterBuilder searchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    public FilterConstraint build() {
        FilterConstraint filter = new FilterConstraint();
        filter.setId(id);
        filter.setSearchCategory(searchCategory);
        filter.setSearchPredicate(searchPredicate);
        filter.setSearchTerm(searchTerm);
        return filter;
    }

}
