package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;
 
@Component
public class ApplicationCompletedStateResolver implements StateTransitionResolver {
	
	@Inject
	private StateService stateService;

	@Override
    public StateTransition resolve(Resource resource, Comment comment) {
		return stateService.getPredefinedStateTransition(resource, comment);
    }

}
