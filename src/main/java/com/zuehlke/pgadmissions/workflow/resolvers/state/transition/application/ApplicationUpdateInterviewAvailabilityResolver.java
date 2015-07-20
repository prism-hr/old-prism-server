package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationUpdateInterviewAvailabilityResolver implements StateTransitionResolver<Application> {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Application resource, Comment comment) {
		if (resource.getPreviousState().getId().equals(APPLICATION_INTERVIEW)) {
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW);
		}
		return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW_PENDING_SCHEDULING);
	}

}
