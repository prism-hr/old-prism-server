package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProgramEscalatedResolver implements StateTransitionResolver {

	@Inject
	private StateService stateService;
	
	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		if (BooleanUtils.isTrue(resource.getProgram().getImported())) {
			return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_DISABLED_PENDING_REACTIVATION);
		} else {
			return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_DISABLED_COMPLETED);
		}
	}

}
