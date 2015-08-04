package com.zuehlke.pgadmissions.workflow.selectors;

import java.util.List;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface PrismSelector<T> {

	List<T> getSelected(Resource resource);

	List<T> getPromoted(Resource resource);

	List<T> getPossible(Resource resource);

}
