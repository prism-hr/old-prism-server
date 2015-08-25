package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreatorUnnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditInactive;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramWithdrawn extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(programEmailCreatorUnnapproved()); //
		stateActions.add(programViewEditInactive()); //
	}

}
