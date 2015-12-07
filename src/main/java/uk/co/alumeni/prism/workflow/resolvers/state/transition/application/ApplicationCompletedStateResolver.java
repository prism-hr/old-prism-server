package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationCompletedStateResolver implements StateTransitionResolver<Application> {

	@Inject
	private StateService stateService;

	@Override
    public StateTransition resolve(Application resource, Comment comment) {
		return stateService.getPredefinedStateTransition(resource, comment);
    }

}
