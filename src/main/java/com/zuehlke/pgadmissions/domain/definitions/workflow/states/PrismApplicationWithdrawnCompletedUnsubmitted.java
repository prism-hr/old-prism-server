package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PURGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationWithdrawnCompletedUnsubmitted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_PURGE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_VIEW_EDIT) //
		        .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_AS_CREATOR)); //
	}

}
