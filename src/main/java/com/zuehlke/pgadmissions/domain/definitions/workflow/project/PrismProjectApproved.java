package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectSuspendApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectApproved extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(new PrismStateAction() //
		        .withAction(PROJECT_CREATE_APPLICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_UNSUBMITTED) //
		                .withTransitionAction(APPLICATION_COMPLETE) //
		                .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

		stateActions.add(projectEmailCreator()); //
		stateActions.add(projectEscalateApproved()); //
		stateActions.add(projectSuspendApproved()); //
		stateActions.add(projectTerminateApproved()); //
		stateActions.add(projectViewEditApproved()); //
	}

}
