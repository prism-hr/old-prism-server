package com.zuehlke.pgadmissions.workflow.selectors;

import java.util.List;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface PrismSelector<T> {

	public List<T> getSelected(Resource resource);

	public List<T> getPromoted(Resource resource);

	public List<T> getPossible(Resource resource);

}
