package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReserved.applicationCompleteReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReserved.applicationEscalateReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReserved.applicationWithdrawnReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;

public class PrismApplicationReservedPendingReallocation extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalateReserved()); //

		stateActions.add(applicationCompleteReserved() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST));

		stateActions.add(applicationViewEditWithViewerRecruiter(state)); //
		stateActions.add(applicationWithdrawnReserved());
	}
}
