package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproval.projectEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproval.projectViewEditApproval;

public class PrismProjectRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectEmailCreatorApproval()); //

		stateActions.add(projectViewEditApproval()); //
	}

}
