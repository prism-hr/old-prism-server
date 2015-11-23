package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.selection;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface StateTransitionSelectionResolver {

    boolean resolve(Resource resource);

}
