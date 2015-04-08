package com.zuehlke.pgadmissions.workflow.resolvers.state.transition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class ProgramCreatedResolver implements StateTransitionResolver {

	@Inject
	private RoleService roleService;

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		if (roleService.hasUserRole(resource, comment.getUser(), INSTITUTION_ADMINISTRATOR_GROUP)
		        || BooleanUtils.isTrue(resource.getProgram().getImported())) {
			return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), PROGRAM_APPROVED);
		}
		return stateService.getStateTransition(resource.getParentResource(), comment.getAction(), PROGRAM_APPROVAL);
	}

}
