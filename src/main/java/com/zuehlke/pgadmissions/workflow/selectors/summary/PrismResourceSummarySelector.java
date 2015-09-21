package com.zuehlke.pgadmissions.workflow.selectors.summary;

import java.util.Collection;
import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface PrismResourceSummarySelector {

    List<Integer> getPossible(Resource resource, PrismScope entityScope, Collection<Integer> entityIds);

}
