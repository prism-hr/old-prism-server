package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditUnapproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(programEmailCreator()); //
		stateActions.add(programViewEditUnapproved()); //
	}

}
