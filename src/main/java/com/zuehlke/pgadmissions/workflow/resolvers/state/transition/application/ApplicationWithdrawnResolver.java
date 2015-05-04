package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationWithdrawnResolver implements StateTransitionResolver {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		if (BooleanUtils.isTrue(resource.getProgram().getImported())) {
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_WITHDRAWN_PENDING_EXPORT);
		} else {
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_WITHDRAWN_COMPLETED);
		}
	}

}
