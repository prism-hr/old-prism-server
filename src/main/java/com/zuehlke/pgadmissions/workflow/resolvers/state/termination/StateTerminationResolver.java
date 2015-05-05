package com.zuehlke.pgadmissions.workflow.resolvers.state.termination;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface StateTerminationResolver {

	boolean resolve(Resource resource);

}
