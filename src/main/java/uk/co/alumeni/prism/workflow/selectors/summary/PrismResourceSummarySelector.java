package uk.co.alumeni.prism.workflow.selectors.summary;

import java.util.Collection;
import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;

public interface PrismResourceSummarySelector {

    List<String> getPossible(Resource resource, PrismScope entityScope, Collection<String> entityIds);

}
