package com.zuehlke.pgadmissions.workflow.resolvers.state.transition;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_IMPORT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;

@Component
public class ProgramCreatedResolver implements StateTransitionResolver {

	@Inject
	private RoleService roleService;

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		if (roleService.hasUserRole(resource, comment.getUser(), INSTITUTION_ADMINISTRATOR) || comment.getAction().getId() == INSTITUTION_IMPORT_PROGRAM) {
			return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_APPROVED);
		}
		return stateService.getStateTransition(resource, comment.getAction(), PROGRAM_APPROVAL);
	}

}
