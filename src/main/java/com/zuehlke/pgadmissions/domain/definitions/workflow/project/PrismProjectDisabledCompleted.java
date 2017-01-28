package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectDisabledCompleted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectEmailCreator());
		stateActions.add(projectViewEditApproved());
	}

}
