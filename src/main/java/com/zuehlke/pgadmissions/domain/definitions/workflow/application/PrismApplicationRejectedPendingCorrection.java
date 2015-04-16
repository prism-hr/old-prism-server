package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationRejected.applicationEscalateRejected;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationRejected.applicationReverseRejection;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationRejectedPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationCorrect(APPLICATION_REJECTED_PENDING_EXPORT)); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalateRejected()); //
		stateActions.add(applicationReverseRejection());
		stateActions.add(applicationViewEditCorrect());
	}

}
