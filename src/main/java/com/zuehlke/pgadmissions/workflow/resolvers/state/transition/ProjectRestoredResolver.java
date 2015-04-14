package com.zuehlke.pgadmissions.workflow.resolvers.state.transition;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class ProjectRestoredResolver implements StateTransitionResolver {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		PrismState transitionState = stateService.getProjectLastApprovedOrDeactivatedState((Project) resource);
		return stateService.getStateTransition(resource, comment.getAction(), transitionState);
	}

}
