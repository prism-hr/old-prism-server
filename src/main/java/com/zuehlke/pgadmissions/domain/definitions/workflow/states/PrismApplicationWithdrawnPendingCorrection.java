package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingCorrection.applicationCorrect;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingExport.applicationViewEditApprovedPendingExport;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationWithdrawnPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(applicationCorrect(APPLICATION_WITHDRAWN_PENDING_EXPORT)); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(applicationViewEditApprovedPendingExport()); //
	}

}
