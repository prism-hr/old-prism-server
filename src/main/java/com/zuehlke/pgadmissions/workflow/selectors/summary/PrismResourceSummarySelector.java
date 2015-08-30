package com.zuehlke.pgadmissions.workflow.selectors.summary;

import java.util.List;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface PrismResourceSummarySelector {

    List<Integer> getPossible(PrismScope scope, Resource parent, Set<Integer> importedEntities);

}
