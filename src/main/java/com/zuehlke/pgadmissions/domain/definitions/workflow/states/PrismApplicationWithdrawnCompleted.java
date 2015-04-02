package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PURGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_PURGED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingExport.applicationViewEditApprovedPendingExport;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationWithdrawnCompleted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_PURGE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_PURGED) //
		                .withTransitionAction(APPLICATION_PURGE))); //

		stateActions.add(applicationViewEditApprovedPendingExport()); //
	}

}
