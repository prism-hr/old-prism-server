package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_EXPORT_PROGRAM_INSTANCE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationExportedResolver implements StateTransitionResolver {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		String exportException = comment.getExportException();
		if (exportException == null || exportException.contains(SYSTEM_NO_EXPORT_PROGRAM_INSTANCE.name())) {
			return stateService.getStateTransition(resource, comment.getAction(),
			        PrismState.valueOf(resource.getState().getStateGroup().getId() + "_COMPLETED"));
		}
		return stateService.getStateTransition(resource, comment.getAction(),
		        PrismState.valueOf(resource.getState().getStateGroup().getId() + "_PENDING_CORRECTION"));
	}

}
