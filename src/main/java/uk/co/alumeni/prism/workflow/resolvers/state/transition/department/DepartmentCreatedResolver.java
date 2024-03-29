package uk.co.alumeni.prism.workflow.resolvers.state.transition.department;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

import javax.inject.Inject;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.*;

@Component
public class DepartmentCreatedResolver implements StateTransitionResolver<Department> {

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Department resource, Comment comment) {
        State initialState = comment.getTransitionState();
        ResourceParent parentResource = (ResourceParent) resource.getParentResource();
        if (initialState == null) {
            User user = comment.getUser();
            if (resourceService.isInState(resource.getInstitution(), "APPROVAL")) {
                return stateService.getStateTransition(parentResource, comment.getAction(), DEPARTMENT_APPROVAL_PARENT_APPROVAL);
            } else if (roleService.hasUserRole(resource, user, INSTITUTION_ADMINISTRATOR_GROUP)) {
                return stateService.getStateTransition(parentResource, comment.getAction(), DEPARTMENT_APPROVED);
            }
            return stateService.getStateTransition(parentResource, comment.getAction(), DEPARTMENT_APPROVAL);
        }
        return stateService.getStateTransition(parentResource, comment.getAction(), DEPARTMENT_UNSUBMITTED);
    }

}
