package com.zuehlke.pgadmissions.workflow.resolvers.state.transition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_VERIFICATION;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class ApplicationCompletedVerificationStateResolver implements StateTransitionResolver {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		if (resource.getState().getStateGroup().getId() == APPLICATION_VERIFICATION) {
			return stateService.getPredefinedStateTransition(resource, comment);
		} else {
			return stateService.getStateTransition(resource, comment.getAction());
		}
	}

}
