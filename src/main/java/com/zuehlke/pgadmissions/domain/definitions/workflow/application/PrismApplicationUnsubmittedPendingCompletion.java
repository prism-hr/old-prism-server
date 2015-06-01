package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationUnsubmitted.applicationCompleteUnsubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationUnsubmittedPendingCompletion extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCompleteUnsubmitted() //
		        .withRaisesUrgentFlag() //
		        .withNotification(APPLICATION_COMPLETE_REQUEST)); //

		stateActions.add(applicationEscalate(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED)); //
		stateActions.add(applicationWithdraw());
	}

}
