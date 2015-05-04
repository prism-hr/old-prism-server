package com.zuehlke.pgadmissions.workflow.selectors;

import com.zuehlke.pgadmissions.domain.resource.Resource;

import java.util.List;

public interface PrismSelector<T> {

	List<T> getSelected(Resource resource);

	List<T> getPromoted(Resource resource);

	List<T> getPossible(Resource resource);

}
