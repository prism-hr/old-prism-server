package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ProjectApprovedPartnerResolver implements StateTransitionResolver {

    @Inject
    private ActionService actionService;

    @Inject
    private StateService stateService;

    @Inject
    private RoleService roleService;

    @Override
    public StateTransition resolve(Resource resource, Comment comment) {
        if (comment.isProjectPartnerApproveComment()) {
            List<PrismRole> roles = roleService.getRoles(comment.getUser());
            Action approveAction = actionService.getPermittedAction(PROJECT_APPROVAL, PROJECT_COMPLETE_APPROVAL_STAGE, roles);
            if (approveAction != null) {
                return stateService.getStateTransition(resource, comment.getAction(), PROJECT_APPROVED);
            }
        }
        return stateService.getPredefinedStateTransition(resource, comment);
    }

}
