package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_RESERVED_PENDING_REALLOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReserved.applicationCompleteReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReserved.applicationWithdrawnReserved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReservedWaiting extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalate(APPLICATION_RESERVED_PENDING_REALLOCATION)); //
		stateActions.add(applicationCompleteReserved(state));
        stateActions.add(applicationUploadReference(state));
		stateActions.add(applicationViewEditWithViewerRecruiter(state)); //
		stateActions.add(applicationWithdrawnReserved());
	}

}
