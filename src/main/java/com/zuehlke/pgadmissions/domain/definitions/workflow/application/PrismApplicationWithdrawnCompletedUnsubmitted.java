package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationPurge;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationWithdrawnCompletedUnsubmitted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationPurge(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED)); //
		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_VIEW_EDIT) //
		        .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_AS_CREATOR)); //
	}

}