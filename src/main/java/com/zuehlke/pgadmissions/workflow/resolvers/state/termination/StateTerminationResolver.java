package com.zuehlke.pgadmissions.workflow.resolvers.state.termination;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface StateTerminationResolver<T extends Resource<?>> {

    boolean resolve(T resource);

}
