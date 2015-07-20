package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;

@Component
public class ApplicationClosingDateResolver implements StateDurationResolver<Application> {

	@Override
	public LocalDate resolve(Application resource, Comment comment) {
		return resource.getClosingDate();
	}

}
