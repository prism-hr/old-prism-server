package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationProvidedReviewResolver implements StateTransitionResolver<Application> {

	@Inject
	private RoleService roleService;

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Application resource, Comment comment) {
		if (roleService.getRoleUsers(resource, APPLICATION_REVIEWER).size() == 1) {
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_REVIEW_PENDING_COMPLETION);
		}
		return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_REVIEW_PENDING_FEEDBACK);
	}

}
