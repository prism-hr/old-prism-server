package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationCompleteInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationProvideInterviewFeedback;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationViewEditInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationWithdrawInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEscalate;

public class PrismApplicationInterviewPendingFeedback extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationCompleteInterviewScheduled()); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationEscalate(APPLICATION_INTERVIEW_PENDING_COMPLETION)); //

		stateActions.add(applicationProvideInterviewFeedback() //
		        .withTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION //
		                .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_GROUP)));

		stateActions.add(applicationViewEditInterviewScheduled(state)); //
		stateActions.add(applicationWithdrawInterviewScheduled());
	}

}
