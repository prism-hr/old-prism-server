package com.zuehlke.pgadmissions.workflow.resolvers.state.termination;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface StateTerminationResolver {

	public boolean resolve(Resource resource);
	
}
