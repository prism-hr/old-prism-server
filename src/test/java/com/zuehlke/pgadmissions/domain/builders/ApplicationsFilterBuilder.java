package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;

public class ApplicationsFilterBuilder {

    private Integer id;

    private SearchCategory searchCategory;

    private SearchPredicate searchPredicate;

    private String searchTerm;

    public ApplicationsFilterBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationsFilterBuilder searchCategory(SearchCategory searchCategory) {
        this.searchCategory = searchCategory;
        return this;
    }

    public ApplicationsFilterBuilder searchPredicate(SearchPredicate searchPredicate) {
        this.searchPredicate = searchPredicate;
        return this;
    }

    public ApplicationsFilterBuilder searchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    public ApplicationsFilter build() {
        ApplicationsFilter filter = new ApplicationsFilter();
        filter.setId(id);
        filter.setSearchCategory(searchCategory);
        filter.setSearchPredicate(searchPredicate);
        filter.setSearchTerm(searchTerm);
        return filter;
    }

}
