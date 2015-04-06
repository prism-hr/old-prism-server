package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproved extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(new PrismStateAction() //
		        .withAction(PROGRAM_CREATE_APPLICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_UNSUBMITTED) //
		                .withTransitionAction(APPLICATION_COMPLETE) //
		                .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

		stateActions.add(programCreateProject()); //
		stateActions.add(programEmailCreator());
		stateActions.add(programEscalateApproved());
		stateActions.add(programViewEditApproved()); //
	}

	public static PrismStateAction programCreateProject() {
		return new PrismStateAction() //
		        .withAction(PROGRAM_CREATE_PROJECT) //
		        .withTransitions(PROJECT_CREATE_TRANSITION //
		                .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP));
	}

}
