package uk.co.alumeni.prism.workflow.resolvers.state.transition;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.workflow.StateTransition;

public interface StateTransitionResolver<T extends Resource> {

    StateTransition resolve(T resource, Comment comment);

}
