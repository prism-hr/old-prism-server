package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationRejected.applicationEscalateRejected;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationRejected.applicationReverseRejection;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCorrect;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationForgetExport;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditCorrect;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationRejectedPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationCorrect(APPLICATION_REJECTED_PENDING_EXPORT)); //
		stateActions.add(applicationForgetExport(APPLICATION_REJECTED_COMPLETED)); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalateRejected()); //
		stateActions.add(applicationReverseRejection());
		stateActions.add(applicationViewEditCorrect());
	}

}
