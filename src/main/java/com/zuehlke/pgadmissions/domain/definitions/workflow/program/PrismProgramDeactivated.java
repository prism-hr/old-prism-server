package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramApproval.programEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramApproved.programCreateProjectApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramApproved.programEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramApproved.programViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramDeactivated extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(programEmailCreatorApproval()); //

		stateActions.add(programViewEditApproved()); //

		stateActions.add(programCreateProjectApproved()); //

		stateActions.add(programEscalateApproved()); //
	}

}
