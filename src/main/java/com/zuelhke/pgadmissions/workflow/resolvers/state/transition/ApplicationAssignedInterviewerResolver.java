package com.zuelhke.pgadmissions.workflow.resolvers.state.transition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class ApplicationAssignedInterviewerResolver implements StateTransitionResolver {

	@Inject
	private StateService stateService;

	@Override
	public StateTransition resolve(Resource resource, Comment comment) {
		if (comment.isApplicationDelegateAdministrationComment()) {
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW);
		} else {
			DateTime baseline = new DateTime();
			if (comment.isApplicationInterviewRecordedComment(baseline)) {
				return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW_PENDING_FEEDBACK);
			} else if (comment.isApplicationInterviewScheduledComment(baseline)) {
				return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW_PENDING_INTERVIEW);
			}
			return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_INTERVIEW_PENDING_AVAILABILITY);
		}
	}

}
