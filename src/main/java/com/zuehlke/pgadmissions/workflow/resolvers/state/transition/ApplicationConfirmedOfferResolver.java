package com.zuehlke.pgadmissions.workflow.resolvers.state.transition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class ApplicationConfirmedOfferResolver implements StateTransitionResolver {

	@Inject
	private CommentService commentService;

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		Comment recruitedComment = commentService.getEarliestComment((ResourceParent) resource, Application.class, APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
		State parentResourceTransitionState = recruitedComment.getParentResourceTransitionState();
		if (parentResourceTransitionState == null) {
			return stateService.getStateTransition(resource, comment.getAction(), resource.getState().getId());
		} else {
			return stateService.getStateTransition(resource, comment.getAction(), recruitedComment.getParentResourceTransitionState().getId());
		}
	}

}
