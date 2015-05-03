package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DISABLED_COMPLETED;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

@Component
public class ProjectEndDateResolver implements StateDurationResolver {

	@Override
	public LocalDate resolve(Resource resource, Comment comment) {
		return comment.getTransitionState().getId() == PROJECT_DISABLED_COMPLETED ? null : resource.getProject().getEndDate();
	}

}
