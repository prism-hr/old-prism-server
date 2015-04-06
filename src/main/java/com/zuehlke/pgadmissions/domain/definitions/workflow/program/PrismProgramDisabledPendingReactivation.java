package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramDisabledPendingReactivation extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(programEmailCreator()); //
		stateActions.add(programEscalateApproved()); //
		stateActions.add(programViewEditApproved());
	}

}
