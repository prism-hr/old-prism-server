package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_RESTORE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_RESTORE_TRANSITION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectDisabledPendingReactivation extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectEmailCreator());
		stateActions.add(projectEscalateApproved());

		stateActions.add(new PrismStateAction() //
		        .withAction(PROJECT_RESTORE) //
		        .withTransitions(PROJECT_RESTORE_TRANSITION_GROUP));

		stateActions.add(projectViewEditApproved());
	}

}
