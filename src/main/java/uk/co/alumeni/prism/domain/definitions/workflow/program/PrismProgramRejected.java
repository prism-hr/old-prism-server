package uk.co.alumeni.prism.domain.definitions.workflow.program;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(PrismProgramWorkflow.programEmailCreatorUnnapproved()); //
		stateActions.add(PrismProgramWorkflow.programViewEditInactive()); //
	}

}
