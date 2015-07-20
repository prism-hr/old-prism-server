package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.institution;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class InstitutionApprovedResolver implements StateTransitionResolver<Institution> {

	@Inject
	private StateService stateService;
	
	@Override
	public StateTransition resolve(Institution resource, Comment comment) {
		return stateService.getPredefinedStateTransition(resource, comment);
	}

}
