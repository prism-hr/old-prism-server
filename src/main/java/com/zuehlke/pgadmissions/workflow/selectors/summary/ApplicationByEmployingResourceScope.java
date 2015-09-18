package com.zuehlke.pgadmissions.workflow.selectors.summary;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationByEmployingResourceScope implements PrismResourceSummarySelector {

    @Inject
    private ApplicationService applicationService;

    @Override
    public List<Integer> getPossible(Resource resource, PrismScope entityScope, Collection<Integer> entityIds) {
        return applicationService.getApplicationsByEmployingResourceScope((ResourceParent) resource, entityScope, entityIds);
    }

}
