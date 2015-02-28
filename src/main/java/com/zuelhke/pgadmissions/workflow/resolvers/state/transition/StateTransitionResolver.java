package com.zuelhke.pgadmissions.workflow.resolvers.state.transition;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface StateTransitionResolver {

	public void resolve(Resource resource, Comment comment);
	
}
