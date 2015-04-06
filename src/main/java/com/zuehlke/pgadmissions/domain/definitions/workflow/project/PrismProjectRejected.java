package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditUnapproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectEmailCreator()); //
		stateActions.add(projectViewEditUnapproved()); //
	}

}
