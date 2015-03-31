package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval.programEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproved.programViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismProgramDisabledPendingReactivation extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(programEmailCreatorApproval()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(PROGRAM_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROGRAM_DISABLED_COMPLETED) //
		                .withTransitionAction(PROGRAM_ESCALATE))); //

		stateActions.add(programViewEditApproved());
	}

}
