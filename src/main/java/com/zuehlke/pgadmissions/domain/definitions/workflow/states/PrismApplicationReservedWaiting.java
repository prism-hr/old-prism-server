package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_RESERVED_PENDING_REALLOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReserved.applicationCompleteReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReserved.applicationWithdrawnReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;

public class PrismApplicationReservedWaiting extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalate(APPLICATION_RESERVED_PENDING_REALLOCATION)); //
		stateActions.add(applicationCompleteReserved());
		stateActions.add(applicationViewEditWithViewerRecruiter(state)); //
		stateActions.add(applicationWithdrawnReserved());
	}

}
