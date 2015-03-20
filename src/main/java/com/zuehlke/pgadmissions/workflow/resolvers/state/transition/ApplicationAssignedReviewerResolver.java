package com.zuehlke.pgadmissions.workflow.resolvers.state.transition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class ApplicationAssignedReviewerResolver implements StateTransitionResolver {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		if (comment.isApplicationDelegateAdministrationComment()) {
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_REVIEW);
		}
		return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_REVIEW_PENDING_FEEDBACK);
	}

}