package uk.co.alumeni.prism.workflow.resolvers.state.termination;

import uk.co.alumeni.prism.domain.resource.Resource;

public interface StateTerminationResolver<T extends Resource> {

    boolean resolve(T resource);

}
