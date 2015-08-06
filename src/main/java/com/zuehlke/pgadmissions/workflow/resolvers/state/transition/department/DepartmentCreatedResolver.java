package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL_PARENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVED;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class DepartmentCreatedResolver implements StateTransitionResolver<Department> {

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Department resource, Comment comment) {
        User user = comment.getUser();
        Institution institution = resource.getInstitution();
        List<PrismState> activeInstitutionStates = stateService.getActiveResourceStates(INSTITUTION);
        if (!activeInstitutionStates.contains(institution.getState().getId())) {
            return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), DEPARTMENT_APPROVAL_PARENT);
        } else if (roleService.hasUserRole(resource, user, INSTITUTION_ADMINISTRATOR_GROUP)) {
            return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), DEPARTMENT_APPROVED);
        }
        return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), DEPARTMENT_APPROVAL);
    }

}
