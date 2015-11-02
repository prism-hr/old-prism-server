package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PARENT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

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
        User user = comment.getUser();
        if (resourceService.isUnderApproval(resource)) {
            return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_APPROVAL_PARENT_APPROVAL);
        } else if (roleService.hasUserRole(resource, user, PROGRAM_ADMINISTRATOR_GROUP)) {
            return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_APPROVED);
        }
        return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_APPROVAL);
    }

}
