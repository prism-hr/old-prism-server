package uk.co.alumeni.prism.workflow.resolvers.state.transition.project;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_PARENT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_UNSUBMITTED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProjectCompletedResolver implements StateTransitionResolver<Project> {

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Project resource, Comment comment) {
        State transitionState = comment.getTransitionState();
        if (transitionState == null) {
            User user = comment.getUser();
            if (resourceService.isUnderApproval(resource)) {
                return stateService.getStateTransition(resource, comment.getAction(), PROJECT_APPROVAL_PARENT_APPROVAL);
            } else if (roleService.hasUserRole(resource, user, PROJECT_ADMINISTRATOR_GROUP)) {
                return stateService.getStateTransition(resource, comment.getAction(), PROJECT_APPROVED);
            }
            return stateService.getStateTransition(resource, comment.getAction(), PROJECT_APPROVAL);
        }
        return stateService.getStateTransition(resource, comment.getAction(), PROJECT_UNSUBMITTED);
    }

}
