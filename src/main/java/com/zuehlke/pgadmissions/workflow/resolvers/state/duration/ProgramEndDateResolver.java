package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

@Component
public class ProgramEndDateResolver implements StateDurationResolver {

	@Override
	public LocalDate resolve(Resource resource, Comment comment) {
		return comment.getTransitionState().getId() == PROGRAM_DISABLED_COMPLETED ? null : resource.getProgram().getEndDate();
	}

}
