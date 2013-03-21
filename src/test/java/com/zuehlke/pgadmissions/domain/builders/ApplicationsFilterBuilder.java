package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;

public class ApplicationsFilterBuilder {

    private Integer id;

    private RegisteredUser user;

    private SearchCategory searchCategory;

    private String searchTerm;

    public ApplicationsFilterBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationsFilterBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public ApplicationsFilterBuilder searchCategory(SearchCategory searchCategory) {
        this.searchCategory = searchCategory;
        return this;
    }

    public ApplicationsFilterBuilder searchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    public ApplicationsFilter build() {
        ApplicationsFilter filter = new ApplicationsFilter();
        filter.setId(id);
        filter.setUser(user);
        filter.setSearchCategory(searchCategory);
        filter.setSearchTerm(searchTerm);
        return filter;
    }

}
