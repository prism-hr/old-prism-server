package com.zuelhke.pgadmissions.workflow.resolvers.state.duration;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface StateDurationResolver {

	public void resolve(Resource resource, Comment comment);
	
}
