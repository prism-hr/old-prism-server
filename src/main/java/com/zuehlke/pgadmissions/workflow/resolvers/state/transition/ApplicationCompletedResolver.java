package com.zuehlke.pgadmissions.workflow.resolvers.state.transition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class ApplicationCompletedResolver implements StateTransitionResolver {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		LocalDate closingDate = resource.getApplication().getClosingDate();
		if (closingDate == null || closingDate.isBefore(new LocalDate())) {
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_VALIDATION_PENDING_COMPLETION);
		}
		return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_VALIDATION);
	}

}
