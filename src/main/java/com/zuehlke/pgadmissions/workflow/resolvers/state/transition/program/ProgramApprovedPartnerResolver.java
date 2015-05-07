package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;

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
public class ProgramApprovedPartnerResolver implements StateTransitionResolver {
    
    @Inject
    private ActionService actionService;
    
    @Inject
    private RoleService roleService;
    
	@Inject
	private StateService stateService;
	
	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
	    if (comment.isProgramPartnerApproveComment()) {
	        List<PrismRole> roles = roleService.getRoles(comment.getUser());
	        Action approveAction = actionService.getPermittedAction(PROGRAM_APPROVAL, PROGRAM_COMPLETE_APPROVAL_STAGE, roles);
	        if (approveAction != null) {
	            return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_APPROVED);
	        }
	    }
		return stateService.getPredefinedStateTransition(resource, comment);
	}

}
