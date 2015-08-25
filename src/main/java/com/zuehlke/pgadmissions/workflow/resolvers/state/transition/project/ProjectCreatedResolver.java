package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProjectCreatedResolver implements StateTransitionResolver<Project> {

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Project resource, Comment comment) {
        User user = comment.getUser();
        ResourceParent parentResource = (ResourceParent) resource.getParentResource();
        if (roleService.hasUserRole(resource, user, PROGRAM_ADMINISTRATOR_GROUP)) {
            return stateService.getStateTransition(parentResource, comment.getAction(), PROJECT_APPROVED);
        }
        return stateService.getStateTransition(parentResource, comment.getAction(), PROJECT_APPROVAL);
    }

}
