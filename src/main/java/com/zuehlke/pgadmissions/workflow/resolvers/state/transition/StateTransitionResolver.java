package com.zuehlke.pgadmissions.workflow.resolvers.state.transition;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;

public interface StateTransitionResolver {

	StateTransition resolve(Resource resource, Comment comment);

}
