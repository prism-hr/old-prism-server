package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_RESERVED_PENDING_REALLOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproved.applicationMoveToDifferentState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationViewEditVerification;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationReservedWaiting extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_RESERVED_PENDING_REALLOCATION) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(applicationMoveToDifferentState());

		stateActions.add(applicationViewEditVerification(state)); //

		stateActions.add(PrismApplicationValidation.applicationWithdrawValidation());
	}

}
