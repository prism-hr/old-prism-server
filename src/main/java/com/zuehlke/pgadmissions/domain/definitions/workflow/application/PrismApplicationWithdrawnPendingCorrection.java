package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWithdrawn.applicationEscalateWithdrawn;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationWithdrawnPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationCorrect(APPLICATION_WITHDRAWN_PENDING_EXPORT)); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalateWithdrawn()); //
		stateActions.add(applicationViewEditCorrect());
	}

}
