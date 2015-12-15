package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationProvidedReferenceResolver implements StateTransitionResolver<Application> {

	@Inject
	private RoleService roleService;

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Application resource, Comment comment) {
		if (resource.getState().getStateGroup().getId().equals(PrismStateGroup.APPLICATION_REFERENCE)) {
			if (roleService.getRoleUsers(resource, APPLICATION_REFEREE).size() == 1) {
				return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_REFERENCE_PENDING_COMPLETION);
			}
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_REFERENCE);
		} else {
			return stateService.getStateTransition(resource, comment.getAction());
		}
	}

}
