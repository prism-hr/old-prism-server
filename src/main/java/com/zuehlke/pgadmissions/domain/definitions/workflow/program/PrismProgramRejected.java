package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditUnapproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(programEmailCreator()); //
		stateActions.add(programViewEditUnapproved()); //
	}

}
