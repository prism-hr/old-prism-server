package uk.co.alumeni.prism.workflow.selectors.summary;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.services.ApplicationService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ApplicationByRejectionReasonSelector implements PrismResourceSummarySelector {

    @Inject
    private ApplicationService applicationService;

    @Override
    public List<String> getPossible(Resource resource, PrismScope entityScope, Collection<String> entityIds) {
        return applicationService.getApplicationsByRejectionReason((ResourceParent) resource, entityIds).stream().map(rr -> rr.toString()).collect(toList());
    }

}
