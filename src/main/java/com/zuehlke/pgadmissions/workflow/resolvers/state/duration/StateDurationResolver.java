package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface StateDurationResolver {

	LocalDate resolve(Resource resource, Comment comment);

}
