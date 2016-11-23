package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

import javax.inject.Inject;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;

@Component
public class ApplicationConfirmedManagementResolver implements StateTransitionResolver<Application> {

    @Inject
    private UserService userService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        if (userService.getUsersWithRoles(resource, APPLICATION_HIRING_MANAGER).size() == 1) {
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVAL_PENDING_COMPLETION);
        }
        return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVAL_PENDING_FEEDBACK);
    }

}
