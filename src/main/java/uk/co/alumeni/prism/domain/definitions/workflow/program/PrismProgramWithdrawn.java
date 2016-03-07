package uk.co.alumeni.prism.domain.definitions.workflow.program;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramWithdrawn extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(PrismProgramWorkflow.programSendMessageUnnapproved()); //
		stateActions.add(PrismProgramWorkflow.programViewEditInactive()); //
	}

}
