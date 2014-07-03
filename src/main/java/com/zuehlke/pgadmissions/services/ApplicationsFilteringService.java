package com.zuehlke.pgadmissions.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.FilterConstraint;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.ApplicationListFilterCategory;
import com.zuehlke.pgadmissions.domain.definitions.ApplicationListSortCategory;
import com.zuehlke.pgadmissions.domain.definitions.ResourceListSearchPredicate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Service
@Transactional
public class ApplicationsFilteringService {
    
    @Autowired
    private ScopeService scopeService;

    // TODO: Generalise for other list types
    public Filter getDefaultApplicationFiltering(User user) {
        Filter filtering;
        if (user.getUserAccount().getFilters().get(PrismScope.APPLICATION) != null) {
            filtering = user.getUserAccount().getFilters().get(scopeService.getById(PrismScope.APPLICATION));
        } else {
            filtering = getActiveApplicationFiltering();
        }
        return filtering;
    }

    public Filter getActiveApplicationFiltering() {
        Filter filtering = new Filter();
        Set<FilterConstraint> filters = filtering.getFilterConstraints();
        filters.add(getFilterForNonStatus(PrismState.APPLICATION_APPROVED));
        filters.add(getFilterForNonStatus(PrismState.APPLICATION_REJECTED));
        filters.add(getFilterForNonStatus(PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT));
        return filtering;
    }
    
    public Filter getUrgentApplicationFiltering() {
        Filter filtering = new Filter();
        filtering.setSortCategory(ApplicationListSortCategory.URGENT);
        return filtering;
    }

    private FilterConstraint getFilterForNonStatus(PrismState status) {
        FilterConstraint filter = new FilterConstraint();
        filter.setSearchCategory(ApplicationListFilterCategory.APPLICATION_STATUS);
        filter.setSearchPredicate(ResourceListSearchPredicate.TEXT_NOT_CONTAINING);
        filter.setSearchTerm(status.name());
        return filter;
    }
}
