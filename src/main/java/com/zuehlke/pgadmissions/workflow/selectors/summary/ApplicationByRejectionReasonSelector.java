package com.zuehlke.pgadmissions.workflow.selectors.summary;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationByRejectionReasonSelector implements PrismResourceSummarySelector {

    @Inject
    private ApplicationService applicationService;

    @Override
    public List<String> getPossible(Resource resource, PrismScope entityScope, Collection<String> entityIds) {
        return applicationService.getApplicationsByRejectionReason((ResourceParent) resource, entityIds).stream().map(rr -> rr.toString()).collect(toList());
    }

}