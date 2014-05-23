package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationFilter;
import com.zuehlke.pgadmissions.domain.ApplicationFilterGroup;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;

@Service
@Transactional
public class ApplicationsFilteringService {

    public ApplicationFilterGroup getDefaultApplicationFiltering(User user) {
        ApplicationFilterGroup filtering;
        if (user.getUserAccount().getFilterGroup() != null) {
            filtering = user.getUserAccount().getFilterGroup();
        } else {
            filtering = getActiveApplicationFiltering();
        }
        return filtering;
    }

    public ApplicationFilterGroup getActiveApplicationFiltering() {
        ApplicationFilterGroup filtering = new ApplicationFilterGroup();
        List<ApplicationFilter> filters = filtering.getFilters();
        filters.add(getFilterForNonStatus(PrismState.APPLICATION_APPROVED));
        filters.add(getFilterForNonStatus(PrismState.APPLICATION_REJECTED));
        filters.add(getFilterForNonStatus(PrismState.APPLICATION_WITHDRAWN));
        return filtering;
    }
    
    public ApplicationFilterGroup getUrgentApplicationFiltering() {
        ApplicationFilterGroup filtering = new ApplicationFilterGroup();
        filtering.setSortCategory(SortCategory.URGENT);
        return filtering;
    }

    private ApplicationFilter getFilterForNonStatus(PrismState status) {
        ApplicationFilter filter = new ApplicationFilter();
        filter.setSearchCategory(SearchCategory.APPLICATION_STATUS);
        filter.setSearchPredicate(SearchPredicate.NOT_CONTAINING);
        filter.setSearchTerm(status.name());
        return filter;
    }
}
