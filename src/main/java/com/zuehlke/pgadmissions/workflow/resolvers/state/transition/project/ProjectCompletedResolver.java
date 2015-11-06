package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_PARENT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_UNSUBMITTED;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

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
