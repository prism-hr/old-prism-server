package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_REFERENCE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationCompletedReferenceStateResolver implements StateTransitionResolver<Application> {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Application resource, Comment comment) {
		if (resource.getState().getStateGroup().getId() == APPLICATION_REFERENCE) {
			return stateService.getPredefinedStateTransition(resource, comment);
		} else {
			return stateService.getStateTransition(resource, comment.getAction());
		}
	}

}
