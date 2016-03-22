package uk.co.alumeni.prism.workflow.resolvers.state.transition.department;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL_PARENT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class DepartmentCompletedResolver implements StateTransitionResolver<Department> {

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Department resource, Comment comment) {
        State transitionState = comment.getTransitionState();
        if (resourceService.isUnderApproval(resource.getInstitution())) {
            return stateService.getStateTransition(resource, comment.getAction(), DEPARTMENT_APPROVAL_PARENT_APPROVAL);
        } else if (transitionState == null) {
            User user = comment.getUser();
            if (roleService.hasUserRole(resource, user, DEPARTMENT_ADMINISTRATOR) || roleService.createsUserRole(comment, user, DEPARTMENT_ADMINISTRATOR)) {
                return stateService.getStateTransition(resource, comment.getAction(), DEPARTMENT_APPROVED);
            }
            return stateService.getStateTransition(resource, comment.getAction(), DEPARTMENT_APPROVAL);
        }
        return stateService.getPredefinedStateTransition(resource, comment);
    }

}
