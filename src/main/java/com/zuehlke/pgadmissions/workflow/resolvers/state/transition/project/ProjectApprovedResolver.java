package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.project;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProjectApprovedResolver implements StateTransitionResolver<Project> {

	@Inject
	private StateService stateService;
	
	@Override
	public StateTransition resolve(Project resource, Comment comment) {
		return stateService.getPredefinedStateTransition(resource, comment);
	}

}
