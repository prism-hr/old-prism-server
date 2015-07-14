package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

@Component
public class ApplicationClosingDateResolver implements StateDurationResolver {

	@Override
	public LocalDate resolve(Resource resource, Comment comment) {
		return resource.getApplication().getClosingDate();
	}

}
