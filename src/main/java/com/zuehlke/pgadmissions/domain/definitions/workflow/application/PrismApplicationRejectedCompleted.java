package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_REJECTED_PURGED_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationRejected.applicationReverseRejection;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationPurge;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationRejectedCompleted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationPurge(APPLICATION_REJECTED_PURGED_TRANSITION)); //
		stateActions.add(applicationReverseRejection());
		stateActions.add(applicationViewEdit()); //
	}

}
