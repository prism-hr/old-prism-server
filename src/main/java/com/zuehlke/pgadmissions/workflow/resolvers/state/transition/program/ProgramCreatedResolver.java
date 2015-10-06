package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_UNSUBMITTED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProgramCreatedResolver implements StateTransitionResolver<Program> {

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Program resource, Comment comment) {
        State initialState = comment.getTransitionState();
        ResourceParent parentResource = (ResourceParent) resource.getParentResource();
        if (initialState == null) {
            User user = comment.getUser();
            if (roleService.hasUserRole(resource, user, DEPARTMENT_ADMINISTRATOR_GROUP)) {
                return stateService.getStateTransition(parentResource, comment.getAction(), PROGRAM_APPROVED);
            }
            return stateService.getStateTransition(parentResource, comment.getAction(), PROGRAM_APPROVAL);
        }
        return stateService.getStateTransition(parentResource, comment.getAction(), PROGRAM_UNSUBMITTED);
    }

}
