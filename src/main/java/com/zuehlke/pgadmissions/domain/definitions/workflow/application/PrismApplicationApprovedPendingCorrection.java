package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproved.applicationEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationApprovedPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationCorrect(APPLICATION_APPROVED_PENDING_EXPORT)); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalateApproved()); //
		stateActions.add(applicationViewEditCorrect());
	}

}
