package uk.co.alumeni.prism.workflow.resolvers.state.transition.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PARENT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_UNSUBMITTED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProgramCompletedResolver implements StateTransitionResolver<Program> {

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Program resource, Comment comment) {
        State transitionState = comment.getTransitionState();
        if (transitionState == null) {
            User user = comment.getUser();
            if (resourceService.isUnderApproval(resource)) {
                return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_APPROVAL_PARENT_APPROVAL);
            } else if (roleService.hasUserRole(resource, user, PROGRAM_ADMINISTRATOR_GROUP)) {
                return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_APPROVED);
            }
            return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_APPROVAL);
        }
        return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_UNSUBMITTED);
    }

}
