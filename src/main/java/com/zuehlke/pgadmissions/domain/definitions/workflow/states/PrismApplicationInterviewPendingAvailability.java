package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationCompleteInterviewScheduling;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationConfirmInterviewArrangements;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationProvideInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationUpdateInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationViewEditInterviewScheduling;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationWithdrawInterviewScheduling;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEscalate;

public class PrismApplicationInterviewPendingAvailability extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationCompleteInterviewScheduling());
		stateActions.add(applicationConfirmInterviewArrangements()); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationEscalate(APPLICATION_INTERVIEW_PENDING_SCHEDULING)); //

		stateActions.add(applicationProvideInterviewAvailability() //
		        .withTransitions(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION //
		                .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP))); //

		stateActions.add(applicationUpdateInterviewAvailability(state));
		stateActions.add(applicationViewEditInterviewScheduling(state)); //
		stateActions.add(applicationWithdrawInterviewScheduling());
	}

}
