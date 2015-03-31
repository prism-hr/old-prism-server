package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationUnsubmitted.applicationCompleteUnsubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationUnsubmitted.applicationWithdrawUnsubmitted;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationUnsubmittedPendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCompleteUnsubmitted() //
		        .withRaisesUrgentFlag() //
		        .withNotification(APPLICATION_COMPLETE_REQUEST)); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(applicationWithdrawUnsubmitted());
	}

}
