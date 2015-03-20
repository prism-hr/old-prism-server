package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;

@Component
public class ProjectEndDateResolver implements StateDurationResolver {

	@Override
	public LocalDate resolve(Resource resource, Comment comment) {
		return comment.getTransitionState().getId() == PrismState.PROJECT_DISABLED_COMPLETED ? null : resource.getProject().getEndDate();
	}

}