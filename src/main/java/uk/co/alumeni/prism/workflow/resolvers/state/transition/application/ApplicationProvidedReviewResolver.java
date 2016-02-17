package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REVIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationProvidedReviewResolver implements StateTransitionResolver<Application> {

	@Inject
	private UserService userService;

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Application resource, Comment comment) {
		if (userService.getUsersWithRoles(resource, APPLICATION_REVIEWER).size() == 1) {
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_REVIEW_PENDING_COMPLETION);
		}
		return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_REVIEW_PENDING_FEEDBACK);
	}

}
