package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;

@Service
@Transactional
public class ApplicationsFilteringService {

    public ApplicationsFiltering getDefaultApplicationFiltering(User user) {
        ApplicationsFiltering filtering;
        if (user.getAccount().getFiltering() != null) {
            filtering = user.getAccount().getFiltering();
        } else {
            filtering = getActiveApplicationFiltering();
        }
        return filtering;
    }

    public ApplicationsFiltering getActiveApplicationFiltering() {
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        List<ApplicationsFilter> filters = filtering.getFilters();
        filters.add(getFilterForNonStatus(ApplicationFormStatus.APPROVED));
        filters.add(getFilterForNonStatus(ApplicationFormStatus.REJECTED));
        filters.add(getFilterForNonStatus(ApplicationFormStatus.WITHDRAWN));
        return filtering;
    }
    
    public ApplicationsFiltering getUrgentApplicationFiltering() {
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        filtering.setSortCategory(SortCategory.URGENT);
        return filtering;
    }

    private ApplicationsFilter getFilterForNonStatus(ApplicationFormStatus status) {
        ApplicationsFilter filter = new ApplicationsFilter();
        filter.setSearchCategory(SearchCategory.APPLICATION_STATUS);
        filter.setSearchPredicate(SearchPredicate.NOT_CONTAINING);
        filter.setSearchTerm(status.displayValue());
        return filter;
    }
}
