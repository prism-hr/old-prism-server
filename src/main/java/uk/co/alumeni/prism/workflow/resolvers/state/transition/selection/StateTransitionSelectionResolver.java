package uk.co.alumeni.prism.workflow.resolvers.state.transition.selection;

import uk.co.alumeni.prism.domain.resource.Resource;

public interface StateTransitionSelectionResolver {

    boolean resolve(Resource resource);

}
