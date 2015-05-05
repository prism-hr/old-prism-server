package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditUnapproved;

public class PrismProgramDisabledCompleted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(programEmailCreator()); //
		stateActions.add(programViewEditUnapproved()); //
	}

}
